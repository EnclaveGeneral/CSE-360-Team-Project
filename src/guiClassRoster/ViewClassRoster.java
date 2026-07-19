package guiClassRoster;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;



/*******
 * <p> Title: ViewMyView Class </p>
 *
 * <p> Description: This class implements the View component of the MVC design pattern for
 * the unified MyView Board page. It handles all user interactions from MyView,
 * validates input via ModelMyView, and delegates all database operations to Database.
 * The purpose of this gui is to allow users to interact strictly with a filtered view of their 
 * own posts and replies to those posts. Users will be able to filter responses from specific users, specific keywords,
 * and between all messages vs only unread messages. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Omid Kadkhodaei
 *
 * @version 1.00	2026-07-19	For the express purpose of Team project 3.

 */
public class ViewClassRoster {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	private static ViewClassRoster theView = null;
	private static Scene theRoster;
	private static Pane  theRootPane;

	static Stage              theStage;
	static entityClasses.User theUser;
	
	private static boolean selection = false;

	private static double width  	= FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;
	
	
	/***
	 * Records number of posts.
	 */
	public static int numPosts = 0;
	
	/***
	 * Records number of replies.
	 */
	public static int numReplies = 0;
	
	/***
	 * Records number of read replies.
	 */
	public static int readReplies = 0;
	
	/***
	 * Records number of new replies to a post.
	 */
	public static int newReplies = 0;
	
	/***
	 * Records the id of the current post selected to be passed to different methods.
	 */
	public static int currentPostId = 0;


	/*-*******************************************************************************************

	GUI Widgets — Header

	**********************************************************************************************/

	static Label  label_PageTitle     = new Label("Class Roster");
	static Label  label_UserDetails   = new Label();
	static Line   line_Sep1 = new Line(20, 70, width - 20, 70);


	/*-*******************************************************************************************

	GUI Widgets — Post Input Area

	**********************************************************************************************/

	/*-*******************************************************************************************

	GUI Widgets — Post List

	**********************************************************************************************/

	static ListView<HBox> listView_Posts = new ListView<>();


	static Line line_Sep2 = new Line(20, 655, width - 20, 655);


	/*-*******************************************************************************************

	GUI Widgets — Status and Navigation

	**********************************************************************************************/

//	static Label  label_ErrorMessage = new Label("");
	static Button button_Back       	= new Button("Back");
	static Button button_ShowAll        = new Button("Show all");
//	static Button button_UnreadOnly     = new Button("Show unread");


	/*-*******************************************************************************************

	Display method

	**********************************************************************************************/

	/*******
	 * <p> Method: displayDiscussion(Stage ps, User user) </p>
	 *
	 * <p> Description: Entry point called from Role1 and Admin home pages. Creates the singleton
	 * on the first call; subsequent calls simply swap the scene and refresh the post list. </p>
	 *
	 * @param ps   is the Stage object onto which this page's scene will be set and displayed.
	 *
	 * @param user is the currently logged-in User whose username is displayed in the header.
	 *
	 */
	public static void displayClassRoster(Stage ps, entityClasses.User user) {
		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewClassRoster();

		label_UserDetails.setText("User: " + theUser.getUserName());
//		label_ErrorMessage.setText("");

		theStage.setTitle("CSE 360: Class Roster");
		theStage.setScene(theRoster);
		theStage.show();

		ControllerClassRoster.refreshPostList();
	}


	/*-*******************************************************************************************

	Private constructor — initializes all GUI elements (runs once, Singleton)

	**********************************************************************************************/

	/*******
	 * <p> Method: ViewMyView() </p>
	 *
	 * <p> Description: This private constructor initializes all elements of the graphical user
	 * interface. It determines the location, size, font, colour, and event handlers for each
	 * GUI widget. This method is only called once due to the Singleton design pattern. </p>
	 *
	 */
	private ViewClassRoster() {

		theRootPane        = new Pane();
		theRoster = new Scene(theRootPane, width, height);
		theRoster.getStylesheets().add(
			ViewClassRoster.class.getResource("/dark-theme.css").toExternalForm());

		// ── Header (y 8–70) ──────────────────────────────────────────────────────
		setupLabel(label_PageTitle,     "Arial", 22, width, Pos.CENTER,        0,  10);
		setupLabel(label_UserDetails,   "Arial", 18, 500,   Pos.BASELINE_LEFT, 20, 44);



		// ── Post list (y 215–358) ─────────────────────────────────────────────────
		listView_Posts.setLayoutX(20);
		listView_Posts.setLayoutY(80);
		listView_Posts.setPrefWidth(760);
		listView_Posts.setPrefHeight(560);
		listView_Posts.setOnMouseClicked((_) -> {
			ControllerClassRoster.selectPost();
			});

		// ── Reply input (y 370–470) ───────────────────────────────────────────────
		setupButton(button_Back, "Dialog", 13, 110, Pos.CENTER, 660, 665);
		button_Back.setOnAction((_) -> {ControllerClassRoster.performBack(); });
		
		setupButton(button_ShowAll, "Dialog", 13, 110, Pos.CENTER, 20, 665);
		button_ShowAll.setOnAction((_) -> {});
		
//		setupButton(button_UnreadOnly, "Dialog", 13, 110, Pos.CENTER, 150, 665);
//		button_UnreadOnly.setOnAction((_) -> {});

		// Add all widgets to the pane
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Sep1,
			listView_Posts,
			line_Sep2,
			button_Back,
			button_ShowAll
		);

		// Ensure discussion tables exist
		FoundationsMain.database.createDiscussionTables();
	}
	


	/*-*******************************************************************************************

	Helper methods

	**********************************************************************************************/
	

	/*******
	 * <p> Method: setupLabel </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a Label. </p>
	 *
	 * @param l   The Label object to be initialized
	 * @param ff  The font family to be used
	 * @param f   The size of the font to be used
	 * @param w   The minimum width of the Label
	 * @param p   The alignment (e.g. left, centered, or right)
	 * @param x   The location from the left edge (x axis)
	 * @param y   The location from the top (y axis)
	 */
	private void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}


	/*******
	 * <p> Method: setupText </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a TextField. </p>
	 *
	 * @param t   The TextField object to be initialized
	 * @param ff  The font family to be used
	 * @param f   The size of the font to be used
	 * @param w   The minimum width of the TextField
	 * @param p   The alignment (e.g. left, centered, or right)
	 * @param x   The location from the left edge (x axis)
	 * @param y   The location from the top (y axis)
	 */
	private void setupText(TextField t, String ff, double f, double w, Pos p, double x, double y) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}


	/*******
	 * <p> Method: setupButton </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a Button. </p>
	 *
	 * @param b   The Button object to be initialized
	 * @param ff  The font family to be used
	 * @param f   The size of the font to be used
	 * @param w   The minimum width of the Button
	 * @param p   The alignment (e.g. left, centered, or right)
	 * @param x   The location from the left edge (x axis)
	 * @param y   The location from the top (y axis)
	 */
	private void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}
