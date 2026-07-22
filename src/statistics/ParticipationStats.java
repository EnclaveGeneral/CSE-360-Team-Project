package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;

/*******
 * <p> Title: ParticipationStats Class </p>
 *
 * <p> Description: The Aggregate Statistics Engine (TP3 Aspect #4). Given the discussion board's
 * posts and replies, it computes for each student how many posts and replies they authored and,
 * critically, how many <b>distinct other students</b> whose questions they have answered. From
 * that last count it derives a compliance verdict against the course rule: a student must have
 * answered questions from at least three different students. </p>
 *
 * <p> The engine is a pure function of its inputs. It reads the two lists it is given and returns
 * results; it never modifies a post or a reply, opens a database connection, or touches any state
 * outside itself. That property is what makes it safe to run during grading and trivial to test
 * with small in-memory fixtures. </p>
 *
 * <p> <b>Why "distinct" and not a reply count.</b> The requirement is about coverage, not
 * activity. A student who posts ten replies to one classmate has helped one classmate; a student
 * who posts three replies to three classmates has met the bar. Counting distinct post-authors,
 * rather than replies, is the whole point of the metric, and it is why a Set &mdash; which
 * silently absorbs duplicates &mdash; is the natural data structure. </p>
 *
 * <p> <b>Why self-answers are excluded.</b> Answering your own question does not demonstrate that
 * you have helped another student, and counting it would let any student self-certify by replying
 * to themselves three times. The engine removes the student's own identity from their answered
 * set before counting. </p>
 *
 * <p> <b>Why matching is case-insensitive.</b> Usernames elsewhere in the codebase are compared
 * with toLowerCase() (for example in guiMyView's filterByKeyword). The engine follows that
 * house rule so that "Alice" and "alice" are treated as one student, and a participation grade
 * can never hinge on capitalisation. All identities are canonicalised to lower case on the way
 * in, and canonical form is what StudentStat stores and returns. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00	2026-07-14	Initial prototype for HW3 Task 5
 */
public class ParticipationStats {

	/*
	 * The course rule: a student must have answered questions from at least this many distinct
	 * other students. It is a single named constant, deliberately, so that if the instructor
	 * changes the threshold the change is made in exactly one place and every verdict follows.
	 */
	public static final int REQUIRED_DISTINCT_STUDENTS = 3;


	/*******
	 * <p> Method: countDistinctStudentsAnswered(String student, List posts, List replies) </p>
	 *
	 * <p> Description: Returns the number of distinct <i>other</i> students whose posts the given
	 * student has replied to. Replies to the student's own posts are excluded; multiple replies
	 * to the same other student are counted once. This is the graded metric. </p>
	 *
	 * <p> The method works in two passes. First it builds a map from post id to the canonical
	 * author of that post, so that a reply &mdash; which knows only its parent post's id &mdash;
	 * can be attributed to the student who wrote that post. Then it walks the given student's
	 * replies, looks up each parent post's author, and collects those authors into a Set, skipping
	 * the student's own identity. The size of the Set is the answer. </p>
	 *
	 * @param student specifies the student whose coverage is being counted; matched case-insensitively
	 *
	 * @param posts   specifies all posts on the board, used to resolve each reply to its post's author
	 *
	 * @param replies specifies all replies on the board
	 *
	 * @return the number of distinct other students the given student has answered
	 *
	 */
	public int countDistinctStudentsAnswered(String student,
	                                          List<DiscussionPost> posts,
	                                          List<DiscussionReply> replies) {

		// A null or blank student answers no one. Guard first so the rest can assume a real name.
		if (student == null || student.trim().isEmpty()) return 0;
		String me = canonical(student);

		// Pass 1: post id -> canonical author of that post.
		Map<Integer, String> postAuthorById = buildPostAuthorMap(posts);

		// Pass 2: collect the distinct authors of the posts this student replied to.
		Set<String> answered = new HashSet<>();
		for (DiscussionReply r : replies) {
			if (r == null) continue;

			String replier = canonical(r.getAuthor());
			// Skip replies that are not this student's, and replies with no attributable author.
			if (replier == null || !replier.equals(me)) continue;

			String postAuthor = postAuthorById.get(r.getPostId());
			// An orphaned reply (its post is gone) has no resolvable author; it cannot be
			// credited to answering anyone, so it is skipped rather than crashing the run.
			if (postAuthor == null) continue;

			// The exclusion that makes self-answers not count.
			if (postAuthor.equals(me)) continue;

			answered.add(postAuthor);
		}
		return answered.size();
	}


	/*******
	 * <p> Method: meetsRequirement(String student, List posts, List replies) </p>
	 *
	 * <p> Description: Returns true iff the student has answered questions from at least
	 * REQUIRED_DISTINCT_STUDENTS distinct other students. The comparison is inclusive at the
	 * threshold: exactly three distinct students is a pass. </p>
	 *
	 * @param student specifies the student whose compliance is being decided
	 *
	 * @param posts   specifies all posts on the board
	 *
	 * @param replies specifies all replies on the board
	 *
	 * @return true if the student meets the distinct-students requirement, false otherwise
	 *
	 */
	public boolean meetsRequirement(String student,
	                                List<DiscussionPost> posts,
	                                List<DiscussionReply> replies) {
		return countDistinctStudentsAnswered(student, posts, replies)
			>= REQUIRED_DISTINCT_STUDENTS;
	}


	/*******
	 * <p> Method: computeFor(String student, List posts, List replies) </p>
	 *
	 * <p> Description: Builds the full StudentStat record for one student: posts authored, replies
	 * authored, distinct students answered, and the verdict. The two authored-counts are tallied
	 * independently of the distinct logic, because they answer a different question (how active is
	 * this student) than the graded metric (how many classmates did they help). </p>
	 *
	 * @param student specifies the student to compute; matched case-insensitively
	 *
	 * @param posts   specifies all posts on the board
	 *
	 * @param replies specifies all replies on the board
	 *
	 * @return the completed StudentStat record for the student
	 *
	 */
	public StudentStat computeFor(String student,
	                              List<DiscussionPost> posts,
	                              List<DiscussionReply> replies) {
		String me = canonical(student);

		int postsAuthored = 0;
		for (DiscussionPost p : posts)
			if (p != null && me != null && me.equals(canonical(p.getAuthor())))
				postsAuthored++;

		int repliesAuthored = 0;
		for (DiscussionReply r : replies)
			if (r != null && me != null && me.equals(canonical(r.getAuthor())))
				repliesAuthored++;

		int distinct = countDistinctStudentsAnswered(student, posts, replies);

		return new StudentStat(me, postsAuthored, repliesAuthored, distinct,
			distinct >= REQUIRED_DISTINCT_STUDENTS);
	}


	/*******
	 * <p> Method: computeAll(List posts, List replies) </p>
	 *
	 * <p> Description: Produces one StudentStat record for every distinct person who appears on
	 * the board as a post author or a reply author. This is the roster the grading dashboard
	 * (Aspect #2) renders and the export (Aspect #8) serialises. </p>
	 *
	 * <p> "Every distinct person" is gathered from both posts and replies so that a student who
	 * only ever replied &mdash; and so wrote no posts &mdash; still appears, and a student who
	 * only ever posted still appears with a distinct-answered count of zero. </p>
	 *
	 * @param posts   specifies all posts on the board
	 *
	 * @param replies specifies all replies on the board
	 *
	 * @return a list of per-student records, one per distinct author on the board
	 *
	 */
	public List<StudentStat> computeAll(List<DiscussionPost> posts,
	                                    List<DiscussionReply> replies) {
		// Collect every distinct author from both sources.
		Set<String> everyone = new HashSet<>();
		for (DiscussionPost p : posts)
			if (p != null) { String a = canonical(p.getAuthor()); if (a != null) everyone.add(a); }
		for (DiscussionReply r : replies)
			if (r != null) { String a = canonical(r.getAuthor()); if (a != null) everyone.add(a); }

		List<StudentStat> roster = new ArrayList<>();
		for (String student : everyone)
			roster.add(computeFor(student, posts, replies));
		return roster;
	}


	/*-*******************************************************************************************

	Private helpers

	**********************************************************************************************/

	/*******
	 * <p> Method: buildPostAuthorMap(List posts) </p>
	 *
	 * <p> Description: Builds a lookup from post id to the canonical author of that post, so a
	 * reply can be traced to the student it answered. Factored out because both the distinct
	 * count and any future per-answer breakdown need the same lookup. </p>
	 *
	 * @param posts specifies all posts on the board
	 *
	 * @return a map from post id to the canonical username of that post's author
	 *
	 */
	private Map<Integer, String> buildPostAuthorMap(List<DiscussionPost> posts) {
		Map<Integer, String> byId = new HashMap<>();
		for (DiscussionPost p : posts) {
			if (p == null) continue;
			String a = canonical(p.getAuthor());
			if (a != null) byId.put(p.getId(), a);
		}
		return byId;
	}


	/*******
	 * <p> Method: canonical(String name) </p>
	 *
	 * <p> Description: Reduces a username to its canonical comparison form: trimmed and
	 * lower-cased. Returns null for a null or blank name so that callers can uniformly treat
	 * "no attributable author" as a single case. Centralising the rule here is what guarantees
	 * every comparison in the engine uses the same notion of identity. </p>
	 *
	 * @param name specifies the raw username to canonicalise, possibly null
	 *
	 * @return the trimmed, lower-cased name, or null if the input was null or blank
	 *
	 */
	private String canonical(String name) {
		if (name == null) return null;
		String t = name.trim();
		return t.isEmpty() ? null : t.toLowerCase();
	}
}