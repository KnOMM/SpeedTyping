package org.development.examples;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class ScrollLinesExample {
    public static void main(String[] args) {
        try {
            // Create a terminal and screen
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            // Fill the screen with initial content
            for (int i = 0; i < 20; i++) {
                screen.setCursorPosition(new TerminalPosition(0, i));
                screen.newTextGraphics().putString(0, i, "Line " + (i + 1));
            }
            screen.refresh();

            // Define scrollable area
            int firstLine = 4;
            int lastLine = screen.getTerminalSize().getRows() - 4;

            // Wait for user input to scroll
            while (true) {
                System.out.println('i');
                KeyStroke keyStroke = screen.readInput();
                System.out.println("after");

//                if (keyStroke != null) {
                if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                    // Scroll down one line in the specified range
                    screen.scrollLines(firstLine, lastLine, 1);
                    TerminalPosition cursorPosition = screen.getCursorPosition();
                    cursorPosition = cursorPosition.withRelativeRow(1);
                    screen.setCursorPosition(cursorPosition);
                    screen.refresh();
                } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                    // Scroll up one line in the specified range
                    screen.scrollLines(firstLine, lastLine, -1);
                    TerminalPosition cursorPosition = screen.getCursorPosition().withRelativeRow(-1);
                    screen.setCursorPosition(cursorPosition);
                    screen.refresh();
                } else if (keyStroke.getKeyType() == KeyType.Escape) {
                    // Exit on ESC key press
                    break;
//                    }
                }
                System.out.println("in");
            }

            System.out.println("end");
            // Stop the screen and close the terminal
            screen.stopScreen();
            terminal.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

