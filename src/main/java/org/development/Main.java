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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

                // Create a new GUI for the secondary window
                MultiWindowTextGUI secondaryGui = new MultiWindowTextGUI(screen);

                secondaryWindow.setComponent(new Button("Exit", secondaryWindow::close));
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
                    screen.newTextGraphics().putString(10, 10, TEXT);
                    screen.setCursorPosition(new TerminalPosition(10, 10));
                    screen.refresh();

                    long timeStart = System.currentTimeMillis();
                    while (input.size() < TEXT.length()) {
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
                            screen.setCursorPosition(new TerminalPosition(9 + input.size() + 1, 10));
                            screen.setCharacter(9 + input.size(), 6, input.peek());
                            screen.refresh();

//                            TextCharacter cur = input.peek();
//                            cur.withBackgroundColor(cur.getForegroundColor()).withForegroundColor(TextColor.ANSI.DEFAULT);
//                            cur.withCharacter(TEXT.charAt(input.size()));
                            TextCharacter cur2 = new TextCharacter(TEXT.charAt(input.size() - 1), TextColor.ANSI.BLACK, input.peek().getForegroundColor());
//                            cur2.withBackgroundColor(input.peek().getForegroundColor());
                            screen.setCharacter(9 + input.size(), 10, cur2);
                        }
                        if (keyStroke.getKeyType() == KeyType.Backspace && !input.isEmpty()) {
                            TextCharacter blank = new TextCharacter(' ', null, TextColor.ANSI.DEFAULT);
                            screen.setCharacter(9 + input.size(), 6, blank);
                            screen.setCursorPosition(new TerminalPosition(9 + input.size(), 10));

                            TextCharacter cur = new TextCharacter(TEXT.charAt(input.size() - 1));
                            screen.setCharacter(9 + input.size(), 10, cur);
                            input.pop();
                        }

                        screen.refresh();
                    }
                    long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
                    // Convert elapsed time to HH:mm:ss format   timeInstant.use
                    long hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis);
                    screen.clear();
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60;
                    long milliseconds = elapsedTimeMillis % 1000;
//                    TimeUnit.MILLISECONDS.
                    // Format elapsed time as a string screen.refresh();
                    String elapsedTimeFormatted = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds);

                    screen.newTextGraphics().putString(10, 10, elapsedTimeFormatted);

                    double myTimeMinutes = elapsedTimeMillis / 1000.0 / 60;
                    screen.newTextGraphics().putString(10, 11, "Chars/minute: " + (countCharacters(TEXT)/myTimeMinutes));
                    screen.newTextGraphics().putString(10, 12, "Words/minute: " + (countWords(
                            TEXT) / myTimeMinutes));
                    screen.refresh();
                    Thread.sleep(10000);


                    input = new Stack<>();
                    window.setVisible(true);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
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

    public static int countCharacters(String string) {
        return string == null ? 0 : string.length();
    }

    public static int countWords(String string) {
        return string.trim().split("\\s+").length;
    }
}
