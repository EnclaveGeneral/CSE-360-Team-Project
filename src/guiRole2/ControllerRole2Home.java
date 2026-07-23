package guiRole2;


/*******
 * <p> Title: ControllerRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 2 Home Page, now serving as the Grader Home Page
 * (TP3 Aspect #1: Instructor/Grader Role &amp; Secure Access). Originally an unused stub role;
 * repurposed here instead of adding a fourth role flag to entityClasses.User, which would have
 * touched the database schema, login, and registration flows. This class provides the
 * controller actions based on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation
 * @version 2.00		2026-07-19 Repurposed as the Grader Home Page for TP3 Aspect #1
 */

public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerRole2Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
	
	/*******
	 * <p> Method: openGraderView() </p>
	 *
	 * <p> Description: Navigates to the Grader View page (TP3 Aspect #1), the secure read-only
	 * browser over every student's posts and replies. Replaces the earlier "forum" method, which
	 * sent the Grader into the full read-write Discussion board instead. </p>
	 *
	 */
	protected static void openGraderView() {
		guiGraderView.ViewGraderView.displayGraderView(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}
}
