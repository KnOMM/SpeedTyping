package org.development.gui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GUIBackdrop;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

import java.util.Random;

public class Background extends GUIBackdrop {
    @Override
    protected ComponentRenderer<EmptySpace> createDefaultRenderer() {

        return new ComponentRenderer<>() {
            @Override
            public TerminalSize getPreferredSize(EmptySpace component) {
                return TerminalSize.ONE;
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, EmptySpace component) {
                TerminalSize terminalSize = graphics.getSize();
//                        .getTextGUI()
//                        .getScreen().getTerminalSize();

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
