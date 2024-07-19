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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SpeedType implements Runnable {
    private Window window;
    private static Screen screen;
    private String text;
    private static int columnStart;
    private int column;
    private static int row = 5;
    public static int offset;
    static List<Stack<TextCharacter>> input;
    private int length;

    // color codes for characters
    TextColor right = TextColor.ANSI.GREEN;
    TextColor wrong = TextColor.ANSI.RED;
    TextColor textBGDefault = TextColor.ANSI.DEFAULT;
    TextColor textFGTyped = TextColor.ANSI.BLUE;
    TextColor textFGDefault = TextColor.ANSI.WHITE;

    SpeedType(Window window, Screen screen, String text) {
        this.window = window;
        SpeedType.screen = screen;
        this.text = text;
        column = 10;
        columnStart = 10;
        offset = 0;
        input = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            window.setVisible(false);
            screen.clear();

            input.add(new Stack<>());

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
            List<String> linesList = Arrays.asList(lines);
            Collections.shuffle(linesList);
            lines = linesList.stream().map(String::trim).toArray(String[]::new);

            // starting update thread
            ScreenUpdate screenUpdate = new ScreenUpdate(screen, lines);
            Thread screenUpdateThread = new Thread(screenUpdate);
            screenUpdateThread.start();
            // draw lines
//            drawLines(offset, lines);

//            screen.setCursorPosition(new TerminalPosition(column, 0));
            screen.refresh();
            System.out.println(Arrays.toString(lines));

            long timeStart = System.currentTimeMillis();


            // current stack
            Stack<TextCharacter> stackLine = input.get(offset);
            while (true) {
                KeyStroke keyStroke = screen.readInput();
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    screenUpdate.terminate();
                    break;
                }

                if (keyStroke.getKeyType() == KeyType.Character && stackLine.size() < lines[offset].length()) {

                    Character character = lines[offset].charAt(stackLine.size());
                    TextCharacter textCharacter = new TextCharacter(character);
                    textCharacter = textCharacter.withForegroundColor(textFGTyped);
                    if (keyStroke.getCharacter() == character) {
                        textCharacter = textCharacter.withBackgroundColor(right);
                    } else {
                        textCharacter = textCharacter.withBackgroundColor(wrong);
                    }
                    stackLine.push(textCharacter);
                } else if (keyStroke.getKeyType() == KeyType.Backspace && !stackLine.isEmpty()) {
                    TextCharacter poppedChar = stackLine.pop();
                    poppedChar = poppedChar.withBackgroundColor(textBGDefault).withForegroundColor(textFGDefault);
                    screen.setCharacter(columnStart + stackLine.size(), row, poppedChar);
                } else if (keyStroke.getKeyType() == KeyType.Enter && stackLine.size() == lines[offset].length()) {
                    stackLine = new Stack<>();
                    input.add(stackLine);
                    offset++;
                    screen.clear();
                    drawDrawn();
                    drawLines(offset, lines);
                } else if (keyStroke.getKeyType() == KeyType.Backspace && stackLine.isEmpty() && !input.isEmpty() && offset > 0) {
                    input.remove(input.size() - 1);
                    screen.clear();
                    drawDrawn();
                    drawLines(--offset, lines);
                    stackLine = input.get(offset);
                }


                if (!stackLine.isEmpty()) {
                    screen.setCharacter(columnStart + stackLine.size() - 1, row, stackLine.peek());
                }
                screen.setCursorPosition(new TerminalPosition(columnStart + stackLine.size(), row));
                screen.refresh();
            }

            // show statistics
            long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
            statistics(elapsedTimeMillis);
            screen.refresh();

            Thread.sleep(1000);

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


    public static void drawDrawn() {
        if (input.isEmpty()) return;

        int start, end;
        int toDraw = input.size() - 1;

        if (toDraw > row) {
            start = toDraw - row;
            end = toDraw;
            for (int i = start; i < end; i++) {
                List<TextCharacter> list = new ArrayList<>(input.get(i));
                for (int j = 0; j < list.size(); j++) {
                    screen.setCharacter(columnStart + j, i - start, list.get(j));
                }
            }
        } else {
            start = 0;
            int dif = toDraw - start;
            for (int i = start; i < toDraw; i++) {
                List<TextCharacter> list = new ArrayList<>(input.get(i));
                for (int j = 0; j < list.size(); j++) {
                    screen.setCharacter(columnStart + j, i - start + row - dif
                            , list.get(j));
                }
            }
        }
    }

    public static void drawLines(int offset, String[] lines) {
        Stack<TextCharacter> originalStack = input.get(offset);
        List<TextCharacter> list = new ArrayList<>(originalStack);


        for (int i = offset; i < lines.length; i++) {
            screen.newTextGraphics().putString(columnStart, row + i - offset, lines[i].trim());
        }
        for (int i = 0; i < list.size(); i++) {
            screen.setCharacter(columnStart + i, row, list.get(i));
        }
    }

    public void statistics(long elapsedTimeMillis) {
        // Convert elapsed time to HH:mm:ss format   timeInstant.use
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis);
        screen.clear();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60;
        long milliseconds = elapsedTimeMillis % 1000;
        // Format elapsed time as a string screen.refresh();
        String elapsedTimeFormatted = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds);

        screen.newTextGraphics().putString(10, 10, elapsedTimeFormatted);

        double myTimeMinutes = elapsedTimeMillis / 1000.0 / 60;
        screen.newTextGraphics().putString(10, 11, "Chars/minute: " + (countCharacters(text) / myTimeMinutes));
        screen.newTextGraphics().putString(10, 12, "Words/minute: " + (countWords(
                text) / myTimeMinutes));
    }
}
