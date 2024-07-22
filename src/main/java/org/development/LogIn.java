package org.development;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;

import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

public class LogIn implements Runnable {
    private final Window window;
    private final Screen screen;
    private final Connection connection;
    private static String username;

    LogIn(Window window, Screen screen, Connection connection) {
        this.window = window;
        this.screen = screen;
        this.connection = connection;
    }


    @Override
    public void run() {
        // Create a secondary window
        if (username == null) {
            window.setVisible(false);
            Window secondaryWindow = new BasicWindow();
            secondaryWindow
                    .setHints(List.of(Window.Hint.CENTERED));

            // Create a new GUI for the secondary window
            MultiWindowTextGUI secondaryGui = new MultiWindowTextGUI(screen);


            Panel contentPanel = new Panel(new GridLayout(2));

            GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
            gridLayout.setVerticalSpacing(1);
            contentPanel.setLayoutManager(gridLayout);

            Label title = new Label("Type your login twice to login/register");
            title.setLayoutData(GridLayout.createLayoutData(
                    GridLayout.Alignment.CENTER, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                    GridLayout.Alignment.CENTER, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                    true,       // Give the component extra horizontal space if available
                    false,        // Give the component extra vertical space if available
                    2,                  // Horizontal span
                    1));                  // Vertical span
            contentPanel.addComponent(title);

            final TextBox login1 = (new TextBox().setValidationPattern(Pattern.compile(".{1,14}")).setPreferredSize(new TerminalSize(15, 1)).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));
            contentPanel.addComponent(new Label("Login: ").setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));
            contentPanel.addComponent(login1);

            final TextBox login2 = (new TextBox().setValidationPattern(Pattern.compile(".{1,14}")).setPreferredSize(new TerminalSize(15, 1)).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));
            contentPanel.addComponent(new Label("Login Again: ").setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));
            contentPanel.addComponent(login2);

            contentPanel.addComponent(new Button("Submit", () -> {
                if (login1.getText().isBlank() || login2.getText().isBlank()) {
                    MessageDialog.showMessageDialog(secondaryGui, "Error", "Login cannot be blank!!!");
                } else if (!login1.getText().trim().equals(login2.getText().trim())) {
                    MessageDialog.showMessageDialog(secondaryGui, "Error", "Logins must match");
                } else {
                    ResultSet resultSet;
                    PreparedStatement preparedStatement;
                    try {
                        preparedStatement = connection.prepareStatement("SELECT login FROM TestUsers WHERE login = ?");
                        preparedStatement.setString (1, login1.getText());
                        resultSet = preparedStatement.executeQuery();
                        String login = "";

                        int count = 0;
                        while (resultSet.next()) {
                            login = resultSet.getString("login");
                            count++;
                        }
                        System.out.println(count);
                        if (count == 0) {
                            String insert = "INSERT INTO TestUsers (login) VALUES (?)";
                            preparedStatement = connection.prepareStatement(insert);
                            preparedStatement.setString(1, login1.getText());
                            preparedStatement.executeUpdate();
                            username = login1.getText();

                        } else {
                            username = login;
                        }
                        secondaryWindow.close();

                    } catch (SQLException e) {
                        System.out.println(e.getSQLState());
                        System.exit(-1);
                    }
                }
            }
            ).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER)));
            contentPanel.addComponent(new Button("Exit", secondaryWindow::close).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER)));


            secondaryWindow.setComponent(contentPanel);
            secondaryGui.addWindowAndWait(secondaryWindow);
            window.setVisible(true);
        } else {
            MessageDialog.showMessageDialog(window.getTextGUI(), "Attention", "You've already logged in as " + username.toUpperCase());
        }
    }

    public static String getUsername() {
        return username;
    }

    public static void logout() {
        username = null;
    }
}
