package guiDiscussion;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import applicationMain.FoundationsMain;

/*******
 * <p> Title: ControllerDiscussion Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the Discussion Board page. It handles all user interactions from ViewDiscussion, performs
 * input validation, delegates database operations to the Database class, and refreshes the
 * View after each operation. It manages CRUD operations for both posts and replies, and
 * tracks which post and reply are currently selected in the ListViews. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 2.00	2026-06-23
 */
public class ControllerDiscussion {

	/*-*******************************************************************************************

	Attributes

	**********************************************************************************************/

	// Tracks which post is currently selected in listView_Posts (-1 means nothing selected)
	private static int selectedPostId = -1;

	// Tracks which reply is currently selected in listView_Replies (-1 means nothing selected)
	private static int selectedReplyId = -1;

	// Reference to the shared database singleton established in FoundationsMain
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
	 * <p> Description: Retrieves all posts from the database and repopulates the listView_Posts
	 * widget in ViewDiscussion. Each post is displayed as "[postId] title - authorUsername".
	 * Called after any CRUD operation on posts to keep the list current. </p>
	 *
	 */
	protected static void refreshPostList() {
		try {
			List<Post> posts = theDatabase.getAllPosts();

			// Clear the existing list before repopulating to avoid duplicate entries
			ViewDiscussion.listView_Posts.getItems().clear();

			for (Post p : posts) {
				ViewDiscussion.listView_Posts.getItems().add(
					"[" + p.getPostId() + "] " + p.getTitle() + " - " + p.getAuthorUsername()
				);
			}
		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error loading posts: " + e.getMessage());
		}
	}


	/*******
	 * <p> Method: refreshReplyList(int postId) </p>
	 *
	 * <p> Description: Retrieves all replies belonging to the specified post from the database
	 * and repopulates the listView_Replies widget in ViewDiscussion. Each reply is displayed
	 * as "[replyId] body - authorUsername". Called after any CRUD operation on replies, and
	 * when a new post is selected. </p>
	 *
	 * @param postId is an int that specifies the unique identifier of the parent post whose
	 * 		replies should be loaded.
	 *
	 */
	protected static void refreshReplyList(int postId) {
		try {
			List<Reply> replies = theDatabase.getReplyByPostId(postId);

			// Clear the existing list before repopulating to avoid duplicate entries
			ViewDiscussion.listView_Replies.getItems().clear();

			for (Reply reply : replies) {
				ViewDiscussion.listView_Replies.getItems().add(
					"[" + reply.getReplyId() + "] " + reply.getBody() + " - " + reply.getAuthorUsername()
				);
			}
		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error loading replies: " + e.getMessage());
		}
	}


	/*-*******************************************************************************************

	Selection methods

	**********************************************************************************************/

	/*******
	 * <p> Method: selectPost() </p>
	 *
	 * <p> Description: Handles the user clicking a post in listView_Posts. Retrieves the
	 * selected post from the database using the list index, stores its postId in selectedPostId,
	 * populates the post input fields with the post's data, and calls refreshReplyList() to
	 * load the replies for the selected post. </p>
	 *
	 */
	protected static void selectPost() {

		// Get the index of the selected item — returns -1 if nothing is selected
		int index = ViewDiscussion.listView_Posts.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		try {
			List<Post> posts = theDatabase.getAllPosts();

			Post selectedPost = posts.get(index);

			// Store the postId so subsequent CRUD operations know which post to act on
			selectedPostId = selectedPost.getPostId();

			// Populate the input fields with the selected post's current data
			ViewDiscussion.text_Title.setText(selectedPost.getTitle());
			ViewDiscussion.text_Author.setText(selectedPost.getAuthorUsername());
			ViewDiscussion.text_Body.setText(selectedPost.getBody());
			ViewDiscussion.check_IsPinned.setSelected(selectedPost.getIsPinned());

			// Load the replies for this post into the reply ListView
			refreshReplyList(selectedPostId);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error selecting post: " + e.getMessage());
		}
	}


	/*******
	 * <p> Method: selectReply() </p>
	 *
	 * <p> Description: Handles the user clicking a reply in listView_Replies. Retrieves the
	 * selected reply from the database using the list index, stores its replyId in
	 * selectedReplyId, and populates the reply input fields with the reply's data. </p>
	 *
	 */
	protected static void selectReply() {

		// Get the index of the selected item — returns -1 if nothing is selected
		int index = ViewDiscussion.listView_Replies.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		try {
			// Use getReplyByPostId to ensure the index matches what is shown in the ListView
			List<Reply> replies = theDatabase.getReplyByPostId(selectedPostId);

			Reply selectedReply = replies.get(index);

			// Store the replyId so subsequent CRUD operations know which reply to act on
			selectedReplyId = selectedReply.getReplyId();

			// Populate the reply input fields with the selected reply's current data
			ViewDiscussion.text_ReplyBody.setText(selectedReply.getBody());
			ViewDiscussion.check_IsAccepted.setSelected(selectedReply.getIsAccepted());

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error selecting reply: " + e.getMessage());
		}
	}


	/*-*******************************************************************************************

	Post CRUD action methods

	**********************************************************************************************/

	/*******
	 * <p> Method: performCreatePost() </p>
	 *
	 * <p> Description: Handles the Create Post button action. Validates that the title and body
	 * fields are not empty, creates a new Post object with a generated timestamp, inserts it
	 * into the database, refreshes the post list, and clears the input fields. The postId is
	 * set to 0 since the database auto-generates it via AUTO_INCREMENT. </p>
	 *
	 */
	protected static void performCreatePost() {

		// Validate that the title field is not empty before attempting to create a new Post 
		String titleError = ModelDiscussion.validatePostTitle(ViewDiscussion.text_Title.getText().trim());
		if (!titleError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(titleError);
			return; 
		}

		// Validate that the body field is not empty before attempting to create a new Post 
		String bodyError = ModelDiscussion.validateBody(ViewDiscussion.text_Body.getText().trim());
		if (!bodyError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(bodyError);
			return;
		}
		
		// Validate that the author field is not empty before attempting to create a new Post 
		String authorError = ModelDiscussion.validateAuthor(ViewDiscussion.text_Author.getText().trim());
		if (!authorError.isEmpty()) {
		    ViewDiscussion.label_ErrorMessage.setText(authorError);
		    return;
		}

		// postId is 0 because the database auto-generates the actual ID via AUTO_INCREMENT
		Post newPost = new Post(0,
				ViewDiscussion.text_Author.getText().trim(),
				ViewDiscussion.text_Title.getText().trim(),
				ViewDiscussion.text_Body.getText().trim(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
				ViewDiscussion.check_IsPinned.isSelected());

		try {
			theDatabase.createPost(newPost);
			refreshPostList();

			ViewDiscussion.label_ErrorMessage.setText("Post created successfully!");

			// Clear the input fields after a successful create operation
			ViewDiscussion.text_Title.setText("");
			ViewDiscussion.text_Body.setText("");
			ViewDiscussion.text_Author.setText("");
			ViewDiscussion.check_IsPinned.setSelected(false);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error creating post: " + e.getMessage());
		}
	}


	/*******
	 * <p> Method: performUpdatePost() </p>
	 *
	 * <p> Description: Handles the Update Post button action. Validates that a post is selected
	 * and that the title and body fields are not empty, creates an updated Post object using
	 * the selectedPostId, updates the database record, and refreshes the post list. </p>
	 *
	 */
	protected static void performUpdatePost() {

		// A post must be selected before an update can be performed
		if (selectedPostId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a post to update.");
			return;
		}

		// Validate that the title field is not empty before attempting to update a Post
		String titleError = ModelDiscussion.validatePostTitle(ViewDiscussion.text_Title.getText().trim());
		if (!titleError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(titleError);
			return; 
		}

		// Validate that the body field is not empty before attempting to update a Post
		String bodyError = ModelDiscussion.validateBody(ViewDiscussion.text_Body.getText().trim());
		if (!bodyError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(bodyError);
			return;
		}
		
		// Validate that the author field is not empty before attempting to update a Post
		String authorError = ModelDiscussion.validateAuthor(ViewDiscussion.text_Author.getText().trim());
		if (!authorError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(authorError);
			return; 
		}

		// Use selectedPostId to ensure the correct database record is updated
		Post updatedPost = new Post(selectedPostId,
				ViewDiscussion.text_Author.getText().trim(),
				ViewDiscussion.text_Title.getText().trim(),
				ViewDiscussion.text_Body.getText().trim(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
				ViewDiscussion.check_IsPinned.isSelected());

		try {
			theDatabase.updatePost(updatedPost);
			refreshPostList();

			ViewDiscussion.label_ErrorMessage.setText("Post updated successfully!");

			// Clear the input fields after a successful update operation
			ViewDiscussion.text_Title.setText("");
			ViewDiscussion.text_Body.setText("");
			ViewDiscussion.text_Author.setText("");
			ViewDiscussion.check_IsPinned.setSelected(false);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error updating post: " + e.getMessage());
		}
	}


	/*******
	 * <p> Method: performDeletePost() </p>
	 *
	 * <p> Description: Handles the Delete Post button action. Validates that a post is selected,
	 * deletes the post from the database, resets selectedPostId to -1, clears the reply list
	 * since the parent post no longer exists, and refreshes the post list. </p>
	 *
	 */
	protected static void performDeletePost() {

		// A post must be selected before a delete can be performed
		if (selectedPostId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a post to delete.");
			return;
		}

		try {
			theDatabase.deletePost(selectedPostId);

			// Reset the selection state since the selected post no longer exists
			selectedPostId = -1;

			// Clear the reply list since replies belonged to the now-deleted post
			ViewDiscussion.listView_Replies.getItems().clear();

			refreshPostList();

			ViewDiscussion.label_ErrorMessage.setText("Post deleted successfully!");

			// Clear the input fields after a successful delete operation
			ViewDiscussion.text_Title.setText("");
			ViewDiscussion.text_Body.setText("");
			ViewDiscussion.text_Author.setText("");
			ViewDiscussion.check_IsPinned.setSelected(false);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error deleting post: " + e.getMessage());
		}
	}


	/*-*******************************************************************************************

	Reply CRUD action methods

	**********************************************************************************************/

	/*******
	 * <p> Method: performCreateReply() </p>
	 *
	 * <p> Description: Handles the Create Reply button action. Validates that a post is selected
	 * and that the reply body field is not empty, creates a new Reply object linked to the
	 * selected post via selectedPostId, inserts it into the database, and refreshes the reply
	 * list. The replyId is set to 0 since the database auto-generates it via AUTO_INCREMENT. </p>
	 *
	 */
	protected static void performCreateReply() {

		// A post must be selected before a reply can be created
		if (selectedPostId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a post to reply to.");
			return;
		}

		// Validate that the reply body field is not empty before attempting to create
		String bodyError = ModelDiscussion.validateBody(ViewDiscussion.text_ReplyBody.getText().trim());
		if (!bodyError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(bodyError);
			return; 
		}

		// replyId is 0 because the database auto-generates the actual ID via AUTO_INCREMENT
		Reply newReply = new Reply(0,
				selectedPostId,
				ViewDiscussion.text_Author.getText().trim(),
				ViewDiscussion.text_ReplyBody.getText().trim(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
				ViewDiscussion.check_IsAccepted.isSelected());

		try {
			theDatabase.createReply(newReply);
			refreshReplyList(selectedPostId);

			ViewDiscussion.label_ErrorMessage.setText("Reply created successfully!");

			// Clear the reply input fields after a successful create operation
			ViewDiscussion.text_ReplyBody.setText("");
			ViewDiscussion.check_IsAccepted.setSelected(false);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error creating reply: " + e.getMessage());
		}
	}


	/*******
	 * <p> Method: performUpdateReply() </p>
	 *
	 * <p> Description: Handles the Update Reply button action. Validates that both a post and
	 * a reply are selected and that the reply body field is not empty, creates an updated Reply
	 * object using the selectedReplyId and selectedPostId, updates the database record, and
	 * refreshes the reply list. </p>
	 *
	 */
	protected static void performUpdateReply() {

		// A post must be selected before a reply can be updated
		if (selectedPostId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a post first.");
			return;
		}

		// A reply must be selected before an update can be performed
		if (selectedReplyId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a reply to update.");
			return;
		}

		// Validate that the reply body field is not empty before attempting to update
		String bodyError = ModelDiscussion.validateBody(ViewDiscussion.text_ReplyBody.getText().trim());
		if (!bodyError.isEmpty()) {
			ViewDiscussion.label_ErrorMessage.setText(bodyError);
			return; 
		}

		// Use selectedReplyId and selectedPostId to ensure the correct record is updated
		Reply updatedReply = new Reply(selectedReplyId,
				selectedPostId,
				ViewDiscussion.text_Author.getText().trim(),
				ViewDiscussion.text_ReplyBody.getText().trim(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
				ViewDiscussion.check_IsAccepted.isSelected());

		try {
			theDatabase.updateReply(updatedReply);
			refreshReplyList(selectedPostId);

			ViewDiscussion.label_ErrorMessage.setText("Reply updated successfully!");

			// Clear the reply input fields after a successful update operation
			ViewDiscussion.text_ReplyBody.setText("");
			ViewDiscussion.check_IsAccepted.setSelected(false);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error updating reply: " + e.getMessage());
		}
	}


	/*******
	 * <p> Method: performDeleteReply() </p>
	 *
	 * <p> Description: Handles the Delete Reply button action. Validates that both a post and
	 * a reply are selected, deletes the reply from the database, resets selectedReplyId to -1,
	 * and refreshes the reply list. </p>
	 *
	 */
	protected static void performDeleteReply() {

		// A post must be selected before a reply can be deleted
		if (selectedPostId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a post first.");
			return;
		}

		// A reply must be selected before a delete can be performed
		if (selectedReplyId == -1) {
			ViewDiscussion.label_ErrorMessage.setText("Error: Please select a reply to delete.");
			return;
		}

		try {
			theDatabase.deleteReply(selectedReplyId);

			// Reset the selection state since the selected reply no longer exists
			selectedReplyId = -1;

			refreshReplyList(selectedPostId);

			ViewDiscussion.label_ErrorMessage.setText("Reply deleted successfully!");

			// Clear the reply input fields after a successful delete operation
			ViewDiscussion.text_ReplyBody.setText("");
			ViewDiscussion.check_IsAccepted.setSelected(false);

		} catch (SQLException e) {
			ViewDiscussion.label_ErrorMessage.setText("Error deleting reply: " + e.getMessage());
		}
	}


	/*-*******************************************************************************************

	Navigation methods

	**********************************************************************************************/

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Handles the Back button action. Navigates the user back to the Role1
	 * home page by calling displayRole1Home() with the current stage and username. </p>
	 *
	 */
	protected static void performBack() {
		// Navigate back to the Role1 home page passing the current stage and logged-in username
		guiRole1.ViewRole1Home.displayRole1Home(ViewDiscussion.theStage,
				guiRole1.ViewRole1Home.theUser);
	}
	
	/*-*******************************************************************************************

	Image methods

	**********************************************************************************************/
	
	/*******
	 * <p> Method: performAddImage(Stage stage) </p>
	 *
	 * <p> Description: Opens a FileChooser for the user to select an image, saves it to
	 * the database via saveImageEntry, and refreshes the image grid. </p>
	 *
	 * @param stage is the current JavaFX Stage needed for the FileChooser dialog.
	 *
	 */
	protected static void performAddImage(javafx.stage.Stage stage) {
	    javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
	    fc.setTitle("Select Image");
	    fc.getExtensionFilters().add(
	        new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
	    java.io.File home = new java.io.File(System.getProperty("user.home"), "Downloads");
	    if (home.exists()) fc.setInitialDirectory(home);

	    java.io.File selected = fc.showOpenDialog(stage);
	    if (selected != null) {
	        try (java.io.FileInputStream fis = new java.io.FileInputStream(selected)) {
	            javafx.scene.image.Image img = new javafx.scene.image.Image(fis);
	            java.util.ArrayList<guiForum.comment> emptyComments = new java.util.ArrayList<>();
	            theDatabase.saveImageEntry(
	                ViewDiscussion.theUser.getUserName(),
	                selected.getName(),
	                img,
	                emptyComments,
	                0, 0
	            );
	            refreshImageGrid();
	        } catch (java.io.IOException e) {
	            ViewDiscussion.label_ErrorMessage.setText("Error loading image: " + e.getMessage());
	        }
	    }
	}

	/*******
	 * <p> Method: refreshImageGrid() </p>
	 *
	 * <p> Description: Loads all image entries from the database and rebuilds the image
	 * grid in the ViewDiscussion scroll pane. Each image shows as a clickable thumbnail
	 * with filename and uploader beneath it, plus a Delete button. </p>
	 *
	 */
	protected static void refreshImageGrid() {
	    ViewDiscussion.grid_Images.getChildren().clear();

	    java.util.HashMap<guiForum.png, java.util.ArrayList<guiForum.comment>> data =
	        theDatabase.loadImageEntries();
	    data.remove(null);

	    int col = 0, row = 0;
	    for (java.util.Map.Entry<guiForum.png, java.util.ArrayList<guiForum.comment>> entry
	            : data.entrySet()) {
	        final guiForum.png currentPng = entry.getKey();

	        javafx.scene.image.ImageView iv =
	            new javafx.scene.image.ImageView(currentPng.get_pic());
	        iv.setFitWidth(80);
	        iv.setFitHeight(80);
	        iv.setPreserveRatio(true);

	        javafx.scene.control.Label info =
	            new javafx.scene.control.Label(
	                currentPng.get_filename() + "\n" + currentPng.get_user());
	        info.setWrapText(true);
	        info.setStyle("-fx-font-size: 11px;");

	        javafx.scene.control.Button btnImg = new javafx.scene.control.Button();
	        btnImg.setGraphic(iv);
	        btnImg.setOnAction(e ->
	            guiComment.ViewComment.displayComment(
	                ViewDiscussion.theStage, ViewDiscussion.theUser, currentPng));

	        javafx.scene.control.Button btnDel = new javafx.scene.control.Button("Delete");
	        btnDel.setStyle("-fx-font-size: 11px;");
	        btnDel.setOnAction(e -> {
	            theDatabase.deleteImageEntry(currentPng.get_filename());
	            refreshImageGrid();
	        });

	        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(4,
	            btnImg, info, btnDel);
	        vbox.setAlignment(javafx.geometry.Pos.CENTER);

	        ViewDiscussion.grid_Images.add(vbox, col, row);
	        col++;
	        if (col == 4) { col = 0; row++; }
	    }
	}

}