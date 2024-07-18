package org.development;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.development.gui.Background;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    static MultiWindowTextGUI gui;
    static boolean isOn = true;
    final static String TEXT = "some text to be typed using different characters1234314! {} [] []: '";

    public static void main(String[] args) throws IOException {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
//        gui = new MultiWindowTextGUI(screen);
        screen.startScreen();


        Window window = new BasicWindow("window");

        gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager(), null, new Background());
        TextGUIThread guiThread = gui.getGUIThread();
        try {
            ((AsynchronousTextGUIThread) guiThread).start();

            new Thread(() -> {
                while (isOn && ((AsynchronousTextGUIThread) guiThread).getState() != AsynchronousTextGUIThread.State.STOPPED) {
                    guiThread.invokeLater(window::invalidate);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }).start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Panel menu = new Panel(new LinearLayout(Direction.VERTICAL));
        menu.addComponent(new Label("test label"));
        menu.addComponent(new Label("test label2"));
        menu.addComponent(new Button("Log In", new LogIn(window, screen)));
        menu.addComponent(new Button("Register", new LogIn(window, screen)));
        menu.addComponent(new Button("type test", new SpeedType(window, screen, TEXT)));
        menu.addComponent(new Button("Exit", () -> {
            window.close();
            ((AsynchronousTextGUIThread) guiThread).stop();
            isOn = false;
        }));

        window.setComponent(menu);
        gui.addWindowAndWait(window);
    }
}
