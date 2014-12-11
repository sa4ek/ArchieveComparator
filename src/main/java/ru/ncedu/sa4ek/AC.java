package ru.ncedu.sa4ek;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by Михаил on 01.11.2014.
 */
public class AC extends JFrame {
    String path1, path2;

    public AC() {
        super("ArchieveCompare");
        createGUI();
    }

    public void createGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        JButton button1 = new JButton("Открыть файл 1");
        button1.setBounds(5, 5, 185, 30);
        panel.add(button1);
        JButton button2 = new JButton("Открыть файл 2");
        button2.setBounds(5, 40, 185, 30);
        panel.add(button2);
        JButton button3 = new JButton("Сравнить");
        button3.setBounds(5, 75, 185, 30);
        panel.add(button3);

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret1 = fileopen.showDialog(null, "Открыть файл 1");
                if (ret1 == JFileChooser.APPROVE_OPTION) {
                    File file1 = fileopen.getSelectedFile();
                    path1 = file1.getName();
                }
            }
        });
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser file2open = new JFileChooser();
                int ret2 = file2open.showDialog(null, "Открыть файл 2");
                if (ret2 == JFileChooser.APPROVE_OPTION) {
                    File file2 = file2open.getSelectedFile();
                    path2 = file2.getName();
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZipCompare c = new ZipCompare(path1, path2);
                try {
                    c.compare();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        getContentPane().add(panel);
        setPreferredSize(new Dimension(285, 145));
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    AC frame = new AC();
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
        }
        if (args.length == 2) {
            ZipCompare c = new ZipCompare(args[0], args[1]);
            try {
                c.compare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Usage: ArchieveComparator [file1] [file2] or without args (to start UI) ");
            System.exit(1);
        }

    }

}
