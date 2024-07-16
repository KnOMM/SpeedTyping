package org.development.tests;


import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;


import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Calculator {
    public static void main(String[] args) throws IOException {
        // Setup terminal and screen layers

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

//        SeparateTextGUIThread

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        final Label lblOutput = new Label("");

        panel.addComponent(new Label("Num 1"));
        final TextBox txtNum1 = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Num 2"));
        final TextBox txtNum2 = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Operation"));
        final ComboBox<String> operations = new ComboBox<String>();
        operations.addItem("Add");
        operations.addItem("Subtract");
        panel.addComponent(operations);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        new Button("Calculate!", new Runnable() {
            @Override
            public void run() {
                int num1 = Integer.parseInt(txtNum1.getText());
                int num2 = Integer.parseInt(txtNum2.getText());
                if (operations.getSelectedIndex() == 0) {
                    lblOutput.setText(Integer.toString(num1 + num2));
                } else if (operations.getSelectedIndex() == 1) {
                    lblOutput.setText(Integer.toString(num1 - num2));
                }
            }
        }).addTo(panel);
//
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(lblOutput);
//
//         Create window to hold the panel
        BasicWindow window = new BasicWindow();
//        CustomWindow window = new CustomWindow()
        window.setComponent(panel);


        CustomBackground background = new CustomBackground();
        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager(), null, background);
        TextGUIThread guiThread = gui.getGUIThread();
        try {
            ((AsynchronousTextGUIThread) guiThread).start();

            // Start the background update thread
            long timeLong = System.currentTimeMillis();

//            window.setHints(new ArrayList<>((Collection) Window.Hint.NO_POST_RENDERING));
            gui.addWindow(window);
            new Thread(() -> {
                while (true) {
                    guiThread.invokeLater(() -> {
//                        try {
//                            gui.updateScreen();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }

//                        System.out.println(gui.isPendingUpdate());
                        window.invalidate(); // Force the window to redraw itself
//                        try {
//                            guiThread.processEventsAndUpdate(); // Process any pending events and update the GUI
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
                    });
                    try {
                        Thread.yield();
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } finally {
        }
    }


    static class CustomBackground extends GUIBackdrop {


        @Override
        protected ComponentRenderer<EmptySpace> createDefaultRenderer() {


            boolean redrawRequired = true;


            return new ComponentRenderer<EmptySpace>() {
                //                componentRenderer = this;


                @Override
                public TerminalSize getPreferredSize(EmptySpace component) {
                    return TerminalSize.ONE;
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, EmptySpace component) {
                    TerminalSize terminalSize = graphics.getTextGUI().getScreen().getTerminalSize();
                    Random random = new Random();

                    for (int c = 0; c < terminalSize.getColumns(); c++) {
                        for (int r = 0; r < terminalSize.getRows(); r++) {
                            TextCharacter character = new TextCharacter(' ', TextColor.ANSI.DEFAULT,
                                    TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]);
                            graphics.setCharacter(c, r, character);
                        }
                    }
                }
            };
        }
    }
}