package guiGraderView;

import java.util.List;
import java.util.ArrayList; 
import java.sql.SQLException; 
import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import applicationMain.FoundationsMain;
import guiRole2.ViewRole2Home;
import guiUserLogin.ViewUserLogin;
import guiClassRoster.ViewClassRoster;
import guiStatistics.ViewStatistics;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/*******
 * <p> Title: ControllerGraderView Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the Grader View page (TP3 Aspect 1: Instructor/Grader Role & Secure Access). It gives the
 * Grader role read access to every student's posts and replies, unlike ControllerMyView, which
 * scopes to the logged-in user, and unlike ControllerDiscussion, which exposes full CRUD to
 * whoever opens it. This class has no create, update, or delete method at all -- the "secure,
 * read-only" requirement is enforced by the class simply not containing the capability, rather
 * than by a role check a future change could accidentally bypass. </p>
 *
 * <p> Selecting a post also surfaces that post's author's answer-coverage status (TP3 Aspect 3:
 * Reply-to-Question Traceability) through Database.countDistinctStudentsAnswered and
 * hasMetAnswerCoverageRequirement so the Grader can see a student's discussion activity and
 * their coverage standing in one place instead of referencing the Class Roster page
 * separately for every post they read. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack Holtrey (TP3 Aspect 1: Instructor/Grader Role & Secure Access)
 * @author Katya Patrusheva (TP3 Aspect 5: Filtered Multi-View Grading Interface, Aspect 6: Answer Quality Review Workflow)
 *
 * @version 1.00	2026-07-19	Initial version for TP3
 */
public class ControllerGraderView {

	/*********************************************************************************************

	Attributes

	**********************************************************************************************/

	private static Database theDatabase = FoundationsMain.database;
	private static int selectedPostId  = -1;
    private static int selectedReplyId = -1;
    private static List<Integer> currentReplyIds = new ArrayList<>();


	/*********************************************************************************************

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


	/*********************************************************************************************

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
		
		selectedPostId = p.getId();
		
		refreshReplyList(p.getId());

		try {
			int distinctCount = theDatabase.countDistinctStudentsAnswered(p.getAuthor());
			boolean meetsRequirement = theDatabase.hasMetAnswerCoverageRequirement(p.getAuthor());
			ViewGraderView.label_Coverage.setText(
				p.getAuthor() + " has answered " + distinctCount + " distinct student"
				+ (distinctCount == 1 ? "" : "s") + " — "
				+ (meetsRequirement ? "meets requirement" : "does not yet meet requirement"));
		} catch (SQLException e) {
			// coverage lookup failing must not prevent grader from reading the post itself
			// only coverage line is degraded, the reason is shown rather than hidden
			ViewGraderView.label_Coverage.setText("Coverage lookup failed: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: refreshReplyList(int postId) </p>
	 *
	 * <p> Description: Loads every reply for the given post, read-only, into
	 * listView_Replies. </p>
	 *
	 * @param postId is an int that specifies the unique identifier of the post whose replies should be loaded
	 *
	 */
	protected static void refreshReplyList(int postId) {
		ViewGraderView.listView_Replies.getItems().clear();
		
		currentReplyIds.clear();
		
		List<DiscussionReply> replies = theDatabase.getRepliesForPost(postId);
		
		for (DiscussionReply r : replies) {
			
			currentReplyIds.add(r.getId());		// store ID in background
			
			String qualityStatus = r.getQuality() ? "[VALID]" : "[INVALID]";
			
			// string displayed to grader with no visible ID
			ViewGraderView.listView_Replies.getItems().add(
				qualityStatus + " " + r.getBody() + " \u2014 " + r.getAuthor());
		}
	}


	/*********************************************************************************************

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
		ViewClassRoster.displayClassRoster(ViewGraderView.theStage, ViewGraderView.theUser);
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
		ViewStatistics.displayStatistics(ViewGraderView.theStage, ViewGraderView.theUser);
	}

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns the Grader to their Role2 home page. </p>
	 *
	 */
	protected static void performBack() {
		ViewRole2Home.displayRole2Home(ViewGraderView.theStage, ViewGraderView.theUser);
	}

	/*******
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the normal login page. </p>
	 *
	 */
	protected static void performLogout() {
		ViewUserLogin.displayUserLogin(ViewGraderView.theStage);
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
	
	/*********************************************************************************************

	Grader Page Filter methods

	**********************************************************************************************/
	
	/*******
	 * <p> Method: applyFilter </p>
	 *
	 * <p> Description: Evaluates the currently selected filter type and the specific student 
	 * username, clearing the current discussion views and filling them based on the 
	 * selected criteria. This satisfies TP3 Aspect 5, the filtered multiview grading interface, 
	 * by giving the instructional team specific views of the discussion board (all posts in chronological order, 
	 * specific student's posts, or all replies to a specific student's posts). </p>
	 *
	 */
	protected static void applyFilter() {
	    String filterType = ViewGraderView.combobox_FilterType.getValue();
	    String evaluatedStudent = ViewGraderView.text_EvaluateStudent.getText().trim();
	    
	    ViewGraderView.listView_Posts.getItems().clear();
	    ViewGraderView.listView_Replies.getItems().clear();
	    ViewGraderView.label_Coverage.setText("");
	    ViewGraderView.label_QualityStatus.setText("Quality Evaluation: Select a reply...");
	    selectedPostId = -1;
	    selectedReplyId = -1;

	    if (filterType.equals("Chronological Order (All)")) {
	        refreshPostList();
	    } 
	    else if (filterType.equals("Student Posts")) {
	        if (evaluatedStudent.isEmpty()) {
	        	return;
	        }
	        
	        List<DiscussionPost> posts = theDatabase.getStudentPosts(evaluatedStudent);
	        
	        // display error message if non-existent student or no posts exist
	        if (posts.isEmpty()) {
	            HBox errorBox = new HBox(new Label("Error: Student not found or no posts exist"));
	            ViewGraderView.listView_Posts.getItems().add(errorBox);
	            return;
	        }
	        
	        for (DiscussionPost p : posts) {
	            String icon = p.isImagePost() ? "\uD83D\uDDBC" : "\uD83D\uDCC4";
	            Label postLabel = new Label(icon + " " + p.getTitle() + " — " + p.getAuthor());
	            HBox postBox = new HBox(10);
	            postBox.getChildren().add(postLabel);
	            ViewGraderView.listView_Posts.getItems().add(postBox);
	        }
	    } 
	    else if (filterType.equals("Replies to Student Posts")) {
	        if (evaluatedStudent.isEmpty()) {
	        	return;
	        }
	        
	        // uses multitable JOIN method
	        List<DiscussionReply> replies = theDatabase.getStudentReplies(evaluatedStudent);
	        
	        // display error message if non-existent student or no replies exist
	        if (replies.isEmpty()) {
	            ViewGraderView.listView_Replies.getItems().add("Error: Student not found or no replies exist");
	            return;
	        }
	        
	        currentReplyIds.clear();
	        
	        for (DiscussionReply r : replies) {
	        	
	        	currentReplyIds.add(r.getId());		// store ID in background, no need to display to grader
	        	
	            String status = r.getQuality() ? "[VALID]" : "[INVALID]";
	            
	            ViewGraderView.listView_Replies.getItems().add(
	                status + " (Post " + r.getPostId() + ") " + r.getBody() + " \u2014 " + r.getAuthor());
	        }
	    }
	}
	
	
	/*********************************************************************************************

	Answer Quality Review methods

	**********************************************************************************************/
	
	/*******
	 * <p> Method: selectReply </p>
	 *
	 * <p> Description: Event handler used when user clicks a reply in the displayed list. 
	 * Takes the reply ID through row index, saves it, and reflects 
	 * reply's current quality status (valid/invalid) </p>
	 *
	 */
	protected static void selectReply() {
		
		int index = ViewGraderView.listView_Replies.getSelectionModel().getSelectedIndex();
	    if (index == -1 || index >= currentReplyIds.size()) {
	    	return;
	    }

	    // take ID by its row index
	    selectedReplyId = currentReplyIds.get(index);

	    String selectedString = ViewGraderView.listView_Replies.getItems().get(index);
	    if (selectedString.contains("[VALID]")) {
	        ViewGraderView.label_QualityStatus.setText("Quality Evaluation: VALID");
	    }
	    else {
	        ViewGraderView.label_QualityStatus.setText("Quality Evaluation: INVALID");
	    }
	}

	/*******
	 * <p> Method: toggleQuality </p>
	 *
	 * <p> Description: Switches or toggles the boolean quality flag of the currently selected reply 
	 * between valid & invalid. Refreshes the list view to display the updated quality status tag. </p>
	 *
	 */
	protected static void toggleQuality() {
	    if (selectedReplyId == -1) {
	    	return;
	    }

	    boolean isCurrentlyValid = ViewGraderView.label_QualityStatus.getText().contains("VALID") && 
	                              !ViewGraderView.label_QualityStatus.getText().contains("IN");
	    
	    boolean newStatus = !isCurrentlyValid;
	    theDatabase.updateAnswerQuality(selectedReplyId, newStatus);
	    
	    // update display to grader
	    ViewGraderView.label_QualityStatus.setText("Quality Evaluation: " + (newStatus ? "VALID" : "INVALID"));
	    
	    // update lists to show new tag visually on the item
	    if (ViewGraderView.combobox_FilterType.getValue().equals("Replies to Student's Posts")) {
	        applyFilter();						// refresh the multi-view list
	    }
	    else if (selectedPostId != -1) {
	        refreshReplyList(selectedPostId); 	// refresh standard reply list
	    }
	}
	
}
