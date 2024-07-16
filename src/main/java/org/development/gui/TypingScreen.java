package org.development.gui;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class TypingScreen implements Runnable {
    private Screen screen;
    private StringBuilder string;

    public TypingScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void run() {
        try {
            screen.startScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                KeyStroke key = screen.readInput();
                if (key.getKeyType() == KeyType.Escape || key.getKeyType() == KeyType.EOF) {
                    screen.startScreen();
                }
                string.append(key.getCharacter());
                screen.newTextGraphics().putString(10, 10, string.toString());
                screen.refresh();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
