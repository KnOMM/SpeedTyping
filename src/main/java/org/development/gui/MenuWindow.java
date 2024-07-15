package org.development.gui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;

import java.util.ArrayList;
import java.util.Collection;

public class MenuWindow extends BasicWindow {

    public MenuWindow() {
        super();
        this.setFixedSize(new TerminalSize(20,5));
        initialize();
    }

    public MenuWindow(String title) {
        super(title);
        this.setFixedSize(new TerminalSize(20,5));
        initialize();
    }

    private void initialize() {
        Collection<Hint> hints = new ArrayList<>();
        hints.add(Hint.CENTERED);
        hints.add(Hint.FIXED_SIZE);
        hints.add(Hint.MODAL);
        hints.add(Hint.NO_POST_RENDERING);
        this.setHints(hints);
    }


}
