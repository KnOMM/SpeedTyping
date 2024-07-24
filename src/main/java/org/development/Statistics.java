package org.development;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Statistics implements Runnable {
    private Connection connection;
    private Window window;
    private Screen screen;
    private List<String> chars;
    private List<String> words;

    Statistics(Window window, Screen screen, Connection connection) {
        this.connection = connection;
        this.window = window;
        this.screen = screen;

        chars = new ArrayList<>();
        words = new ArrayList<>();

    }

    void getStatistics() {
        String selectChars = """
                SELECT AVG(ST.CHAR_MINUTE), S.DATE
                FROM TESTSESSION S
                INNER JOIN TESTUSERS U on S.LOGIN_ID = U.ID
                INNER JOIN TESTSTATISTICS ST on S.STATISTIC_ID = ST.ID
                WHERE U.LOGIN = ?
                GROUP BY S.DATE;
                        """;

        String selectWords = """
                SELECT SUM(ST.TOTAL_WORDS), S.DATE
                FROM TESTSESSION S
                         INNER JOIN TESTUSERS U on S.LOGIN_ID = U.ID
                         INNER JOIN TESTSTATISTICS ST on S.STATISTIC_ID = ST.ID
                WHERE U.LOGIN = ?
                GROUP BY S.DATE
                                """;
        try {
            PreparedStatement charsQuery = connection.prepareStatement(selectChars);
            charsQuery.setString(1, LogIn.getUsername());

            ResultSet rs = charsQuery.executeQuery();
            while (rs.next()) {
                chars.add(rs.getDate(2) + " \t " + rs.getInt(1));
            }

            PreparedStatement wordsQuery = connection.prepareStatement(selectWords);
            wordsQuery.setString(1, LogIn.getUsername());
            rs = wordsQuery.executeQuery();
            while (rs.next()) {
                words.add(rs.getDate(2) + " \t " + rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println(e.getSQLState());
            System.exit(-1);
        }

    }

    private void download() {
        String select = """
                SELECT
                       AVG(ST.CHAR_MINUTE)                           as "chars/min",
                       AVG(ST.CHAR_MINUTE) - AVG(ST.INCORRECT_CHARS) as "correct",
                       SUM(ST.TOTAL_WORDS)                           as "total words",
                       S.DATE
                FROM TESTSESSION S
                         INNER JOIN TESTUSERS U on S.LOGIN_ID = U.ID
                         INNER JOIN TESTSTATISTICS ST on S.STATISTIC_ID = ST.ID
                WHERE U.LOGIN = ?
                GROUP BY S.DATE;
                                """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setString(1, LogIn.getUsername());
            ResultSet rs = preparedStatement.executeQuery();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("statistics.csv"))) {

                bw.write("chars/minute,correct,total words,date\n");
                while (rs.next()) {
                    bw.write(rs.getInt(1) + "," + rs.getInt(2) + "," + rs.getInt(3) + "," + rs.getString(4) + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ;
        } catch (SQLException e) {
            System.out.println(e.getSQLState());
            System.exit(-1);
        }

    }

    @Override
    public void run() {
        if (LogIn.getUsername() == null) {
            MessageDialog
                    .showMessageDialog(window.getTextGUI(), "Info", "You must log in to see statistics");
        } else {
            // headers of the table
            chars.add("DATE \t chars/minute");
            words.add("DATE \t words total");
            getStatistics();

            window.setVisible(false);
            // window alignment
            Window statsWindow = new BasicWindow();
            statsWindow
                    .setHints(List.of(Window.Hint.CENTERED));

            Panel mainPanel = new Panel();
            mainPanel
                    .setLayoutManager(new GridLayout(2)
                            .setVerticalSpacing(1)
                            .setHorizontalSpacing(3))
                    .setSize(new TerminalSize(50, 15));

            createLabel("Username:")
                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END,
                            GridLayout.Alignment.CENTER))
                    .addTo(mainPanel);
            createLabel(LogIn.getUsername().toUpperCase())
                    .addStyle(SGR.BOLD)
                    .addTo(mainPanel);

            // Create a ListBox with data
            ActionListBox listBoxChars = new ActionListBox();
            for (int i = 0; i < chars.size(); i++) {
                listBoxChars.addItem(chars.get(i), () -> {
                });
            }
            ActionListBox listBoxWords = new ActionListBox();
            for (int i = 0; i < words.size(); i++) {
                listBoxWords.addItem(words.get(i), () -> {
                });
            }

            // Set preferred size for the ListBox
            listBoxChars.setPreferredSize(new TerminalSize(25, 15)); // width, height
            listBoxWords.setPreferredSize(new TerminalSize(25, 15)); // width, height


            // Collecting components
            mainPanel.addComponent(listBoxChars);
            mainPanel.addComponent(listBoxWords);
            new Button("Exit", statsWindow::close)
                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.END))
                    .addTo(mainPanel);

            // TODO
            new Button("Download", () -> {


                download();
                MessageDialog.showMessageDialog(statsWindow.getTextGUI(), "Info", "CSV file downloaded successfully.\nGo to the root of the app.");
            })
                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.END))
                    .addTo(mainPanel);

            statsWindow.setComponent(mainPanel);
            MultiWindowTextGUI statsGUI = new MultiWindowTextGUI(screen);
            statsGUI.addWindowAndWait(statsWindow);
            chars = new ArrayList<>();
            words = new ArrayList<>();

            window.setVisible(true);
        }
    }

    // utility method to create labels
    private Label createLabel(String text) {
        return new Label(text)
                .setLayoutData(GridLayout.createLayoutData(
                        GridLayout.Alignment.BEGINNING,
                        GridLayout.Alignment.CENTER
                ));
    }
}
