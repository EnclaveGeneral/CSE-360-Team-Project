package guiDiscussion;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;

import guiDiscussion.ControllerDiscussion;

/*******
 * <p> Title: ViewDiscussion Class </p>
 *
 * <p> Description: This class implements the View component of the MVC design pattern for the
 * Discussion Board page. It provides the graphical user interface for creating, reading,
 * updating, and deleting posts and replies. The page is divided into four areas: the page
 * title, the post input and list area, the reply input and list area, and the navigation
 * and error message area. This class follows the Singleton design pattern so that only one
 * instance of the page is ever created. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 2.00	2026-06-23
 */
public class ViewDiscussion {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	// The singleton instance — only one ViewDiscussion is ever created
	private static ViewDiscussion theView = null;
	private static Scene theDiscussionScene;
	private static Pane theRootPane;
	static Stage theStage;
	
	static entityClasses.User theUser;


	// Window dimensions pulled from FoundationsMain to ensure a consistent window size
	private static double width = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;

	/*-*******************************************************************************************

	GUI Widgets

	**********************************************************************************************/

	// Page title displayed at the top of the window
	static Label label_PageTitle = new Label("Discussion Board");

	// Error / status feedback label shown in red when validation fails
	static Label label_ErrorMessage = new Label("");

	// Section labels that identify the purpose of each area of the page
	static Label label_Posts = new Label("Posts");
	static Label label_Replies = new Label("Replies");
	static Label label_Title = new Label("Title:");
	static Label label_Body = new Label("Body:");
	static Label label_ReplyBody = new Label("Body:");
	static Label label_Author = new Label("Author:");

	// ListViews for displaying posts and replies from the database
	static ListView<String> listView_Posts = new ListView<>();
	static ListView<String> listView_Replies = new ListView<>();

	// Input fields for creating or editing a post
	static TextField text_Title = new TextField();
	static TextField text_Body = new TextField();
	static TextField text_Author = new TextField();
	static CheckBox check_IsPinned = new CheckBox("Pinned");

	// Input fields for creating or editing a reply
	static TextField text_ReplyBody = new TextField();
	static CheckBox check_IsAccepted = new CheckBox("Accepted");

	// Post CRUD buttons — each delegates to the ControllerDiscussion
	static Button button_CreatePost = new Button("Create Post");
	static Button button_UpdatePost = new Button("Update Post");
	static Button button_DeletePost = new Button("Delete Post");

	// Reply CRUD buttons — each delegates to the ControllerDiscussion
	static Button button_CreateReply = new Button("Create Reply");
	static Button button_UpdateReply = new Button("Update Reply");
	static Button button_DeleteReply = new Button("Delete Reply");

	// Navigation button to return to the previous page
	static Button button_Back = new Button("Back");

	// Horizontal separator lines used to visually divide the page into sections
	static javafx.scene.shape.Line line_Separator1 = new javafx.scene.shape.Line(0, 300, width, 300);
	static javafx.scene.shape.Line line_Separator2 = new javafx.scene.shape.Line(0, 500, width, 500);
	
	// Image Post Area
	static javafx.scene.shape.Line line_Separator3 =
	        new javafx.scene.shape.Line(0, 640, FoundationsMain.WINDOW_WIDTH, 640);
	static Label label_Images = new Label("Image Posts");
	static javafx.scene.control.ScrollPane scrollPane_Images =
	        new javafx.scene.control.ScrollPane();
	static javafx.scene.layout.GridPane grid_Images =
	        new javafx.scene.layout.GridPane();
	static Button button_AddImage = new Button("Add Image");


	/*-*******************************************************************************************

	Display method

	**********************************************************************************************/

	/**********
	 * <p> Method: displayDiscussion(Stage ps) </p>
	 *
	 * <p> Description: This method is used to display the Discussion Board page. If the singleton
	 * instance has not yet been created, it creates it by calling the private constructor. It then
	 * sets the scene onto the stage and displays the window. This method is called from the
	 * Role1 home page when the user navigates to the Discussion Board. </p>
	 *
	 * @param ps is the Stage object onto which this page's scene will be set and displayed. user is the 
	 * current signed in user
	 *
	 */
	public static void displayDiscussion(Stage ps, entityClasses.User user) {
	    theStage = ps;
	    theUser = user;

	    if (theView == null) theView = new ViewDiscussion();

	    theStage.setTitle("CSE 360: Discussion Board");
	    theStage.setScene(theDiscussionScene);
	    theStage.show();

	    ControllerDiscussion.refreshPostList();
	}



	/*-*******************************************************************************************

	Private constructor — initializes all GUI elements (runs once due to Singleton pattern)

	**********************************************************************************************/

	/**********
	 * <p> Method: ViewDiscussion() </p>
	 *
	 * <p> Description: This private constructor initializes all elements of the graphical user
	 * interface. It determines the location, size, font, color, and event handlers for each
	 * GUI widget. This method is only called once due to the Singleton design pattern.
	 * Subsequent calls to displayDiscussion() reuse the already-initialized scene. </p>
	 *
	 */
	private ViewDiscussion() {
		// Create the root pane and scene that will hold all widgets
		theRootPane = new Pane();
		theDiscussionScene = new Scene(theRootPane, width, height);

		// GUI Area 1 - Page Title
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		// GUI Area 2 - Post input fields and post list
		setupLabelUI(label_Posts, "Arial", 16, 200, Pos.BASELINE_LEFT, 20, 50);
		setupLabelUI(label_Author, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 80);
		setupTextUI(text_Author, "Arial", 14, 200, Pos.BASELINE_LEFT, 100, 78);

		setupLabelUI(label_Title, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 110);
		setupTextUI(text_Title, "Arial", 14, 400, Pos.BASELINE_LEFT, 100, 108);

		setupLabelUI(label_Body, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 140);
		setupTextUI(text_Body, "Arial", 14, 400, Pos.BASELINE_LEFT, 100, 138);

		// Checkbox to mark a post as pinned — positioned manually since it has no label companion
		check_IsPinned.setLayoutX(520);
		check_IsPinned.setLayoutY(110);

		// Post ListView — displays all posts loaded from the database
		// Clicking a post triggers selectPost() to populate the input fields and load replies
		listView_Posts.setLayoutX(20);
		listView_Posts.setLayoutY(175);
		listView_Posts.setPrefWidth(760);
		listView_Posts.setPrefHeight(120);
		listView_Posts.setOnMouseClicked((_) -> { ControllerDiscussion.selectPost(); });

		// Post CRUD buttons — each button delegates its action to the ControllerDiscussion
		setupButtonUI(button_CreatePost, "Dialog", 14, 150, Pos.CENTER, 20, 305);
		button_CreatePost.setOnAction((_) -> { ControllerDiscussion.performCreatePost(); });

		setupButtonUI(button_UpdatePost, "Dialog", 14, 150, Pos.CENTER, 180, 305);
		button_UpdatePost.setOnAction((_) -> { ControllerDiscussion.performUpdatePost(); });

		setupButtonUI(button_DeletePost, "Dialog", 14, 150, Pos.CENTER, 340, 305);
		button_DeletePost.setOnAction((_) -> { ControllerDiscussion.performDeletePost(); });

		// GUI Area 3 - Reply input fields and reply list
		setupLabelUI(label_Replies, "Arial", 16, 200, Pos.BASELINE_LEFT, 20, 345);

		// label_ReplyBody is a separate label object from label_Body to avoid repositioning conflicts
		setupLabelUI(label_ReplyBody, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 375);
		setupTextUI(text_ReplyBody, "Arial", 14, 500, Pos.BASELINE_LEFT, 100, 373);

		// Checkbox to mark a reply as the accepted answer
		check_IsAccepted.setLayoutX(620);
		check_IsAccepted.setLayoutY(375);

		// Reply ListView — displays only replies belonging to the currently selected post
		listView_Replies.setLayoutX(20);
		listView_Replies.setLayoutY(405);
		listView_Replies.setPrefWidth(760);
		listView_Replies.setPrefHeight(100);
		listView_Replies.setOnMouseClicked((_) -> { ControllerDiscussion.selectReply(); });

		// Reply CRUD buttons — each button delegates its action to the ControllerDiscussion
		setupButtonUI(button_CreateReply, "Dialog", 14, 150, Pos.CENTER, 20, 515);
		button_CreateReply.setOnAction((_) -> { ControllerDiscussion.performCreateReply(); });

		setupButtonUI(button_UpdateReply, "Dialog", 14, 150, Pos.CENTER, 180, 515);
		button_UpdateReply.setOnAction((_) -> { ControllerDiscussion.performUpdateReply(); });

		setupButtonUI(button_DeleteReply, "Dialog", 14, 150, Pos.CENTER, 340, 515);
		button_DeleteReply.setOnAction((_) -> { ControllerDiscussion.performDeleteReply(); });

		// GUI Area 4 - Error message and navigation
		// Red text is used for the error label so validation failures are immediately visible
		label_ErrorMessage.setTextFill(Color.RED);
		setupLabelUI(label_ErrorMessage, "Arial", 14, width, Pos.BASELINE_LEFT, 20, 565);

		setupButtonUI(button_Back, "Dialog", 14, 150, Pos.CENTER, 20, 600);
		button_Back.setOnAction((_) -> { ControllerDiscussion.performBack(); });

		// Add all widgets to the root pane so they are rendered on screen
		theRootPane.getChildren().addAll(
			label_PageTitle,
			label_Author, text_Author,
			label_Title, text_Title,
			label_Body, label_ReplyBody, text_Body,
			check_IsPinned,
			label_Posts, listView_Posts,
			button_CreatePost, button_UpdatePost, button_DeletePost,
			line_Separator1,
			label_Replies, text_ReplyBody, check_IsAccepted,
			listView_Replies,
			button_CreateReply, button_UpdateReply, button_DeleteReply,
			line_Separator2,
			label_ErrorMessage,
			button_Back
		);
		
		// Image section
		setupLabelUI(label_Images, "Arial", 16, 200, Pos.BASELINE_LEFT, 20, 645);

		setupButtonUI(button_AddImage, "Dialog", 14, 150, Pos.CENTER, 200, 643);
		button_AddImage.setOnAction((_) -> { ControllerDiscussion.performAddImage(theStage); });

		grid_Images.setHgap(30);
		grid_Images.setVgap(20);

		scrollPane_Images.setContent(grid_Images);
		scrollPane_Images.setLayoutX(20);
		scrollPane_Images.setLayoutY(670);
		scrollPane_Images.setPrefWidth(760);
		scrollPane_Images.setPrefHeight(60);
		scrollPane_Images.setHbarPolicy(
		    javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane_Images.setVbarPolicy(
		    javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

		theRootPane.getChildren().addAll(
		    line_Separator3, label_Images, button_AddImage, scrollPane_Images
		);

		database.Database db = applicationMain.FoundationsMain.database;
		db.createImageEntriesTable();
		ControllerDiscussion.refreshImageGrid();

	}


	/*-*******************************************************************************************

	Helper methods to reduce code length

	**********************************************************************************************/

	/**********
	 * <p> Method: setupLabelUI </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a Label. </p>
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font family to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the Label
	 * @param p		The alignment of the text within the Label
	 * @param x		The x-coordinate of the Label's position on the pane
	 * @param y		The y-coordinate of the Label's position on the pane
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}


	/**********
	 * <p> Method: setupTextUI </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a TextField. </p>
	 *
	 * @param t		The TextField object to be initialized
	 * @param ff	The font family to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the TextField
	 * @param p		The alignment of the text within the TextField
	 * @param x		The x-coordinate of the TextField's position on the pane
	 * @param y		The y-coordinate of the TextField's position on the pane
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}


	/**********
	 * <p> Method: setupButtonUI </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a Button. </p>
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font family to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the Button
	 * @param p		The alignment of the text within the Button
	 * @param x		The x-coordinate of the Button's position on the pane
	 * @param y		The y-coordinate of the Button's position on the pane
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}