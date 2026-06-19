/**
 * @author Joshua Sprague
 */
package guiForum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;

/**
 * the controller for the forum
 */
public class ControllerForum {
	/**
	 * the constructor for forum
	 */
	public ControllerForum() {
	}

	/**********
	 * <p> Method: refreshUserList() </p>
	 *
	 * <p> Description: Fetches the current list of users from the database and loads it into the
	 * ListView. The database returns a list whose first element is the "&lt;Select a User&gt;"
	 * header used by the selection pages; since this page only displays users, that header is
	 * skipped so the list shows real accounts only. </p>
	 */
	protected static void refreshPage() {
		HashMap<png, ArrayList<comment>> forum_data = new HashMap<png, ArrayList<comment>>();
		ViewForum.forum_data_view.clear();

		for (Map.Entry<png, ArrayList<comment>> entry : forum_data.entrySet()) {
		    ViewForum.forum_data_view.put(entry.getKey(), entry.getValue());
		}

		// If there are no users beyond the header, tell the admin rather than showing a blank box.
		if (ViewForum.forum_data_view.isEmpty()) {
			ViewForum.forum_data_view.put(null, null);
		}
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the admin to the Admin Home page. </p>
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewForum.theStage, ViewForum.theUser);
	}
	

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs the current user out and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewForum.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Gracefully terminates the execution of the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
	
	/**
	 * opens the comments for image
	 * 
	 * @param currentKey
	 */
	protected static void displayComment(png currentKey) {
		guiComment.ViewComment.displayComment(ViewForum.theStage, ViewForum.theUser, currentKey);
	}
}
