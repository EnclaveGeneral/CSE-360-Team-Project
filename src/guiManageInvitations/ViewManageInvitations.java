package guiManageInvitations;

import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewManageInvitations Class. </p>
 *
 * <p> Description: The Java/FX-based Manage Invitations Page. This class provides the JavaFX GUI
 * widgets that display every outstanding invitation code in the system and allow an admin to
 * cancel a selected one. It follows the same singleton + Model-View-Controller pattern used by the
 * other admin sub-pages (e.g. guiAddRemoveRoles, guiListUsers). All access starts by invoking the
 * static method displayManageInvitations; no other code should instantiate this class directly. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00		2026-06-06 Initial version
 */
public class ViewManageInvitations {

	/*-*******************************************************************************************

	Attributes

	*/

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: page title, whose account is being used, and a button to update that account
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	private static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// GUI Area 2: the list of outstanding invitations and a button to cancel the selected one
	protected static Label label_Instructions =
			new Label("Outstanding invitations (code | email | role):");
	protected static ListView<String> listView_Invitations = new ListView<String>();
	protected static Button button_CancelInvitation = new Button("Cancel Selected Invitation");

	// Separator above the navigation buttons
	private static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	// GUI Area 3: navigation - return to the Admin Home page, log out, or quit
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// Singleton / shared state, mirroring the other pages
	private static ViewManageInvitations theView;	// Determines if instantiation is needed
	protected static Stage theStage;		// The Stage that JavaFX has established for us
	protected static Pane theRootPane;		// The Pane that holds all the GUI widgets
	protected static User theUser;			// The current user of the application

	public static Scene theManageInvitationsScene = null;	// The Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	*/

	/**********
	 * <p> Method: displayManageInvitations(Stage ps, User user) </p>
	 *
	 * <p> Description: The single entry point from outside this package to display the Manage
	 * Invitations page. It establishes the shared attributes, instantiates the page the first time
	 * it is used, refreshes the list of invitations from the database, and then shows the scene. </p>
	 *
	 * @param ps	specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user	specifies the current user of the application
	 */
	public static void displayManageInvitations(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, create the singleton instance of this class
		if (theView == null) theView = new ViewManageInvitations();

		// Refresh the dynamic content for the current state of the system
		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerManageInvitations.refreshInvitationList();

		// Display the page and wait for the admin to do something
		theStage.setTitle("CSE 360 Foundation Code: Manage Invitations Page");
		theStage.setScene(theManageInvitationsScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewManageInvitations() </p>
	 *
	 * <p> Description: This constructor initializes all the static elements of the graphical user
	 * interface: the location, size, font, alignment, and the event handlers for each widget. As
	 * this is a singleton, it runs just once; subsequent uses reuse these widgets and only refresh
	 * the changeable fields via displayManageInvitations. </p>
	 */
	public ViewManageInvitations() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theManageInvitationsScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Manage Invitations Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: ");
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) ->
			{ guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		// GUI Area 2
		setupLabelUI(label_Instructions, "Arial", 20, 500, Pos.BASELINE_LEFT, 20, 130);
		listView_Invitations.setLayoutX(20);
		listView_Invitations.setLayoutY(170);
		listView_Invitations.setPrefWidth(width - 40);
		listView_Invitations.setPrefHeight(290);

		setupButtonUI(button_CancelInvitation, "Dialog", 16, 260, Pos.CENTER, 20, 475);
		button_CancelInvitation.setOnAction((_) ->
			{ ControllerManageInvitations.performCancelInvitation(); });

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> { ControllerManageInvitations.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> { ControllerManageInvitations.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> { ControllerManageInvitations.performQuit(); });

		// Place every widget into the Pane. This page is static; only the list contents change
		// between uses, so there is no dynamic repaint as on the AddRemoveRoles page.
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				label_Instructions, listView_Invitations, button_CancelInvitation,
				line_Separator4, button_Return, button_Logout, button_Quit);
	}

	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	*/

	/**********
	 * Private local method to initialize the standard fields for a label.
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a button.
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
