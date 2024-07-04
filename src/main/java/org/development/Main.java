package org.development;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = defaultTerminalFactory.createTerminal();
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
    }
}