package guiForcePasswordReset;

import database.Database;
import javafx.scene.paint.Color;

/*******
 * <p> Title: ControllerForcePasswordReset Class. </p>
 *
 * <p> Description: The controller actions for the Force Password Reset page. This page is shown
 * when a user logs in with a one-time password. They must set a valid new password before
 * proceeding to their home page. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00	2026-06-06 Initial version
 */
public class ControllerForcePasswordReset {

    public ControllerForcePasswordReset() {
    }

    private static Database theDatabase = applicationMain.FoundationsMain.database;
    private static String newPassword = "";

    /**********
     * <p> Method: setNewPassword() </p>
     *
     * <p> Description: Called on every keystroke in the password field. Evaluates the current
     * input and updates the requirement labels live. </p>
     */
    protected static void setNewPassword() {
        newPassword = ViewForcePasswordReset.text_NewPassword.getText();

        // Check max length first
        if (newPassword.length() > 24) {
            ViewForcePasswordReset.label_ReqShortEnough
                    .setText("No more than 24 characters - Not satisfied");
            ViewForcePasswordReset.label_ReqShortEnough.setTextFill(Color.RED);
        } else {
            ViewForcePasswordReset.label_ReqShortEnough
                    .setText("No more than 24 characters - Satisfied");
            ViewForcePasswordReset.label_ReqShortEnough.setTextFill(Color.GREEN);
        }

        // Run the evaluator
        passwordPopUpWindow.Model.evaluatePassword(newPassword);

        // Update labels
        ViewForcePasswordReset.label_ReqUpperCase.setTextFill(
                passwordPopUpWindow.Model.foundUpperCase ? Color.GREEN : Color.RED);
        ViewForcePasswordReset.label_ReqUpperCase.setText("At least one upper case letter - " +
                (passwordPopUpWindow.Model.foundUpperCase ? "Satisfied" : "Not yet satisfied"));

        ViewForcePasswordReset.label_ReqLowerCase.setTextFill(
                passwordPopUpWindow.Model.foundLowerCase ? Color.GREEN : Color.RED);
        ViewForcePasswordReset.label_ReqLowerCase.setText("At least one lower case letter - " +
                (passwordPopUpWindow.Model.foundLowerCase ? "Satisfied" : "Not yet satisfied"));

        ViewForcePasswordReset.label_ReqNumericDigit.setTextFill(
                passwordPopUpWindow.Model.foundNumericDigit ? Color.GREEN : Color.RED);
        ViewForcePasswordReset.label_ReqNumericDigit.setText("At least one numeric digit - " +
                (passwordPopUpWindow.Model.foundNumericDigit ? "Satisfied" : "Not yet satisfied"));

        ViewForcePasswordReset.label_ReqSpecialChar.setTextFill(
                passwordPopUpWindow.Model.foundSpecialChar ? Color.GREEN : Color.RED);
        ViewForcePasswordReset.label_ReqSpecialChar.setText("At least one special character - " +
                (passwordPopUpWindow.Model.foundSpecialChar ? "Satisfied" : "Not yet satisfied"));

        ViewForcePasswordReset.label_ReqLongEnough.setTextFill(
                passwordPopUpWindow.Model.foundLongEnough ? Color.GREEN : Color.RED);
        ViewForcePasswordReset.label_ReqLongEnough.setText("At least 8 characters - " +
                (passwordPopUpWindow.Model.foundLongEnough ? "Satisfied" : "Not yet satisfied"));
    }

    /**********
     * <p> Method: performSavePassword() </p>
     *
     * <p> Description: Validates the new password on submit, updates the database, and
     * navigates the user to their home page. </p>
     */
    protected static void performSavePassword() {
        // On-submit validation gate
        String passwordError = passwordPopUpWindow.Model.evaluatePassword(newPassword);
        if (passwordError.length() > 0) {
            ViewForcePasswordReset.label_ErrorMessage.setText(passwordError);
            return;
        }

        // Check the two password fields match
        String confirm = ViewForcePasswordReset.text_ConfirmPassword.getText();
        if (!newPassword.equals(confirm)) {
            ViewForcePasswordReset.label_ErrorMessage
                    .setText("The two passwords do not match. Please try again.");
            return;
        }

        // Update the password in the database
        theDatabase.updatePassword(ViewForcePasswordReset.theUsername, newPassword);

        // Fetch the updated user details and navigate to their home page
        theDatabase.getUserAccountDetails(ViewForcePasswordReset.theUsername);
        entityClasses.User user = new entityClasses.User(
                ViewForcePasswordReset.theUsername, newPassword,
                theDatabase.getCurrentFirstName(), theDatabase.getCurrentMiddleName(),
                theDatabase.getCurrentLastName(), theDatabase.getCurrentPreferredFirstName(),
                theDatabase.getCurrentEmailAddress(), theDatabase.getCurrentAdminRole(),
                theDatabase.getCurrentNewRole1(), theDatabase.getCurrentNewRole2());

        int numberOfRoles = theDatabase.getNumberOfRoles(user);
        if (numberOfRoles == 1) {
            if (user.getAdminRole())
                guiAdminHome.ViewAdminHome.displayAdminHome(
                        ViewForcePasswordReset.theStage, user);
            else if (user.getNewRole1())
                guiRole1.ViewRole1Home.displayRole1Home(
                        ViewForcePasswordReset.theStage, user);
            else if (user.getNewRole2())
                guiRole2.ViewRole2Home.displayRole2Home(
                        ViewForcePasswordReset.theStage, user);
        } else if (numberOfRoles > 1) {
            guiMultipleRoleDispatch.ViewMultipleRoleDispatch.displayMultipleRoleDispatch(
                    ViewForcePasswordReset.theStage, user);
        }
    }

    /**********
     * <p> Method: performQuit() </p>
     *
     * <p> Description: Gracefully terminates the execution of the application. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}