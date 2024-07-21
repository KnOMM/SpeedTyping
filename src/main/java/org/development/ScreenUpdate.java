package org.development;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class ScreenUpdate implements Runnable {
    private Screen screen;
    private String[] lines;
    private boolean isOn;

    ScreenUpdate(Screen screen, String[] lines) {
        this.screen = screen;
        this.lines = lines;
        isOn = true;
    }

//    ScreenUpdate

    @Override
    public void run() {
        try {

            screen.clear();
            SpeedType.drawTyped();
            SpeedType.drawLines(SpeedType.offset, lines);
            screen.refresh();

            while (isOn) {

                TerminalSize terminalSize = screen.doResizeIfNecessary();
                if (terminalSize != null) {
                    screen.clear();
                    SpeedType.drawTyped();
                    SpeedType.drawLines(SpeedType.offset, lines);
                    screen.refresh();
                }

                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminate() {
        isOn = false;
    }
}
