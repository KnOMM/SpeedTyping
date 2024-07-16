package org.development.tests;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class WindowGUI {
    static Screen screen;
    static DefaultTerminalFactory terminalFactory;
    static WindowBasedTextGUI gui;

    public static void main(String[] args) {


        terminalFactory = new DefaultTerminalFactory();
        screen = null;
        Terminal terminal = null;
        try {
            terminal = terminalFactory.createTerminal();
            screen = new TerminalScreen(terminal);
            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new CustomBackground());
            screen.startScreen();

            BasicWindow root = new BasicWindow("Root window");
//            root.setDecoratedSize(screen.getTerminalSize());
            TerminalSize terminalSize = screen.getTerminalSize();
//            root.setPosition(new TerminalPosition(terminalSize.getColumns() / 2, terminalSize.getRows() / 2));
            BasicWindow secondary = new BasicWindow("Secondary");
//            secondary.setPosition(new TerminalPosition(terminalSize.getColumns() / 2, terminalSize.getRows() / 2));
//            gui.addWindow(root);
//            gui.addWindowAndWait(root);
            Collection<Window.Hint> hints = new ArrayList<>();
//            hints.add(Window.Hint.NO_DECORATIONS);
            hints.add(Window.Hint.NO_POST_RENDERING);
            hints.add(Window.Hint.CENTERED);
            hints.add(Window.Hint.FIT_TERMINAL_WINDOW);
//            hints.add(Window.Hint.FULL_SCREEN);
            hints.add(Window.Hint.EXPANDED);


            root.setHints(hints);


//            terminal.setBackgroundColor(TextColor.ANSI.RED_BRIGHT);
//            terminal.flush();
            Theme theme = gui.getTheme();
            // TODO Theme
            SimpleTheme newTheme = new SimpleTheme(TextColor.ANSI.CYAN, TextColor.ANSI.CYAN);
            newTheme = newTheme.makeTheme(true, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE, TextColor.ANSI.BLUE, TextColor.ANSI.YELLOW, TextColor.ANSI.RED, TextColor.ANSI.CYAN, TextColor.ANSI.MAGENTA);
            gui.setTheme(newTheme);
            ThemeStyle insensitive = theme.getDefaultDefinition().getInsensitive();
            ThemeStyle active = theme.getDefaultDefinition().getActive();
            ThemeStyle normal = theme.getDefaultDefinition().getNormal();
            ThemeStyle preLight = theme.getDefaultDefinition().getPreLight();
            ThemeStyle selected = theme.getDefaultDefinition().getSelected();

//            MyWindow myWindow = new MyWindow();

            System.out.println("insensitive b/f: " + insensitive.getBackground().toString() + "/" + insensitive.getForeground().toString() + "/" + insensitive.getSGRs() +
                    "\nactive: " + active.getBackground().toString() + "/" + active.getForeground() + "/" + active.getSGRs()
                    + "\nnormal: " + normal.getBackground().toString() + "/" + normal.getForeground() + "/" + normal.getSGRs()
                    + "\npreLight: " + preLight.getBackground().toString() + "/" + preLight.getForeground() + "/" + preLight.getSGRs()
                    + "\nselected: " + selected.getBackground().toString() + "/" + selected.getForeground() + "/" + selected.getSGRs());
//            gui.addWindow(secondary);

//            gui.addWindow(root);
            gui.addWindowAndWait(new MyButton(root));
//            gui.removeWindow(root);


//            root.waitUntilClosed();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

            if (screen != null) {
                try {
                    /*
                    The close() call here will restore the terminal by exiting from private mode which was done in
                    the call to startScreen(), and also restore things like echo mode and intr
                     */
                    Collection<Window> windows = gui.getWindows();
                    windows.forEach(Window::close);
                    screen.stopScreen();
                    screen.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static class MyWindow extends BasicWindow {
        public MyWindow() {
            super("My Window!");
            Panel horizontalPanel = new Panel();
            horizontalPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            Panel leftPanel = new Panel();
            Panel middlePanel = new Panel();
            Panel rightPanel = new Panel();
            Panel fourthPanel = new Panel();


            horizontalPanel.addComponent(leftPanel.withBorder(Borders.doubleLine("Double line")));
            horizontalPanel.addComponent(middlePanel.withBorder(Borders.singleLineBevel("Single line bevel")));
            horizontalPanel.addComponent(fourthPanel.withBorder(Borders.singleLine("Single line")));
//            horizontalPanel.addComponent(rightPanel.withBorder(Borders.doubleLineBevel("Double Line Bevel")));
            horizontalPanel.addComponent(rightPanel.withBorder(Borders.doubleLineBevel("Double Line Bevel")));

            Panel verticalPanel = new Panel();
            horizontalPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            horizontalPanel.addComponent(new Label("This is the first label"));
            horizontalPanel.addComponent(new Label("This is the second label, red").setForegroundColor(TextColor.ANSI.RED));
            horizontalPanel.addComponent(new Label("This is the last label\nSpanning\nMultiple\nRows"));
            setComponent(verticalPanel);

            horizontalPanel.addComponent((new Button("Close", () -> {
                Collection<Window> windows = gui.getWindows();
//                gui.

                windows.forEach(window -> System.out.println(window.getTitle()));
//                windows.forEach(Window::close);

                screen.clear();
                this.close();
//                try {
//                    screen.stopScreen();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            })));

            // This ultimately links in the panels as the window content
            setComponent(horizontalPanel);
        }
    }

    static class MyButton extends BasicWindow {
        MyButton(Window root) {
            super("My Button!");
            Panel panel = new Panel();
            panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            panel.addComponent(new Button("Exit", () -> {
//                Collection<Window> windows = gui.getWindows();
//                windows.forEach(Window::close);
                MyButton.this.close();
//                screen.clear();
                gui.addWindowAndWait(new MyWindow());
            }));
            panel.addComponent(new Button("Pass", () -> {
//                this.close();
                try {
                    Screen screenRun = terminalFactory.createScreen();
                    screenRun.startScreen();

                    boolean state = true;

                    while (state) {
                        KeyStroke keyStroke = screenRun.readInput();
                        TerminalSize terminalSize = screenRun.getTerminalSize();
                        TerminalSize newSize = screenRun.doResizeIfNecessary();

                        if (keyStroke.getKeyType() == KeyType.EOF || keyStroke.getKeyType() == KeyType.Escape) {
                            state = false;
                        }

                        if (newSize != null) {
                            terminalSize = newSize;
//                            System.out.println("in");
//                            screen.refresh();
                        }

                        TerminalPosition terminalPosition = new TerminalPosition(terminalSize.getColumns() / 3, terminalSize.getRows() / 3);
                        TextCharacter textCharacter;
                        if (keyStroke.getKeyType() == KeyType.Character) {
                            textCharacter = new TextCharacter(keyStroke.getCharacter());
//                            textCharacter = TextCharacter.fromCharacter(keyStroke.getCharacter())[0];
                            screenRun.setCharacter(terminalPosition.getColumn(), terminalPosition.getRow(), textCharacter);
                        } else {
                            TextGraphics textGraphics = screenRun.newTextGraphics();
                            textGraphics.putString(terminalPosition, keyStroke.toString());
//                            screen.setCharacter(keyStroke.toString().toCharArray());
                        }
                        TextGraphics pos = screenRun.newTextGraphics();
                        screenRun.refresh();
                        screenRun.clear();

                    }
                    this.close();
                    screenRun.stopScreen();
                    screen.clear();
                    Collection<Window> windows = gui.getWindows();

                    for (Window window : windows) {
                        System.out.println(window.getTitle());
                    }

//                    gui.addWindow(root);
                    gui.addWindowAndWait(new MyButton(root));
//                    gui.removeWindow(root);
//                    screen.stopScreen();

//                    gui.addWindow(root);
//                    gui.addWindowAndWait(new MyButton(gui, root));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));

            setComponent(panel);

        }
    }

    static class CustomBackground extends GUIBackdrop {


        @Override
        protected ComponentRenderer<EmptySpace> createDefaultRenderer() {

            return new ComponentRenderer<EmptySpace>() {

                @Override
                public TerminalSize getPreferredSize(EmptySpace component) {
                    return TerminalSize.ONE;
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, EmptySpace component) {
                    ThemeDefinition themeDefinition = component.getTheme().getDefinition(GUIBackdrop.class);
//                    graphics.fill(themeDefinition.getCharacter("BACKGROUND", ' '));

                    TerminalSize terminalSize = graphics.getSize();
                    Random random = new Random();

                    for (int c = 0; c < terminalSize.getColumns(); c++) {
                        for (int r = 0; r < terminalSize.getRows(); r++) {
                            graphics.setCharacter(c,r, new TextCharacter(' ', TextColor.ANSI.DEFAULT,
                                    TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]));
                        }
                    }
//                    graphics.applyThemeStyle(themeDefinition.getNormal());
                }
            };

        }
    }
}
