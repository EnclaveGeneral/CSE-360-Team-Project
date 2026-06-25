package guiMyView;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;

/*******
 * <p> Title: ViewDiscussion Class </p>
 *
 * <p> Description: This class implements the View component of the MVC design pattern for the
 * unified Discussion Board page. It provides the graphical user interface for full CRUD on
 * text posts, create and delete for image posts, and full CRUD on text replies. This class
 * follows the Singleton design pattern so that only one instance of the page is ever
 * created. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang, Joshua Sprague
 *
 * @version 1.00	2026-06-15	Initial HW2 text-only discussion board
 * @version 2.00	2026-06-22	Added image post section
 * @version 3.00	2026-06-23	Unified board; text/image toggle; full CRUD for text posts/replies
 */
public class ViewMyView {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	private static ViewMyView theView = null;
	private static Scene theDiscussionScene;
	private static Pane  theRootPane;

	static Stage              theStage;
	static entityClasses.User theUser;
	
	private static boolean reset = false;

	private static double width  = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;


	/*-*******************************************************************************************

	GUI Widgets — Header

	**********************************************************************************************/

	static Label  label_PageTitle     = new Label("Discussion Board");
	static Label  label_UserDetails   = new Label();
	static Button button_AccountUpdate = new Button("Account Update");
	static Line   line_Sep1 = new Line(20, 70, width - 20, 70);


	/*-*******************************************************************************************

	GUI Widgets — Post Input Area

	**********************************************************************************************/

	static Label label_PostSection = new Label("Posts");

	// Shared author field used by both posts and replies
	static Label     label_Author = new Label("Author:");
	static TextField text_Author  = new TextField();

	static Label     label_Title = new Label("Title:");
	static TextField text_Title  = new TextField();
	
	static Label     label_Tags = new Label("Tags:");
	static TextField text_tags  = new TextField();

	// Toggle: TEXT vs IMAGE
	static ToggleGroup toggle_PostType = new ToggleGroup();
	static RadioButton radio_Text      = new RadioButton("Text");
	static RadioButton radio_Image     = new RadioButton("Image");

	// Text body — shown when TEXT is selected
	static Label     label_Body = new Label("Body:");
	static TextField text_Body  = new TextField();

	// Image file picker — shown when IMAGE is selected
	static Label  label_ImageFile  = new Label("No file selected.");
	static Button button_PickImage = new Button("Browse\u2026");

	// Post CRUD buttons
	// Note: Update is only enabled for text posts; image posts support create and delete only
	static Button button_CreatePost = new Button("Create Post");
	static Button button_reset_filter = new Button("reset filter");
	static Button button_UpdatePost = new Button("Update Post");
	static Button button_DeletePost = new Button("Delete Post");

	static Line line_Sep2 = new Line(20, 358, width - 20, 358);


	/*-*******************************************************************************************

	GUI Widgets — Post List

	**********************************************************************************************/

	static ListView<HBox> listView_Posts = new ListView<>();


	/*-*******************************************************************************************

	GUI Widgets — Reply Area

	**********************************************************************************************/

	static Label label_ReplySection = new Label("Replies");

	static Label     label_ReplyBody = new Label("Body:");
	static TextField text_ReplyBody  = new TextField();

	// Reply CRUD buttons — replies are always text
	static Button button_CreateReply = new Button("Create Reply");
	static Button button_UpdateReply = new Button("Update Reply");
	static Button button_DeleteReply = new Button("Delete Reply");

	static ListView<String> listView_Replies = new ListView<>();

	static Line line_Sep3 = new Line(20, 615, width - 20, 615);


	/*-*******************************************************************************************

	GUI Widgets — Status and Navigation

	**********************************************************************************************/

	static Label  label_ErrorMessage = new Label("");
	static Button button_Back        = new Button("Back");


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
	public static void displayDiscussion(Stage ps, entityClasses.User user) {
		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewMyView();

		label_UserDetails.setText("User: " + theUser.getUserName());
		label_ErrorMessage.setText("");

		theStage.setTitle("CSE 360: Discussion Board");
		theStage.setScene(theDiscussionScene);
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
		theDiscussionScene = new Scene(theRootPane, width, height);
		theDiscussionScene.getStylesheets().add(
			ViewMyView.class.getResource("/dark-theme.css").toExternalForm());

		// ── Header (y 8–70) ──────────────────────────────────────────────────────
		setupLabel(label_PageTitle,     "Arial", 22, width, Pos.CENTER,        0,  10);
		setupLabel(label_UserDetails,   "Arial", 13, 500,   Pos.BASELINE_LEFT, 20, 44);
		setupButton(button_AccountUpdate, "Dialog", 12, 145, Pos.CENTER, 635, 37);
		button_AccountUpdate.setOnAction((_) ->
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser));

		// ── Post input (y 80–180) ─────────────────────────────────────────────────
		setupLabel(label_PostSection, "Arial", 13, 100, Pos.BASELINE_LEFT, 20, 82);

		// Author and Title on the same row
		setupLabel(label_Author, "Arial", 12, 50,  Pos.BASELINE_LEFT, 20,  108);
		setupText (text_Author,  "Arial", 12, 170, Pos.BASELINE_LEFT, 72,  105);
		setupLabel(label_Title,  "Arial", 12, 30,  Pos.BASELINE_LEFT, 255, 108);
		setupText (text_Title,   "Arial", 12, 350, Pos.BASELINE_LEFT, 290, 105);
		setupLabel(label_Tags,  "Arial", 12, 30,  Pos.BASELINE_LEFT, 255, 133);
		setupText (text_tags,   "Arial", 12, 350, Pos.BASELINE_LEFT, 290, 133);

		// TEXT / IMAGE toggle
		radio_Text.setToggleGroup(toggle_PostType);
		radio_Image.setToggleGroup(toggle_PostType);
		radio_Text.setSelected(true);
		radio_Text.setLayoutX(20);  radio_Text.setLayoutY(133);
		radio_Image.setLayoutX(82); radio_Image.setLayoutY(133);

		// Text body (default visible)
		setupLabel(label_Body, "Arial", 12, 30,  Pos.BASELINE_LEFT, 20,  158);
		setupText (text_Body,  "Arial", 12, 605, Pos.BASELINE_LEFT, 55,  155);

		// Image picker (default hidden)
		label_ImageFile.setFont(Font.font("Arial", 12));
		label_ImageFile.setLayoutX(55);
		label_ImageFile.setLayoutY(158);
		label_ImageFile.setMinWidth(400);
		setupButton(button_PickImage, "Dialog", 11, 85, Pos.CENTER, 668, 154);
		button_PickImage.setOnAction((_) -> ControllerMyView.performPickImage(theStage));
		label_ImageFile.setVisible(false);
		button_PickImage.setVisible(false);

		// Swap visible controls when toggle changes
		toggle_PostType.selectedToggleProperty().addListener((obs, oldT, newT) -> {
			boolean img = (newT == radio_Image);
			label_Body.setVisible(!img);
			text_Body.setVisible(!img);
			label_ImageFile.setVisible(img);
			button_PickImage.setVisible(img);
			// Update Post disabled for image posts — no meaningful field to edit
			button_UpdatePost.setDisable(img);
		});

		// Post CRUD buttons
		setupButton(button_CreatePost, "Dialog", 12, 130, Pos.CENTER, 20,  183);
		button_CreatePost.setOnAction((_) -> ControllerMyView.performCreatePost());
		
		setupButton(button_reset_filter, "Dialog", 12, 130, Pos.CENTER, 450,  183);
		button_reset_filter.setOnAction((_) -> ControllerMyView.refreshPostList());
		
		setupButton(button_UpdatePost, "Dialog", 12, 130, Pos.CENTER, 160, 183);
		button_UpdatePost.setOnAction((_) -> ControllerMyView.performUpdatePost());

		setupButton(button_DeletePost, "Dialog", 12, 130, Pos.CENTER, 300, 183);
		button_DeletePost.setOnAction((_) -> ControllerMyView.performDeletePost());

		// ── Post list (y 215–358) ─────────────────────────────────────────────────
		listView_Posts.setLayoutX(20);
		listView_Posts.setLayoutY(215);
		listView_Posts.setPrefWidth(760);
		listView_Posts.setPrefHeight(135);
		listView_Posts.setOnMouseClicked((_) -> ControllerMyView.selectPost());

		// ── Reply input (y 370–470) ───────────────────────────────────────────────
		setupLabel(label_ReplySection, "Arial", 13, 100, Pos.BASELINE_LEFT, 20, 372);

		setupLabel(label_ReplyBody, "Arial", 12, 30,  Pos.BASELINE_LEFT, 20,  398);
		setupText (text_ReplyBody,  "Arial", 12, 605, Pos.BASELINE_LEFT, 55,  395);

		// Reply CRUD buttons
		setupButton(button_CreateReply, "Dialog", 12, 130, Pos.CENTER, 20,  425);
		button_CreateReply.setOnAction((_) -> ControllerMyView.performCreateReply());

		setupButton(button_UpdateReply, "Dialog", 12, 130, Pos.CENTER, 160, 425);
		button_UpdateReply.setOnAction((_) -> ControllerMyView.performUpdateReply());

		setupButton(button_DeleteReply, "Dialog", 12, 130, Pos.CENTER, 300, 425);
		button_DeleteReply.setOnAction((_) -> ControllerMyView.performDeleteReply());

		// ── Reply list (y 458–615) ────────────────────────────────────────────────
		listView_Replies.setLayoutX(20);
		listView_Replies.setLayoutY(458);
		listView_Replies.setPrefWidth(760);
		listView_Replies.setPrefHeight(150);
		listView_Replies.setOnMouseClicked((_) -> ControllerMyView.selectReply());

		// ── Status + navigation (y 623–710) ──────────────────────────────────────
		label_ErrorMessage.setTextFill(Color.web("#ff6b6b"));
		setupLabel(label_ErrorMessage, "Arial", 12, width - 160, Pos.BASELINE_LEFT, 20, 625);
		setupButton(button_Back, "Dialog", 12, 110, Pos.CENTER, 660, 620);
		button_Back.setOnAction((_) -> ControllerMyView.performBack());

		// Add all widgets to the pane
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_AccountUpdate, line_Sep1,
			label_PostSection,
			label_Author, text_Author,
			label_Title,  text_Title,
			radio_Text, radio_Image,
			label_Body, text_Body,
			label_Tags, text_tags,
			label_ImageFile, button_PickImage,
			button_CreatePost, button_UpdatePost, button_DeletePost,
			listView_Posts,
			line_Sep2,
			label_ReplySection,
			label_ReplyBody, text_ReplyBody,
			button_CreateReply, button_UpdateReply, button_DeleteReply,
			listView_Replies,
			line_Sep3,
			label_ErrorMessage, button_Back
		);

		// Ensure discussion tables exist
		FoundationsMain.database.createDiscussionTables();
	}
	
	public static void set_reset(boolean c){
		reset = c;
		
		if(reset) {
			theRootPane.getChildren().add(button_reset_filter);
		}
		else {
			theRootPane.getChildren().remove(button_reset_filter);
		}
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
