package org.development;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;

import java.sql.Connection;

public class LogOut implements Runnable{
    private final Window window;
    private final Screen screen;
    private final Connection connection;

    LogOut(Window window, Screen screen, Connection connection) {
        this.window = window;
        this.screen = screen;
        this.connection = connection;
    }
    @Override
    public void run() {
       if (LogIn.getUsername() == null) {
           MessageDialog.showMessageDialog(window.getTextGUI(), "Error", "You must login first!!!");
       }
    }
}
