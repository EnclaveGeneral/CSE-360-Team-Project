package guiListUsers;

import java.util.List;

import database.Database;

/*******
 * <p> Title: ControllerListUsers Class. </p>
 *
 * <p> Description: The controller actions for the List All Users page. Like the other controllers
 * in this application, this is not an instantiated class; it is a collection of protected static
 * methods invoked by the View when the admin interacts with the GUI widgets. The methods are
 * declared "protected" so only the View (and Model) within this package can call them. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00		2026-06-06 Initial version
 */
public class ControllerListUsers {

	/*-*******************************************************************************************

	User Interface Actions for this page

	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerListUsers() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> Method: refreshUserList() </p>
	 *
	 * <p> Description: Fetches the current list of users from the database and loads it into the
	 * ListView. The database returns a list whose first element is the "&lt;Select a User&gt;"
	 * header used by the selection pages; since this page only displays users, that header is
	 * skipped so the list shows real accounts only. </p>
	 */
	protected static void refreshUserList() {
		List<String> userList = theDatabase.getUserList();
		ViewListUsers.listView_Users.getItems().clear();

		// getUserList() returns null only if the query failed; guard against it.
		if (userList == null) {
			ViewListUsers.listView_Users.getItems().add("*** Unable to read the user list ***");
			return;
		}

		// Element 0 is the "<Select a User>" header used by the selection pages; skip it here.
		for (int i = 1; i < userList.size(); i++) {
			ViewListUsers.listView_Users.getItems().add(userList.get(i));
		}

		// If there are no users beyond the header, tell the admin rather than showing a blank box.
		if (ViewListUsers.listView_Users.getItems().isEmpty()) {
			ViewListUsers.listView_Users.getItems().add("There are no users in the system.");
		}
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the admin to the Admin Home page. </p>
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage, ViewListUsers.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs the current user out and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewListUsers.theStage);
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
