package org.development;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = defaultTerminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.setCursorPosition(0, 0);
        terminal.setCursorVisible(false);
        final TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);

        textGraphics.putString(2, 1, "Lanterna Tutorial 2 - Press ESC to exit", SGR.BOLD);
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
        textGraphics.putString(5 + "Terminal Size: ".length(), 3, terminal.getTerminalSize().toString());

        terminal.flush();
        Thread.sleep(2000);


        terminal.addResizeListener(new TerminalResizeListener() {
            @Override
            public void onResized(Terminal terminal, TerminalSize newSize) {
                // Be careful here though, this is likely running on a separate thread. Lanterna is threadsafe in
                // a best-effort way so while it shouldn't blow up if you call terminal methods on multiple threads,
                // it might have unexpected behavior if you don't do any external synchronization
                textGraphics.drawLine(5, 3, newSize.getColumns() - 10, 3, ' ');
                textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
                textGraphics.putString(5 + "Terminal Size: ".length(), 3, newSize.toString());
                try {
                    terminal.flush();
                } catch (IOException e) {
                    // Not much we can do here
                    throw new RuntimeException(e);
                }
            }
        });

        Thread.sleep(2000);


        textGraphics.putString(5, 4, "Last Keystroke: ", SGR.BOLD);
        textGraphics.putString(5 + "Last Keystroke: ".length(), 4, "<Pending>");
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

        KeyStroke keyStroke = terminal.pollInput();
        while (keyStroke.getKeyType() != KeyType.Escape) {
            textGraphics.drawLine(5, 10, terminal.getTerminalSize().getColumns() - 1, 10, ' ');
            textGraphics.putString(5, 10, "Last Keystroke: ", SGR.BOLD);
            textGraphics.putString(5 + "Last Keystroke: ".length(), 10, keyStroke.toString());
            terminal.flush();
//            Thread.sleep(1000);
            keyStroke = terminal.readInput();
        }

//        terminal.exitPrivateMode();
    }
}