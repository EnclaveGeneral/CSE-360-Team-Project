package entityClasses;

/*******
 * <p> Title: DiscussionReply Class </p>
 *
 * <p> Description: Represents a reply to a DiscussionPost. Replies are text-only and belong
 * to exactly one parent post. A reply may be marked as the accepted answer via the
 * isAccepted flag. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00	2026-06-22	Initial version
 * @version 2.00	2026-06-23	Added isAccepted field for unified board
 */
public class DiscussionReply {

	/*
	 * These are the private attributes for this entity object
	 */
	private final int     id;
	private final int     postId;
	private final String  author;
	private final String  body;
	private final boolean isAccepted;
	private final String  createdAt;


	/*******
	 * <p> Method: DiscussionReply </p>
	 *
	 * <p> Description: Full constructor used by Database.getRepliesForPost(). </p>
	 *
	 * @param id         specifies the primary key from the replies table
	 *
	 * @param postId     specifies the FK to the parent post
	 *
	 * @param author     specifies the username of the replier
	 *
	 * @param body       specifies the reply text
	 *
	 * @param isAccepted specifies whether this reply has been marked as the accepted answer
	 *
	 * @param createdAt  specifies the timestamp string from H2
	 *
	 */
	public DiscussionReply(int id, int postId, String author, String body,
	                       boolean isAccepted, String createdAt) {
		this.id         = id;
		this.postId     = postId;
		this.author     = author;
		this.body       = body;
		this.isAccepted = isAccepted;
		this.createdAt  = createdAt;
	}


	/** @return the reply's primary key */
	public int     getId()         { return id; }

	/** @return the parent post's id */
	public int     getPostId()     { return postId; }

	/** @return the username of the replier */
	public String  getAuthor()     { return author; }

	/** @return the reply body text */
	public String  getBody()       { return body; }

	/** @return true if this reply is the accepted answer */
	public boolean getIsAccepted() { return isAccepted; }

	/** @return H2 timestamp string */
	public String  getCreatedAt()  { return createdAt; }

}
