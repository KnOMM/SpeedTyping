package org.development.tests;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasePane;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Random;

public class ActionListBoxExample {
    static Screen screen = null;
    static Terminal terminal = null;

    public static void main(String[] args) {

        try {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        TerminalSize size = new TerminalSize(14, 10);
        ActionListBox actionListBox = new ActionListBox(size);

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        BasicWindow window = new BasicWindow();
        window.setComponent(actionListBox);
        actionListBox.addItem("change bg", new Runnable() {
            @Override
            public void run() {
                // Code to run when action activated
                try {
                    TerminalSize terminalSize = screen.getTerminalSize();
                    Random random = new Random();

                    for (int c = 0; c < terminalSize.getColumns(); c++) {
                        for (int r = 0; r < terminalSize.getRows(); r++) {
                            TextCharacter character = new TextCharacter(' ', TextColor.ANSI.DEFAULT,
                                    TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]);
                            screen.setCharacter(c, r, character);
                        }
                    }
                    screen.refresh();
                    Thread.sleep(200);
                    Thread.yield();
                    gui.addWindowAndWait(new BasicWindow("new"));
//                    gui.waitForWindowToClose();

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        gui.addWindowAndWait(window);




    }
}
