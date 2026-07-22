package statistics;

/*******
 * <p> Title: StudentStat Class </p>
 *
 * <p> Description: An immutable record of the participation statistics computed for a single
 * student by the Aggregate Statistics Engine (TP3 Aspect #4). It carries the three tallies the
 * engine produces &mdash; posts authored, replies authored, and the count of distinct other
 * students whose questions the student has answered &mdash; together with the compliance verdict
 * derived from the last of these. </p>
 *
 * <p> The distinct-students-answered count is the field that drives grading: the course requires
 * each student to have answered questions from at least three different students. The other two
 * tallies are reported for context on the grading dashboard (Aspect #2) but do not affect the
 * verdict. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00	2026-07-14	
 */
public class StudentStat {

	/* The student's username, stored in its canonical (lower-cased) form. See ParticipationStats
	 * for why matching is case-insensitive. */
	private final String  student;

	/* Number of posts this student authored. */
	private final int     postsAuthored;

	/* Number of replies this student authored, including any that do not count toward the
	 * distinct total (self-answers, repeat answers). Reported for context, not for grading. */
	private final int     repliesAuthored;

	/* Number of DISTINCT other students whose posts this student replied to. This is the graded
	 * metric. */
	private final int     distinctStudentsAnswered;

	/* True iff distinctStudentsAnswered meets the course threshold. Stored rather than recomputed
	 * so that a caller reading the record cannot accidentally apply a different threshold. */
	private final boolean meetsRequirement;


	/*******
	 * <p> Method: StudentStat </p>
	 *
	 * <p> Description: Constructs an immutable statistics record. The verdict is passed in rather
	 * than derived here, because the threshold that defines it belongs to the engine
	 * (ParticipationStats), not to this data holder. Keeping the rule in one place is what lets
	 * the instructor change the threshold without editing this class. </p>
	 *
	 * @param student                  specifies the student's canonical username
	 *
	 * @param postsAuthored            specifies the number of posts the student authored
	 *
	 * @param repliesAuthored          specifies the number of replies the student authored
	 *
	 * @param distinctStudentsAnswered specifies the number of distinct other students answered
	 *
	 * @param meetsRequirement         specifies the compliance verdict for this student
	 *
	 */
	public StudentStat(String student, int postsAuthored, int repliesAuthored,
	                   int distinctStudentsAnswered, boolean meetsRequirement) {
		this.student                  = student;
		this.postsAuthored            = postsAuthored;
		this.repliesAuthored          = repliesAuthored;
		this.distinctStudentsAnswered = distinctStudentsAnswered;
		this.meetsRequirement         = meetsRequirement;
	}


	/** @return the student's canonical username */
	public String  getStudent()                  { return student; }

	/** @return the number of posts the student authored */
	public int     getPostsAuthored()            { return postsAuthored; }

	/** @return the number of replies the student authored */
	public int     getRepliesAuthored()          { return repliesAuthored; }

	/** @return the number of distinct other students the student answered (the graded metric) */
	public int     getDistinctStudentsAnswered() { return distinctStudentsAnswered; }

	/** @return true iff the student met the distinct-students requirement */
	public boolean meetsRequirement()            { return meetsRequirement; }


	/*******
	 * <p> Method: toString </p>
	 *
	 * <p> Description: Renders the record as one human-readable line for console output and for
	 * the plain-text portion of the grading export (Aspect #8). </p>
	 *
	 * @return a single-line summary of this student's statistics and verdict
	 *
	 */
	@Override
	public String toString() {
		return String.format("%-12s posts=%d replies=%d distinctAnswered=%d  %s",
			student, postsAuthored, repliesAuthored, distinctStudentsAnswered,
			meetsRequirement ? "MET" : "NOT MET");
	}
}