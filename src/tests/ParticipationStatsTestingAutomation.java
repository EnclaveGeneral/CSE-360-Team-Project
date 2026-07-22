package tests;

import java.util.ArrayList;
import java.util.List;

import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import statistics.ParticipationStats;
import statistics.StudentStat;

/*******
 * <p> Title: ParticipationStatsTestingAutomation Class </p>
 *
 * <p> Description: The test suite for the Aggregate Statistics Engine (TP3 Aspect #4). It is the
 * executable form of the test plan documented in "TP3 Testing Details and Rationale.pdf": each of
 * the fourteen cases T1&ndash;T14 there corresponds to one performTestCase call here, with the
 * documented expected output as the assertion. </p>
 *
 * <p> The suite is deliberately built on small, in-memory fixtures of posts and replies rather
 * than on the live database. Because the engine is a pure function of its inputs, every expected
 * value can be verified by reading the fixture, which makes a failure unambiguous: it is a defect
 * in the engine, never a flaky environment. Replies are constructed with body text where the
 * substance of the test needs it and with empty bodies otherwise, since the engine reads only
 * authorship and parent-post id. </p>
 *
 * <p> The cases follow the Test Driven Development cycle required by Task 5: they were written to
 * express the requirements before the engine's logic was finalised, and the engine was then
 * written and refined until all fourteen pass. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00	2026-07-14	
 */
public class ParticipationStatsTestingAutomation {

	static int numPassed = 0;
	static int numFailed = 0;

	/* The single engine instance under test. It is stateless, so one instance serves every case. */
	static ParticipationStats engine = new ParticipationStats();


	/*******
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Runs all fourteen test cases in order and prints the running report and the
	 * final pass/fail summary. No database connection and no JavaFX toolkit are required, so the
	 * class runs as a plain Java Application. </p>
	 *
	 * @param args is the array of command line arguments; not used.
	 *
	 */
	public static void main(String[] args) {
		System.out.println("____________________________________________________________________");
		System.out.println("\nAggregate Statistics Engine — Test Suite (TP3 Aspect #4)");
		System.out.println("Rule under test: a student must answer at least "
			+ ParticipationStats.REQUIRED_DISTINCT_STUDENTS + " distinct other students.");
		System.out.println("____________________________________________________________________\n");

		positiveCases();
		negativeAndBoundaryCases();
		readOnlyCase();

		System.out.println("____________________________________________________________________");
		System.out.println("\nTests Passed: " + numPassed);
		System.out.println("Tests Failed: " + numFailed);
		System.out.println("____________________________________________________________________");
	}


	/*-*******************************************************************************************

	Fixtures

	**********************************************************************************************/

	/*******
	 * <p> Method: post(int id, String author) </p>
	 *
	 * <p> Description: Builds a minimal text DiscussionPost for a fixture. Only id and author
	 * matter to the engine; the remaining fields are filled with harmless placeholders. </p>
	 *
	 * @param id     specifies the post's primary key
	 *
	 * @param author specifies the post's author
	 *
	 * @return a text DiscussionPost with the given id and author
	 *
	 */
	static DiscussionPost post(int id, String author) {
		return new DiscussionPost(id, author, "title " + id, "body",
			"text", null, null, "2026-07-14 00:00:00", "");
	}


	/*******
	 * <p> Method: reply(int id, int postId, String author) </p>
	 *
	 * <p> Description: Builds a minimal DiscussionReply for a fixture. Only postId and author
	 * matter to the engine. </p>
	 *
	 * @param id     specifies the reply's primary key
	 *
	 * @param postId specifies the id of the post being answered
	 *
	 * @param author specifies the reply's author
	 *
	 * @return a DiscussionReply with the given id, parent post id, and author
	 *
	 */
	static DiscussionReply reply(int id, int postId, String author) {
		return new DiscussionReply(id, postId, author, "reply body", "2026-07-14 00:00:00", false);
	}


	/*******
	 * <p> Method: fixtureF1() </p>
	 *
	 * <p> Description: Fixture F1 from the test plan &mdash; a compliant student. Alice has
	 * answered three different students (Bob, Carol, Dave). Returns the two lists bundled in a
	 * Board so a single call yields a complete fixture. </p>
	 *
	 * @return the F1 board: posts by Bob, Carol, Dave and three replies from Alice
	 *
	 */
	static Board fixtureF1() {
		List<DiscussionPost> posts = new ArrayList<>();
		posts.add(post(1, "Bob"));
		posts.add(post(2, "Carol"));
		posts.add(post(3, "Dave"));

		List<DiscussionReply> replies = new ArrayList<>();
		replies.add(reply(10, 1, "Alice"));   // Alice -> Bob
		replies.add(reply(11, 2, "Alice"));   // Alice -> Carol
		replies.add(reply(12, 3, "Alice"));   // Alice -> Dave
		return new Board(posts, replies);
	}


	/*******
	 * <p> Method: fixtureF2() </p>
	 *
	 * <p> Description: Fixture F2 from the test plan &mdash; a busy-looking but non-compliant
	 * student. Erin made four replies but to only two distinct students (Frank, Grace). </p>
	 *
	 * @return the F2 board: posts by Frank and Grace, and four replies from Erin
	 *
	 */
	static Board fixtureF2() {
		List<DiscussionPost> posts = new ArrayList<>();
		posts.add(post(1, "Frank"));
		posts.add(post(2, "Grace"));

		List<DiscussionReply> replies = new ArrayList<>();
		replies.add(reply(10, 1, "Erin"));    // Erin -> Frank
		replies.add(reply(11, 1, "Erin"));    // Erin -> Frank (repeat)
		replies.add(reply(12, 2, "Erin"));    // Erin -> Grace
		replies.add(reply(13, 1, "Erin"));    // Erin -> Frank (repeat)
		return new Board(posts, replies);
	}


	/*******
	 * <p> Title: Board Class </p>
	 *
	 * <p> Description: A tiny immutable pair bundling a fixture's posts and replies, so a fixture
	 * builder can return both in one value. </p>
	 *
	 */
	static class Board {
		final List<DiscussionPost>  posts;
		final List<DiscussionReply> replies;
		Board(List<DiscussionPost> posts, List<DiscussionReply> replies) {
			this.posts = posts; this.replies = replies;
		}
	}


	/*-*******************************************************************************************

	Positive cases (T1-T4)

	**********************************************************************************************/

	/*******
	 * <p> Method: positiveCases() </p>
	 *
	 * <p> Description: Cases T1&ndash;T4, which confirm the engine recognises what it should:
	 * a student at the threshold, a student above it, correct authored-counts, and a correct
	 * whole-board roster. </p>
	 *
	 */
	static void positiveCases() {
		System.out.println("=== Positive Cases ===\n");

		// T1 — three distinct students is the value at which the requirement is first met.
		Board f1 = fixtureF1();
		performTestCase("T1",
			"Alice answered 3 distinct students -> distinct=3, MET",
			engine.countDistinctStudentsAnswered("Alice", f1.posts, f1.replies)
				+ "/" + engine.meetsRequirement("Alice", f1.posts, f1.replies),
			"3/true");

		// T2 — one above the boundary; the verdict must remain met.
		Board f1b = fixtureF1();
		f1b.posts.add(post(4, "Heidi"));
		f1b.replies.add(reply(13, 4, "Alice"));   // Alice -> Heidi
		performTestCase("T2",
			"Alice answered 4 distinct students -> distinct=4, MET",
			engine.countDistinctStudentsAnswered("Alice", f1b.posts, f1b.replies)
				+ "/" + engine.meetsRequirement("Alice", f1b.posts, f1b.replies),
			"4/true");

		// T3 — authored counts are tallied independently of the distinct logic.
		StudentStat alice = engine.computeFor("Alice", f1.posts, f1.replies);
		performTestCase("T3",
			"Alice authored 0 posts and 3 replies",
			alice.getPostsAuthored() + "/" + alice.getRepliesAuthored(),
			"0/3");

		// T4 — computeAll yields one record per distinct author; the post authors answered no one.
		List<StudentStat> roster = engine.computeAll(f1.posts, f1.replies);
		int met = 0, notMet = 0;
		for (StudentStat s : roster) { if (s.meetsRequirement()) met++; else notMet++; }
		performTestCase("T4",
			"computeAll on F1 -> 4 records, 1 MET (Alice), 3 NOT MET (Bob/Carol/Dave)",
			roster.size() + " records, " + met + " met, " + notMet + " notMet",
			"4 records, 1 met, 3 notMet");

		System.out.println();
	}


	/*-*******************************************************************************************

	Negative and boundary cases (T5-T13)

	**********************************************************************************************/

	/*******
	 * <p> Method: negativeAndBoundaryCases() </p>
	 *
	 * <p> Description: Cases T5&ndash;T13, which confirm the engine cannot be fooled and does not
	 * miscount at the edges: the below-threshold boundary, repeat-answers, self-answers, the
	 * reply-less student, the empty board, orphaned replies, null authors, and case-insensitive
	 * identity. </p>
	 *
	 */
	static void negativeAndBoundaryCases() {
		System.out.println("=== Negative and Boundary Cases ===\n");

		// T5 — two distinct students, the boundary from below, must read NOT MET.
		Board f1 = fixtureF1();
		f1.replies.remove(2);   // drop Alice -> Dave, leaving Bob and Carol
		performTestCase("T5",
			"Alice answered only 2 distinct students -> distinct=2, NOT MET",
			engine.countDistinctStudentsAnswered("Alice", f1.posts, f1.replies)
				+ "/" + engine.meetsRequirement("Alice", f1.posts, f1.replies),
			"2/false");

		// T6 — four replies but two distinct students; distinct, not raw count, must win.
		Board f2 = fixtureF2();
		performTestCase("T6",
			"Erin made 4 replies to 2 distinct students -> distinct=2, NOT MET",
			engine.countDistinctStudentsAnswered("Erin", f2.posts, f2.replies)
				+ "/" + engine.meetsRequirement("Erin", f2.posts, f2.replies),
			"2/false");

		// T7 — a pure self-answer counts nobody.
		List<DiscussionPost> p7 = new ArrayList<>();
		p7.add(post(1, "Alice"));
		List<DiscussionReply> r7 = new ArrayList<>();
		r7.add(reply(10, 1, "Alice"));   // Alice -> Alice
		performTestCase("T7",
			"Alice replied only to her own post -> distinct=0, NOT MET",
			engine.countDistinctStudentsAnswered("Alice", p7, r7)
				+ "/" + engine.meetsRequirement("Alice", p7, r7),
			"0/false");

		// T8 — a self-answer mixed with real answers removes only the self-answer.
		Board f1c = fixtureF1();
		f1c.posts.add(post(4, "Alice"));
		f1c.replies.add(reply(13, 4, "Alice"));   // Alice -> Alice, must be ignored
		performTestCase("T8",
			"Self-answer alongside 3 real answers -> distinct=3 (self ignored), MET",
			engine.countDistinctStudentsAnswered("Alice", f1c.posts, f1c.replies)
				+ "/" + engine.meetsRequirement("Alice", f1c.posts, f1c.replies),
			"3/true");

		// T9 — a student who only posted answered no one.
		performTestCase("T9",
			"Bob only posted, never replied -> distinct=0, NOT MET",
			engine.countDistinctStudentsAnswered("Bob", f1c.posts, f1c.replies)
				+ "/" + engine.meetsRequirement("Bob", f1c.posts, f1c.replies),
			"0/false");

		// T10 — an empty board yields an empty roster and no exception.
		String t10;
		try {
			List<StudentStat> roster = engine.computeAll(new ArrayList<>(), new ArrayList<>());
			t10 = "size=" + roster.size() + ", noException";
		} catch (Exception e) {
			t10 = "threw " + e.getClass().getSimpleName();
		}
		performTestCase("T10",
			"Empty board -> empty roster, no exception",
			t10,
			"size=0, noException");

		// T11 — an orphaned reply (its post is absent) must not crash and cannot be credited.
		String t11;
		try {
			List<DiscussionPost> p11 = new ArrayList<>();   // no posts at all
			List<DiscussionReply> r11 = new ArrayList<>();
			r11.add(reply(10, 999, "Alice"));               // answers a post that does not exist
			int d = engine.countDistinctStudentsAnswered("Alice", p11, r11);
			t11 = "distinct=" + d + ", noException";
		} catch (Exception e) {
			t11 = "threw " + e.getClass().getSimpleName();
		}
		performTestCase("T11",
			"Orphaned reply (parent post deleted) -> not counted, no exception",
			t11,
			"distinct=0, noException");

		// T12 — a reply with a null author is skipped, not fatal.
		String t12;
		try {
			List<DiscussionPost> p12 = new ArrayList<>();
			p12.add(post(1, "Bob"));
			List<DiscussionReply> r12 = new ArrayList<>();
			r12.add(reply(10, 1, null));                    // null-authored reply
			List<StudentStat> roster = engine.computeAll(p12, r12);
			t12 = "size=" + roster.size() + ", noException";
		} catch (Exception e) {
			t12 = "threw " + e.getClass().getSimpleName();
		}
		performTestCase("T12",
			"Null-authored reply -> skipped, no exception (roster has only Bob)",
			t12,
			"size=1, noException");

		// T13 — "Alice" and "alice" are the same student under case-insensitive matching.
		List<DiscussionPost> p13 = new ArrayList<>();
		p13.add(post(1, "Bob"));
		List<DiscussionReply> r13 = new ArrayList<>();
		r13.add(reply(10, 1, "alice"));   // lower-case replier
		int viaUpper = engine.countDistinctStudentsAnswered("Alice", p13, r13);
		int viaLower = engine.countDistinctStudentsAnswered("alice", p13, r13);
		performTestCase("T13",
			"\"Alice\" and \"alice\" resolve to the same student -> both count Bob",
			viaUpper + "/" + viaLower,
			"1/1");

		System.out.println();
	}


	/*-*******************************************************************************************

	Read-only case (T14)

	**********************************************************************************************/

	/*******
	 * <p> Method: readOnlyCase() </p>
	 *
	 * <p> Description: Case T14. Snapshots a fixture, runs the full computation, and confirms not
	 * one post or reply was modified &mdash; grading must never alter what it grades. Because the
	 * entities are effectively immutable (their fields are final or unused here), the check
	 * compares author and post-id before and after to prove the engine touched nothing. </p>
	 *
	 */
	static void readOnlyCase() {
		System.out.println("=== Read-Only Verification ===\n");

		Board f1 = fixtureF1();

		// Snapshot identifying fields before the run.
		StringBuilder before = new StringBuilder();
		for (DiscussionPost p : f1.posts)   before.append(p.getId()).append(p.getAuthor());
		for (DiscussionReply r : f1.replies) before.append(r.getPostId()).append(r.getAuthor());

		engine.computeAll(f1.posts, f1.replies);   // run everything

		StringBuilder after = new StringBuilder();
		for (DiscussionPost p : f1.posts)   after.append(p.getId()).append(p.getAuthor());
		for (DiscussionReply r : f1.replies) after.append(r.getPostId()).append(r.getAuthor());

		performTestCase("T14",
			"Running computeAll does not modify any post or reply",
			before.toString().equals(after.toString()) ? "unchanged" : "MODIFIED",
			"unchanged");

		System.out.println();
	}


	/*-*******************************************************************************************

	Test harness plumbing

	**********************************************************************************************/

	/*******
	 * <p> Method: performTestCase(String id, String desc, String actual, String expected) </p>
	 *
	 * <p> Description: Runs one test case in the Foundations report format and updates the running
	 * counters. Unlike the TP2 security suite, these are ordinary correctness tests: the expected
	 * value is what the finished engine must produce, so every case is expected to pass, and a
	 * failure marks work still to do under the TDD cycle. </p>
	 *
	 * @param id       specifies the test case identifier, matching the row in the 4.2 document
	 *
	 * @param desc     specifies what the case checks
	 *
	 * @param actual   specifies the value the engine produced
	 *
	 * @param expected specifies the value the finished engine must produce
	 *
	 */
	static void performTestCase(String id, String desc, String actual, String expected) {
		System.out.println("  " + id + ": " + desc);
		System.out.println("    Expected: " + expected);
		System.out.println("    Actual:   " + actual);
		if (actual.equals(expected)) {
			System.out.println("    ***Success***\n");
			numPassed++;
		} else {
			System.out.println("    ***Failure***\n");
			numFailed++;
		}
	}
}