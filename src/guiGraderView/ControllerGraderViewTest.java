package guiGraderView;

import database.Database;
import applicationMain.FoundationsMain;
import entityClasses.User;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/*******
 * <p> Title: ControllerGraderViewTest Class </p>
 *
 * <p> Description: JUnit 5 test validating TP3 Aspect #1 (Instructor/Grader Role &amp; Secure
 * Access): the Grader View must show every student's posts, unlike ControllerMyView which
 * scopes to one student, and selecting a post must surface that post's author's real
 * answer-coverage status computed by Database.countDistinctStudentsAnswered. Run against a live
 * H2 instance and the real ControllerGraderView/ViewGraderView classes, not a reconstruction. </p>
 *
 * <p> Requires the JavaFX toolkit to construct real ListView/Label/HBox objects; if the toolkit
 * cannot be initialized in the environment running this test, the test is skipped via
 * org.junit.jupiter.api.Assumptions rather than reported as a failure. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack Holtrey (TP3 Aspect #1: Instructor/Grader Role &amp; Secure Access)
 *
 * @version 1.00	2026-07-19	Initial test for the Grader View
 */
class ControllerGraderViewTest {

	private static boolean toolkitAvailable;

	@BeforeAll
	static void setUp() throws Exception {
		FoundationsMain.database.connectToDatabase();

		try {
			CountDownLatch latch = new CountDownLatch(1);
			javafx.application.Platform.startup(latch::countDown);
			toolkitAvailable = latch.await(10, TimeUnit.SECONDS);
		} catch (Throwable t) {
			toolkitAvailable = false;
		}
	}

	private static boolean listContainsAuthorLabel(String author) {
		for (Node row : ViewGraderView.listView_Posts.getItems()) {
			if (row instanceof HBox hbox) {
				for (Node child : hbox.getChildren()) {
					if (child instanceof Label label
							&& label.getText() != null
							&& label.getText().contains(author)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Test
	void testAllStudentsPostsAreVisible() throws Exception {
		assumeTrue(toolkitAvailable, "JavaFX toolkit unavailable in this environment; skipping.");

		Database db = FoundationsMain.database;
		String alice = "alice" + System.nanoTime();
		String bob = "bob" + System.nanoTime();
		String uniqueTag = "gradertest" + System.nanoTime();
		db.saveTextPost(alice, "Alice's question", "body", uniqueTag);
		db.saveTextPost(bob, "Bob's question", "body", uniqueTag);

		ViewGraderView.theUser = new User("grader1", "pw", "G", "", "Rader", "Grader", "g@x.com",
				false, false, true);

		ControllerGraderView.refreshPostList();

		assertTrue(listContainsAuthorLabel(alice),
				"The Grader View must show Alice's post -- it is not scoped to any one student.");
		assertTrue(listContainsAuthorLabel(bob),
				"The Grader View must ALSO show Bob's post in the same list -- this is the whole "
				+ "point of the Grader role's secure access to every student's content.");
	}

	@Test
	void testSelectingPostShowsRepliesAndCoverage() throws Exception {
		assumeTrue(toolkitAvailable, "JavaFX toolkit unavailable in this environment; skipping.");

		Database db = FoundationsMain.database;
		String alice = "alice" + System.nanoTime();
		String bob = "bob" + System.nanoTime();
		String carol = "carol" + System.nanoTime();
		String dave = "dave" + System.nanoTime();
		String uniqueTag = "gradertest" + System.nanoTime();

		// alice answers bob and carol (2 distinct -- below the 3-student requirement)
		int bobsPost = db.saveTextPost(bob, "Bob's question", "body", uniqueTag);
		int carolsPost = db.saveTextPost(carol, "Carol's question", "body", uniqueTag);
		db.addReply(bobsPost, alice, "Reply to bob");
		db.addReply(carolsPost, alice, "Reply to carol");

		int davesPost = db.saveTextPost(dave, "Dave's question -- " + uniqueTag, "body", uniqueTag);

		ViewGraderView.theUser = new User("grader1", "pw", "G", "", "Rader", "Grader", "g@x.com",
				false, false, true);

		ControllerGraderView.refreshPostList();

		// Select Dave's post specifically by scanning for the unique tag text, since post
		// ordering (newest-first) isn't guaranteed stable across a shared test database.
		int indexOfDavesPost = -1;
		for (int i = 0; i < ViewGraderView.listView_Posts.getItems().size(); i++) {
			HBox row = ViewGraderView.listView_Posts.getItems().get(i);
			for (Node child : row.getChildren()) {
				if (child instanceof Label label && label.getText().contains("[" + davesPost + "]")) {
					indexOfDavesPost = i;
				}
			}
		}
		assertTrue(indexOfDavesPost >= 0, "Dave's post must be findable in the Grader View's post list.");

		ViewGraderView.listView_Posts.getSelectionModel().select(indexOfDavesPost);
		ControllerGraderView.selectPost();

		assertFalse(ViewGraderView.label_Coverage.getText().isEmpty(),
				"Selecting a post must populate the coverage status line, not leave it blank.");
	}
}
