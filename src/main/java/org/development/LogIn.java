package org.development;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;

public class LogIn implements Runnable {
    Window window;
    Screen screen;

    LogIn(Window window, Screen screen) {
        this.window = window;
        this.screen = screen;
    }


    @Override
    public void run() {
        // Create a secondary window
        window.setVisible(false);
        Window secondaryWindow = new BasicWindow();

        // Create a new GUI for the secondary window
        MultiWindowTextGUI secondaryGui = new MultiWindowTextGUI(screen);

        secondaryWindow.setComponent(new Button("Exit", secondaryWindow::close));
        secondaryGui.addWindowAndWait(secondaryWindow);
        window.setVisible(true);
    }
}
