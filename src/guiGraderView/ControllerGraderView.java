package guiGraderView;

import java.util.List;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/*******
 * <p> Title: ControllerGraderView Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the Grader View page (TP3 Aspect #1: Instructor/Grader Role &amp; Secure Access). It gives the
 * Grader role read access to every student's posts and replies, unlike ControllerMyView, which
 * scopes to the logged-in user, and unlike ControllerDiscussion, which exposes full CRUD to
 * whoever opens it. This class has no create, update, or delete method at all -- the "secure,
 * read-only" requirement is enforced by the class simply not containing the capability, rather
 * than by a role check a future change could accidentally bypass. </p>
 *
 * <p> Selecting a post also surfaces that post's author's answer-coverage status (TP3 Aspect #3:
 * Reply-to-Question Traceability), via Database.countDistinctStudentsAnswered and
 * hasMetAnswerCoverageRequirement, so the Grader can see a student's discussion activity and
 * their coverage standing in one place instead of cross-referencing the Class Roster page
 * separately for every post they read. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack Holtrey (TP3 Aspect #1: Instructor/Grader Role &amp; Secure Access)
 *
 * @version 1.00	2026-07-19	Initial version for TP3
 */
public class ControllerGraderView {

	/*-*******************************************************************************************

	Attributes

	**********************************************************************************************/

	private static Database theDatabase = FoundationsMain.database;


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: ControllerGraderView() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other controller classes. </p>
	 *
	 */
	public ControllerGraderView() {
	}


	/*-*******************************************************************************************

	List refresh methods

	**********************************************************************************************/

	/*******
	 * <p> Method: refreshPostList() </p>
	 *
	 * <p> Description: Retrieves every post from the database -- not scoped to any one student
	 * -- and repopulates listView_Posts. This is the one place in the application that
	 * intentionally shows every student's posts to a single viewer, which is exactly what the
	 * Grader role exists to do. </p>
	 *
	 * <p> Tested by guiGraderView.ControllerGraderViewTest.testAllStudentsPostsAreVisible(). </p>
	 *
	 */
	protected static void refreshPostList() {
		ViewGraderView.listView_Posts.getItems().clear();
		ViewGraderView.label_Coverage.setText("");
		List<DiscussionPost> posts = theDatabase.getAllPosts();

		for (DiscussionPost p : posts) {
			String icon = p.isImagePost() ? "\uD83D\uDDBC" : "\uD83D\uDCC4";
			Label postLabel = new Label(icon + " [" + p.getId() + "] " + p.getTitle() + " — " + p.getAuthor());

			HBox postBox = new HBox(10);
			postBox.setPadding(new Insets(5));
			postBox.getChildren().add(postLabel);

			ViewGraderView.listView_Posts.getItems().add(postBox);
		}
	}

	/*******
	 * <p> Method: selectPost() </p>
	 *
	 * <p> Description: Handles the Grader clicking a post: loads its replies (read-only) and
	 * looks up its author's answer-coverage status via Database.countDistinctStudentsAnswered
	 * and hasMetAnswerCoverageRequirement. </p>
	 *
	 * <p> Tested by guiGraderView.ControllerGraderViewTest.testSelectingPostShowsRepliesAndCoverage(). </p>
	 *
	 */
	protected static void selectPost() {
		int index = ViewGraderView.listView_Posts.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionPost> posts = theDatabase.getAllPosts();
		if (index >= posts.size()) return;

		DiscussionPost p = posts.get(index);
		refreshReplyList(p.getId());

		try {
			int distinctCount = theDatabase.countDistinctStudentsAnswered(p.getAuthor());
			boolean meetsRequirement = theDatabase.hasMetAnswerCoverageRequirement(p.getAuthor());
			ViewGraderView.label_Coverage.setText(
				p.getAuthor() + " has answered " + distinctCount + " distinct student"
				+ (distinctCount == 1 ? "" : "s") + " — "
				+ (meetsRequirement ? "meets requirement" : "does not yet meet requirement"));
		} catch (java.sql.SQLException e) {
			// Coverage lookup failing must not prevent the Grader from reading the post itself;
			// only the coverage line is degraded, and the reason is shown rather than hidden.
			ViewGraderView.label_Coverage.setText("Coverage lookup failed: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: refreshReplyList(int postId) </p>
	 *
	 * <p> Description: Loads every reply for the given post, read-only, into
	 * listView_Replies. </p>
	 *
	 * @param postId is an int that specifies the unique identifier of the post whose replies
	 *               should be loaded.
	 *
	 */
	protected static void refreshReplyList(int postId) {
		ViewGraderView.listView_Replies.getItems().clear();
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		for (DiscussionReply r : replies) {
			ViewGraderView.listView_Replies.getItems().add(
				"[" + r.getId() + "] " + r.getBody() + " \u2014 " + r.getAuthor());
		}
	}


	/*-*******************************************************************************************

	Navigation

	**********************************************************************************************/

	/*******
	 * <p> Method: openClassRoster() </p>
	 *
	 * <p> Description: Navigates to the Class Roster page (TP3 Aspect #7), which shows every
	 * student's coverage status at a glance rather than one post at a time. </p>
	 *
	 */
	protected static void openClassRoster() {
		guiClassRoster.ViewClassRoster.displayClassRoster(ViewGraderView.theStage, ViewGraderView.theUser);
	}

	/*******
	 * <p> Method: openStatistics() </p>
	 *
	 * <p> Description: Navigates to the Aggregate Statistics page (TP3 Aspect #4), which shows
	 * every student's post, reply, and distinct-students-answered counts with the pass/fail
	 * verdict, computed by the statistics.ParticipationStats engine. </p>
	 *
	 */
	protected static void openStatistics() {
		guiStatistics.ViewStatistics.displayStatistics(ViewGraderView.theStage, ViewGraderView.theUser);
	}

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns the Grader to their Role2 home page. </p>
	 *
	 */
	protected static void performBack() {
		guiRole2.ViewRole2Home.displayRole2Home(ViewGraderView.theStage, ViewGraderView.theUser);
	}

	/*******
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the normal login page. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewGraderView.theStage);
	}

	/*******
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the execution of the program. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
