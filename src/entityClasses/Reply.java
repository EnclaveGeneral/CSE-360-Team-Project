package entityClasses;

/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: This Reply class represents a discussion reply entity in the system. It contains
 * the reply's details such as replyId, parentPostId, authorUsername, body, timestamp, and accepted
 * status. Each reply is associated with a parent post via the parentPostId foreign key. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Richard
 * 
 * @version 1.00	2026-06-15 
 */
public class Reply {

	/*
	 * These are the private attributes for this entity object
	 */
	private int replyId;
	private int parentPostId;
	private String authorUsername;
	private String body;
	private String timestamp;
	private boolean isAccepted;


	/*****
	 * <p> Method: Reply() </p>
	 * 
	 * <p> Description: This default constructor is not used in this system. </p>
	 */
	public Reply() {

	}


	/*****
	 * <p> Method: Reply(int replyId, int parentPostId, String authorUsername, String body,
	 * 		String timestamp, boolean isAccepted) </p>
	 * 
	 * <p> Description: This constructor is used to establish reply entity objects. </p>
	 * 
	 * @param replyId specifies the unique identifier for this reply
	 * 
	 * @param parentPostId specifies the unique identifier of the post this reply belongs to
	 * 
	 * @param authorUsername specifies the username of the user who created this reply
	 * 
	 * @param body specifies the main content of this reply
	 * 
	 * @param timestamp specifies the date and time this reply was created
	 * 
	 * @param isAccepted specifies whether this reply has been marked as the accepted answer
	 * 
	 */
	// Constructor to initialize a new Reply object with all attributes.
	public Reply(int replyId, int parentPostId, String authorUsername, String body, String timestamp, boolean isAccepted) {
		this.replyId = replyId;
		this.parentPostId = parentPostId;
		this.authorUsername = authorUsername;
		this.body = body;
		this.timestamp = timestamp;
		this.isAccepted = isAccepted;
	}


	/*****
	 * <p> Method: void setReplyId(int newReplyId) </p>
	 * 
	 * <p> Description: This setter defines the replyId attribute. </p>
	 * 
	 * @param newReplyId is an int that specifies the unique identifier for this reply.
	 * 
	 */
	// Sets the unique identifier for this reply.
	public void setReplyId(int newReplyId) {
		this.replyId = newReplyId;
	}


	/*****
	 * <p> Method: void setParentPostId(int newParentPostId) </p>
	 * 
	 * <p> Description: This setter defines the parentPostId attribute. </p>
	 * 
	 * @param newParentPostId is an int that specifies the unique identifier of the parent post.
	 * 
	 */
	// Sets the parent post identifier that this reply is associated with.
	public void setParentPostId(int newParentPostId) {
		this.parentPostId = newParentPostId;
	}


	/*****
	 * <p> Method: void setAuthorUsername(String newAuthorUsername) </p>
	 * 
	 * <p> Description: This setter defines the authorUsername attribute. </p>
	 * 
	 * @param newAuthorUsername is a String that specifies the username of the reply's author.
	 * 
	 */
	// Sets the username of the author of this reply.
	public void setAuthorUsername(String newAuthorUsername) {
		this.authorUsername = newAuthorUsername;
	}


	/*****
	 * <p> Method: void setBody(String newBody) </p>
	 * 
	 * <p> Description: This setter defines the body attribute. </p>
	 * 
	 * @param newBody is a String that specifies the main content of this reply.
	 * 
	 */
	// Sets the body content of this reply.
	public void setBody(String newBody) {
		this.body = newBody;
	}


	/*****
	 * <p> Method: void setTimestamp(String newTimestamp) </p>
	 * 
	 * <p> Description: This setter defines the timestamp attribute. </p>
	 * 
	 * @param newTimestamp is a String that specifies the date and time this reply was created.
	 * 
	 */
	// Sets the timestamp of this reply.
	public void setTimestamp(String newTimestamp) {
		this.timestamp = newTimestamp;
	}


	/*****
	 * <p> Method: void setIsAccepted(boolean newIsAccepted) </p>
	 * 
	 * <p> Description: This setter defines the isAccepted attribute. </p>
	 * 
	 * @param newIsAccepted is a boolean that specifies whether this reply is the accepted answer
	 * 		(TRUE or FALSE).
	 * 
	 */
	// Sets the accepted status of this reply.
	public void setIsAccepted(boolean newIsAccepted) {
		this.isAccepted = newIsAccepted;
	}


	/*****
	 * <p> Method: int getReplyId() </p>
	 * 
	 * <p> Description: This getter returns the replyId. </p>
	 * 
	 * @return an int of the replyId
	 *
	 */
	// Gets the unique identifier of this reply.
	public int getReplyId() {
		return this.replyId;
	}


	/*****
	 * <p> Method: int getParentPostId() </p>
	 * 
	 * <p> Description: This getter returns the parentPostId. </p>
	 * 
	 * @return an int of the parentPostId
	 *
	 */
	// Gets the unique identifier of the post this reply belongs to.
	public int getParentPostId() {
		return this.parentPostId;
	}


	/*****
	 * <p> Method: String getAuthorUsername() </p>
	 * 
	 * <p> Description: This getter returns the authorUsername. </p>
	 * 
	 * @return a String of the authorUsername
	 *
	 */
	// Gets the username of the author of this reply.
	public String getAuthorUsername() {
		return this.authorUsername;
	}


	/*****
	 * <p> Method: String getBody() </p>
	 * 
	 * <p> Description: This getter returns the body. </p>
	 * 
	 * @return a String of the body
	 *
	 */
	// Gets the body content of this reply.
	public String getBody() {
		return this.body;
	}


	/*****
	 * <p> Method: String getTimestamp() </p>
	 * 
	 * <p> Description: This getter returns the timestamp. </p>
	 * 
	 * @return a String of the timestamp
	 *
	 */
	// Gets the timestamp of this reply.
	public String getTimestamp() {
		return this.timestamp;
	}


	/*****
	 * <p> Method: boolean getIsAccepted() </p>
	 * 
	 * <p> Description: This getter returns the value of the isAccepted attribute. </p>
	 * 
	 * @return true if this reply has been marked as the accepted answer, else false
	 *
	 */
	// Gets the accepted status of this reply.
	public boolean getIsAccepted() {
		return this.isAccepted;
	}

}