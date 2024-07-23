package org.development;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

public class LogOut implements Runnable {
    private final Window window;

    LogOut(Window window) {
        this.window = window;
    }

    @Override
    public void run() {
        if (LogIn.getUsername() == null) {
            MessageDialog.showMessageDialog(window.getTextGUI(), "Error", "You must login first!!!");
        } else {
            LogIn.logout();
            MessageDialog.showMessageDialog(window.getTextGUI(), "Success", "Logged out successfully!");
        }
    }
}
