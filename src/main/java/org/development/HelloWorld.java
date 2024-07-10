package org.development;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Arrays;

public class HelloWorld {
    public static void main(String[] args) throws IOException {

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

//        // Create panel to hold components
        Panel panel = new Panel();
//        panel.setLayoutManager(new GridLayout(2));
//
//        panel.addComponent(new Label("Forename"));
//        panel.addComponent(new TextBox());
//
//        panel.addComponent(new Label("Surname"));
//        panel.addComponent(new TextBox());
//
//        panel.addComponent(new EmptySpace(new TerminalSize(0,0))); // Empty space underneath labels
//        panel.addComponent(new Button("Submit"));
//
//        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.RED));

        panel.setLayoutManager(new BorderLayout());

        // Sets the textbox to be in the center of the screen
        TextBox textBox = new TextBox("top");
        textBox.setLayoutData(BorderLayout.Location.TOP);
        panel.addComponent(textBox);

        TextBox textBox2 = new TextBox("bottom");
        textBox2.setLayoutData(BorderLayout.Location.BOTTOM);
        panel.addComponent(textBox2);

        TextBox textBox3 = new TextBox("center", TextBox.Style.MULTI_LINE);
        textBox3.setLayoutData(BorderLayout.Location.CENTER);
        panel.addComponent(textBox3.withBorder(Borders.singleLineBevel()));
        TextBox textBox4 = new TextBox("left", TextBox.Style.MULTI_LINE);
        textBox4.setLayoutData(BorderLayout.Location.LEFT);
        panel.addComponent(textBox4.withBorder(Borders.doubleLine()));
        TextBox textBox5 = new TextBox("right", TextBox.Style.MULTI_LINE);
        Border border = textBox5.withBorder(Borders.singleLine());
//        textBox5.withBorder(Borders.doubleLine());
        textBox5.withBorder(border);
//        TextBox textBox1 = new TextBox();
        textBox5.setLayoutData(BorderLayout.Location.RIGHT);
        panel.addComponent(textBox5.withBorder(Borders.singleLine()));


//        panel.addComponent(	new TextBox().withBorder(Borders.singleLine("Heading")));
        //...

        window.setComponent(panel);
        // Tip: Sets the window to be full screen.
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));

        gui.addWindowAndWait(window);

    }
}