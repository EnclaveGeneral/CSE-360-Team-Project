package guiGraderView;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;
import entityClasses.User;

/*******
 * <p> Title: ViewGraderView Class </p>
 *
 * <p> Description: The JavaFX-based Grader View page (TP3 Aspect #1). Shows every student's
 * posts and replies read-only, alongside the selected post author's answer-coverage status.
 * There are no Create, Update, or Delete widgets anywhere on this page -- that omission, not a
 * role check, is what makes this view "secure, read-only" by construction. Follows the
 * Singleton design pattern so that only one instance of the page is ever created, matching
 * ViewDiscussion and ViewMyView. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack Holtrey (TP3 Aspect #1: Instructor/Grader Role &amp; Secure Access)
 *
 * @version 1.00	2026-07-19	Initial version for TP3
 */
public class ViewGraderView {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	private static ViewGraderView theView = null;
	private static Scene theGraderViewScene;
	private static Pane  theRootPane;

	static Stage theStage;
	static User  theUser;

	private static double width  = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;


	/*-*******************************************************************************************

	Widget attributes

	**********************************************************************************************/

	// GUI Area 1: page title and current-user context
	protected static Label label_PageTitle   = new Label();
	protected static Label label_UserDetails = new Label();
	private static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// GUI Area 2: the read-only post/reply browser and the coverage status line
	protected static ListView<HBox> listView_Posts   = new ListView<>();
	protected static ListView<String> listView_Replies = new ListView<>();
	protected static Label label_Coverage = new Label();
	protected static Button button_ClassRoster = new Button("Class Roster");
	private static Line line_Separator2 = new Line(20, 480, width - 20, 480);

	// GUI Area 3: navigation
	protected static Button button_Back   = new Button("Back");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit   = new Button("Quit");


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: displayGraderView(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point from outside this package to display the Grader View
	 * page. Establishes shared references, instantiates the singleton view if needed, then
	 * populates the dynamic parts of the page with the current state of the discussion
	 * board. </p>
	 *
	 * <p> Checks that the calling user holds the Admin or Grader (Role2) role before displaying
	 * anything; a user without it is redirected to their own home page via
	 * GUISingleRoleDispatch instead of seeing this page at all. </p>
	 *
	 * @param ps   is the JavaFX Stage to be used for this GUI
	 *
	 * @param user is the user requesting this page; must hold the Admin or Grader role
	 *
	 */
	public static void displayGraderView(Stage ps, User user) {
		if (!user.getAdminRole() && !user.getNewRole2()) {
			guiTools.GUISingleRoleDispatch.doSingleRoleDispatch(ps, user);
			return;
		}

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewGraderView();

		label_UserDetails.setText("Grader: " + theUser.getUserName());
		guiGraderView.ControllerGraderView.refreshPostList();

		theStage.setTitle("CSE 360 Foundations: Grader View");
		theStage.setScene(theGraderViewScene);
		theStage.show();
	}

	/*******
	 * <p> Method: ViewGraderView() </p>
	 *
	 * <p> Description: Initializes every widget on the page. This is a singleton and is only
	 * performed once; subsequent calls to displayGraderView() reuse the already-initialized
	 * scene. </p>
	 *
	 */
	private ViewGraderView() {

		theRootPane = new Pane();
		theGraderViewScene = new Scene(theRootPane, width, height);

		theGraderViewScene.getStylesheets().add(
			ViewGraderView.class.getResource("/dark-theme.css").toExternalForm());

		// GUI Area 1
		label_PageTitle.setText("Grader View — Read Only");
		setupLabelUI(label_PageTitle, "Arial", 26, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("Grader: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 50);

		// GUI Area 2
		listView_Posts.setLayoutX(20);
		listView_Posts.setLayoutY(110);
		listView_Posts.setPrefSize(width - 40, 220);
		listView_Posts.getSelectionModel().selectedItemProperty().addListener(
			(obs, oldV, newV) -> guiGraderView.ControllerGraderView.selectPost());

		setupLabelUI(label_Coverage, "Arial", 16, width - 40, Pos.BASELINE_LEFT, 20, 340);

		listView_Replies.setLayoutX(20);
		listView_Replies.setLayoutY(370);
		listView_Replies.setPrefSize(width - 40, 100);

		setupButtonUI(button_ClassRoster, "Dialog", 16, 200, Pos.CENTER, 20, 485);
		button_ClassRoster.setOnAction((_) -> { guiGraderView.ControllerGraderView.openClassRoster(); });

		// GUI Area 3
		setupButtonUI(button_Back, "Dialog", 18, 150, Pos.CENTER, 20, 540);
		button_Back.setOnAction((_) -> { guiGraderView.ControllerGraderView.performBack(); });

		setupButtonUI(button_Logout, "Dialog", 18, 150, Pos.CENTER, 190, 540);
		button_Logout.setOnAction((_) -> { guiGraderView.ControllerGraderView.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 150, Pos.CENTER, 360, 540);
		button_Quit.setOnAction((_) -> { guiGraderView.ControllerGraderView.performQuit(); });

		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Separator1,
			listView_Posts, label_Coverage, listView_Replies, button_ClassRoster, line_Separator2,
			button_Back, button_Logout, button_Quit);
	}


	/*-*******************************************************************************************

	Helper methods to reduce code length

	**********************************************************************************************/

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
