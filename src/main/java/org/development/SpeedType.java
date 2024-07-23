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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SpeedType implements Runnable {
    private final Window window;
    private static Screen screen;
    private final Connection connection;
    private String text;
    private static int columnStart;
    private static int row;
    public static int offset;
    static List<Stack<TextCharacter>> input;

    // color codes for characters
    private static final TextColor right = TextColor.ANSI.GREEN;
    private static final TextColor wrong = TextColor.ANSI.RED;
    private static final TextColor textBGDefault = TextColor.ANSI.DEFAULT;
    private static final TextColor textFGTyped = TextColor.ANSI.BLUE;
    private static final TextColor textFGDefault = TextColor.ANSI.WHITE;

    // statistic results
    private static int charsTotal;
    private static int wordsTotal;
    private static int incorrectTotal;

    SpeedType(Window window, Screen screen, Connection connection) {
        this.window = window;
        this.connection = connection;
        SpeedType.screen = screen;

    }

    @Override
    public void run() {
        columnStart = 10;
        row = 5;
        offset = 0;
        input = new ArrayList<>();
        try {
            window.setVisible(false);
            screen.clear();

            input.add(new Stack<>());

            text = "";
            String resourcePath = "/text.txt";

            // Read the file from the classpath
            try (InputStream inputStream = SpeedType.class.getResourceAsStream(resourcePath);
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

                int c;
                while ((c = br.read()) != -1) {
                    text += ((char) c);
                }
//                System.out.println(text.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] lines = text.trim().split("\n");
            List<String> linesList = Arrays.asList(lines);
            Collections.shuffle(linesList);
            lines = linesList.stream().map(String::trim).toArray(String[]::new);

            // starting update thread
            ScreenUpdate screenUpdate = new ScreenUpdate(screen, lines);
            Thread screenUpdateThread = new Thread(screenUpdate);
            screenUpdateThread.start();

            screen.setCursorPosition(new TerminalPosition(columnStart, row));
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
                    drawTyped();
                    drawLines(offset, lines);
                } else if (keyStroke.getKeyType() == KeyType.Backspace && stackLine.isEmpty() && !input.isEmpty() && offset > 0) {
                    input.remove(input.size() - 1);
                    screen.clear();
                    drawTyped();
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

            // nulify counters
            input = new ArrayList<>();
            incorrectTotal = 0;
            wordsTotal = 0;
            charsTotal = 0;
            screen.refresh();

            Thread.sleep(3000);

            screenUpdate.terminate();
            window.setVisible(true);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void countStatistics() {
        int words = 0;
        int chars = 0;
        int incorrect = 0;
        for (Stack<TextCharacter> textCharacters : input) {
            List<TextCharacter> lineList = new ArrayList<>(textCharacters);
            String line = "";
            for (int j = 0; j < lineList.size(); j++) {
                chars++;
                if (lineList.get(j).getBackgroundColor() == wrong) {
                    incorrect++;
                }
                line += lineList.get(j).getCharacter();
            }
            // split if blank, ",", ".", "<", or "("
            words += line.split("\\s|,|\\.|<|\\(").length;
        }
        charsTotal = chars;
        wordsTotal = words;
        incorrectTotal = incorrect;
    }


    public static void drawTyped() {
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

        countStatistics();
        int chars = charsTotal;
        int words = wordsTotal;
        double wrong = incorrectTotal * 1.0 / charsTotal;
        System.out.println(chars);
        System.out.println(words);
        System.out.println(1 - wrong);
        screen.newTextGraphics().putString(10, 11, "Chars/minute: " + (chars / myTimeMinutes));
        screen.newTextGraphics().putString(10, 12, "Words/minute: " + (words / myTimeMinutes));
        screen.newTextGraphics().putString(10, 13, "Accuracy: " + String.format("%.2f", (1 - wrong) * 100) + "%");

        int id = getLoginId(LogIn.getUsername());

        if (id != 0) {
            UUID uuid = insertStatistics(elapsedTimeMillis);
            populateSession(id, uuid);
        }
    }

    private UUID insertStatistics(long time) {
        UUID uuid = UUID.randomUUID();
        String insert = "INSERT INTO TestStatistics VALUES (?, ?, ?, ?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, charsTotal);
            preparedStatement.setInt(3, wordsTotal);
            preparedStatement.setInt(4, incorrectTotal);
            preparedStatement.setLong(5, time);
            preparedStatement.setInt(6, (int) (charsTotal / (time / 1000.0 / 60)));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getSQLState());
            System.exit(-1);
        }

        return uuid;
    }

    private void populateSession(int id, UUID uuid) {
        String insert = "INSERT INTO TestSession (login_id, statistic_id) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getLoginId(String username) {
        String select = "SELECT id FROM TestUsers WHERE login = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("id");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
