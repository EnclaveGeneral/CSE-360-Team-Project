package guiForcePasswordReset;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewForcePasswordReset Class. </p>
 *
 * <p> Description: The View for the Force Password Reset page. Shown when a user logs in with
 * a one-time password. The user must set a valid new password before proceeding. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00	2026-06-06 Initial version
 */
public class ViewForcePasswordReset {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static Label label_Title = new Label("Password Reset Required");
    private static Label label_Instructions =
            new Label("Your account requires a new password. Please set one below.");

    private static Label label_NewPassword = new Label("New Password:");
    protected static PasswordField text_NewPassword = new PasswordField();

    // Password requirement labels
    protected static Label label_ReqUpperCase = new Label();
    protected static Label label_ReqLowerCase = new Label();
    protected static Label label_ReqNumericDigit = new Label();
    protected static Label label_ReqSpecialChar = new Label();
    protected static Label label_ReqLongEnough = new Label();
    protected static Label label_ReqShortEnough = new Label();

    private static Label label_ConfirmPassword = new Label("Confirm Password:");
    protected static PasswordField text_ConfirmPassword = new PasswordField();

    protected static Label label_ErrorMessage = new Label();

    private static Button button_SavePassword = new Button("Save New Password");
    private static Button button_Quit = new Button("Quit");

    private static ViewForcePasswordReset theView;
    protected static Stage theStage;
    private static Pane theRootPane;
    private static Scene theForcePasswordResetScene;
    protected static String theUsername;

    /**********
     * <p> Method: displayForcePasswordReset(Stage ps, String username) </p>
     *
     * <p> Description: Single entry point to display the Force Password Reset page. </p>
     */
    public static void displayForcePasswordReset(Stage ps, String username) {
        theStage = ps;
        theUsername = username;

        if (theView == null) theView = new ViewForcePasswordReset();

        // Clear fields on each display
        text_NewPassword.setText("");
        text_ConfirmPassword.setText("");
        label_ErrorMessage.setText("");

        theStage.setTitle("CSE 360 Foundation Code: Password Reset Required");
        theStage.setScene(theForcePasswordResetScene);
        theStage.show();
    }

    private ViewForcePasswordReset() {
        theRootPane = new Pane();
        theForcePasswordResetScene = new Scene(theRootPane, width, height);

        setupLabelUI(label_Title, "Arial", 28, width, Pos.CENTER, 0, 10);
        setupLabelUI(label_Instructions, "Arial", 16, width, Pos.CENTER, 0, 55);

        setupLabelUI(label_NewPassword, "Arial", 18, 200, Pos.BASELINE_LEFT, 50, 100);
        setupTextUI(text_NewPassword, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 130);
        text_NewPassword.setPromptText("Enter new password");
        text_NewPassword.textProperty().addListener((_, _, _) ->
                { ControllerForcePasswordReset.setNewPassword(); });

        // Password requirement labels
        setupLabelUI(label_ReqUpperCase, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 165);
        label_ReqUpperCase.setText("At least one upper case letter - Not yet satisfied");
        label_ReqUpperCase.setTextFill(Color.RED);

        setupLabelUI(label_ReqLowerCase, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 183);
        label_ReqLowerCase.setText("At least one lower case letter - Not yet satisfied");
        label_ReqLowerCase.setTextFill(Color.RED);

        setupLabelUI(label_ReqNumericDigit, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 201);
        label_ReqNumericDigit.setText("At least one numeric digit - Not yet satisfied");
        label_ReqNumericDigit.setTextFill(Color.RED);

        setupLabelUI(label_ReqSpecialChar, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 219);
        label_ReqSpecialChar.setText("At least one special character - Not yet satisfied");
        label_ReqSpecialChar.setTextFill(Color.RED);

        setupLabelUI(label_ReqLongEnough, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 237);
        label_ReqLongEnough.setText("At least 8 characters - Not yet satisfied");
        label_ReqLongEnough.setTextFill(Color.RED);

        setupLabelUI(label_ReqShortEnough, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 255);
        label_ReqShortEnough.setText("No more than 24 characters - Not yet satisfied");
        label_ReqShortEnough.setTextFill(Color.RED);

        setupLabelUI(label_ConfirmPassword, "Arial", 18, 200, Pos.BASELINE_LEFT, 50, 285);
        setupTextUI(text_ConfirmPassword, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 315);
        text_ConfirmPassword.setPromptText("Confirm new password");

        setupLabelUI(label_ErrorMessage, "Arial", 16, width, Pos.CENTER, 0, 360);
        label_ErrorMessage.setTextFill(Color.RED);

        setupButtonUI(button_SavePassword, "Dialog", 18, 250, Pos.CENTER, 50, 400);
        button_SavePassword.setOnAction((_) ->
                { ControllerForcePasswordReset.performSavePassword(); });

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 320, 400);
        button_Quit.setOnAction((_) ->
                { ControllerForcePasswordReset.performQuit(); });

        theRootPane.getChildren().addAll(
                label_Title, label_Instructions,
                label_NewPassword, text_NewPassword,
                label_ReqUpperCase, label_ReqLowerCase, label_ReqNumericDigit,
                label_ReqSpecialChar, label_ReqLongEnough, label_ReqShortEnough,
                label_ConfirmPassword, text_ConfirmPassword,
                label_ErrorMessage, button_SavePassword, button_Quit);
    }

    private void setupLabelUI(Label l, String ff, double f, double w,
            Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private void setupTextUI(PasswordField t, String ff, double f, double w,
            Pos p, double x, double y) {
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
    }

    private void setupButtonUI(Button b, String ff, double f, double w,
            Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}