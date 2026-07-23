package gradingSupportTests;

import database.Database;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: AnswerCoverageDatabaseTest Class </p>
 *
 * <p> Description: JUnit 5 tests for Database.countDistinctStudentsAnswered and
 * Database.hasMetAnswerCoverageRequirement (TP3 Aspect #3: Reply-to-Question Traceability).
 * Run directly against a live H2 instance and the real posts/replies tables, not a
 * reconstruction, so a passing suite here demonstrates the production method works, not just
 * that a standalone prototype did. Each test uses a uniquely tagged/named set of posts and
 * replies (via System.nanoTime()) so repeated runs never collide with data left behind by a
 * previous run. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack Holtrey (TP3 Aspect #3: Reply-to-Question Traceability)
 *
 * @version 1.00	2026-07-19	Initial production test suite, ported from the HW3 prototype
 */
class AnswerCoverageDatabaseTest {

	private static Database db;

	/*******
	 * <p> Method: setUpDatabase() </p>
	 *
	 * <p> Description: Connects once to the real H2 database before any test runs. </p>
	 *
	 */
	@BeforeAll
	static void setUpDatabase() throws Exception {
		db = new Database();
		db.connectToDatabase();
	}

	/** Builds a fresh, uniquely-tagged post and returns its id. */
	private static int makePost(String author) {
		String tag = "coveragetest" + System.nanoTime();
		return db.saveTextPost(author, "Question by " + author, "Body text.", tag);
	}

	/** Builds a fresh reply to postId, authored by author, and returns its id. */
	private static int makeReply(int postId, String author) {
		return db.addReply(postId, author, "Reply by " + author + ".");
	}

	/** A fresh, collision-free username for a single test run. */
	private static String freshUser(String base) {
		return base + System.nanoTime();
	}

	// ---- countDistinctStudentsAnswered: boundary cases around the pass/fail threshold ----

	@Test
	void testZeroReplies() throws Exception {
		String alice = freshUser("alice");
		assertEquals(0, db.countDistinctStudentsAnswered(alice),
				"A student with zero replies must count zero distinct students answered.");
	}

	@Test
	void testTwoDistinctBelowThreshold() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");
		String carol = freshUser("carol");

		makeReply(makePost(bob), alice);
		makeReply(makePost(carol), alice);

		assertEquals(2, db.countDistinctStudentsAnswered(alice),
				"Replying to 2 distinct students must count as 2, one below the pass/fail line.");
	}

	@Test
	void testExactlyThreeDistinct() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");
		String carol = freshUser("carol");
		String dave = freshUser("dave");

		makeReply(makePost(bob), alice);
		makeReply(makePost(carol), alice);
		makeReply(makePost(dave), alice);

		assertEquals(3, db.countDistinctStudentsAnswered(alice),
				"Exactly 3 distinct students is the pass/fail boundary itself.");
	}

	@Test
	void testFourDistinctAboveThreshold() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");
		String carol = freshUser("carol");
		String dave = freshUser("dave");
		String erin = freshUser("erin");

		makeReply(makePost(bob), alice);
		makeReply(makePost(carol), alice);
		makeReply(makePost(dave), alice);
		makeReply(makePost(erin), alice);

		assertEquals(4, db.countDistinctStudentsAnswered(alice),
				"4 distinct students must count as 4; there is no artificial cap at the threshold.");
	}

	@Test
	void testDuplicateRepliesToSameStudentCountOnce() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");

		int bobsPost = makePost(bob);
		makeReply(bobsPost, alice);
		makeReply(makePost(bob), alice); // a second post by the same bob, replied to again

		assertEquals(1, db.countDistinctStudentsAnswered(alice),
				"Two replies to the same student's posts must still count as 1 distinct student, "
				+ "not 2 -- COUNT(DISTINCT ...) is what makes this correct rather than COUNT(*).");
	}

	@Test
	void testSelfReplyExcluded() throws Exception {
		String alice = freshUser("alice");

		makeReply(makePost(alice), alice);

		assertEquals(0, db.countDistinctStudentsAnswered(alice),
				"Replying to your own post must not count as answering a different student.");
	}

	@Test
	void testReplyToDeletedPostExcluded() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");

		int bobsPost = makePost(bob);
		makeReply(bobsPost, alice);
		db.deletePost(bobsPost); // cascade-deletes the reply along with its parent post

		assertEquals(0, db.countDistinctStudentsAnswered(alice),
				"A reply whose parent post has been deleted must not count -- proven here by "
				+ "actually deleting the post, not by injecting an artificial invalid postId.");
	}

	@Test
	void testNullUsernameReturnsZero() throws Exception {
		assertEquals(0, db.countDistinctStudentsAnswered(null),
				"A null username must return 0 instead of throwing (A10, CWE-476).");
	}

	@Test
	void testBlankUsernameReturnsZero() throws Exception {
		assertEquals(0, db.countDistinctStudentsAnswered("   "),
				"A blank username must return 0 instead of querying the database.");
	}

	// ---- hasMetAnswerCoverageRequirement: the direct pass/fail question the grader asks ----

	@Test
	void testExactlyThreeMeetsRequirement() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");
		String carol = freshUser("carol");
		String dave = freshUser("dave");

		makeReply(makePost(bob), alice);
		makeReply(makePost(carol), alice);
		makeReply(makePost(dave), alice);

		assertTrue(db.hasMetAnswerCoverageRequirement(alice),
				"Exactly 3 distinct students must satisfy the requirement.");
	}

	@Test
	void testTwoDoesNotMeetRequirement() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");
		String carol = freshUser("carol");

		makeReply(makePost(bob), alice);
		makeReply(makePost(carol), alice);

		assertFalse(db.hasMetAnswerCoverageRequirement(alice),
				"2 distinct students must NOT satisfy the requirement.");
	}

	@Test
	void testFourStillMeetsRequirement() throws Exception {
		String alice = freshUser("alice");
		String bob = freshUser("bob");
		String carol = freshUser("carol");
		String dave = freshUser("dave");
		String erin = freshUser("erin");

		makeReply(makePost(bob), alice);
		makeReply(makePost(carol), alice);
		makeReply(makePost(dave), alice);
		makeReply(makePost(erin), alice);

		assertTrue(db.hasMetAnswerCoverageRequirement(alice),
				"4 distinct students must still satisfy the requirement.");
	}

	@Test
	void testZeroDoesNotMeetRequirement() throws Exception {
		String alice = freshUser("alice");

		assertFalse(db.hasMetAnswerCoverageRequirement(alice),
				"Zero replies must not satisfy the requirement, and must not throw.");
	}
}
