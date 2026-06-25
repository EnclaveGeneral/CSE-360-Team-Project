package guiSetOnetimePassword;

import database.Database;
import java.util.UUID;

/*******
 * <p> Title: ControllerSetOnetimePassword Class. </p>
 *
 * <p> Description: The controller actions for the Set One-Time Password page. The admin selects
 * a user, clicks Generate, and the system stores a temporary password that forces the user to
 * reset it on next login. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00	2026-06-06 Initial version
 */
public class ControllerSetOnetimePassword {

    public ControllerSetOnetimePassword() {
    }

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Method: refreshUserList() </p>
     *
     * <p> Description: Loads all users into the ListView, skipping the header entry. </p>
     */
    protected static void refreshUserList() {
        java.util.List<String> users = theDatabase.getUserList();
        ViewSetOnetimePassword.listView_Users.getItems().clear();

        if (users == null || users.isEmpty()) {
            ViewSetOnetimePassword.listView_Users.getItems().add("No users found.");
            return;
        }

        // Skip index 0 which is "<Select a User>"
        for (int i = 1; i < users.size(); i++) {
            ViewSetOnetimePassword.listView_Users.getItems().add(users.get(i));
        }
    }

    /**********
     * <p> Method: performGenerateOTP() </p>
     *
     * <p> Description: Generates a random one-time password for the selected user, stores it
     * in the database, and displays it to the admin so they can pass it to the user. </p>
     */
    protected static void performGenerateOTP() {
        String selected =
                ViewSetOnetimePassword.listView_Users.getSelectionModel().getSelectedItem();

        if (selected == null || selected.equals("No users found.")) {
            return;
        }

        // Generate a random 8-character one-time password
        String otp = UUID.randomUUID().toString().substring(0, 8);

        // Store it in the database
        theDatabase.setOnetimePassword(selected, otp);

        // Show it to the admin
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("One-Time Password Generated");
        alert.setHeaderText("One-time password set for: " + selected);
        alert.setContentText("Give this code to the user:\n\n" + otp +
                "\n\nThey will be required to set a new password on next login.");
        alert.showAndWait();
    }

    /**********
     * <p> Method: performReturn() </p>
     *
     * <p> Description: Returns the admin to the Admin Home page. </p>
     */
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(
                ViewSetOnetimePassword.theStage, ViewSetOnetimePassword.theUser);
    }

    /**********
     * <p> Method: performLogout() </p>
     *
     * <p> Description: Logs the current user out and returns to the login page. </p>
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewSetOnetimePassword.theStage);
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