package guiStatistics;

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
 * <p> Title: ViewStatistics Class </p>
 *
 * <p> Description: The JavaFX-based Aggregate Statistics page (TP3 Aspect #4). Shows one row per
 * student on the discussion board: posts authored, replies authored, distinct other students
 * answered, and the pass/fail verdict against the three-student rule, all computed by the
 * ParticipationStats engine. The page is read-only by construction &mdash; it contains no
 * Create, Update, or Delete widgets at all. Follows the Singleton design pattern so that only
 * one instance of the page is ever created, matching ViewGraderView and ViewClassRoster. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang (TP3 Aspect #4: Aggregate Statistics Engine)
 *
 * @version 1.00	2026-07-23	Initial version wiring the HW3 engine into TP3
 */
public class ViewStatistics {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	private static ViewStatistics theView = null;
	private static Scene theStatisticsScene;
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

	// GUI Area 2: the per-student statistics list
	protected static ListView<HBox> listView_Stats = new ListView<>();
	private static Line line_Separator2 = new Line(20, 520, width - 20, 520);

	// GUI Area 3: navigation
	protected static Button button_Back = new Button("Back");


	/*-*******************************************************************************************

	Display method

	**********************************************************************************************/

	/*******
	 * <p> Method: displayStatistics(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point from outside this package to display the Aggregate
	 * Statistics page. Establishes shared references, instantiates the singleton view if
	 * needed, then recomputes and repopulates the statistics from the current state of the
	 * discussion board. </p>
	 *
	 * <p> Checks that the calling user holds the Admin or Grader (Role2) role before displaying
	 * anything; a user without it is redirected to their own home page via
	 * GUISingleRoleDispatch instead of seeing this page at all, matching ViewGraderView. </p>
	 *
	 * @param ps   is the JavaFX Stage to be used for this GUI
	 *
	 * @param user is the user requesting this page; must hold the Admin or Grader role
	 *
	 */
	public static void displayStatistics(Stage ps, User user) {
		if (!user.getAdminRole() && !user.getNewRole2()) {
			guiTools.GUISingleRoleDispatch.doSingleRoleDispatch(ps, user);
			return;
		}

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewStatistics();

		label_UserDetails.setText("Viewer: " + theUser.getUserName());
		guiStatistics.ControllerStatistics.refreshStatistics();

		theStage.setTitle("CSE 360 Foundations: Aggregate Statistics");
		theStage.setScene(theStatisticsScene);
		theStage.show();
	}


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: ViewStatistics() </p>
	 *
	 * <p> Description: Initializes every widget on the page. This is a singleton and is only
	 * performed once; subsequent calls to displayStatistics() reuse the already-initialized
	 * scene. </p>
	 *
	 */
	private ViewStatistics() {

		theRootPane = new Pane();
		theStatisticsScene = new Scene(theRootPane, width, height);

		theStatisticsScene.getStylesheets().add(
			ViewStatistics.class.getResource("/dark-theme.css").toExternalForm());

		// GUI Area 1
		label_PageTitle.setText("Aggregate Statistics — Read Only");
		setupLabelUI(label_PageTitle, "Arial", 26, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("Viewer: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 50);

		// GUI Area 2
		listView_Stats.setLayoutX(20);
		listView_Stats.setLayoutY(110);
		listView_Stats.setPrefSize(width - 40, 390);

		// GUI Area 3
		setupButtonUI(button_Back, "Dialog", 18, 150, Pos.CENTER, 20, 540);
		button_Back.setOnAction((_) -> { guiStatistics.ControllerStatistics.performBack(); });

		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Separator1,
			listView_Stats, line_Separator2,
			button_Back);
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
