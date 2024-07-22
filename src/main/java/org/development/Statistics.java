package org.development;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;

import java.sql.Connection;
import java.util.List;

public class Statistics implements Runnable {
    Connection connection;
    Window window;
    Screen screen;

    Statistics(Window window, Screen screen, Connection connection) {
        this.connection = connection;
        this.window = window;
        this.screen = screen;
    }

    @Override
    public void run() {
        if (LogIn.getUsername() == null) {
            MessageDialog.showMessageDialog(window.getTextGUI(), "Info", "You must log in to see statistics");
        } else {
            window.setVisible(false);
            Window statsWindow = new BasicWindow();
            statsWindow.setHints(List.of(Window.Hint.CENTERED));
            MultiWindowTextGUI statsGUI = new MultiWindowTextGUI(screen);
            Panel contentPanel = new Panel(new GridLayout(2));

            statsWindow.setComponent(contentPanel);
            statsGUI.addWindowAndWait(statsWindow);
        }
    }
}
