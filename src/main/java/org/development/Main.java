package org.development;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.ThemeStyle;
import com.googlecode.lanterna.graphics.ThemedTextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.development.gui.Background;
import org.development.gui.MainDisplay;

import javax.swing.*;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Main {
    static MultiWindowTextGUI gui;
    static boolean isOn = true;
    final static String TEXT = "some text to be typed using different characters1234314! {} [] []: '";
    static Stack<TextCharacter> input = new Stack<>();

    public static void main(String[] args) throws IOException {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
//        gui = new MultiWindowTextGUI(screen);
        screen.startScreen();


        Window window = new BasicWindow("window");

        gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager(), null, new Background());
        TextGUIThread guiThread = gui.getGUIThread();
        try {
            ((AsynchronousTextGUIThread) guiThread).start();

            new Thread(() -> {
                while (isOn && ((AsynchronousTextGUIThread) guiThread).getState() != AsynchronousTextGUIThread.State.STOPPED) {
                    guiThread.invokeLater(window::invalidate);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }).start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Panel menu = new Panel(new LinearLayout(Direction.VERTICAL));
        menu.addComponent(new Label("test label"));
        menu.addComponent(new Label("test label2"));
        menu.addComponent(new Button("gui", new Runnable() {
            @Override
            public void run() {
                // Create a secondary window
                window.setVisible(false);
                Window secondaryWindow = new BasicWindow();

                // Clear the screen for the secondary window
                //                    Screen newScreen = terminalFactory.createScreen();
//                    newScreen.startScreen();
//                    newScreen.clear();
//                    screen.setCharacter(1, 1, new TextCharacter('!'));
//                    TextCharacter backCharacter = screen.getBackCharacter(1, 1);
//                    System.out.println(backCharacter);
//                    screen.refresh();
//                    TextCharacter backCharacter1 = screen.getBackCharacter(1, 1);
//                    System.out.println("back2: " + backCharacter1);

                // Create a new GUI for the secondary window
                MultiWindowTextGUI secondaryGui = new MultiWindowTextGUI(screen);

                secondaryWindow.setComponent(new Button("Exit", secondaryWindow::close));
//                    secondaryGui.addWindow(secondaryWindow);

                // Create a thread to wait for 1 second and close the secondary window
//                    new Thread(() -> {
//                        try {
//                            Thread.sleep(1000);
//                            secondaryWindow.close();
////                            newScreen.stopScreen();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }).start();

                secondaryGui.addWindowAndWait(secondaryWindow);
                window.setVisible(true);
            }
        }));
        menu.addComponent(new Button("type test", new Runnable() {

            @Override
            public void run() {
                try {
                    window.setVisible(false);

                    screen.clear();

                    screen.newTextGraphics().putString(10, 5, TEXT);
                    screen.refresh();

                    while (true) {
                        KeyStroke keyStroke = screen.readInput();

                        if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
                            break;
                        }

                        if (keyStroke.getKeyType() == KeyType.Character) {
                            TextCharacter ch;
                            if (TEXT.charAt(input.size()) == keyStroke.getCharacter()) {
                                ch = new TextCharacter(keyStroke.getCharacter(), TextColor.ANSI.GREEN, TextColor.ANSI.DEFAULT);
                            } else {
                                ch = new TextCharacter(keyStroke.getCharacter(), TextColor.ANSI.RED, TextColor.ANSI.DEFAULT);
                            }

                            input.push(ch);

//                            input += ch;
                            screen.setCursorPosition(new TerminalPosition(9+input.size(), 6));
                            screen.setCharacter(9 + input.size(), 6, input.peek());
                            screen.refresh();
                        }
                        if (keyStroke.getKeyType() == KeyType.Backspace) {
                            TextCharacter blank = new TextCharacter(' ', null, TextColor.ANSI.DEFAULT);
                            screen.setCharacter(9 + input.size(), 6, blank);
                            screen.setCursorPosition(new TerminalPosition(9+input.size(), 6));
                            input.pop();
                        }

                        screen.refresh();
                    }
                    window.setVisible(true);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
        menu.addComponent(new Button("Exit", () -> {
            window.close();

            ((AsynchronousTextGUIThread) guiThread).stop();
            isOn = false;
        }));

        window.setComponent(menu);


        gui.addWindowAndWait(window);
    }
}
