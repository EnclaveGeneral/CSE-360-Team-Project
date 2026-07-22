package tp2Tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;

/*******
 * <p> Title: SearchAndMyViewTestingAutomation Class </p>
 *
 * <p> Description: A database-backed test suite that applies Boundary Value Testing and Coverage
 * Testing to the TP2 search and filter methods in {@link database.Database}, and to the scoping of
 * the guiMyView page. Neither was written by the author of this suite, which satisfies the Task 2.3
 * requirement that the classes under test not be the tester's own. </p>
 *
 * <p> Three weaknesses from "Top Defects.pdf" are covered: A05:2025 Injection, A06:2025 Insecure
 * Design, and Allocation of Resources Without Limits or Throttling. </p>
 *
 * <p> Each test states the value a SECURE implementation would return. A failure therefore reports
 * a defect rather than a broken test, and every such defect is collected in the Defect Register
 * printed at the end of the run. </p>
 *
 * <p> To run: close FoundationsMain first (H2 holds an exclusive lock on ~/FoundationDatabase),
 * then run as a Java Application with VM argument --enable-native-access=javafx.graphics. The
 * suite seeds its own rows and deletes them all in tearDown(). </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00	2026-07-13
 */
public class SearchAndMyViewTestingAutomation {

	static int numPassed = 0;
	static int numFailed = 0;

	static List<String> defectRegister = new ArrayList<>();

	static Database theDatabase = new Database();

	static List<Integer> seededPostIds = new ArrayList<>();

	static int postAlice;       // "Test Alice Walton" — carries the three replies
	static int postBob;         // "Test Bob Ferguson"
	static int postCarol;       // "Test Carol Isaac"  — title contains a LITERAL '%'
	static int postImage;       // "Test Alice Walton" — 2000x2000 image, for Group D

	static int preExistingPostCount = 0;
	static int totalPostCount = 0;

	/** The user whose MyView page is notionally open during Group B. */
	static final String VIEWER = "Test Alice Walton";


	/*******
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Starts the JavaFX toolkit (Database decodes image BLOBs into
	 * javafx.scene.image.Image objects, which cannot be constructed before the toolkit is up),
	 * connects to H2, seeds the fixture, runs the four test groups, cleans up, and prints the
	 * summary and Defect Register. </p>
	 *
	 * @param args is the array of command line arguments; not used.
	 */
	public static void main(String[] args) {

		System.out.println("____________________________________________________________________________");
		System.out.println("\nTP2 Boundary Value and Coverage Test Suite");
		System.out.println("Under test: database.Database (search/filter) and guiMyView scoping");
		System.out.println("Weaknesses: A05 Injection, A06 Insecure Design, Unbounded Allocation");
		System.out.println("____________________________________________________________________________\n");

		// Needed before any Image can be constructed from a BLOB.
		try {
			new javafx.embed.swing.JFXPanel();
		} catch (Throwable t) {
			System.out.println("WARNING: JavaFX toolkit unavailable. Image tests will be skipped.\n");
		}

		try {
			theDatabase.connectToDatabase();
		} catch (SQLException e) {
			System.out.println("FATAL: cannot connect to H2. Is FoundationsMain still running?");
			e.printStackTrace();
			return;
		}

		setUp();

		testGroupA_Injection();
		testGroupB_InsecureDesign();
		testGroupC_Coverage();
		testGroupD_UnboundedAllocation();

		tearDown();

		System.out.println("____________________________________________________________________________");
		System.out.println("\nTests Passed: " + numPassed);
		System.out.println("Tests Failed: " + numFailed);
		System.out.println("____________________________________________________________________________\n");

		printDefectRegister();

		System.exit(0);   // the JavaFX toolkit thread is non-daemon and would hang the JVM
	}


	/*******
	 * <p> Method: setUp() </p>
	 *
	 * <p> Description: Inserts the fixture. Three authors make cross-user leakage observable;
	 * Carol's title contains a literal '%' so that a correct search for "%" has exactly one right
	 * answer; the image post makes the missing size cap observable. </p>
	 */
	static void setUp() {
		System.out.println("--- Fixture setup ---");

		preExistingPostCount = theDatabase.getAllPosts().size();

		postAlice = theDatabase.saveTextPost(
			"Test Alice Walton", "Recursion theory help",
			"How do I choose a base case for a recursive descent parser?", "java");

		postBob = theDatabase.saveTextPost(
			"Test Bob Ferguson", "Midterm question",
			"Which modules are on the midterm?", "exam");

		// The literal '%' in this title is the whole point of the row: a correct search for "%"
		// must return this post and only this post.
		postCarol = theDatabase.saveTextPost(
			"Test Carol Isaac", "Is there a 100% grade curve?",
			"Asking about the curve policy.", "exam");

		// Three replies on Alice's post, from three different authors, all created read = FALSE.
		theDatabase.addReply(postAlice, "David Dai",       "Look at the base case for recursion stop.");
		theDatabase.addReply(postAlice, "Bob Menendez",    "What is the recursive rule and structure?");
		theDatabase.addReply(postAlice, "Kosuke Furukawa", "Make sure you can visualize what is happening.");

		try {
			javafx.scene.image.WritableImage big = new javafx.scene.image.WritableImage(2000, 2000);
			postImage = theDatabase.saveImagePost(
				"Test Alice Walton", "Screenshot of my stack trace", "huge.png", big, "java");
		} catch (Throwable t) {
			postImage = -1;
			System.out.println("  (image fixture skipped)");
		}

		for (int id : new int[] { postAlice, postBob, postCarol, postImage })
			if (id > 0) seededPostIds.add(id);

		totalPostCount = theDatabase.getAllPosts().size();

		System.out.println("  Pre-existing posts: " + preExistingPostCount);
		System.out.println("  Seeded posts:       " + seededPostIds.size());
		System.out.println("  Total on board:     " + totalPostCount + "\n");
	}


	/*******
	 * <p> Method: tearDown() </p>
	 *
	 * <p> Description: Deletes every seeded post. Replies go with them via the ON DELETE CASCADE
	 * on replies.post_id, so the suite is safe to re-run against the working database. </p>
	 */
	static void tearDown() {
		System.out.println("--- Fixture teardown ---");
		for (int id : seededPostIds) theDatabase.deletePost(id);

		int remaining = theDatabase.getAllPosts().size();
		System.out.println("  Posts remaining: " + remaining +
			(remaining == preExistingPostCount ? "  (database restored)" : "  *** RESIDUE ***") + "\n");
	}


	/*******
	 * <p> Method: testGroupA_Injection() </p>
	 *
	 * <p> Description: Boundary Value Testing on the search keyword. Two injection channels are
	 * tested separately. The SQL channel (tests 1-2) is expected to be safe, because the team used
	 * PreparedStatement throughout; those passing tests are the evidence that the mitigation exists
	 * and will fail loudly if anyone later concatenates the query. The LIKE-pattern channel
	 * (tests 3-5) is not safe: PreparedStatement binds '%' and '_' as ordinary characters, but LIKE
	 * still interprets them as wildcards. That is the injection the team actually has. </p>
	 */
	static void testGroupA_Injection() {
		System.out.println("=== Group A — A05:2025 Injection (Boundary Value Testing) ===\n");

		// Bound as a parameter, this payload is just a string no post contains.
		performTestCase(1,
			"SQL tautology payload is treated as literal text",
			String.valueOf(theDatabase.searchPosts("' OR '1'='1").size()),
			"0", null);

		// The second half of the expected value is the assertion that matters: the table survives.
		int afterDrop = theDatabase.searchPosts("'; DROP TABLE posts; --").size();
		performTestCase(2,
			"Stacked DROP payload matches nothing and does not execute",
			afterDrop + "/" + theDatabase.getAllPosts().size(),
			"0/" + totalPostCount, null);

		// BOUNDARY: '%' sits exactly on the edge between literal text and pattern syntax.
		// Only Carol's title contains one, so a correct search returns exactly 1.
		int pctHits = theDatabase.searchPosts("%").size();
		performTestCase(3,
			"BOUNDARY | keyword \"%\" matches only the post containing a literal '%'",
			String.valueOf(pctHits), "1",
			"D1: the LIKE wildcard '%' is not escaped. searchPosts(\"%\") returned " + pctHits
			+ " of " + totalPostCount + " posts — the entire board. One keystroke dumps every post.");

		// BOUNDARY: '_' matches any single character. No seeded post contains a literal one.
		int underHits = theDatabase.searchPosts("_").size();
		performTestCase(4,
			"BOUNDARY | keyword \"_\" matches only posts containing a literal underscore",
			String.valueOf(underHits), "0",
			"D2: the LIKE wildcard '_' is not escaped. searchPosts(\"_\") returned " + underHits
			+ " posts. Any post at least one character long matches.");

		// BOUNDARY: the empty string is the lower bound of the keyword's length domain.
		// It becomes the pattern '%%', which matches everything.
		int emptyHits = theDatabase.searchPosts("").size();
		performTestCase(5,
			"BOUNDARY | empty keyword (length 0) returns no posts",
			String.valueOf(emptyHits), "0",
			"D3: the empty keyword is not rejected. It becomes '%%' and returns " + emptyHits
			+ " posts. Pressing Search on an empty field exposes the whole board.");

		// BOUNDARY: length 1, the smallest legitimate keyword. 'q' appears in Bob's title.
		performTestCase(6,
			"BOUNDARY | single-character keyword \"q\" performs a normal substring match",
			String.valueOf(theDatabase.searchPosts("q").size() >= 1),
			"true", null);

		// searchPosts() does not trim; getFilteredReplies() does. The same blank input therefore
		// means "match three literal spaces" in one method and "no filter at all" in the other.
		int spacesPosts   = theDatabase.searchPosts("   ").size();
		int spacesReplies = theDatabase.getFilteredReplies(postAlice, "   ", false, false).size();
		performTestCase(7,
			"Whitespace-only keyword is handled consistently by both methods",
			"posts=" + spacesPosts + ", replies=" + spacesReplies,
			"posts=0, replies=0",
			"D4: inconsistent blank handling. searchPosts() does not trim the keyword; "
			+ "getFilteredReplies() does. The same input dropped the filter entirely in one method, "
			+ "returning " + spacesReplies + " of 3 replies instead of 0.");

		System.out.println();
	}


	/*******
	 * <p> Method: testGroupB_InsecureDesign() </p>
	 *
	 * <p> Description: guiMyView is documented as showing the user "strictly a filtered view of
	 * their own posts and replies". These tests treat that sentence as the requirement and ask
	 * whether the data layer can even express it. It cannot: no method in the search-and-filter
	 * API takes a viewer identity, so a correct MyView is not reachable from the interface as
	 * designed. That is why this is A06 (Insecure Design) and not A01 — there is no incorrect
	 * check to fix, because there is no check at all, and repairing it means changing the shape
	 * of the API rather than adding an if-statement. </p>
	 */
	static void testGroupB_InsecureDesign() {
		System.out.println("=== Group B — A06:2025 Insecure Design (MyView is not scoped) ===\n");

		// ControllerMyView.refreshPostList() fills the list from exactly this call. Count the rows
		// it hands back that do not belong to the viewer.
		List<DiscussionPost> myViewSees = theDatabase.getAllPosts();
		long foreign = myViewSees.stream()
			.filter(p -> !VIEWER.equals(p.getAuthor()))
			.count();
		performTestCase(8,
			"MyView's backing query returns no posts authored by other users",
			String.valueOf(foreign), "0",
			"D5: ControllerMyView.refreshPostList() calls Database.getAllPosts(), which takes no "
			+ "viewer argument and applies no author predicate. " + foreign + " posts belonging to "
			+ "other users appear on a page documented as showing only the user's own. No scoped "
			+ "alternative exists — there is no getPostsByAuthor() to call instead.");

		
		// searchPosts() matches the keyword against the author column as well as title, body and
		// tags. A student can therefore pull up another student's posts just by typing their name,
		// from a page that is supposed to show only their own content.
		int leaked = theDatabase.searchPosts("Test Bob Ferguson").size();
		performTestCase(9,
			"Searching another user's name returns none of their posts",
			String.valueOf(leaked), "0",
			"D6: searchPosts() includes the author column in its WHERE clause. Typing another "
			+ "student's name returned " + leaked + " of their posts. Author should not be a "
			+ "searchable field, and the query should be scoped to the viewer in the first place.");

		// CONTROL: getRepliesForPost() DOES take a scoping parameter and DOES honour it. Included
		// deliberately — it proves the omission above is a design choice, not a framework limit.
		List<DiscussionReply> onAlice = theDatabase.getRepliesForPost(postAlice);
		boolean allBelong = onAlice.stream().allMatch(r -> r.getPostId() == postAlice);
		performTestCase(10,
			"CONTROL | getRepliesForPost() honours its scoping parameter",
			String.valueOf(allBelong && onAlice.size() == 3),
			"true", null);

		System.out.println();
	}


	/*******
	 * <p> Method: testGroupC_Coverage() </p>
	 *
	 * <p> Description: Coverage Testing of Database.getFilteredReplies(postId, keyword, byUser,
	 * unreadOnly). The method builds its SQL with three guards: unreadOnly appends a read clause;
	 * a non-blank keyword appends a LIKE clause; and byUser then chooses whether that LIKE targets
	 * the author or the body. Because byUser is unreachable when the keyword is blank, the eight
	 * nominal boolean combinations collapse to six real paths. This group drives all six. </p>
	 *
	 * <p> Coverage testing is what surfaces the unread defect: paths 1 and 2 differ only in
	 * unreadOnly and must return different row counts, but they do not. No boundary test and no
	 * casual code read would catch that — only deliberately walking both paths and comparing. </p>
	 */
	static void testGroupC_Coverage() {
		System.out.println("=== Group C — Coverage Testing of getFilteredReplies ===\n");
		System.out.println("  Fixture: post " + postAlice + " has 3 replies (David Dai, "
			+ "Bob Menendez, Kosuke Furukawa), all created read = FALSE.\n");

		int p = postAlice;

		// Path 1: no optional clause appended. Baseline.
		performTestCase(11,
			"COVERAGE Path 1 | unreadOnly=F, no keyword -> all 3 replies",
			String.valueOf(theDatabase.getFilteredReplies(p, "", false, false).size()),
			"3", null);

		// Path 3: body LIKE. "base case" appears only in David Dai's reply.
		performTestCase(12,
			"COVERAGE Path 3 | keyword \"base case\", byUser=F -> body match, 1 reply",
			String.valueOf(theDatabase.getFilteredReplies(p, "base case", false, false).size()),
			"1", null);

		// Path 4: author LIKE.
		performTestCase(13,
			"COVERAGE Path 4 | keyword \"Bob Menendez\", byUser=T -> author match, 1 reply",
			String.valueOf(theDatabase.getFilteredReplies(p, "Bob Menendez", true, false).size()),
			"1", null);

		// Path 5: the longest path — both optional clauses append. Worth testing on its own
		// because the read clause is inserted between the two bound parameters, so a parameter
		// index error would show up here and nowhere else.
		performTestCase(14,
			"COVERAGE Path 5 | unreadOnly=T + keyword + byUser=T -> parameters still bind",
			String.valueOf(theDatabase.getFilteredReplies(p, "Bob Menendez", true, true).size()),
			"1", null);

		// Path 6: read clause + body clause. "recursive rule" appears only in Bob Menendez's reply.
		performTestCase(15,
			"COVERAGE Path 6 | unreadOnly=T + keyword + byUser=F -> read + body clauses",
			String.valueOf(theDatabase.getFilteredReplies(p, "recursive rule", false, true).size()),
			"1", null);

		// Path 2 vs Path 1 — THE DEFECT. unreadOnly=TRUE appends " AND read = FALSE", which should
		// narrow the result. It does not, because nothing ever writes read = TRUE back to the
		// database: ControllerDiscussion.selectReply() sets it on the entity object and discards it.
		int unreadOn  = theDatabase.getFilteredReplies(p, "", false, true).size();
		int unreadOff = theDatabase.getFilteredReplies(p, "", false, false).size();
		performTestCase(16,
			"COVERAGE Path 2 | unreadOnly=T narrows the result relative to unreadOnly=F",
			"unreadOnly=T -> " + unreadOn + ", unreadOnly=F -> " + unreadOff,
			"the two must differ",
			unreadOn == unreadOff
				? "D7: the unread filter is inert. replies.read is never updated. "
				+ "ControllerDiscussion.selectReply() calls setRead(true) on the entity only; no "
				+ "Database method writes it back. Every reply is unread forever, so unreadOnly=TRUE "
				+ "and unreadOnly=FALSE both return all " + unreadOff + " rows. The entire unread "
				+ "feature is non-functional."
				: null);

		// With a blank keyword the byUser branch is never entered, so flipping it must be a no-op.
		// Confirming that is what proves the 8-to-6 path collapse rather than assuming it.
		int blankT = theDatabase.getFilteredReplies(p, "", true,  false).size();
		int blankF = theDatabase.getFilteredReplies(p, "", false, false).size();
		performTestCase(17,
			"COVERAGE | byUser is unreachable when keyword is blank (8 paths collapse to 6)",
			String.valueOf(blankT == blankF), "true", null);

		// The wildcard defect is not confined to searchPosts(). Same root cause, different method.
		int wildAuthors = theDatabase.getFilteredReplies(p, "%", true, false).size();
		performTestCase(18,
			"BOUNDARY | byUser filter with keyword \"%\" matches only a literal-'%' author",
			String.valueOf(wildAuthors), "0",
			"D8: the wildcard defect is not confined to searchPosts(). getFilteredReplies() with "
			+ "byUser=TRUE and keyword \"%\" returned all " + wildAuthors + " replies. A filter meant "
			+ "to isolate one author isolates nobody. Escaping must be applied centrally, in one "
			+ "helper, not patched query by query.");

		System.out.println();
	}


	/*******
	 * <p> Method: testGroupD_UnboundedAllocation() </p>
	 *
	 * <p> Description: Two limits are missing and they compound. Nothing caps the size of an image
	 * an author may store, and nothing caps the number of rows a query may return — while every
	 * returned row decodes its image BLOB into a live Image object. Combined with D1, one keystroke
	 * matches every row and materialises every image on the board. </p>
	 */
	static void testGroupD_UnboundedAllocation() {
		System.out.println("=== Group D — Allocation Without Limits or Throttling ===\n");

		if (postImage <= 0) {
			System.out.println("  Skipped: no image fixture (JavaFX toolkit unavailable).\n");
			return;
		}

		// There is no cap to exceed, so there is no boundary to probe. The missing boundary is
		// itself the finding.
		performTestCase(19,
			"An oversized image post is rejected by a size limit",
			postImage > 0 ? "accepted" : "rejected",
			"rejected",
			"D9: no image size cap exists anywhere in the stack. A 2000x2000 image (~16 MB "
			+ "uncompressed) was stored without validation. ModelDiscussion.validateImageFile() "
			+ "checks only that the File reference is non-null; it never calls file.length().");

		long t0 = System.currentTimeMillis();
		List<DiscussionPost> all = theDatabase.searchPosts("%");
		long elapsed = System.currentTimeMillis() - t0;
		long images = all.stream().filter(DiscussionPost::isImagePost).count();

		System.out.println("  searchPosts(\"%\") returned " + all.size() + " posts in " + elapsed
			+ " ms, materialising " + images + " full-size Image object(s).");

		performTestCase(20,
			"Search results are capped at a page limit (<= 50 rows)",
			String.valueOf(all.size() <= 50), "true",
			"D9: no LIMIT or pagination clause exists on any query in Database.java. The fixture is "
			+ "too small to breach a 50-row page, so this test passes today — but the cap is absent, "
			+ "not merely generous. The defect is latent and surfaces at class-roster scale.");

		System.out.println();
	}


	/*******
	 * <p> Method: performTestCase(int id, String desc, String actual, String secureExpected,
	 *             String defect) </p>
	 *
	 * <p> Description: Runs one test case and updates the counters. The expected value is what a
	 * SECURE implementation would produce, not what the current one happens to produce, so a
	 * failure is a finding rather than a broken test. Findings are collected in the Defect Register
	 * so the screencast can report them without transcribing them by hand. </p>
	 *
	 * @param id             is an int that uniquely identifies this test case.
	 *
	 * @param desc           is a String describing the requirement under test.
	 *
	 * @param actual         is a String holding the value the system produced.
	 *
	 * @param secureExpected is a String holding the value a correct implementation would produce.
	 *
	 * @param defect         is a String describing the defect to register on failure, or null.
	 */
	static void performTestCase(int id, String desc, String actual, String secureExpected,
	                            String defect) {
		System.out.println("  Test " + id + ": " + desc);
		System.out.println("    Expected (secure): " + secureExpected);
		System.out.println("    Actual:            " + actual);

		if (actual.equals(secureExpected)) {
			System.out.println("    ***Success***\n");
			numPassed++;
		} else {
			numFailed++;
			if (defect != null) {
				defectRegister.add(defect);
				System.out.println("    ***Failure*** — DEFECT FOUND:");
				System.out.println("      " + defect + "\n");
			} else {
				System.out.println("    ***Failure*** — unexpected; investigate before the demo.\n");
			}
		}
	}


	/*******
	 * <p> Method: printDefectRegister() </p>
	 *
	 * <p> Description: Prints every defect found during the run. Task 2.4 does not require these to
	 * be repaired, but does require that they be listed and discussed. Generating the list from the
	 * run itself means it cannot drift out of step with what the tests actually found. </p>
	 */
	static void printDefectRegister() {
		System.out.println("DEFECT REGISTER — " + defectRegister.size() + " defect(s) found");
		System.out.println("____________________________________________________________________________\n");
		if (defectRegister.isEmpty()) {
			System.out.println("  None.\n");
			return;
		}
		for (String d : defectRegister) System.out.println("  * " + d + "\n");
		System.out.println("Reported, not repaired: Task 2.4 asks for the list of defects found in");
		System.out.println("TP2; repair is scheduled as TP3 backlog work.");
		System.out.println("____________________________________________________________________________");
	}
}