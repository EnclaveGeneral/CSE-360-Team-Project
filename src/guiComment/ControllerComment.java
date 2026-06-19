/**
 * @author Joshua Sprague
 */
package guiComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.Database;
import guiForum.comment;
import guiForum.png;

/**
 * controller for comments
 */
public class ControllerComment {
		/**
		 * constructor for controller of comments
		 */
		public ControllerComment() {
		}

		private static Database theDatabase = applicationMain.FoundationsMain.database;

		/**
		 * makes em comments
		 */
		protected static void refreshPage() {
	        HashMap<png, ArrayList<comment>> forum_data = theDatabase.loadImageEntries();
	        ViewComment.forum_data_view.clear();

	        for (Map.Entry<png, ArrayList<comment>> entry : forum_data.entrySet()) {
	            ViewComment.forum_data_view.put(entry.getKey(), entry.getValue());
	        }

	        if (ViewComment.forum_data_view.isEmpty()) {
	            ViewComment.forum_data_view.put(null, null);
	        }
	    }

		/**********
		 * <p> Method: performReturn() </p>
		 *
		 * <p> Description: Returns the admin to the Admin Home page. </p>
		 */
		protected static void performReturn() {
			guiForum.ViewForum.displayForum(ViewComment.theStage, ViewComment.theUser);
		}
		

		/**********
		 * <p> Method: performLogout() </p>
		 *
		 * <p> Description: Logs the current user out and returns to the login page. </p>
		 */
		protected static void performLogout() {
			guiUserLogin.ViewUserLogin.displayUserLogin(ViewComment.theStage);
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
