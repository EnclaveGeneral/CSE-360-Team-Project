package guiMyView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import guiAdminHome.ViewAdminHome;
import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerMyView Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
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
public class ControllerMyView {

	/*-*******************************************************************************************

	Attributes

	**********************************************************************************************/

	// -1 means nothing selected
	private static int selectedPostId  = -1;
	private static int selectedReplyId = -1;

	// Holds the image file chosen via FileChooser until Create Post is pressed
	private static java.io.File pendingImageFile = null;

	private static Database theDatabase = FoundationsMain.database;


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: ControllerMyView() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other controller classes. </p>
	 *
	 */
	public ControllerMyView() {
	}


	/*-*******************************************************************************************

	List refresh methods

	**********************************************************************************************/

	/*******
	 * <p> Method: refreshPostList() </p>
	 *
	 * <p> Description: Retrieves all posts from the database and repopulates listView_Posts.
	 * Text posts are prefixed with a document icon; image posts with an image icon so the
	 * user can distinguish post types at a glance. Called after any post CRUD operation. </p>
	 *
	 */
	protected static void refreshPostList() {
	    ViewMyView.listView_Posts.getItems().clear();
	    List<DiscussionPost> posts = theDatabase.getAllPosts();
//	    ViewMyView.set_reset(false);
	    
	    for (DiscussionPost p : posts) {
	        String icon = p.isImagePost() ? "\uD83D\uDDBC" : "\uD83D\uDCC4";

	        Label postLabel = new Label(icon + " [" + p.getId() + "] " + p.getTitle() + " — " + p.getAuthor());

	        HBox postBox = new HBox(10);
	        postBox.setPadding(new Insets(5));
	        postBox.getChildren().add(postLabel);

	        String[] tags = p.getTags().split(" ");
	        for (String tag : tags) {
	            Button tagButton = new Button("#" + tag);
	            tagButton.setOnAction(event -> filter_by_tags(tag));
	            postBox.getChildren().add(tagButton);
	        }

	        ViewMyView.listView_Posts.getItems().add(postBox);
	    }
	}

	protected static void filter_by_tags(String tag) {
	    ViewMyView.listView_Posts.getItems().clear();
	    List<DiscussionPost> posts = theDatabase.getAllPosts();
//	    ViewMyView.set_reset(true);
	    
	    for (DiscussionPost p : posts) {
	        String[] tags = p.getTags().split(" ");
	        if (!Arrays.asList(tags).contains(tag)) {
	            continue;
	        }

	        String icon = p.isImagePost() ? "\uD83D\uDDBC" : "\uD83D\uDCC4";
	        Label postLabel = new Label(icon + " [" + p.getId() + "] " + p.getTitle() + " — " + p.getAuthor());

	        HBox postBox = new HBox(10);
	        postBox.setPadding(new Insets(5));
	        postBox.getChildren().add(postLabel);

	        for (String t : tags) {
	            Button tagButton = new Button("#" + t);
	            tagButton.setOnAction(event -> filter_by_tags(t));
	            postBox.getChildren().add(tagButton);
	        }
	     
	        ViewMyView.listView_Posts.getItems().add(postBox);
	    }
	}


	/*******
	 * <p> Method: refreshReplyList(int postId) </p>
	 *
	 * <p> Description: Retrieves all replies for the given post and repopulates
	 * listView_Replies. Each reply displays as "[id] body — author". Called after any reply
	 * CRUD operation and when a new post is selected. </p>
	 *
	 * @param postId is an int that specifies the unique identifier of the parent post whose
	 *               replies should be loaded.
	 *
	 */
	protected static void refreshReplyList(int postId) {
		ViewMyView.listView_Replies.getItems().clear();
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		for (DiscussionReply r : replies) {
			ViewMyView.listView_Replies.getItems().add(
				"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
		}
	}
	
//	protected static void refreshReplyListUnreadOnly(int postId) {
//		ViewMyView.listView_Replies.getItems().clear();
//		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
//		for (DiscussionReply r : replies) {
//			
//			ViewMyView.listView_Replies.getItems().add(
//				"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
//		}
//	}
	
	
	
	


	/*-*******************************************************************************************

	Selection methods

	**********************************************************************************************/

	/*******
	 * <p> Method: selectPost() </p>
	 *
	 * <p> Description: Handles the user clicking a post in listView_Posts. Stores its id in
	 * selectedPostId, populates the input fields with the post's data, and loads its replies.
	 * For image posts, the body field is left blank since there is no editable text body. </p>
	 *
	 */
	protected static void selectPost() {
		int index = ViewMyView.listView_Posts.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionPost> posts = theDatabase.getAllPosts();
		if (index >= posts.size()) return;

		DiscussionPost p = posts.get(index);
		selectedPostId  = p.getId();
		selectedReplyId = -1;

		// Populate shared fields
//		ViewMyView.text_Author.setText(p.getAuthor());
//		ViewMyView.text_Title.setText(p.getTitle());

//		if (p.isImagePost()) {
//			// Switch toggle to IMAGE and show filename — body is not editable for image posts
//			ViewMyView.radio_Keyword.setSelected(true);
//			ViewMyView.label_ImageFile.setText(p.getImageFilename() != null
//				? p.getImageFilename() : "image post");
//			ViewMyView.text_Filter.setText("");
//		} else {
//			// Switch toggle to TEXT and populate the body field
//			ViewMyView.radio_User.setSelected(true);
//			ViewMyView.text_Filter.setText(p.getBody() != null ? p.getBody() : "");
//		}

//		ViewMyView.label_ErrorMessage.setText(
//			"Selected: \"" + p.getTitle() + "\" (" + p.getPostType() + " post)");

		refreshReplyList(selectedPostId);
	}


	/*******
	 * <p> Method: selectReply() </p>
	 *
	 * <p> Description: Handles the user clicking a reply in listView_Replies. Stores its id
	 * in selectedReplyId and populates the reply body field with its current text. </p>
	 *
	 */
	protected static void selectReply() {
		int index = ViewMyView.listView_Replies.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionReply> replies = theDatabase.getRepliesForPost(selectedPostId);
		if (index >= replies.size()) return;

		DiscussionReply r = replies.get(index);
		selectedReplyId = r.getId();

		// Populate the reply body field so the user can edit it
//		ViewMyView.text_ReplyBody.setText(r.getBody());
	}


	/*-*******************************************************************************************

	Post action methods

	**********************************************************************************************/

	/*******
	 * <p> Method: performPickImage(Stage stage) </p>
	 *
	 * <p> Description: Opens a FileChooser dialog so the user can select a PNG, JPG, or JPEG
	 * file. Stores the chosen file in pendingImageFile and updates the filename label. The
	 * file is not saved to the database until Create Post is pressed. </p>
	 *
	 * @param stage is the current JavaFX Stage required to open the FileChooser dialog.
	 *
	 */
//	protected static void performPickImage(Stage stage) {
//		javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
//		fc.setTitle("Select Image");
//		fc.getExtensionFilters().add(
//			new javafx.stage.FileChooser.ExtensionFilter(
//				"Image Files", "*.png", "*.jpg", "*.jpeg"));
//
//		java.io.File home = new java.io.File(System.getProperty("user.home"), "Downloads");
//		if (home.exists()) fc.setInitialDirectory(home);
//
//		java.io.File chosen = fc.showOpenDialog(stage);
//		if (chosen != null) {
//			pendingImageFile = chosen;
//			ViewMyView.label_ImageFile.setText(chosen.getName());
//		}
//	}
	
	public static void launchMyView() {
		guiMyView.ViewMyView.displayMyView(ViewMyView.theStage, ViewMyView.theUser);
	}


	/*-*******************************************************************************************

	Navigation

	**********************************************************************************************/

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns the user to their home page. Reads activeHomePage to decide
	 * whether to route to the Admin home (1) or Role1 home (any other value). </p>
	 *
	 */
	protected static void xx() {
		if (FoundationsMain.activeHomePage == 1) {
			guiAdminHome.ViewAdminHome.displayAdminHome(
				ViewMyView.theStage, ViewMyView.theUser);
		} else {
			guiRole1.ViewRole1Home.displayRole1Home(
				ViewMyView.theStage, ViewMyView.theUser);
		}
	}


	/*-*******************************************************************************************

	Private helper methods

	**********************************************************************************************/

	/*******
	 * <p> Method: setError(String message) </p>
	 *
	 * <p> Description: Sets the error message label in ViewDiscussion with the provided text.
	 * Centralises all error reporting through a single call. </p>
	 *
	 * @param message is a String that specifies the error message to display.
	 *
	 */
//	private static void setError(String message) {
//		ViewMyView.label_ErrorMessage.setText("Error: " + message);
//	}


	/*******
	 * <p> Method: setSuccess(String message) </p>
	 *
	 * <p> Description: Sets the status label in ViewDiscussion with a success confirmation
	 * message. Centralises all success reporting through a single call. </p>
	 *
	 * @param message is a String that specifies the success message to display.
	 *
	 */
//	private static void setSuccess(String message) {
//		ViewMyView.label_ErrorMessage.setTextFill(javafx.scene.paint.Color.web("#6bcb77"));
//		ViewMyView.label_ErrorMessage.setText(message);
//	}
	
	public static boolean doNothing() {
		return true;
	}


	/*******
	 * <p> Method: clearPostFields() </p>
	 *
	 * <p> Description: Clears the post input fields (Author, Title, Body) and resets the
	 * toggle to TEXT after any successful post CRUD operation. </p>
	 *
	 */
	private static void clearPostFields() {
		ViewMyView.radio_User.setSelected(true);
//		ViewMyView.label_ImageFile.setText("No file selected.");
		pendingImageFile = null;
	}

}
