package org.development;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class SpeedType implements Runnable {
    private Window window;
    private static Screen screen;
    private String text;
    private int columnStart;
    private int column;
    private int row;
    public static int offset;
    static Stack<TextCharacter> input;

    SpeedType(Window window, Screen screen, String text) {
        this.window = window;
        SpeedType.screen = screen;
        this.text = text;
        column = 10;
        columnStart = 10;
        offset = 0;
        input = new Stack<>();
    }

    @Override
    public void run() {
        try {
            window.setVisible(false);
            screen.clear();

            // TODO clear this
            text = "";
            Path textFile = Paths.get("text.txt");
            System.out.println(Paths.get("text.txt").toAbsolutePath());
            try (BufferedReader br = Files.newBufferedReader(textFile)) {
                int c;
                while ((c = br.read()) != -1) {
                    text += (char) c;
                }
            }
            String[] lines = text.trim().split("\n");

            // starting update thread
            ScreenUpdate screenUpdate = new ScreenUpdate(screen, lines);
            Thread screenUpdateThread = new Thread(screenUpdate);
            screenUpdateThread.start();
            // draw lines
            drawLines(offset, lines);

//            screen.setCursorPosition(new TerminalPosition(column, 0));
            screen.refresh();

            long timeStart = System.currentTimeMillis();

            while (true) {
                KeyStroke keyStroke = screen.readInput();
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    screenUpdate.terminate();
                    break;
                }
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    offset++;
                    drawLines(offset, lines);
                }
                if (column == columnStart && keyStroke.getKeyType() == KeyType.Backspace && offset > 0) {
                    offset--;
                }

                if (keyStroke.getKeyType() == KeyType.Character) {
                    Character character = keyStroke.getCharacter();
                    TextCharacter textCharacter;
                    if (character == lines[offset].charAt(input.size())) {
                        textCharacter = new TextCharacter(character, TextColor.ANSI.BLUE, TextColor.ANSI.GREEN);
                    } else {
                        textCharacter = new TextCharacter(lines[offset].charAt(input.size()), TextColor.ANSI.BLUE, TextColor.ANSI.RED);
                    }

                    input.push(textCharacter);
                    screen.setCharacter(column++, offset, textCharacter);
                }


                screen.refresh();
            }


//            while (input.size() < text.length()) {
//                KeyStroke keyStroke = screen.readInput();
//
//                if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
//                    break;
//                }
//
//                if (keyStroke.getKeyType() == KeyType.Enter) {
////                    screen.scrollLines(1);
//                    System.out.println("in");
//                    // TODO redraw(lineStart, line End)
//                }
//                if (keyStroke.getKeyType() == KeyType.Character) {
//                    TextCharacter ch;
//                    if (text.charAt(input.size()) == keyStroke.getCharacter()) {
//                        ch = new TextCharacter(keyStroke.getCharacter(), TextColor.ANSI.GREEN, TextColor.ANSI.DEFAULT);
//                    } else {
//                        ch = new TextCharacter(keyStroke.getCharacter(), TextColor.ANSI.RED, TextColor.ANSI.DEFAULT);
//                    }
//
//                    input.push(ch);
//
////                            input += ch;
//                    screen.setCursorPosition(new TerminalPosition(9 + input.size() + 1, 5));
//                    screen.setCharacter(9 + input.size(), 5, input.peek());
//                    screen.refresh();
//
////                            TextCharacter cur = input.peek();
////                            cur.withBackgroundColor(cur.getForegroundColor()).withForegroundColor(TextColor.ANSI.DEFAULT);
////                            cur.withCharacter(text.charAt(input.size()));
//                    TextCharacter cur2 = new TextCharacter(text.charAt(input.size() - 1), TextColor.ANSI.BLACK, input.peek().getForegroundColor());
////                            cur2.withBackgroundColor(input.peek().getForegroundColor());
//                    screen.setCharacter(9 + input.size(), 5, cur2);
//                }
//                if (keyStroke.getKeyType() == KeyType.Backspace && !input.isEmpty()) {
//                    TextCharacter blank = new TextCharacter(' ', null, TextColor.ANSI.DEFAULT);
//                    screen.setCharacter(9 + input.size(), 6, blank);
//                    screen.setCursorPosition(new TerminalPosition(9 + input.size(), 5));
//
//                    TextCharacter cur = new TextCharacter(text.charAt(input.size() - 1));
//                    screen.setCharacter(9 + input.size(), 5, cur);
//                    input.pop();
//                }
//
//                screen.refresh();
//            }
            long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
            statistics(elapsedTimeMillis);
            screen.refresh();

            Thread.sleep(1000);


//            input = new Stack<>();
            screenUpdate.terminate();
            window.setVisible(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countCharacters(String string) {
        return string == null ? 0 : string.length();
    }

    public static int countWords(String string) {
        return string.trim().split("\\s+").length;
    }


    public static void drawLines(int offset, String[] lines) {
        for (int i = offset; i < lines.length; i++) {
            screen.newTextGraphics().putString(10, i - offset, lines[i].trim());
        }
    }

    public void statistics(long elapsedTimeMillis) {
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
        screen.newTextGraphics().putString(10, 11, "Chars/minute: " + (countCharacters(text) / myTimeMinutes));
        screen.newTextGraphics().putString(10, 12, "Words/minute: " + (countWords(
                text) / myTimeMinutes));
    }
}
