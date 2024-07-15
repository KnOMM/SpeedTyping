package org.development.tests;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class MessageDialogs {
    static String initial = "Initial text";


    public static void main(String[] args) {
        // Setup terminal and screen layers
        Terminal terminal = null;
        try {
            terminal = new DefaultTerminalFactory().createTerminal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Screen screen = null;
        try {
            screen = new TerminalScreen(terminal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            screen.startScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Setup WindowBasedTextGUI for dialogs
        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);


        Panel panel = new Panel();
        panel.addComponent(new Button("Test", new Runnable() {
            @Override
            public void run() {
                MessageDialogButton messageDialogButton = new MessageDialogBuilder()
                        .setTitle("Here is the title")
                        .setText("Here is a message")
                        .addButton(MessageDialogButton.Abort)
                        .addButton(MessageDialogButton.Cancel)
                        .build()
                        .showDialog(textGUI);


                if (messageDialogButton == MessageDialogButton.Abort) {
                    System.out.println("aborting");
                } else {
                    System.out.println("not aborting");
                }
            }
        }));

        panel.addComponent(new Button("Test", new Runnable() {
            @Override
            public void run() {

                String s = new TextInputDialogBuilder()
                        .setTitle("Title")
                        .setDescription(initial)
                        .setValidationPattern(Pattern.compile("[0-9]"), "You didn't enter a single number!")
                        .build()
                        .showDialog(textGUI);

//                String input = TextInputDialog.showDialog(textGUI, "Title", "This is the description", "Initial content");

//                TextInputDialog.showDialog(textGUI, "Title", "This is the description", input);
                if (s != null) {
                    initial = s;
                }
            }
        }));

        panel.addComponent(new Button("Files", new Runnable() {
            @Override
            public void run() {
                File file = new FileDialogBuilder()
                        .setTitle("Open File")
                        .setDescription("Choose a file")
                        .setActionLabel("Open")
                        .build()
                        .showDialog(textGUI);


                if (file != null) {
                    Path path = file.toPath();
                    if (Files.exists(path)) {
                        System.out.println("exists");
                        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

                            int line;
                            while ((line = br.read()) != -1) {
                                System.out.print((char) line);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return;
                    }
                }
            }
        }));

        panel.addComponent(new Button("Test", new Runnable() {
            @Override
            public void run() {
                File input = new DirectoryDialogBuilder()
                        .setTitle("Select directory")
                        .setDescription("Choose a directory")
                        .setActionLabel("Select")
                        .build()
                        .showDialog(textGUI);
                System.out.println(input);
            }
        }));

        Table<String> table = new Table<String>("Column 1", "Column 2", "Column 3");
        table.getTableModel().addRow("1", "2", "3");
        table.getTableModel().addRow("1", "2", "3");
        table.getTableModel().addRow("1", "2", "3");

        table.setSelectAction(new Runnable() {
            @Override
            public void run() {
                List<String> data = table.getTableModel().getRow(table.getSelectedRow());
                for (int i = 0; i < data.size(); i++) {
                    System.out.println(data.get(i));
                }
            }
        });

        panel.addComponent(table);

        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }
}
