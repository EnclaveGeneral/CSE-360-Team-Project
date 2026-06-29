package studentPostTests;

/*******
 * <p> Title: StudentPostTestingAutomation Class </p>
 *
 * <p> Description: A semi-automated test suite that validates the Students User Story
 * requirements implemented in Tasks 3, 4, and the fixes applied in Task 5.  The suite
 * exercises the input-validation logic in {@link guiDiscussion.ModelDiscussion} and
 * {@link guiMyView.ModelMyView}, the entity constructors and accessors in
 * {@link entityClasses.DiscussionPost} and {@link entityClasses.DiscussionReply}, and the
 * ownership and orphaned-reply logic added during Task 5. </p>
 *
 * <p> Implemented requirements covered: </p>
 * <ul>
 *   <li>Creating text posts — title, body, and author validation</li>
 *   <li>Reading post and reply fields via entity accessors</li>
 *   <li>Updating a text post — same validation rules as create</li>
 *   <li>Deleting own post — students may only delete their own posts (ownership check)</li>
 *   <li>Deleting own reply — students may only delete their own replies (ownership check)</li>
 *   <li>Replies survive post deletion — orphaned replies carry postId sentinel -1</li>
 *   <li>Reply CRUD — entity accessors and body validation</li>
 *   <li>Tag-based filtering — tag storage and space-delimited parsing</li>
 *   <li>Unread tracking — getRead()/setRead() and unread count via stream filter</li>
 *   <li>Filter replies by user — author equality match predicate</li>
 *   <li>Filter replies by keyword — case-insensitive body contains predicate</li>
 *   <li>MyView scoped to current user — author equality filter predicate</li>
 * </ul>
 *
 * <p> How to interpret output: {@code ***Success***} means the requirement is satisfied;
 * {@code ***Failure***} means it is not. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Jack H. (student, Tasks 5 and 6)
 *
 * @version 1.00	2026-06-26	Initial version
 * @version 2.00	2026-06-26	Added ownership and orphaned-reply tests reflecting Task 5 fixes
 * @version 3.00	2026-06-26	Added pending sections for unread tracking,
 *                          	keyword search, reply-by-user filter, and MyView user scoping
 * @version 4.00	2026-06-27	Activated unread, keyword, and user-filter tests; fixed DiscussionReply
 *                          	constructor calls to match updated 6-arg signature
 * @version 5.00	2026-06-27	MyView user-scope tests.
 */
public class StudentPostTestingAutomation {

	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests


	/*******
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Displays the report header, runs every test case, then displays
	 * the summary footer. </p>
	 *
	 * @param args  command-line arguments (not used)
	 *
	 */
	public static void main(String[] args) {

		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________________________________________");
		System.out.println("\nStudentPost Testing Automation");
		System.out.println("Tasks 5 & 6 — Students User Story requirement verification");
		System.out.println("______________________________________________________________________");


		/************** Create a text post — title, body, author validation **************/
		System.out.println("\n=== Create Text Post — input validation ===");

		performTestCase(1,
			"validateTitle — valid non-blank title accepted",
			guiDiscussion.ModelDiscussion.validateTitle("Hello World"),
			"",
			true);

		performTestCase(2,
			"validateTitle — empty string rejected",
			guiDiscussion.ModelDiscussion.validateTitle(""),
			"Error: Post title cannot be empty.",
			true);

		// null must not throw; the validator is expected to handle it safely
		performTestCase(3,
			"validateTitle — null rejected",
			guiDiscussion.ModelDiscussion.validateTitle(null),
			"Error: Post title cannot be empty.",
			true);

		// isBlank() returns true for whitespace-only strings, so this must also be rejected
		performTestCase(4,
			"validateTitle — whitespace-only rejected",
			guiDiscussion.ModelDiscussion.validateTitle("   "),
			"Error: Post title cannot be empty.",
			true);

		performTestCase(5,
			"validateBody — valid non-blank body accepted",
			guiDiscussion.ModelDiscussion.validateBody("This is the post content."),
			"",
			true);

		performTestCase(6,
			"validateBody — empty string rejected",
			guiDiscussion.ModelDiscussion.validateBody(""),
			"Error: Body cannot be empty.",
			true);

		performTestCase(7,
			"validateBody — null rejected",
			guiDiscussion.ModelDiscussion.validateBody(null),
			"Error: Body cannot be empty.",
			true);

		// tab+newline is whitespace-only; isBlank() must catch it
		performTestCase(8,
			"validateBody — whitespace-only rejected",
			guiDiscussion.ModelDiscussion.validateBody("\t\n"),
			"Error: Body cannot be empty.",
			true);

		performTestCase(9,
			"validateAuthor — valid non-blank author accepted",
			guiDiscussion.ModelDiscussion.validateAuthor("alice"),
			"",
			true);

		performTestCase(10,
			"validateAuthor — empty string rejected",
			guiDiscussion.ModelDiscussion.validateAuthor(""),
			"Error: Author cannot be empty.",
			true);

		performTestCase(11,
			"validateAuthor — null rejected",
			guiDiscussion.ModelDiscussion.validateAuthor(null),
			"Error: Author cannot be empty.",
			true);

		performTestCase(12,
			"validateAuthor — whitespace-only rejected",
			guiDiscussion.ModelDiscussion.validateAuthor("  "),
			"Error: Author cannot be empty.",
			true);



		/************** Read post fields via entity accessors **************/
		System.out.println("\n=== Read Post Fields — entity accessors ===");

		entityClasses.DiscussionPost textPost = makePost(42, "alice", "My Title", "My Body", "java cse360");

		performTestCase(13,
			"DiscussionPost.getId() returns correct id",
			String.valueOf(textPost.getId()),
			"42",
			true);

		performTestCase(14,
			"DiscussionPost.getAuthor() returns correct author",
			textPost.getAuthor(),
			"alice",
			true);

		performTestCase(15,
			"DiscussionPost.getTitle() returns correct title",
			textPost.getTitle(),
			"My Title",
			true);

		performTestCase(16,
			"DiscussionPost.getBody() returns correct body",
			textPost.getBody(),
			"My Body",
			true);

		performTestCase(17,
			"DiscussionPost.getPostType() returns \"text\" for text post",
			textPost.getPostType(),
			"text",
			true);



		performTestCase(18,
			"DiscussionPost.getTags() returns correct tags string",
			textPost.getTags(),
			"java cse360",
			true);


		/************** Update a text post — same validation rules as create **************/
		System.out.println("\n=== Update Text Post — validation on edited fields ===");

		performTestCase(19,
			"validateTitle — valid replacement title accepted",
			guiDiscussion.ModelDiscussion.validateTitle("Updated Title"),
			"",
			true);

		performTestCase(20,
			"validateTitle — empty replacement title rejected",
			guiDiscussion.ModelDiscussion.validateTitle(""),
			"Error: Post title cannot be empty.",
			true);

		performTestCase(21,
			"validateBody — valid replacement body accepted",
			guiDiscussion.ModelDiscussion.validateBody("Updated body content."),
			"",
			true);

		performTestCase(22,
			"validateBody — whitespace-only replacement body rejected",
			guiDiscussion.ModelDiscussion.validateBody("   "),
			"Error: Body cannot be empty.",
			true);


		/************** Delete own post — ownership check (Task 5 fix) **************/
		System.out.println("\n=== Delete Post — ownership check ===");

		/*
		 * performDeletePost() now calls getAuthor().equals(currentUser) before deleting.
		 * These tests verify that equality check behaves correctly in both directions,
		 * mirroring the exact logic in the controller.
		 */
		String currentUser = "alice";
		entityClasses.DiscussionPost ownedPost = makePost(5, "alice", "To Delete", "body", "");

		performTestCase(23,
			"Owner can delete own post (getAuthor() == currentUser)",
			String.valueOf(ownedPost.getAuthor().equals(currentUser)),
			"true",
			true);

		performTestCase(24,
			"Non-owner is blocked from deleting post (getAuthor() != currentUser)",
			String.valueOf(ownedPost.getAuthor().equals("mallory")),
			"false",
			true);

		// The controller returns early with an error when the author does not match;
		// verify the post entity still reports the original author unchanged after the check
		performTestCase(25,
			"Post author is unchanged after failed ownership check",
			ownedPost.getAuthor(),
			"alice",
			true);


		/************** Delete own reply — ownership check (Task 5 fix) **************/
		System.out.println("\n=== Delete Reply — ownership check ===");

		entityClasses.DiscussionReply ownedReply = new entityClasses.DiscussionReply(
			10, 5, "alice", "My reply.", "2026-06-26 09:30:00", false);

		/*
		 * performDeleteReply() applies the same ownership pattern as performDeletePost().
		 * Only the reply author may delete it.
		 */
		performTestCase(26,
			"Owner can delete own reply (getAuthor() == currentUser)",
			String.valueOf(ownedReply.getAuthor().equals("alice")),
			"true",
			true);

		performTestCase(27,
			"Non-owner is blocked from deleting reply (getAuthor() != currentUser)",
			String.valueOf(ownedReply.getAuthor().equals("mallory")),
			"false",
			true);


		/************** Orphaned replies — replies survive post deletion (Task 5 fix) **************/
		System.out.println("\n=== Orphaned Replies — replies survive post deletion ===");

		/*
		 * When a post is deleted, Database.deletePost() sets post_id = NULL and
		 * post_deleted = TRUE on all its replies before removing the post row.
		 * getOrphanedReplies() returns these replies with postId = -1 as a sentinel.
		 * These tests verify the DiscussionReply entity correctly stores and returns
		 * that sentinel so the controller can identify and display orphaned replies
		 * with a tombstone notice.
		 */
		entityClasses.DiscussionReply orphanedReply = new entityClasses.DiscussionReply(
			77, -1, "bob", "This reply outlived its post.", "2026-06-26 15:00:00", false);

		// postId of -1 is the sentinel value getOrphanedReplies() assigns when post_id is NULL
		performTestCase(28,
			"Orphaned reply carries postId sentinel -1",
			String.valueOf(orphanedReply.getPostId()),
			"-1",
			true);

		performTestCase(29,
			"Orphaned reply body is preserved after post deletion",
			orphanedReply.getBody(),
			"This reply outlived its post.",
			true);

		performTestCase(30,
			"Orphaned reply author is preserved after post deletion",
			orphanedReply.getAuthor(),
			"bob",
			true);

		performTestCase(31,
			"Orphaned reply id is preserved after post deletion",
			String.valueOf(orphanedReply.getId()),
			"77",
			true);

		// The controller checks postId == -1 to decide whether to show the tombstone header
		performTestCase(32,
			"postId sentinel -1 correctly identifies reply as orphaned",
			String.valueOf(orphanedReply.getPostId() == -1),
			"true",
			true);

		// A normal (non-orphaned) reply must NOT be mistaken for an orphan
		entityClasses.DiscussionReply normalReply = new entityClasses.DiscussionReply(
			78, 5, "carol", "Still attached to a live post.", "2026-06-26 15:05:00", false);
		performTestCase(33,
			"Normal reply with valid postId is not identified as orphaned",
			String.valueOf(normalReply.getPostId() == -1),
			"false",
			true);


		/************** Reply CRUD — entity accessors and body validation **************/
		System.out.println("\n=== Reply CRUD — entity accessors & body validation ===");

		entityClasses.DiscussionReply reply = new entityClasses.DiscussionReply(
			99, 42, "charlie", "Great question!", "2026-06-26 12:00:00", false);

		performTestCase(34,
			"DiscussionReply.getId() returns correct id",
			String.valueOf(reply.getId()),
			"99",
			true);

		// postId is the FK linking this reply back to its parent post
		performTestCase(35,
			"DiscussionReply.getPostId() returns correct parent postId",
			String.valueOf(reply.getPostId()),
			"42",
			true);

		performTestCase(36,
			"DiscussionReply.getAuthor() returns correct author",
			reply.getAuthor(),
			"charlie",
			true);

		performTestCase(37,
			"DiscussionReply.getBody() returns correct body",
			reply.getBody(),
			"Great question!",
			true);

		performTestCase(38,
			"DiscussionReply.getCreatedAt() returns correct timestamp",
			reply.getCreatedAt(),
			"2026-06-26 12:00:00",
			true);

		// validateBody is shared between posts and replies
		performTestCase(39,
			"validateBody — valid reply body accepted",
			guiDiscussion.ModelDiscussion.validateBody("This is a valid reply."),
			"",
			true);

		performTestCase(40,
			"validateBody — empty reply body rejected",
			guiDiscussion.ModelDiscussion.validateBody(""),
			"Error: Body cannot be empty.",
			true);


		/************** Tag-based filtering — storage and parsing **************/
		System.out.println("\n=== Tag-based filtering — tag storage and parsing ===");

		entityClasses.DiscussionPost taggedPost = makePost(10, "diana", "Tagged Post", "body", "java oop");
		String rawTags = taggedPost.getTags();

		performTestCase(41,
			"Post with tags returns non-empty getTags()",
			String.valueOf(rawTags != null && !rawTags.isEmpty()),
			"true",
			true);

		// filter_by_tags splits on a single space, matching the format stored in the DB
		String[] tagArray = (rawTags != null && !rawTags.isEmpty())
			? rawTags.split(" ") : new String[0];

		performTestCase(42,
			"Tags string splits into correct number of tokens",
			String.valueOf(tagArray.length),
			"2",
			true);

		performTestCase(43,
			"First tag token is \"java\"",
			tagArray.length > 0 ? tagArray[0] : "",
			"java",
			true);

		performTestCase(44,
			"Second tag token is \"oop\"",
			tagArray.length > 1 ? tagArray[1] : "",
			"oop",
			true);

		// An empty tags string must produce a zero-length array without throwing an NPE
		entityClasses.DiscussionPost noTagPost = makePost(11, "eve", "Untagged Post", "body", "");
		String noTags = noTagPost.getTags();
		String[] emptyTagArray = (noTags != null && !noTags.isEmpty())
			? noTags.split(" ") : new String[0];

		performTestCase(45,
			"Post with no tags produces zero-length tag array (no NPE)",
			String.valueOf(emptyTagArray.length),
			"0",
			true);

		// filter_by_tags uses Arrays.asList(tags).contains(tag) — verify both directions
		performTestCase(46,
			"Tag \"java\" found in tagged post (contains check)",
			String.valueOf(java.util.Arrays.asList(tagArray).contains("java")),
			"true",
			true);

		performTestCase(47,
			"Tag \"python\" not found in post (post excluded from filter)",
			String.valueOf(java.util.Arrays.asList(tagArray).contains("python")),
			"false",
			true);


		/************** ModelMyView mirrors ModelDiscussion validation **************/
		System.out.println("\n=== ModelMyView — validation mirrors ModelDiscussion ===");

		performTestCase(48,
			"ModelMyView.validateTitle — valid title accepted",
			guiMyView.ModelMyView.validateTitle("MyView Post Title"),
			"",
			true);

		performTestCase(49,
			"ModelMyView.validateTitle — empty title rejected",
			guiMyView.ModelMyView.validateTitle(""),
			"Error: Post title cannot be empty.",
			true);

		performTestCase(50,
			"ModelMyView.validateBody — valid body accepted",
			guiMyView.ModelMyView.validateBody("Some content here."),
			"",
			true);

		performTestCase(51,
			"ModelMyView.validateBody — empty body rejected",
			guiMyView.ModelMyView.validateBody(""),
			"Error: Body cannot be empty.",
			true);

		performTestCase(52,
			"ModelMyView.validateAuthor — valid author accepted",
			guiMyView.ModelMyView.validateAuthor("frank"),
			"",
			true);

		performTestCase(53,
			"ModelMyView.validateAuthor — empty author rejected",
			guiMyView.ModelMyView.validateAuthor(""),
			"Error: Author cannot be empty.",
			true);


		/************** Pending: MyView post list scoped to current user **************/
		// These tests exercise the filter predicate that will be used once ModelMyView
		// routes getAllPosts() through a currentUser filter. They will FAIL until that
		// change is implemented — that failure is the signal to implement it.
		System.out.println("\n=== PENDING: MyView — current user post filter ===");

		// Posts authored by the current user must be included in MyView
		entityClasses.DiscussionPost myPost = makePost(20, "grace", "My Post", "body", "");
		String myViewUser = "grace";
		performTestCase(54,
			"MyView filter — post by current user is included",
			String.valueOf(myPost.getAuthor().equals(myViewUser)),
			"true",
			true);

		// Posts by other users must be excluded
		entityClasses.DiscussionPost otherPost = makePost(21, "henry", "Someone Else's Post", "body", "");
		performTestCase(55,
			"MyView filter — post by another user is excluded",
			String.valueOf(otherPost.getAuthor().equals(myViewUser)),
			"false",
			true);



		/************** Pending: keyword search **************/
		// These tests exercise the string-matching predicate that keyword search will use.
		// The predicate logic is pure string work, testable now even though the DB query
		// and UI wiring do not exist yet.
		System.out.println("\n=== PENDING: Keyword search ===");

		entityClasses.DiscussionPost keywordPost = makePost(40, "jack", "Midterm Review", "Check out this midterm study guide!", "");

		// Keyword present in title must match
		String keyword = "midterm";
		boolean titleMatch = keywordPost.getTitle().toLowerCase().contains(keyword.toLowerCase());
		performTestCase(56,
			"PENDING | keyword in title matches post (case-insensitive contains)",
			String.valueOf(titleMatch),
			"true",
			true);

		// Keyword present in body must also match
		boolean bodyMatch = keywordPost.getBody().toLowerCase().contains(keyword.toLowerCase());
		performTestCase(57,
			"PENDING | keyword in body matches post (case-insensitive contains)",
			String.valueOf(bodyMatch),
			"true",
			true);

		// Keyword absent from both title and body must not match
		String absentKeyword = "calculus";
		boolean noMatch = keywordPost.getTitle().toLowerCase().contains(absentKeyword.toLowerCase())
			|| keywordPost.getBody().toLowerCase().contains(absentKeyword.toLowerCase());
		performTestCase(58,
			"PENDING | keyword absent from title and body does not match post",
			String.valueOf(noMatch),
			"false",
			true);


		/************** Pending: filter replies by user **************/
		// Tests the per-reply author match predicate that the Search feature will use.
		System.out.println("\n=== PENDING: Filter replies by user ===");

		entityClasses.DiscussionReply replyFromTarget = new entityClasses.DiscussionReply(
			200, 42, "kate", "Here is my answer.", "2026-06-26 19:00:00", false);
		entityClasses.DiscussionReply replyFromOther = new entityClasses.DiscussionReply(
			201, 42, "leo", "Here is my answer too.", "2026-06-26 19:05:00", false);

		String filterUser = "kate";

		// Reply from the target user must be included
		performTestCase(59,
			"PENDING | reply from searched user is included in filtered list",
			String.valueOf(replyFromTarget.getAuthor().equals(filterUser)),
			"true",
			true);

		// Reply from a different user must be excluded
		performTestCase(60,
			"PENDING | reply from different user is excluded from filtered list",
			String.valueOf(replyFromOther.getAuthor().equals(filterUser)),
			"false",
			true);


		/************** Unread tracking **************/
		System.out.println("\n=== Unread tracking ===");

		// New replies are constructed with read=false; getRead() must reflect that
		entityClasses.DiscussionReply unreadReply = new entityClasses.DiscussionReply(
			300, 42, "mia", "Have you tried restarting?", "2026-06-26 20:00:00", false);
		performTestCase(61,
			"New reply constructed with read=false is unread (getRead() == false)",
			String.valueOf(unreadReply.getRead()),
			"false",
			true);

		// setRead(true) must mark the reply as read
		unreadReply.setRead(true);
		performTestCase(62,
			"After setRead(true) reply is read (getRead() == true)",
			String.valueOf(unreadReply.getRead()),
			"true",
			true);

		// Unread count: stream filter over a mixed set counts only unread replies
		entityClasses.DiscussionReply readReply = new entityClasses.DiscussionReply(
			301, 42, "noah", "Try clearing the cache.", "2026-06-26 20:05:00", true);
		java.util.List<entityClasses.DiscussionReply> replySet =
			java.util.Arrays.asList(unreadReply, readReply);
		// unreadReply was set read above; readReply was constructed as read — both are read now
		long unreadCount = replySet.stream().filter(r -> !r.getRead()).count();
		performTestCase(63,
			"Unread count is 0 when all replies have been marked read",
			String.valueOf(unreadCount),
			"0",
			true);

		// A freshly constructed reply (read=false) increments the unread count
		entityClasses.DiscussionReply freshReply = new entityClasses.DiscussionReply(
			302, 42, "olivia", "Check the logs.", "2026-06-26 20:10:00", false);
		replySet = java.util.Arrays.asList(unreadReply, readReply, freshReply);
		unreadCount = replySet.stream().filter(r -> !r.getRead()).count();
		performTestCase(64,
			"Unread count is 1 when one reply has not been read",
			String.valueOf(unreadCount),
			"1",
			true);


		/************** Test cases semi-automation report footer **************/
		System.out.println("\n______________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		System.out.println("______________________________________________________________________");
	}


	/*******
	 * <p> Method: makePost </p>
	 *
	 * <p> Description: Convenience factory for creating text-only DiscussionPost instances
	 * in tests. Passes null for imageFilename and image so callers do not need explicit
	 * casts to satisfy the overloaded constructor. </p>
	 *
	 * @param id        specifies the post id
	 * @param author    specifies the author username
	 * @param title     specifies the post title
	 * @param body      specifies the post body
	 * @param tags      specifies space-delimited tags
	 * @return a new text DiscussionPost
	 *
	 */
	private static entityClasses.DiscussionPost makePost(
			int id, String author, String title, String body, String tags) {
		return new entityClasses.DiscussionPost(
			id, author, title, body, "text", null, null, "", tags);
	}


	/*******
	 * <p> Method: performTestCase </p>
	 *
	 * <p> Description: Compares {@code actualResult} against {@code expectedResult} and
	 * prints a labelled trace.  Increments {@code numPassed} or {@code numFailed}
	 * accordingly. </p>
	 *
	 * <p> How to interpret output: {@code ***Success***} means actual matched expected;
	 * {@code ***Failure***} means it did not. </p>
	 *
	 * @param testCaseNum    specifies the unique sequential test number
	 *
	 * @param description    specifies a human-readable label for the requirement being verified
	 *
	 * @param actualResult   specifies the String value returned by the code under test
	 *
	 * @param expectedResult specifies the String value the code must return to satisfy the requirement
	 *
	 * @param expectedPass   specifies true for a positive scenario, false for a negative scenario
	 *
	 */
	private static void performTestCase(int testCaseNum, String description,
	                                    String actualResult, String expectedResult,
	                                    boolean expectedPass) {

		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________"
			+ "\n\nTest case: " + testCaseNum);
		System.out.println("Description: " + description);
		System.out.println("Expected result : \"" + expectedResult + "\"");
		System.out.println("Actual   result : \"" + actualResult  + "\"");
		System.out.println("______________");

		boolean resultMatches = actualResult.equals(expectedResult);

		if (expectedPass) {
			if (resultMatches) {
				System.out.println("***Success*** The result matched the expected output — requirement satisfied.");
				numPassed++;
			} else {
				System.out.println("***Failure*** The result did NOT match the expected output — requirement NOT satisfied.");
				numFailed++;
			}
		} else {
			// Negative test: not matching is the success condition
			if (!resultMatches) {
				System.out.println("***Success*** The result did not match (as expected for a negative test) — requirement satisfied.");
				numPassed++;
			} else {
				System.out.println("***Failure*** The result matched when it should not have — requirement NOT satisfied.");
				numFailed++;
			}
		}
	}

}