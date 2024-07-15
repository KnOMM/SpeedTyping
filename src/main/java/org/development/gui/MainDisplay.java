package org.development.gui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

public class MainDisplay {
    private static MainDisplay INSTANCE;
    private static MultiWindowTextGUI gui;
    private Screen screen;
    private static final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

    private MainDisplay () {
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
        } catch (IOException e) {
            System.err.println("Error creating the screen object: " + e);
            System.exit(-1);
        }
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

    public void drawMain() {

        MenuWindow menuWindow = new MenuWindow("Menu");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
       panel.addComponent(new Button("test"));
       panel.addComponent(new CheckBox());


        menuWindow.setComponent(panel);
        Theme theme = new SimpleTheme(TextColor.ANSI.YELLOW, TextColor.ANSI.RED, SGR.BORDERED, SGR.BOLD);

        menuWindow.setTheme(theme);
        gui.addWindowAndWait(menuWindow);

    }
}
