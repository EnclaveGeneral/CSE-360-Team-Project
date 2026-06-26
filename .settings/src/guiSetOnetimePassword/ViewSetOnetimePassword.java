package guiSetOnetimePassword;

import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewSetOnetimePassword Class. </p>
 *
 * <p> Description: The View for the Set One-Time Password page. The admin selects a user from
 * the list and clicks Generate to create a temporary password for that user. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00	2026-06-06 Initial version
 */
public class ViewSetOnetimePassword {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static Label label_Title = new Label("Set a One-Time Password");
    private static Label label_Instructions =
            new Label("Select a user and click Generate to create a temporary password.");
    protected static ListView<String> listView_Users = new ListView<>();
    private static Button button_Generate = new Button("Generate One-Time Password");
    private static Button button_Return = new Button("Return");
    private static Button button_Logout = new Button("Logout");
    private static Button button_Quit = new Button("Quit");

    private static ViewSetOnetimePassword theView;
    protected static Stage theStage;
    private static Pane theRootPane;
    private static Scene theSetOnetimePasswordScene;
    protected static User theUser;

    /**********
     * <p> Method: displaySetOnetimePassword(Stage ps, User user) </p>
     *
     * <p> Description: Single entry point to display the Set One-Time Password page. </p>
     */
    public static void displaySetOnetimePassword(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewSetOnetimePassword();

        ControllerSetOnetimePassword.refreshUserList();

        theStage.setTitle("CSE 360 Foundation Code: Set One-Time Password");
        theStage.setScene(theSetOnetimePasswordScene);
        theStage.show();
    }

    private ViewSetOnetimePassword() {
        theRootPane = new Pane();
        theSetOnetimePasswordScene = new Scene(theRootPane, width, height);
        
        // Enable dark mode
        theSetOnetimePasswordScene.getStylesheets().add(ViewSetOnetimePassword.class.getResource("/dark-theme.css").toExternalForm());

        setupLabelUI(label_Title, "Arial", 28, width, Pos.CENTER, 0, 10);
        setupLabelUI(label_Instructions, "Arial", 16, width, Pos.CENTER, 0, 55);

        listView_Users.setLayoutX(50);
        listView_Users.setLayoutY(90);
        listView_Users.setMinWidth(width - 100);
        listView_Users.setPrefHeight(300);

        setupButtonUI(button_Generate, "Dialog", 16, 250, Pos.CENTER, 50, 410);
        button_Generate.setOnAction((_) ->
                { ControllerSetOnetimePassword.performGenerateOTP(); });

        setupButtonUI(button_Return, "Dialog", 16, 150, Pos.CENTER, 50, 460);
        button_Return.setOnAction((_) ->
                { ControllerSetOnetimePassword.performReturn(); });

        setupButtonUI(button_Logout, "Dialog", 16, 150, Pos.CENTER, 220, 460);
        button_Logout.setOnAction((_) ->
                { ControllerSetOnetimePassword.performLogout(); });

        setupButtonUI(button_Quit, "Dialog", 16, 150, Pos.CENTER, 390, 460);
        button_Quit.setOnAction((_) ->
                { ControllerSetOnetimePassword.performQuit(); });

        theRootPane.getChildren().addAll(label_Title, label_Instructions,
                listView_Users, button_Generate, button_Return, button_Logout, button_Quit);
    }

    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}