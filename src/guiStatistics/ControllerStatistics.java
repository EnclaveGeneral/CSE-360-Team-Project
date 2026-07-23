package guiStatistics;

import java.util.List;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import statistics.ParticipationStats;
import statistics.StudentStat;

/*******
 * <p> Title: ControllerStatistics Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the Aggregate Statistics page (TP3 Aspect #4). It fetches the complete discussion board from
 * the database, hands it to the ParticipationStats engine, and renders one row per student in
 * the View's list. All computation lives in the engine; all data access lives in Database; this
 * class only connects the two to the page. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang (TP3 Aspect #4: Aggregate Statistics Engine)
 *
 * @version 1.00	2026-07-23	Initial version wiring the HW3 engine into TP3
 */
public class ControllerStatistics {

	/*-*******************************************************************************************

	Attributes

	**********************************************************************************************/

	private static Database theDatabase = FoundationsMain.database;

	/* The engine is stateless, so a single shared instance serves every refresh. */
	private static ParticipationStats engine = new ParticipationStats();


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: ControllerStatistics() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other controller classes. </p>
	 *
	 */
	public ControllerStatistics() {
	}


	/*-*******************************************************************************************

	List refresh methods

	**********************************************************************************************/

	/*******
	 * <p> Method: refreshStatistics() </p>
	 *
	 * <p> Description: Loads every post and every reply from the database, runs the engine's
	 * computeAll over them, and repopulates the View's list with a header row followed by one
	 * row per student: posts authored, replies authored, distinct students answered, and the
	 * compliance verdict against the three-student rule. </p>
	 *
	 */
	protected static void refreshStatistics() {
		ViewStatistics.listView_Stats.getItems().clear();

		List<DiscussionPost>  posts   = theDatabase.getAllPosts();
		List<DiscussionReply> replies = theDatabase.getAllReplies();
		List<StudentStat>     stats   = engine.computeAll(posts, replies);

		// Alphabetical order gives the grader a stable, scannable roster; computeAll's own
		// ordering follows Set iteration and is not guaranteed stable between runs.
		stats.sort((a, b) -> a.getStudent().compareTo(b.getStudent()));

		ViewStatistics.listView_Stats.getItems().add(makeRow(
			"Student", "Posts", "Replies", "Distinct Answered", "Verdict"));

		for (StudentStat s : stats) {
			ViewStatistics.listView_Stats.getItems().add(makeRow(
				s.getStudent(),
				String.valueOf(s.getPostsAuthored()),
				String.valueOf(s.getRepliesAuthored()),
				String.valueOf(s.getDistinctStudentsAnswered()),
				s.meetsRequirement() ? "MET" : "NOT MET"));
		}
	}

	/*******
	 * <p> Method: makeRow(String name, String posts, String replies, String distinct,
	 * String verdict) </p>
	 *
	 * <p> Description: Builds one fixed-width five-column HBox row for the statistics list. Used
	 * for the header and for every student row so the columns always line up. </p>
	 *
	 * @param name     specifies the text for the student column
	 *
	 * @param posts    specifies the text for the posts-authored column
	 *
	 * @param replies  specifies the text for the replies-authored column
	 *
	 * @param distinct specifies the text for the distinct-students-answered column
	 *
	 * @param verdict  specifies the text for the compliance verdict column
	 *
	 * @return the assembled HBox row
	 *
	 */
	private static HBox makeRow(String name, String posts, String replies,
	                            String distinct, String verdict) {
		Label l1 = new Label(name);     l1.setPrefWidth(200);
		Label l2 = new Label(posts);    l2.setPrefWidth(80);
		Label l3 = new Label(replies);  l3.setPrefWidth(80);
		Label l4 = new Label(distinct); l4.setPrefWidth(160);
		Label l5 = new Label(verdict);  l5.setPrefWidth(100);

		HBox row = new HBox(10);
		row.setPadding(new Insets(5));
		row.getChildren().addAll(l1, l2, l3, l4, l5);
		return row;
	}


	/*-*******************************************************************************************

	Navigation

	**********************************************************************************************/

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns the user to the Grader View page, the page this one is reached
	 * from. </p>
	 *
	 */
	protected static void performBack() {
		guiGraderView.ViewGraderView.displayGraderView(ViewStatistics.theStage, ViewStatistics.theUser);
	}
}
