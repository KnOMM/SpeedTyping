package org.development;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
//        System.out.println("Hello world!");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = defaultTerminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.setCursorPosition(0, 0);
        terminal.setCursorVisible(false);
//        TextGraphics textGraphics = terminal.newTextGraphics();
//        textGraphics.setForegroundColor(TextColor.ANSI.RED);
//        textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
//
//        textGraphics.putString(2, 1, "Lanterna Tutorial 2 - Press ESC to exit", SGR.BOLD);
//        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
//        textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
//        textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
//        textGraphics.putString(5 + "Terminal Size: ".length(), 3, terminal.getTerminalSize().toString());
//
//        terminal.flush();
//        Thread.sleep(2000);
//
//
//        terminal.addResizeListener(new TerminalResizeListener() {
//            @Override
//            public void onResized(Terminal terminal, TerminalSize newSize) {
//                // Be careful here though, this is likely running on a separate thread. Lanterna is threadsafe in
//                // a best-effort way so while it shouldn't blow up if you call terminal methods on multiple threads,
//                // it might have unexpected behavior if you don't do any external synchronization
//                textGraphics.drawLine(5, 3, newSize.getColumns() - 10, 3, ' ');
//                textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
//                textGraphics.putString(5 + "Terminal Size: ".length(), 3, newSize.toString());
//                try {
//                    terminal.flush();
//                } catch (IOException e) {
//                    // Not much we can do here
//                    throw new RuntimeException(e);
//                }
//            }
//        });

        Thread.sleep(2000);


//        textGraphics.putString(5, 4, "Last Keystroke: ", SGR.BOLD);
//        textGraphics.putString(5 + "Last Keystroke: ".length(), 4, "<Pending>");
        terminal.flush();

        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('\n');
        terminal.flush();


        TerminalPosition startPosition = terminal.getCursorPosition();
        terminal.setCursorPosition(startPosition.withRelativeColumn(3).withRelativeRow(2));
        terminal.flush();
        Thread.sleep(2000);

        terminal.setBackgroundColor(TextColor.ANSI.BLUE);
        terminal.setForegroundColor(TextColor.ANSI.YELLOW);

        terminal.putCharacter('Y');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('w');
        terminal.putCharacter(' ');
        terminal.putCharacter('o');
        terminal.putCharacter('n');
        terminal.putCharacter(' ');
        terminal.putCharacter('b');
        terminal.putCharacter('l');
        terminal.putCharacter('u');
        terminal.putCharacter('e');
        terminal.flush();
        Thread.sleep(2000);


        terminal.setCursorPosition(startPosition.withRelativeColumn(3).withRelativeRow(3));
        terminal.flush();
        Thread.sleep(2000);
        terminal.enableSGR(SGR.BOLD);
        terminal.putCharacter('Y');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('w');
        terminal.putCharacter(' ');
        terminal.putCharacter('o');
        terminal.putCharacter('n');
        terminal.putCharacter(' ');
        terminal.putCharacter('b');
        terminal.putCharacter('l');
        terminal.putCharacter('u');
        terminal.putCharacter('e');
        terminal.flush();
        Thread.sleep(2000);

        terminal.resetColorAndSGR();
        terminal.setCursorPosition(terminal.getCursorPosition().withColumn(0).withRelativeRow(1));
        terminal.putCharacter('D');
        terminal.putCharacter('o');
        terminal.putCharacter('n');
        terminal.putCharacter('e');
        terminal.putCharacter('\n');
        terminal.flush();

        Thread.sleep(2000);

        terminal.bell();
        terminal.flush();
        Thread.sleep(200);

//        KeyStroke keyStroke = terminal.pollInput();
//
//        while (keyStroke.getKeyType() != KeyType.Escape) {
//            textGraphics.drawLine(5, 10, terminal.getTerminalSize().getColumns() - 1, 10, ' ');
//            textGraphics.putString(5, 10, "Last Keystroke: ", SGR.BOLD);
//            textGraphics.putString(5 + "Last Keystroke: ".length(), 10, keyStroke.toString());
//            terminal.flush();
////            Thread.sleep(1000);
//            keyStroke = terminal.readInput();
//        }

//        terminal.exitPrivateMode();

        defaultTerminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            terminal = defaultTerminalFactory.createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);

            Random random = new Random();
            TerminalSize terminalSize = screen.getTerminalSize();
            for (int column = 0; column < terminalSize.getColumns(); column++) {
                for (int row = 0; row < terminalSize.getRows(); row++) {
                    screen.setCharacter(column, row, new TextCharacter(
                            ' ',
                            TextColor.ANSI.DEFAULT,
                            // This will pick a random background color
                            TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]));
                }
            }
            screen.refresh();

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 2000) {
//                System.out.println("inside");
                // The call to pollInput() is not blocking, unlike readInput()
                if (screen.pollInput() != null) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                    System.out.println("error");
                    break;
                }
            }

//            screen.clear();

            while (true) {
                KeyStroke keyStroke = screen.pollInput();
                if (keyStroke != null && (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                    break;
                }
                TerminalSize newSize = screen.doResizeIfNecessary();
                if (newSize != null) {
                    terminalSize = newSize;
//                    System.out.println("while resize");
                }

                // Increase this to increase speed
                final int charactersToModifyPerLoop = 1;
                for (int i = 0; i < charactersToModifyPerLoop; i++) {
                    TerminalPosition cellToModify = new TerminalPosition(
                            random.nextInt(terminalSize.getColumns()),
                            random.nextInt(terminalSize.getRows()));
                    TextColor.ANSI color = TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)];
                    TextCharacter characterInBackBuffer = screen.getBackCharacter(cellToModify);
                    characterInBackBuffer = characterInBackBuffer.withBackgroundColor(color);
                    characterInBackBuffer = characterInBackBuffer.withCharacter(' ');   // Because of the label box further down, if it shrinks
                    screen.setCharacter(cellToModify, characterInBackBuffer);
                }

                String sizeLabel = "Terminal Size: " + terminalSize;
                TerminalPosition labelBoxTopLeft = new TerminalPosition(1, 1);
                TerminalSize labelBoxSize = new TerminalSize(sizeLabel.length() + 2, 3);
                TerminalPosition labelBoxTopRightCorner = labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 1);
                TextGraphics textGraphics = screen.newTextGraphics();
                //This isn't really needed as we are overwriting everything below anyway, but just for demonstrative purpose
                textGraphics.fillRectangle(labelBoxTopLeft, labelBoxSize, ' ');
                textGraphics.drawLine(
                        labelBoxTopLeft.withRelativeColumn(1),
                        labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 2),
                        Symbols.DOUBLE_LINE_HORIZONTAL);
                textGraphics.drawLine(
                        labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(1),
                        labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(labelBoxSize.getColumns() - 2),
                        Symbols.DOUBLE_LINE_HORIZONTAL);

                textGraphics.setCharacter(labelBoxTopLeft, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
                textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
                textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
                textGraphics.setCharacter(labelBoxTopRightCorner, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
                textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
                textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);

                textGraphics.putString(labelBoxTopLeft.withRelative(1, 1), sizeLabel);

                screen.refresh();
                Thread.yield();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}