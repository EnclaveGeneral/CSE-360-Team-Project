package guiMyView;



import java.util.List;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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
	 * <p> Description: Retrieves all posts from the database and repopulates listView_Posts
	 * with only the posts authored by the currently logged-in user. Text posts are prefixed
	 * with a document icon; image posts with an image icon so the user can distinguish post
	 * types at a glance. Called after any post CRUD operation. </p>
	 *
	 * <p> Author-scoped by design (CWE-200): a previous version of this method called
	 * getAllPosts() with no filter, so MyView showed every student's posts instead of just the
	 * logged-in user's own. The filter below is the fix. </p>
	 *
	 * <p> Tested by guiMyView.ControllerMyViewSecurityTest. </p>
	 *
	 */
	protected static void refreshPostList() {
	    ViewMyView.listView_Posts.getItems().clear();
	    String currentUser = ViewMyView.theUser.getUserName();
	    List<DiscussionPost> posts = theDatabase.getAllPosts();

	    for (DiscussionPost p : posts) {
	        if (!p.getAuthor().equals(currentUser)) continue;

	        String icon = p.isImagePost() ? "\uD83D\uDDBC" : "\uD83D\uDCC4";

	        Label postLabel = new Label(icon + " [" + p.getId() + "] " + p.getTitle() + " — " + p.getAuthor());

	        HBox postBox = new HBox(10);
	        postBox.setPadding(new Insets(5));
	        postBox.getChildren().add(postLabel);

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
		ViewMyView.currentPostId = postId;
		ViewMyView.numReplies = 0;
		ViewMyView.readReplies = 0;
		ViewMyView.listView_Replies.getItems().clear();
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		for (DiscussionReply r : replies) {
			ViewMyView.listView_Replies.getItems().add(
				"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
			if (r.getRead()) {ViewMyView.readReplies++;}
			ViewMyView.numReplies++;
		}
		ViewMyView.newReplies = ViewMyView.numReplies - ViewMyView.readReplies;
	}
	
	/*******
	 * <p> Method: refreshReplyListUnreadOnly(int postId) </p>
	 *
	 * <p> Description: Refreshes the view to only show replies that have not been marked as read. </p>
	 *
	 * @param postId is an int that specifies the unique identifier of the parent post whose
	 *               replies should be loaded.
	 */
	protected static void refreshReplyListUnreadOnly(int postId) {
		ViewMyView.currentPostId = postId;
		ViewMyView.listView_Replies.getItems().clear();
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		for (DiscussionReply r : replies) {
			if (!theDatabase.unreadReply(r.getId())) {
			ViewMyView.listView_Replies.getItems().add(
				"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
			}

		}
	}
	
	/*******
	 * <p> Method: filterByKeyword(int postId, boolean filterByName, String keyword) </p>
	 *
	 * <p> Description: Refreshes the view to only show replies matches the keyword.
	 * this keyword can be used to target the User name or text body. </p>
	 *
	 * @param postId is an int that specifies the unique identifier of the parent post whose
	 *               replies should be loaded.
	 *               
	 * @param filterByName A boolean value which specifies which of the two toggle buttons are selected.
	 * 
	 * @param keyword String key that will be used to filter the messages.
	 */
	protected static void filterByKeyword(int postId, boolean filterByName, String keyword) {
		ViewMyView.currentPostId = postId;
		ViewMyView.listView_Replies.getItems().clear();
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		
		if (filterByName) {
			for (DiscussionReply r : replies) {
				if (r.getAuthor().toLowerCase().equals(keyword.toLowerCase())) {
					ViewMyView.listView_Replies.getItems().add(
						"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
					if (r.getRead()) {ViewMyView.readReplies++;}
					ViewMyView.numReplies++;
				}
			}			
		} else {
			for (DiscussionReply r : replies) {
				if (r.getBody().toLowerCase().contains(keyword.toLowerCase())) {
					ViewMyView.listView_Replies.getItems().add(
						"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
					if (r.getRead()) {ViewMyView.readReplies++;}
					ViewMyView.numReplies++;
				}
			}
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
		int index = ViewMyView.listView_Posts.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionPost> posts = theDatabase.getAllPosts();
		if (index >= posts.size()) return;

		DiscussionPost p = posts.get(index);
		selectedPostId  = p.getId();
		selectedReplyId = -1;

		refreshReplyList(selectedPostId);
	}


	/*******
	 * <p> Method: selectReply() </p>
	 *
	 * <p> Description: Handles the user clicking a reply in listView_Replies. Stores its id
	 * in selectedReplyId and populates the reply body field with its current text. </p>
	 *
	 */
	protected static void selectReply(){
		int index = ViewMyView.listView_Replies.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionReply> replies = theDatabase.getRepliesForPost(selectedPostId);
		if (index >= replies.size()) return;

		DiscussionReply r = replies.get(index);
		selectedReplyId = r.getId();
		theDatabase.updateRead(selectedReplyId);
	}


	/*-*******************************************************************************************

	Post action methods

	**********************************************************************************************/
	
	/*******
	 * <p> Method: launchMyView() </p>
	 *
	 * <p> Description: method that is used to launch the gui of myView in MVC style. </p>
	 *
	 */
	public static void launchMyView() {
		guiMyView.ViewMyView.displayMyView(ViewMyView.theStage, ViewMyView.theUser);
	}


	/*-*******************************************************************************************

	Navigation

	**********************************************************************************************/

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns the user to their own role's home page, based on
	 * FoundationsMain.activeHomePage (Admin: 1; Role1: 2; Role2: 3, set by each role's home
	 * page when it is displayed). </p>
	 *
	 * <p> Previously this method only distinguished activeHomePage == 1 (routed to the
	 * Discussion board) from every other value (routed to Role1 Home regardless of which role
	 * was actually active), so an Admin or Role2 user reaching MyView would be sent to the
	 * wrong home page on Back. Each of the three roles is now routed to its own home page. </p>
	 *
	 */
	protected static void performBack() {
		if (FoundationsMain.activeHomePage == 1) {
			guiAdminHome.ViewAdminHome.displayAdminHome(
				ViewMyView.theStage, ViewMyView.theUser);
		} else if (FoundationsMain.activeHomePage == 3) {
			guiRole2.ViewRole2Home.displayRole2Home(
				ViewMyView.theStage, ViewMyView.theUser);
		} else {
			guiRole1.ViewRole1Home.displayRole1Home(
				ViewMyView.theStage, ViewMyView.theUser);
		}
	}


}
