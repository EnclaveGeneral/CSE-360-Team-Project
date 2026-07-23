package guiMyView;

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
 * <p> Title: ControllerMyViewSecurityTest Class </p>
 *
 * <p> Description: JUnit 5 test validating the CWE-200 fix in
 * ControllerMyView.refreshPostList(): a student's MyView must show only their own posts, never
 * another student's. Placed in package guiMyView (rather than a separate test package) because
 * refreshPostList() is protected, and this is a security-boundary test that needs to call the
 * real method directly rather than test around it. Run against a live H2 instance and the real
 * ControllerMyView/ViewMyView classes. </p>
 *
 * <p> This test requires the JavaFX toolkit to construct real ListView/Label/HBox objects, the
 * same way ControllerMyView and ViewMyView do outside of tests. If the toolkit cannot be
 * initialized in the environment running this test, the test is skipped (via
 * org.junit.jupiter.api.Assumptions) with a clear reason printed, rather than reported as a
 * failure. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack Holtrey (CWE-200: Exposure of Sensitive Information to an Unauthorized Actor)
 *
 * @version 1.00	2026-07-19	Initial test for the refreshPostList() author-scoping fix
 */
class ControllerMyViewSecurityTest {

	private static boolean toolkitAvailable;

	/*******
	 * <p> Method: setUp() </p>
	 *
	 * <p> Description: Connects to the real database and attempts to initialize the JavaFX
	 * toolkit once, before any test runs. </p>
	 *
	 */
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

	/** Checks whether any post row currently shown in MyView carries the given author's name. */
	private static boolean listContainsAuthorLabel(String author) {
		for (Node row : ViewMyView.listView_Posts.getItems()) {
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
	void testOwnPostsAppearInMyView() throws Exception {
		assumeTrue(toolkitAvailable, "JavaFX toolkit unavailable in this environment; skipping.");

		Database db = FoundationsMain.database;
		String alice = "alice" + System.nanoTime();
		String uniqueTag = "myviewsecuritytest" + System.nanoTime();
		db.saveTextPost(alice, "Alice's own question", "body", uniqueTag);

		ViewMyView.theUser = new User(alice, "pw", "A", "", "Lice", "Alice", "a@x.com",
				false, true, false);

		ControllerMyView.refreshPostList();

		assertTrue(listContainsAuthorLabel(alice),
				"A student's own post must appear in their own MyView.");
	}

	@Test
	void testOtherStudentsPostsDoNotAppearInMyView() throws Exception {
		assumeTrue(toolkitAvailable, "JavaFX toolkit unavailable in this environment; skipping.");

		Database db = FoundationsMain.database;
		String alice = "alice" + System.nanoTime();
		String bob = "bob" + System.nanoTime();
		String uniqueTag = "myviewsecuritytest" + System.nanoTime();
		db.saveTextPost(bob, "Bob's own question", "body", uniqueTag);

		ViewMyView.theUser = new User(alice, "pw", "A", "", "Lice", "Alice", "a@x.com",
				false, true, false);

		ControllerMyView.refreshPostList();

		assertFalse(listContainsAuthorLabel(bob),
				"Bob's post must NOT appear in Alice's MyView -- this is the CWE-200 regression "
				+ "this test guards against: refreshPostList() must filter by author.");
	}
}
