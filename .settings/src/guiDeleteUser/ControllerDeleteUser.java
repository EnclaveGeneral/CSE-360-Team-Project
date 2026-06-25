package guiDeleteUser;

import database.Database;

/*******
 * <p> Title: ControllerDeleteUser Class. </p>
 *
 * <p> Description: The controller actions for the Delete User page. Like the other controllers
 * in this application, this is not an instantiated class; it is a collection of protected static
 * methods invoked by the View when the admin interacts with the GUI widgets. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00	2026-06-06 Initial version
 */
public class ControllerDeleteUser {

	/**
	 * Default constructor is not used.
	 */
	public ControllerDeleteUser() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> Method: refreshUserList() </p>
	 *
	 * <p> Description: Fetches the current list of users from the database and loads them into
	 * the ListView. If there are no users, a placeholder message is shown. </p>
	 */
	protected static void refreshUserList() {
		java.util.List<String> users = theDatabase.getUserList();
		ViewDeleteUser.listView_Users.getItems().clear();

		if (users == null || users.isEmpty()) {
			ViewDeleteUser.listView_Users.getItems().add("No users found.");
			return;
		}

		// getUserList() adds "<Select a User>" at index 0 — skip it
		for (int i = 1; i < users.size(); i++) {
			ViewDeleteUser.listView_Users.getItems().add(users.get(i));
		}
	}

	/**********
	 * <p> Method: performDeleteUser() </p>
	 *
	 * <p> Description: Deletes the user currently selected in the ListView after showing a
	 * confirmation dialog. If nothing is selected, the method does nothing. </p>
	 */
	protected static void performDeleteUser() {
		String selected =
				ViewDeleteUser.listView_Users.getSelectionModel().getSelectedItem();

		// Do nothing if there is no real selection
		if (selected == null || selected.equals("No users found.")) {
			return;
		}

		// Show confirmation dialog before deleting
		javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirm Delete");
		confirm.setHeaderText("Delete user: " + selected + "?");
		confirm.setContentText("This action cannot be undone.");

		java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
		if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
			theDatabase.deleteUserAccount(selected);
			refreshUserList();
		}
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the admin to the Admin Home page. </p>
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(
				ViewDeleteUser.theStage, ViewDeleteUser.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs the current user out and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
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