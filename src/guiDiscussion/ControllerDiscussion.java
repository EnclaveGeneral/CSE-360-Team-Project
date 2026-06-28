package guiDiscussion;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
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
 * <p> Title: ControllerDiscussion Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the unified Discussion Board page. It handles all user interactions from ViewDiscussion,
 * validates input via ModelDiscussion, and delegates all database operations to Database.
 * Text posts support full CRUD. Image posts support create and delete only — update is not
 * meaningful for a BLOB image. Replies are always text and support full CRUD. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang, Joshua Sprague
 *
 * @version 1.00	2026-06-15	Initial HW2 text-only discussion board
 * @version 2.00	2026-06-22	Added image post support
 * @version 3.00	2026-06-23	Unified board; text CRUD + image create/delete; reply CRUD
 */
public class ControllerDiscussion {

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
	 * <p> Method: ControllerDiscussion() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other controller classes. </p>
	 *
	 */
	public ControllerDiscussion() {
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
	    ViewDiscussion.listView_Posts.getItems().clear();
	    List<DiscussionPost> posts = theDatabase.getAllPosts();
	    ViewDiscussion.set_reset(false);
	    
	    for (DiscussionPost p : posts) {
	        String icon = p.isImagePost() ? "\uD83D\uDDBC" : "\uD83D\uDCC4";

	        Label postLabel = new Label(icon + " [" + p.getId() + "] " + p.getTitle() + " — " + p.getAuthor());

	        HBox postBox = new HBox(10);
	        postBox.setPadding(new Insets(5));
	        postBox.getChildren().add(postLabel);
	        
	        String rawTags = p.getTags();
	        String[] tags = (rawTags != null && !rawTags.isEmpty()) ? rawTags.split(" ") : new String[0];
	        for (String tag : tags) {
	            Button tagButton = new Button("#" + tag);
	            tagButton.setOnAction(event -> filter_by_tags(tag));
	            postBox.getChildren().add(tagButton);
	        }

	        ViewDiscussion.listView_Posts.getItems().add(postBox);
	    }
	}

	protected static void filter_by_tags(String tag) {
	    ViewDiscussion.listView_Posts.getItems().clear();
	    List<DiscussionPost> posts = theDatabase.getAllPosts();
	    ViewDiscussion.set_reset(true);
	    
	    for (DiscussionPost p : posts) {
	    	
	    	String rawTags = p.getTags();
	        String[] tags = (rawTags != null && !rawTags.isEmpty()) ? rawTags.split(" ") : new String[0];
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
	     
	        ViewDiscussion.listView_Posts.getItems().add(postBox);
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
		ViewDiscussion.listView_Replies.getItems().clear();
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		for (DiscussionReply r : replies) {
			ViewDiscussion.listView_Replies.getItems().add(
				"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
		}
	}


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
		int index = ViewDiscussion.listView_Posts.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionPost> posts = theDatabase.getAllPosts();
		if (index >= posts.size()) return;

		DiscussionPost p = posts.get(index);
		selectedPostId  = p.getId();
		selectedReplyId = -1;

		// Populate shared fields
		ViewDiscussion.text_Author.setText(p.getAuthor());
		ViewDiscussion.text_Title.setText(p.getTitle());

		if (p.isImagePost()) {
			// Switch toggle to IMAGE and show filename — body is not editable for image posts
			ViewDiscussion.radio_Image.setSelected(true);
			ViewDiscussion.label_ImageFile.setText(p.getImageFilename() != null
				? p.getImageFilename() : "image post");
			ViewDiscussion.text_Body.setText("");
		} else {
			// Switch toggle to TEXT and populate the body field
			ViewDiscussion.radio_Text.setSelected(true);
			ViewDiscussion.text_Body.setText(p.getBody() != null ? p.getBody() : "");
		}
		
		ViewDiscussion.text_tags.setText(p.getTags() != null ? p.getTags() : "");

		ViewDiscussion.label_ErrorMessage.setText(
			"Selected: \"" + p.getTitle() + "\" (" + p.getPostType() + " post)");

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
		int index = ViewDiscussion.listView_Replies.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionReply> replies = theDatabase.getRepliesForPost(selectedPostId);
		if (index >= replies.size()) return;

		DiscussionReply r = replies.get(index);
		r.setRead(true);
		selectedReplyId = r.getId();

		// Populate the reply body field so the user can edit it
		ViewDiscussion.text_ReplyBody.setText(r.getBody());
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
	protected static void performPickImage(Stage stage) {
		javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
		fc.setTitle("Select Image");
		fc.getExtensionFilters().add(
			new javafx.stage.FileChooser.ExtensionFilter(
				"Image Files", "*.png", "*.jpg", "*.jpeg"));

		java.io.File home = new java.io.File(System.getProperty("user.home"), "Downloads");
		if (home.exists()) fc.setInitialDirectory(home);

		java.io.File chosen = fc.showOpenDialog(stage);
		if (chosen != null) {
			pendingImageFile = chosen;
			ViewDiscussion.label_ImageFile.setText(chosen.getName());
		}
	}


	/*******
	 * <p> Method: performCreatePost() </p>
	 *
	 * <p> Description: Handles the Create Post button. For text posts, validates author, title,
	 * and body then calls saveTextPost(). For image posts, validates author, title, and file
	 * selection then calls saveImagePost(). Refreshes the post list on success. </p>
	 *
	 */
	protected static void performCreatePost() {
		String author = ViewDiscussion.text_Author.getText().trim();
		String title  = ViewDiscussion.text_Title.getText().trim();
		String tags = ViewDiscussion.text_tags.getText().trim();

		String authorErr = ModelDiscussion.validateAuthor(author);
		if (!authorErr.isEmpty()) { setError(authorErr); return; }

		String titleErr = ModelDiscussion.validateTitle(title);
		if (!titleErr.isEmpty()) { setError(titleErr); return; }

		if (ViewDiscussion.radio_Image.isSelected()) {
			// Image post — validate file and write bytes to the database
			String fileErr = ModelDiscussion.validateImageFile(pendingImageFile);
			if (!fileErr.isEmpty()) { setError(fileErr); return; }

			try (FileInputStream fis = new FileInputStream(pendingImageFile)) {
				javafx.scene.image.Image img = new javafx.scene.image.Image(fis);
				int id = theDatabase.saveImagePost(
					author, title, pendingImageFile.getName(), img, tags);
				if (id == -1) { setError("Error: Failed to save image post."); return; }
			} catch (IOException e) {
				setError("Error loading image: " + e.getMessage()); return;
			}
			pendingImageFile = null;
			ViewDiscussion.label_ImageFile.setText("No file selected.");

		} 
		else {
			// Text post — validate body and insert into the posts table
			String body    = ViewDiscussion.text_Body.getText().trim();
			String bodyErr = ModelDiscussion.validateBody(body);
			if (!bodyErr.isEmpty()) { setError(bodyErr); return; }

			int id = theDatabase.saveTextPost(author, title, body, tags);
			if (id == -1) { setError("Error: Failed to save post."); return; }
		}

		clearPostFields();
		setSuccess("Post created successfully!");
		refreshPostList();
	}


	/*******
	 * <p> Method: performUpdatePost() </p>
	 *
	 * <p> Description: Handles the Update Post button. Only valid for text posts — image posts
	 * cannot be updated through this UI. Validates that a text post is selected and that the
	 * title and body fields are not empty, then updates the database record. </p>
	 *
	 */
	protected static void performUpdatePost() {
		if (selectedPostId == -1) { setError("Please select a post to update."); return; }

		// Guard: do not allow updating image posts
		if (ViewDiscussion.radio_Image.isSelected()) {
			setError("Image posts cannot be updated. Delete and re-upload instead.");
			return;
		}

		String author = ViewDiscussion.text_Author.getText().trim();
		String title  = ViewDiscussion.text_Title.getText().trim();
		String body   = ViewDiscussion.text_Body.getText().trim();

		String authorErr = ModelDiscussion.validateAuthor(author);
		if (!authorErr.isEmpty()) { setError(authorErr); return; }

		String titleErr = ModelDiscussion.validateTitle(title);
		if (!titleErr.isEmpty()) { setError(titleErr); return; }

		String bodyErr = ModelDiscussion.validateBody(body);
		if (!bodyErr.isEmpty()) { setError(bodyErr); return; }
		
		String tags = ViewDiscussion.text_tags.getText().trim();
		theDatabase.updatePost(selectedPostId, author, title, body, tags);
		clearPostFields();
		setSuccess("Post updated successfully!");
		refreshPostList();
	}


	/*******
	 * <p> Method: performDeletePost() </p>
	 *
	 * <p> Description: Handles the Delete Post button. Validates that a post is selected,
	 * deletes it from the database (cascade removes its replies), resets both selection
	 * sentinels, clears the reply list, and refreshes the post list. </p>
	 *
	 */
	protected static void performDeletePost() {
		if (selectedPostId == -1) { setError("Please select a post to delete."); return; }
		
		// confirmation
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("confirmation");
        alert.setHeaderText("confirmation");
        alert.setContentText("are you sure you want to delete the post?");

        ButtonType buttonYes = new ButtonType("Yes", ButtonData.YES);
        ButtonType buttonNo = new ButtonType("No", ButtonData.NO);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonNo) {
            return;
        }
		
		theDatabase.deletePost(selectedPostId);
		selectedPostId  = -1;
		selectedReplyId = -1;
		ViewDiscussion.listView_Replies.getItems().clear();
		clearPostFields();
		setSuccess("Post deleted.");
		refreshPostList();
	}


	/*-*******************************************************************************************

	Reply action methods

	**********************************************************************************************/

	/*******
	 * <p> Method: performCreateReply() </p>
	 *
	 * <p> Description: Handles the Create Reply button. Validates that a post is selected and
	 * that the author and reply body fields are not empty, inserts the reply linked to
	 * selectedPostId, and refreshes the reply list. </p>
	 *
	 */
	protected static void performCreateReply() {
		if (selectedPostId == -1) { setError("Please select a post to reply to."); return; }

		String author  = ViewDiscussion.text_Author.getText().trim();
		String body    = ViewDiscussion.text_ReplyBody.getText().trim();

		String authorErr = ModelDiscussion.validateAuthor(author);
		if (!authorErr.isEmpty()) { setError(authorErr); return; }

		String bodyErr = ModelDiscussion.validateBody(body);
		if (!bodyErr.isEmpty()) { setError(bodyErr); return; }

		int id = theDatabase.addReply(selectedPostId, author, body);
		if (id == -1) { setError("Error: Failed to save reply."); return; }

		ViewDiscussion.text_ReplyBody.setText("");
		setSuccess("Reply created successfully!");
		refreshReplyList(selectedPostId);
	}


	/*******
	 * <p> Method: performUpdateReply() </p>
	 *
	 * <p> Description: Handles the Update Reply button. Validates that both a post and a reply
	 * are selected and that the reply body is not empty, then updates the reply record. </p>
	 *
	 */
	protected static void performUpdateReply() {
		if (selectedPostId  == -1) { setError("Please select a post first."); return; }
		if (selectedReplyId == -1) { setError("Please select a reply to update."); return; }

		String body    = ViewDiscussion.text_ReplyBody.getText().trim();
		String bodyErr = ModelDiscussion.validateBody(body);
		if (!bodyErr.isEmpty()) { setError(bodyErr); return; }

		theDatabase.updateReply(selectedReplyId, body);
		ViewDiscussion.text_ReplyBody.setText("");
		setSuccess("Reply updated successfully!");
		refreshReplyList(selectedPostId);
	}


	/*******
	 * <p> Method: performDeleteReply() </p>
	 *
	 * <p> Description: Handles the Delete Reply button. Validates that both a post and a reply
	 * are selected, deletes the reply, resets selectedReplyId, and refreshes the reply list. </p>
	 *
	 */
	protected static void performDeleteReply() {
		if (selectedPostId  == -1) { setError("Please select a post first."); return; }
		if (selectedReplyId == -1) { setError("Please select a reply to delete."); return; }

		theDatabase.deleteReply(selectedReplyId);
		selectedReplyId = -1;
		ViewDiscussion.text_ReplyBody.setText("");
		setSuccess("Reply deleted.");
		refreshReplyList(selectedPostId);
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
	protected static void performBack() {
		if (FoundationsMain.activeHomePage == 1) {
			guiAdminHome.ViewAdminHome.displayAdminHome(
				ViewDiscussion.theStage, ViewDiscussion.theUser);
		} else {
			guiRole1.ViewRole1Home.displayRole1Home(
				ViewDiscussion.theStage, ViewDiscussion.theUser);
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
	private static void setError(String message) {
		ViewDiscussion.label_ErrorMessage.setText(message);
	}


	/*******
	 * <p> Method: setSuccess(String message) </p>
	 *
	 * <p> Description: Sets the status label in ViewDiscussion with a success confirmation
	 * message. Centralises all success reporting through a single call. </p>
	 *
	 * @param message is a String that specifies the success message to display.
	 *
	 */
	private static void setSuccess(String message) {
		ViewDiscussion.label_ErrorMessage.setTextFill(javafx.scene.paint.Color.web("#6bcb77"));
		ViewDiscussion.label_ErrorMessage.setText(message);
	}


	/*******
	 * <p> Method: clearPostFields() </p>
	 *
	 * <p> Description: Clears the post input fields (Author, Title, Body, Tag) and resets the
	 * toggle to TEXT after any successful post CRUD operation. </p>
	 *
	 */
	private static void clearPostFields() {
		ViewDiscussion.text_Author.setText("");
		ViewDiscussion.text_Title.setText("");
		ViewDiscussion.text_Body.setText("");
		ViewDiscussion.radio_Text.setSelected(true);
		ViewDiscussion.label_ImageFile.setText("No file selected.");
		ViewDiscussion.text_tags.setText("");
		pendingImageFile = null;
	}

}
