package org.development.tests;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Menus {


    public static void main(String[] args) {
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
        MenuBar menubar = new MenuBar();

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        // "File" menu
        Menu menuFile = new Menu("File");
//        menuFile.add();
//        MenuItem menuItem = new MenuItem();

        menubar.add(menuFile);
        menuFile.add(new MenuItem("Open...", new Runnable() {
            public void run() {
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null)
                    MessageDialog.showMessageDialog(
                            textGUI, "Open", "Selected file:\n" + file, MessageDialogButton.OK);
            }
        }));
        menuFile.add(new MenuItem("Exit", new Runnable() {
            public void run() {
                System.exit(0);
            }
        }));

        // "Help" menu
        Menu menuHelp = new Menu("Help");
        menubar.add(menuHelp);
        menuHelp.add(new MenuItem("Homepage", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "Homepage", "https://github.com/mabe02/lanterna", MessageDialogButton.OK);
            }
        }));
        menuHelp.add(new MenuItem("About", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "About", "Lanterna drop-down menu", MessageDialogButton.OK);
            }
        }));

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(menubar);

        menubar.setPosition(new TerminalPosition(50,50));
        MenuBar newMenu = new MenuBar();
        newMenu.add(menuFile.add(new MenuItem("Open...", new Runnable() {
            public void run() {
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null)
                    MessageDialog.showMessageDialog(
                            textGUI, "Open", "Selected file:\n" + file, MessageDialogButton.OK);
            }
        })));
//        newMenu.setPosition(new TerminalPosition(20,20));
//        newMenu.setSize(new TerminalSize(20,20));
        window.setComponent(newMenu);

//        textGUI.addWindow(window);
        BasicWindow window2 = new BasicWindow("second");
        window2.setComponent(newMenu);
        window2.setFixedSize(new TerminalSize(50,20));
//        window2.setPosition(new TerminalPosition(20,20));

        textGUI.addWindowAndWait(window2);

    }
}
