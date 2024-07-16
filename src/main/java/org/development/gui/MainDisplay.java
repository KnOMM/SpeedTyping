package org.development.gui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class MainDisplay {
    private static MainDisplay INSTANCE;
    private static MultiWindowTextGUI gui;
    private Screen screen;
    private static final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
    StringBuilder string;

    private MainDisplay() {
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
        } catch (IOException e) {
            System.err.println("Error creating the screen object: " + e);
            System.exit(-1);
        }
        string = new StringBuilder();
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new Background());
    }

    public static MainDisplay getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainDisplay();
        }
        return INSTANCE;
    }

    public static MultiWindowTextGUI getGui() {
        return gui;
    }

    public Screen getScreen() {
        return screen;
    }

    public void drawMain() throws IOException {

        MenuWindow menuWindow = new MenuWindow("Menu");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
//       panel.addComponent(new Button("test", new TypingScreen(terminalFactory.createScreen())));
//       panel.addComponent(new CheckBox());
        panel.addComponent(new Button("Type Test", () -> {
            try {
                Screen typeScreen = terminalFactory.createScreen();
                typeScreen.startScreen();
                System.out.println(screen.newTextGraphics().getBackgroundColor());

//                screen.newTextGraphics().setBackgroundColor(TextColor.ANSI.YELLOW);

                while (true) {
                    try {
                        KeyStroke key = typeScreen.readInput();
                        if (key.getKeyType() == KeyType.Escape || key.getKeyType() == KeyType.EOF) {
//                            screen.startScreen();
                            break;
                        }
                        typeScreen.setCursorPosition(new TerminalPosition(10, 10));
                        if (key.getKeyType() == KeyType.Character) {
                            string.append(key.getCharacter());
                            typeScreen.newTextGraphics().putString(10, 10, string.toString());
//                            typeScreen.setCursorPosition(new TerminalPosition(10+string.length(), 10));
                            typeScreen.setCharacter(10 + string.length() - 1, 10,  new TextCharacter(key.getCharacter()));
                            typeScreen.setCursorPosition(new TerminalPosition(10+string.length(), 10));
                        }
                        if (key.getKeyType() == KeyType.Backspace) {
                            string.setLength(string.length() - 1);
                            typeScreen.newTextGraphics().putString(10,10, string + " ");
                            typeScreen.setCursorPosition(new TerminalPosition(10+string.length(), 10));
                        }
                        typeScreen.newTextGraphics().setBackgroundColor(TextColor.ANSI.YELLOW);
                        typeScreen.refresh();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
//                typeScreen.clear();
//                typeScreen.stopScreen();
//                typeScreen.close();
//                List<Window> windows = gui.getWindows().stream().toList();
//                windows.get(0).invalidate();

//                typeScreen.scrollLines();
//                typeScreen = null;
                string = new StringBuilder();
                gui.addWindowAndWait(menuWindow);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }));

        menuWindow.setComponent(panel);
        Theme theme = new SimpleTheme(TextColor.ANSI.YELLOW, TextColor.ANSI.RED, SGR.BORDERED, SGR.BOLD);

        menuWindow.setTheme(theme);
        gui.addWindowAndWait(menuWindow);

    }
}
