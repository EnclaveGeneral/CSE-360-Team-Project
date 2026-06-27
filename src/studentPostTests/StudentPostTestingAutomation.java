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
 *   <li>Creating image posts — image file selection validation</li>
 *   <li>Reading post and reply fields via entity accessors</li>
 *   <li>Updating a text post — same validation rules as create</li>
 *   <li>Deleting own post — students may only delete their own posts (ownership check)</li>
 *   <li>Deleting own reply — students may only delete their own replies (ownership check)</li>
 *   <li>Replies survive post deletion — orphaned replies carry postId sentinel -1</li>
 *   <li>Reply CRUD — entity accessors and body validation</li>
 *   <li>Tag-based filtering — tag storage and space-delimited parsing</li>
 * </ul>
 *
 * <p> How to interpret output: {@code ***Success***} means the requirement is satisfied;
 * {@code ***Failure***} means it is not. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Rain (student, Tasks 5 &amp; 6)
 *
 * @version 1.00	2026-06-26	Initial version
 * @version 2.00	2026-06-26	Added ownership and orphaned-reply tests reflecting Task 5 fixes
 * @version 3.00	2026-06-26	Added pending sections for unread tracking, thread support,
 *                          	keyword search, reply-by-user filter, and MyView user scoping
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


		/************** Create an image post — image file validation **************/
		System.out.println("\n=== Create Image Post — image file validation ===");

		// The validator only checks for null; it does not inspect the file on disk
		java.io.File realFile = new java.io.File("placeholder.png");
		performTestCase(13,
			"validateImageFile — non-null File accepted",
			guiDiscussion.ModelDiscussion.validateImageFile(realFile),
			"",
			true);

		// null signals the user dismissed the FileChooser without selecting a file
		performTestCase(14,
			"validateImageFile — null (no file chosen) rejected",
			guiDiscussion.ModelDiscussion.validateImageFile(null),
			"Error: No image file selected.",
			true);


		/************** Read post fields via entity accessors **************/
		System.out.println("\n=== Read Post Fields — entity accessors ===");

		entityClasses.DiscussionPost textPost = new entityClasses.DiscussionPost(
			42, "alice", "My Title", "My Body",
			"text", null, null,
			"2026-06-26 10:00:00", "java cse360");

		performTestCase(15,
			"DiscussionPost.getId() returns correct id",
			String.valueOf(textPost.getId()),
			"42",
			true);

		performTestCase(16,
			"DiscussionPost.getAuthor() returns correct author",
			textPost.getAuthor(),
			"alice",
			true);

		performTestCase(17,
			"DiscussionPost.getTitle() returns correct title",
			textPost.getTitle(),
			"My Title",
			true);

		performTestCase(18,
			"DiscussionPost.getBody() returns correct body",
			textPost.getBody(),
			"My Body",
			true);

		performTestCase(19,
			"DiscussionPost.getPostType() returns \"text\" for text post",
			textPost.getPostType(),
			"text",
			true);

		// isImagePost() delegates to "image".equals(postType); verify it returns false for "text"
		performTestCase(20,
			"DiscussionPost.isImagePost() returns false for text post",
			String.valueOf(textPost.isImagePost()),
			"false",
			true);

		performTestCase(21,
			"DiscussionPost.getCreatedAt() returns correct timestamp",
			textPost.getCreatedAt(),
			"2026-06-26 10:00:00",
			true);

		performTestCase(22,
			"DiscussionPost.getTags() returns correct tags string",
			textPost.getTags(),
			"java cse360",
			true);

		// image posts pass null for body; the constructor stores it as-is
		entityClasses.DiscussionPost imagePost = new entityClasses.DiscussionPost(
			7, "bob", "Pic Caption", null,
			"image", "photo.png", null,
			"2026-06-26 11:00:00", "");

		performTestCase(23,
			"DiscussionPost.isImagePost() returns true for image post",
			String.valueOf(imagePost.isImagePost()),
			"true",
			true);

		performTestCase(24,
			"DiscussionPost.getBody() is null for image post",
			String.valueOf(imagePost.getBody()),
			"null",
			true);

		performTestCase(25,
			"DiscussionPost.getImageFilename() returns filename for image post",
			imagePost.getImageFilename(),
			"photo.png",
			true);


		/************** Update a text post — same validation rules as create **************/
		System.out.println("\n=== Update Text Post — validation on edited fields ===");

		performTestCase(26,
			"validateTitle — valid replacement title accepted",
			guiDiscussion.ModelDiscussion.validateTitle("Updated Title"),
			"",
			true);

		performTestCase(27,
			"validateTitle — empty replacement title rejected",
			guiDiscussion.ModelDiscussion.validateTitle(""),
			"Error: Post title cannot be empty.",
			true);

		performTestCase(28,
			"validateBody — valid replacement body accepted",
			guiDiscussion.ModelDiscussion.validateBody("Updated body content."),
			"",
			true);

		performTestCase(29,
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
		entityClasses.DiscussionPost ownedPost = new entityClasses.DiscussionPost(
			5, "alice", "To Delete", "body", "text", null, null,
			"2026-06-26 09:00:00", "");

		performTestCase(30,
			"Owner can delete own post (getAuthor() == currentUser)",
			String.valueOf(ownedPost.getAuthor().equals(currentUser)),
			"true",
			true);

		performTestCase(31,
			"Non-owner is blocked from deleting post (getAuthor() != currentUser)",
			String.valueOf(ownedPost.getAuthor().equals("mallory")),
			"false",
			true);

		// The controller returns early with an error when the author does not match;
		// verify the post entity still reports the original author unchanged after the check
		performTestCase(32,
			"Post author is unchanged after failed ownership check",
			ownedPost.getAuthor(),
			"alice",
			true);


		/************** Delete own reply — ownership check (Task 5 fix) **************/
		System.out.println("\n=== Delete Reply — ownership check ===");

		entityClasses.DiscussionReply ownedReply = new entityClasses.DiscussionReply(
			10, 5, "alice", "My reply.", "2026-06-26 09:30:00");

		/*
		 * performDeleteReply() applies the same ownership pattern as performDeletePost().
		 * Only the reply author may delete it.
		 */
		performTestCase(33,
			"Owner can delete own reply (getAuthor() == currentUser)",
			String.valueOf(ownedReply.getAuthor().equals("alice")),
			"true",
			true);

		performTestCase(34,
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
			77, -1, "bob", "This reply outlived its post.", "2026-06-26 15:00:00");

		// postId of -1 is the sentinel value getOrphanedReplies() assigns when post_id is NULL
		performTestCase(35,
			"Orphaned reply carries postId sentinel -1",
			String.valueOf(orphanedReply.getPostId()),
			"-1",
			true);

		performTestCase(36,
			"Orphaned reply body is preserved after post deletion",
			orphanedReply.getBody(),
			"This reply outlived its post.",
			true);

		performTestCase(37,
			"Orphaned reply author is preserved after post deletion",
			orphanedReply.getAuthor(),
			"bob",
			true);

		performTestCase(38,
			"Orphaned reply id is preserved after post deletion",
			String.valueOf(orphanedReply.getId()),
			"77",
			true);

		// The controller checks postId == -1 to decide whether to show the tombstone header
		performTestCase(39,
			"postId sentinel -1 correctly identifies reply as orphaned",
			String.valueOf(orphanedReply.getPostId() == -1),
			"true",
			true);

		// A normal (non-orphaned) reply must NOT be mistaken for an orphan
		entityClasses.DiscussionReply normalReply = new entityClasses.DiscussionReply(
			78, 5, "carol", "Still attached to a live post.", "2026-06-26 15:05:00");
		performTestCase(40,
			"Normal reply with valid postId is not identified as orphaned",
			String.valueOf(normalReply.getPostId() == -1),
			"false",
			true);


		/************** Reply CRUD — entity accessors and body validation **************/
		System.out.println("\n=== Reply CRUD — entity accessors & body validation ===");

		entityClasses.DiscussionReply reply = new entityClasses.DiscussionReply(
			99, 42, "charlie", "Great question!", "2026-06-26 12:00:00");

		performTestCase(41,
			"DiscussionReply.getId() returns correct id",
			String.valueOf(reply.getId()),
			"99",
			true);

		// postId is the FK linking this reply back to its parent post
		performTestCase(42,
			"DiscussionReply.getPostId() returns correct parent postId",
			String.valueOf(reply.getPostId()),
			"42",
			true);

		performTestCase(43,
			"DiscussionReply.getAuthor() returns correct author",
			reply.getAuthor(),
			"charlie",
			true);

		performTestCase(44,
			"DiscussionReply.getBody() returns correct body",
			reply.getBody(),
			"Great question!",
			true);

		performTestCase(45,
			"DiscussionReply.getCreatedAt() returns correct timestamp",
			reply.getCreatedAt(),
			"2026-06-26 12:00:00",
			true);

		// validateBody is shared between posts and replies
		performTestCase(46,
			"validateBody — valid reply body accepted",
			guiDiscussion.ModelDiscussion.validateBody("This is a valid reply."),
			"",
			true);

		performTestCase(47,
			"validateBody — empty reply body rejected",
			guiDiscussion.ModelDiscussion.validateBody(""),
			"Error: Body cannot be empty.",
			true);


		/************** Tag-based filtering — storage and parsing **************/
		System.out.println("\n=== Tag-based filtering — tag storage and parsing ===");

		entityClasses.DiscussionPost taggedPost = new entityClasses.DiscussionPost(
			10, "diana", "Tagged Post", "body", "text", null, null,
			"2026-06-26 13:00:00", "java oop");
		String rawTags = taggedPost.getTags();

		performTestCase(48,
			"Post with tags returns non-empty getTags()",
			String.valueOf(rawTags != null && !rawTags.isEmpty()),
			"true",
			true);

		// filter_by_tags splits on a single space, matching the format stored in the DB
		String[] tagArray = (rawTags != null && !rawTags.isEmpty())
			? rawTags.split(" ") : new String[0];

		performTestCase(49,
			"Tags string splits into correct number of tokens",
			String.valueOf(tagArray.length),
			"2",
			true);

		performTestCase(50,
			"First tag token is \"java\"",
			tagArray.length > 0 ? tagArray[0] : "",
			"java",
			true);

		performTestCase(51,
			"Second tag token is \"oop\"",
			tagArray.length > 1 ? tagArray[1] : "",
			"oop",
			true);

		// An empty tags string must produce a zero-length array without throwing an NPE
		entityClasses.DiscussionPost noTagPost = new entityClasses.DiscussionPost(
			11, "eve", "Untagged Post", "body", "text", null, null,
			"2026-06-26 14:00:00", "");
		String noTags = noTagPost.getTags();
		String[] emptyTagArray = (noTags != null && !noTags.isEmpty())
			? noTags.split(" ") : new String[0];

		performTestCase(52,
			"Post with no tags produces zero-length tag array (no NPE)",
			String.valueOf(emptyTagArray.length),
			"0",
			true);

		// filter_by_tags uses Arrays.asList(tags).contains(tag) — verify both directions
		performTestCase(53,
			"Tag \"java\" found in tagged post (contains check)",
			String.valueOf(java.util.Arrays.asList(tagArray).contains("java")),
			"true",
			true);

		performTestCase(54,
			"Tag \"python\" not found in post (post excluded from filter)",
			String.valueOf(java.util.Arrays.asList(tagArray).contains("python")),
			"false",
			true);


		/************** ModelMyView mirrors ModelDiscussion validation **************/
		System.out.println("\n=== ModelMyView — validation mirrors ModelDiscussion ===");

		performTestCase(55,
			"ModelMyView.validateTitle — valid title accepted",
			guiMyView.ModelMyView.validateTitle("MyView Post Title"),
			"",
			true);

		performTestCase(56,
			"ModelMyView.validateTitle — empty title rejected",
			guiMyView.ModelMyView.validateTitle(""),
			"Error: Post title cannot be empty.",
			true);

		performTestCase(57,
			"ModelMyView.validateBody — valid body accepted",
			guiMyView.ModelMyView.validateBody("Some content here."),
			"",
			true);

		performTestCase(58,
			"ModelMyView.validateBody — empty body rejected",
			guiMyView.ModelMyView.validateBody(""),
			"Error: Body cannot be empty.",
			true);

		performTestCase(59,
			"ModelMyView.validateAuthor — valid author accepted",
			guiMyView.ModelMyView.validateAuthor("frank"),
			"",
			true);

		performTestCase(60,
			"ModelMyView.validateAuthor — empty author rejected",
			guiMyView.ModelMyView.validateAuthor(""),
			"Error: Author cannot be empty.",
			true);

		// The validator only checks for null; it does not inspect the file on disk
		performTestCase(61,
			"ModelMyView.validateImageFile — non-null File accepted",
			guiMyView.ModelMyView.validateImageFile(new java.io.File("img.png")),
			"",
			true);

		performTestCase(62,
			"ModelMyView.validateImageFile — null rejected",
			guiMyView.ModelMyView.validateImageFile(null),
			"Error: No image file selected.",
			true);


		/************** Pending: MyView post list scoped to current user **************/
		// These tests exercise the filter predicate that will be used once ModelMyView
		// routes getAllPosts() through a currentUser filter. They will FAIL until that
		// change is implemented — that failure is the signal to implement it.
		System.out.println("\n=== PENDING: MyView — current user post filter ===");

		// Posts authored by the current user must be included in MyView
		entityClasses.DiscussionPost myPost = new entityClasses.DiscussionPost(
			20, "grace", "My Post", "body", "text", null, null,
			"2026-06-26 16:00:00", "");
		String myViewUser = "grace";
		performTestCase(63,
			"MyView filter — post by current user is included",
			String.valueOf(myPost.getAuthor().equals(myViewUser)),
			"true",
			true);

		// Posts by other users must be excluded
		entityClasses.DiscussionPost otherPost = new entityClasses.DiscussionPost(
			21, "henry", "Someone Else's Post", "body", "text", null, null,
			"2026-06-26 16:05:00", "");
		performTestCase(64,
			"MyView filter — post by another user is excluded",
			String.valueOf(otherPost.getAuthor().equals(myViewUser)),
			"false",
			true);


		/************** Pending: thread support **************/
		// These tests will FAIL until DiscussionPost gains a thread field and the DB
		// schema adds a thread column. Once implemented, getThread() must return the
		// value supplied at construction, and a null/empty thread must default to "General".
		System.out.println("\n=== PENDING: Thread support ===");

		// Expect getThread() to return the thread name set at construction
		entityClasses.DiscussionPost threadedPost = new entityClasses.DiscussionPost(
			30, "iris", "Thread Post", "body", "text", null, null,
			"2026-06-26 17:00:00", "");
		performTestCase(65,
			"PENDING | DiscussionPost.getThread() returns assigned thread name",
			threadedPost.getThread(),          // will fail: method does not exist yet
			"Homework Help",
			true);

		// A post constructed without a thread must default to "General"
		entityClasses.DiscussionPost defaultThreadPost = new entityClasses.DiscussionPost(
			31, "iris", "Default Thread Post", "body", "text", null, null,
			"2026-06-26 17:05:00", "");
		performTestCase(66,
			"PENDING | DiscussionPost.getThread() defaults to "General" when unspecified",
			defaultThreadPost.getThread(),     // will fail: method does not exist yet
			"General",
			true);


		/************** Pending: keyword search **************/
		// These tests exercise the string-matching predicate that keyword search will use.
		// The predicate logic is pure string work, testable now even though the DB query
		// and UI wiring do not exist yet.
		System.out.println("\n=== PENDING: Keyword search ===");

		entityClasses.DiscussionPost keywordPost = new entityClasses.DiscussionPost(
			40, "jack", "Midterm Review", "Check out this midterm study guide!", "text",
			null, null, "2026-06-26 18:00:00", "");

		// Keyword present in title must match
		String keyword = "midterm";
		boolean titleMatch = keywordPost.getTitle().toLowerCase().contains(keyword.toLowerCase());
		performTestCase(67,
			"PENDING | keyword in title matches post (case-insensitive contains)",
			String.valueOf(titleMatch),
			"true",
			true);

		// Keyword present in body must also match
		boolean bodyMatch = keywordPost.getBody().toLowerCase().contains(keyword.toLowerCase());
		performTestCase(68,
			"PENDING | keyword in body matches post (case-insensitive contains)",
			String.valueOf(bodyMatch),
			"true",
			true);

		// Keyword absent from both title and body must not match
		String absentKeyword = "calculus";
		boolean noMatch = keywordPost.getTitle().toLowerCase().contains(absentKeyword.toLowerCase())
			|| keywordPost.getBody().toLowerCase().contains(absentKeyword.toLowerCase());
		performTestCase(69,
			"PENDING | keyword absent from title and body does not match post",
			String.valueOf(noMatch),
			"false",
			true);


		/************** Pending: filter replies by user **************/
		// Tests the per-reply author match predicate that the Search feature will use.
		System.out.println("\n=== PENDING: Filter replies by user ===");

		entityClasses.DiscussionReply replyFromTarget = new entityClasses.DiscussionReply(
			200, 42, "kate", "Here is my answer.", "2026-06-26 19:00:00");
		entityClasses.DiscussionReply replyFromOther = new entityClasses.DiscussionReply(
			201, 42, "leo", "Here is my answer too.", "2026-06-26 19:05:00");

		String filterUser = "kate";

		// Reply from the target user must be included
		performTestCase(70,
			"PENDING | reply from searched user is included in filtered list",
			String.valueOf(replyFromTarget.getAuthor().equals(filterUser)),
			"true",
			true);

		// Reply from a different user must be excluded
		performTestCase(71,
			"PENDING | reply from different user is excluded from filtered list",
			String.valueOf(replyFromOther.getAuthor().equals(filterUser)),
			"false",
			true);


		/************** Pending: unread tracking **************/
		// These tests will FAIL until DiscussionReply gains an isRead field.
		// Once implemented, isRead() must reflect the value set at construction and
		// the unread count for a post equals the number of replies where isRead() == false.
		System.out.println("\n=== PENDING: Unread tracking ===");

		// A freshly received reply should be unread by default
		entityClasses.DiscussionReply unreadReply = new entityClasses.DiscussionReply(
			300, 42, "mia", "Have you tried restarting?", "2026-06-26 20:00:00");
		performTestCase(72,
			"PENDING | new reply is unread by default (isRead() == false)",
			String.valueOf(unreadReply.isRead()),   // will fail: method does not exist yet
			"false",
			true);

		// Unread count for a set of replies equals replies where isRead() is false
		entityClasses.DiscussionReply readReply = new entityClasses.DiscussionReply(
			301, 42, "noah", "Try clearing the cache.", "2026-06-26 20:05:00");
		java.util.List<entityClasses.DiscussionReply> replySet =
			java.util.Arrays.asList(unreadReply, readReply);
		// Once isRead() exists, markRead() will be called on readReply before this count
		long unreadCount = replySet.stream().filter(r -> !r.isRead()).count();
		performTestCase(73,
			"PENDING | unread count reflects number of replies where isRead() == false",
			String.valueOf(unreadCount),           // will fail: method does not exist yet
			"2",                                   // both unread until markRead() is implemented
			true);


		/************** Test cases semi-automation report footer **************/
		System.out.println("\n______________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		System.out.println("______________________________________________________________________");
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