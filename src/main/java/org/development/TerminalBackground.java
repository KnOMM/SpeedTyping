package org.development;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Random;

public class TerminalBackground {

    public static void main(String[] args) {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = null;

        try {
            terminal = terminalFactory.createTerminal();
            terminal.enterPrivateMode();
            terminal.setCursorVisible(false);

            TerminalSize terminalSize = terminal.getTerminalSize();
            Random random = new Random();

//            terminal.putString("dkjfldsjf");


            for (int r = 0; r < terminalSize.getRows(); r++) {
                for (int c = 0; c < terminalSize.getColumns(); c++) {
                    terminal.setBackgroundColor(TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]);
                    terminal.putCharacter(' ');
                }
            }
            terminal.flush();
            Thread.sleep(2000);

//
            while (true) {
                terminalSize = terminal.getTerminalSize();
                int column = random.nextInt(terminalSize.getColumns());
                int row = random.nextInt(terminalSize.getRows());
                terminal.setCursorPosition(column, row);

                terminal.setBackgroundColor(TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]);
                terminal.putCharacter(' ');

//                terminal.setCursorPosition(0,0);
////                terminal.
//                terminal.setBackgroundColor(TextColor.ANSI.GREEN_BRIGHT);
//                terminal.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
//                terminal.putString("Terminal size: ");
//                terminal.enableSGR(SGR.BOLD);
//                terminal.putString(terminalSize.toString());
//                terminal.resetColorAndSGR();

                String sizeLabel = "Terminal Size: " + terminalSize;
                TerminalPosition labelBoxTopLeft = new TerminalPosition(0, 0);
                TerminalSize labelBoxSize = new TerminalSize(sizeLabel.length() + 2, 3);
                TerminalPosition labelBoxTopRightCorner = labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 1);
                TextGraphics textGraphics = terminal.newTextGraphics();
                //This isn't really needed as we are overwriting everything below anyway, but just for demonstrative purpose
//                textGraphics.fillRectangle(labelBoxTopLeft, labelBoxSize.withRelativeColumns(20), ' ');

                /*
                Draw horizontal lines, first upper then lower
                 */
                textGraphics.drawLine(
                        labelBoxTopLeft.withRelativeColumn(1),
                        labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 2),
                        Symbols.DOUBLE_LINE_HORIZONTAL);
                textGraphics.drawLine(
                        labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(1),
                        labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(labelBoxSize.getColumns() - 2),
                        Symbols.DOUBLE_LINE_HORIZONTAL);

                /*
                Manually do the edges and (since it's only one) the vertical lines, first on the left then on the right
                 */
                textGraphics.setCharacter(labelBoxTopLeft, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
                textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
                textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
                textGraphics.setCharacter(labelBoxTopRightCorner, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
                textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
                textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);

                /*
                Finally put the text inside the box
                 */
                textGraphics.putString(labelBoxTopLeft.withRelative(1, 1), sizeLabel);

                /*
                Ok, we are done and can display the change. Let's also be nice and allow the OS to schedule other
                threads so we don't clog up the core completely.
                 */

                terminal.flush();

//                terminal.newTextGraphics();
                Thread.yield();

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
