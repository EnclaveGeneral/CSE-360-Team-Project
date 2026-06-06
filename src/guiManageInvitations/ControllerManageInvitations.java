package guiManageInvitations;

import java.util.List;

import database.Database;

/*******
 * <p> Title: ControllerManageInvitations Class. </p>
 *
 * <p> Description: The controller actions for the Manage Invitations page. Like the other
 * controllers in this application, this is not an instantiated class; it is a collection of
 * protected static methods invoked by the View when the admin interacts with the GUI widgets.
 * The methods are declared "protected" so only the View (and Model) within this package can call
 * them. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00		2026-06-06 Initial version
 */
public class ControllerManageInvitations {

	/*-*******************************************************************************************

	User Interface Actions for this page

	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerManageInvitations() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> Method: refreshInvitationList() </p>
	 *
	 * <p> Description: Fetches the current outstanding invitations from the database and loads
	 * them into the ListView. Each entry is formatted "code | email | role". If there are none,
	 * a friendly message is shown instead of a blank box. </p>
	 */
	protected static void refreshInvitationList() {
		List<String> invitations = theDatabase.getInvitationList();
		ViewManageInvitations.listView_Invitations.getItems().clear();

		// getInvitationList() returns null only if the query failed; guard against it.
		if (invitations == null) {
			ViewManageInvitations.listView_Invitations.getItems()
					.add("*** Unable to read the invitation list ***");
			return;
		}

		if (invitations.isEmpty()) {
			ViewManageInvitations.listView_Invitations.getItems()
					.add("There are no outstanding invitations.");
			return;
		}

		ViewManageInvitations.listView_Invitations.getItems().addAll(invitations);
	}

	/**********
	 * <p> Method: performCancelInvitation() </p>
	 *
	 * <p> Description: Cancels the invitation currently selected in the ListView by removing it
	 * from the database, then refreshes the list. Each list entry is formatted "code | email |
	 * role", so the invitation code is recovered as the text before the first vertical bar. If
	 * nothing real is selected, the method does nothing. </p>
	 */
	protected static void performCancelInvitation() {
		String selected =
				ViewManageInvitations.listView_Invitations.getSelectionModel().getSelectedItem();

		// Do nothing if there is no selection or the selection is a placeholder message.
		if (selected == null || selected.indexOf("|") < 0) {
			return;
		}

		// The code is the text before the first "|" delimiter.
		String code = selected.substring(0, selected.indexOf("|")).trim();

		// Reuse the existing database method that removes an invitation by its code.
		theDatabase.removeInvitationAfterUse(code);

		// Refresh so the cancelled invitation disappears from the list.
		refreshInvitationList();
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the admin to the Admin Home page. </p>
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(
				ViewManageInvitations.theStage, ViewManageInvitations.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs the current user out and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageInvitations.theStage);
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
