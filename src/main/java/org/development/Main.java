package org.development;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import org.development.gui.MainDisplay;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        MainMenu mainMenu = new MainMenu();
//        Screen screen = mainMenu.getScreen();
//        screen.startScreen();

        MainDisplay mainDisplay = MainDisplay.getInstance();
        MultiWindowTextGUI gui = MainDisplay.getGui();


//        MainMenu main = new MainMenu();
//        BasicWindow window = new BasicWindow("Basic window");
//        window.setComponent(new Button("ddd"));
//        gui.addWindowAndWait(window);

        mainDisplay.drawMain();

    }
}
