package guiMyView;

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
 * @version 1.00	2026-06-25	For the express purpose of Team project 2.

 */
public class ViewMyView {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	private static ViewMyView theView = null;
	private static Scene theMyView;
	private static Pane  theRootPane;

	static Stage              theStage;
	static entityClasses.User theUser;
	
	private static boolean reset = false;

	private static double width  = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;
	
	private static int numPosts = 0;
	private static int numReplies = 0;
	private static int readReplies = 0;
	private static int newReplies = 0;


	/*-*******************************************************************************************

	GUI Widgets — Header

	**********************************************************************************************/

	static Label  label_PageTitle     = new Label("Welcome to MyView");
	static Label  label_UserDetails   = new Label();
	static Line   line_Sep1 = new Line(20, 70, width - 20, 70);


	/*-*******************************************************************************************

	GUI Widgets — Post Input Area

	**********************************************************************************************/

	static Label label_PostSection = new Label("Posts");

	// Shared author field used by both posts and replies
	static Label     label_numPosts = new Label("Number of Posts: " + numPosts);
	

	// Toggle: USER vs KEYWORD
	static ToggleGroup toggle_PostType = new ToggleGroup();
	static RadioButton radio_User      = new RadioButton("User");
	static RadioButton radio_Keyword     = new RadioButton("Keyword");

	// Text body — shown when TEXT is selected
	static Label     label_Body = new Label("Body:");
	static TextField text_Filter  = new TextField();


	static Line line_Sep2 = new Line(20, 358, width - 20, 358);


	/*-*******************************************************************************************

	GUI Widgets — Post List

	**********************************************************************************************/

	static ListView<HBox> listView_Posts = new ListView<>();


	/*-*******************************************************************************************

	GUI Widgets — Reply Area

	**********************************************************************************************/

	static Label 	 label_ReplySection = new Label("Replies");
	static Label     label_numReplies = new Label("Number of replies: " + numReplies);
	static Label 	 label_ReadVsUndreadReplies = new Label("Read Replies: " + readReplies + " New Replies: " + newReplies);
	static Label 	 label_Filter = new Label("Filter by:");

//	static Label     label_ReplyBody = new Label("Body:");
//	static TextField text_ReplyBody  = new TextField();
	

//	// Reply CRUD buttons — replies are always text
//	static Button button_CreateReply = new Button("Create Reply");
//	static Button button_UpdateReply = new Button("Update Reply");
//	static Button button_DeleteReply = new Button("Delete Reply");

	static ListView<String> listView_Replies = new ListView<>();

	static Line line_Sep3 = new Line(20, 655, width - 20, 655);


	/*-*******************************************************************************************

	GUI Widgets — Status and Navigation

	**********************************************************************************************/

//	static Label  label_ErrorMessage = new Label("");
	static Button button_Back        = new Button("Back");
	static Button button_ShowAll        = new Button("Show all");
	static Button button_UnreadOnly        = new Button("Show unread");
	static Button button_Search        = new Button("Search");


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
	public static void displayMyView(Stage ps, entityClasses.User user) {
		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewMyView();

		label_UserDetails.setText("User: " + theUser.getUserName());
//		label_ErrorMessage.setText("");

		theStage.setTitle("CSE 360: MyView");
		theStage.setScene(theMyView);
		theStage.show();

		ControllerMyView.refreshPostList();
	}


	/*-*******************************************************************************************

	Private constructor — initializes all GUI elements (runs once, Singleton)

	**********************************************************************************************/

	/*******
	 * <p> Method: ViewDiscussion() </p>
	 *
	 * <p> Description: This private constructor initializes all elements of the graphical user
	 * interface. It determines the location, size, font, colour, and event handlers for each
	 * GUI widget. This method is only called once due to the Singleton design pattern.
	 * Subsequent calls to displayDiscussion() reuse the already-initialized scene. </p>
	 *
	 */
	private ViewMyView() {

		theRootPane        = new Pane();
		theMyView = new Scene(theRootPane, width, height);
		theMyView.getStylesheets().add(
			ViewMyView.class.getResource("/dark-theme.css").toExternalForm());

		// ── Header (y 8–70) ──────────────────────────────────────────────────────
		setupLabel(label_PageTitle,     "Arial", 22, width, Pos.CENTER,        0,  10);
		setupLabel(label_UserDetails,   "Arial", 18, 500,   Pos.BASELINE_LEFT, 20, 44);
		

		// ── Post input (y 80–180) ─────────────────────────────────────────────────
		setupLabel(label_PostSection, "Arial", 13, 100, Pos.BASELINE_LEFT, 20, 82);

		// Author and Title on the same row
		setupLabel(label_numPosts, "Arial", 13, 50,  Pos.BASELINE_LEFT, 20,  108);


		// Text body (default visible)
		setupLabel(label_Body, "Arial", 13, 30,  Pos.BASELINE_LEFT, 20,  158);
		setupText (text_Filter,  "Arial", 13, 275, Pos.BASELINE_LEFT, 500,  426);


		// ── Post list (y 215–358) ─────────────────────────────────────────────────
		listView_Posts.setLayoutX(20);
		listView_Posts.setLayoutY(130);
		listView_Posts.setPrefWidth(760);
		listView_Posts.setPrefHeight(220);
		listView_Posts.setOnMouseClicked((_) -> ControllerMyView.selectPost());

		// ── Reply input (y 370–470) ───────────────────────────────────────────────
		setupLabel(label_ReplySection, "Arial", 13, 100, Pos.BASELINE_LEFT, 20, 372);
		setupLabel(label_numReplies, "Arial", 13, 30,  Pos.BASELINE_LEFT, 20,  398);
		setupLabel (label_ReadVsUndreadReplies,  "Arial", 13, 605, Pos.BASELINE_LEFT, 20,  426);
		setupLabel(label_Filter, "Arial", 13, 100, Pos.BASELINE_LEFT, 500, 372);
		
		// USER / KEYWORD toggle
		radio_User.setToggleGroup(toggle_PostType);
		radio_Keyword.setToggleGroup(toggle_PostType);
		radio_User.setSelected(true);
		radio_User.setLayoutX(500);  radio_User.setLayoutY(398);
		radio_Keyword.setLayoutX(562); radio_Keyword.setLayoutY(398);


		// ── Reply list (y 458–615) ────────────────────────────────────────────────
		listView_Replies.setLayoutX(20);
		listView_Replies.setLayoutY(458);
		listView_Replies.setPrefWidth(760);
		listView_Replies.setPrefHeight(150);
		listView_Replies.setOnMouseClicked((_) -> ControllerMyView.selectReply());

		// ── Status + navigation (y 623–710) ────────────────────────────────────── XX - Back button not working fix
		setupButton(button_Back, "Dialog", 13, 110, Pos.CENTER, 660, 665);
		button_Back.setOnAction((_) -> ControllerMyView.xx());
		
		setupButton(button_ShowAll, "Dialog", 13, 110, Pos.CENTER, 20, 620);
		button_ShowAll.setOnAction((_) -> ControllerMyView.showAllReplies());       // changed selectPost to showAllReplies
		
		setupButton(button_UnreadOnly, "Dialog", 13, 110, Pos.CENTER, 150, 620);
		button_UnreadOnly.setOnAction((_) -> ControllerMyView.showUnreadReplies()); // changed doNothing to showUnreadReplies
		
		setupButton(button_Search, "Dialog", 13, 110, Pos.CENTER, 660, 393);
		button_Search.setOnAction((_) -> ControllerMyView.performFilterReplies()); // changed doNothing to performFilterReplies

		// Add all widgets to the pane
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Sep1,
			label_PostSection, label_numPosts, label_numReplies,
			radio_User, radio_Keyword,
			text_Filter,
			listView_Posts,
			line_Sep2,
			label_ReplySection, label_ReadVsUndreadReplies, listView_Replies,
			label_Filter,
			line_Sep3,
			button_Back,
			button_ShowAll, button_UnreadOnly, button_Search
		);

		// Ensure discussion tables exist
		FoundationsMain.database.createDiscussionTables();
	}
	
//	public static void set_reset(boolean c){
//		reset = c;
//		
//		if(reset) {
//			theRootPane.getChildren().add(button_reset_filter);
//		}
//		else {
//			theRootPane.getChildren().remove(button_reset_filter);
//		}
//	}


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
