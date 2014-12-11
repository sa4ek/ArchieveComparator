package ru.ncedu.sa4ek;//TO-DO: Положи классы в свой пакет. Не надейся на default package. Для маленьких проектов это некритично, а для больших очень даже.
//Учись для каждого класса определять пакет.
import java.io.*;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipCompare {
    String[] args = new String[2];

    public ZipCompare(String arg1, String arg2) {
        args[0] = arg1;
        args[1] = arg2;
    }

    private String[] getFirstCreated(String[] args){
        String temp = new String();
        File fileOne = new File(args[0]);
        File fileTwo = new File(args[1]);

        if(fileOne.lastModified() <= fileTwo.lastModified()){
            temp = args[0];
            args[0] = args[1];
            args[1] = temp;
        }

        return args;
    }

    public void compare() throws IOException {
        ArrayList<String> newFiles = new ArrayList<String>();
        ArrayList<String> deletedFiles = new ArrayList<String>();
        ArrayList<String> difNameOfOneFile = new ArrayList<String>();
        ArrayList<String> notMatchFiles = new ArrayList<String>();


        //TO-DO: нужно избавиться от этого if'а и такого большого количества переменных.
        //Предлагаю тебе сделать private-функцию, которая на вход возьмет args, сделает файлы, проверит,
        //какой был сделан раньше и поменяет args местами, если надо. Здесь вызвать эту функцию, а потом
        //ZipFile'ы делать в прямом порядке и не мучаться.
        //P.S. Избегай дублирования!!!

        args = getFirstCreated(args);
        ZipFile file1 = new ZipFile(args[0]);
        ZipFile file2 = new ZipFile(args[1]);


        System.out.println("Comparing " + args[0] + " with " + args[1] + ":");

        //TO-DO: Вот здесь у тебя кроется логическая ошибка. Ты, думаешь, что этим присваиванием set3 = set1;
        //сохранишь данные set1, а это неверно. Можешь сам в этом убедиться, распечатав содержимое set3 и set4 до и после
        //итерации по set1 и анализа содержимого. В результате, потом на rename проверяются только файлы из первого архива
        //и новые файлы второго архива.
        //Нужно сначала на rename проверять, а потом уже на все остальное. Причем, если файлы были переименованы, то они, на мой взгляд,
        //не должны считаться удаленными или добавленными. Надо переписать логику.

        Set set1 = new LinkedHashSet();

        for (Enumeration e = file1.entries(); e.hasMoreElements(); )
            set1.add(((ZipEntry) e.nextElement()).getName());

        Set set2 = new LinkedHashSet();

        for (Enumeration e = file2.entries(); e.hasMoreElements(); )
            set2.add(((ZipEntry) e.nextElement()).getName());

        //TO-DO: errcount надо переименовать иначе сразу возникает ощущение, что ты ошибки считаешь, а ты считаешь несоответствия.
        //Это важно, если кому-то после тебя придется этот код поддерживать
        int disparity = 0;
        int filecount = 0;

        for (Iterator i = set1.iterator(); i.hasNext(); )
        {
            String name1 = (String) i.next();
            for (Iterator j = set2.iterator(); j.hasNext(); )
            {
                String name2 = (String) j.next();
                try
                {

                    if (file1.getEntry(name1).getSize() == file2.getEntry(name2).getSize() &&
                            streamsEqual(file1.getInputStream(file1.getEntry(name1)), file2.getInputStream(file2
                                    .getEntry(name2)))
                            && !name1.equals(name2))
                    {
                        System.out.println("diferent names of one file" + name1 + " " + name2);
                        difNameOfOneFile.add(name1 + " " + name2);
                        set1.remove(name1);
                        set2.remove(name2);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator i = set1.iterator(); i.hasNext(); )
        {
            //TIP: можно использовать i.next().toString(). Удобно.
            String name = i.next().toString();

            if (!set2.contains(name))
            {
                System.out.println(name + " deleted ");
                deletedFiles.add(name);
                disparity += 1;
                continue;
            }

            try
            {
                set2.remove(name);

                if (!streamsEqual(file1.getInputStream(file1.getEntry(name)), file2.getInputStream(file2
                        .getEntry(name)))) {

                    System.out.println(name + " does not match");
                    notMatchFiles.add(name);
                    disparity += 1;
                    continue;
                }
            }
            catch (Exception e)
            {
                System.out.println(name + ": IO Error " + e);
                e.printStackTrace();
                disparity += 1;
                continue;
            }

            filecount += 1;
        }

        for (Iterator i = set2.iterator(); i.hasNext();)
        {
            String name = (String) i.next();
            System.out.println(name + " new files ");
            newFiles.add(name);
            disparity += 1;
        }




        System.out.println(filecount + " entries matched");
        if (disparity > 0) {
            System.out.println(disparity + " entries did not match");
        }
        //TO-DO: Здесь лучше сделать не List, а Map<String,ArrayList<String>>.
        //И туда передавать первым параметром сообщения "New files in second archieve" и т.п.
        //А в printList передавать мапу. А там по ней итерироваться и выводить в цикле.

       // List<ArrayList<String>> group = new ArrayList<ArrayList<String>>();
        Map<String, ArrayList<String>> group =  new HashMap<String, ArrayList<String>>();
        group.put("New Files in second archieve", newFiles);
        group.put("Deleted Files in second archieve", deletedFiles);
        group.put("Files doesn`t match", notMatchFiles);
        group.put("Different name of one file", difNameOfOneFile);
        printList(group, args);
    }

    static boolean streamsEqual(InputStream stream1, InputStream stream2) throws Exception {
        CheckedInputStream check1, check2;
        check1 = new CheckedInputStream(stream1, new CRC32());
        check2 = new CheckedInputStream(stream2, new CRC32());
        BufferedInputStream in1 = new BufferedInputStream(check1);
        BufferedInputStream in2 = new BufferedInputStream(check2);
        while (in1.read() != -1) {
        }
        while (in2.read() != -1) {
        }
        return check1.getChecksum().getValue() == check2.getChecksum().getValue();
    }

    public void printList(Map<String,ArrayList<String>> list, String[] args) throws IOException {
        File flt = new File("report.txt");
        PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(flt)));

        out.println(args[0] + " is created earlier than " + args[1]);
        out.println();

        //TIP: Если ты сделаешь Map, то у тебя уйдет это некрасивое дублирование, и тебе
        //самому будет очень приятно!

        for(Map.Entry entry: list.entrySet()){
            out.println(entry.getKey());
            for(String s: (ArrayList<String>)entry.getValue()){
                out.println(s);
            }
        }

        out.flush();
    }
}
