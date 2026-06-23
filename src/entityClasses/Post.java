package entityClasses;

/*******
 * <p> Title: Post Class </p>
 * 
 * <p> Description: This Post class represents a discussion post entity in the system. It contains
 * the post's details such as postId, authorUsername, title, body, timestamp, and pinned status. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Richard
 * 
 * @version 1.00	2026-06-15
 */
public class Post {

	/*
	 * These are the private attributes for this entity object
	 */
	private int postId;
	private String authorUsername;
	private String title;
	private String body;
	private String timestamp;
	private boolean isPinned;


	/*****
	 * <p> Method: Post() </p>
	 * 
	 * <p> Description: This default constructor is not used in this system. </p>
	 */
	public Post() {

	}


	/*****
	 * <p> Method: Post(int postId, String authorUsername, String title, String body,
	 * 		String timestamp, boolean isPinned) </p>
	 * 
	 * <p> Description: This constructor is used to establish post entity objects. </p>
	 * 
	 * @param postId specifies the unique identifier for this post
	 * 
	 * @param authorUsername specifies the username of the user who created this post
	 * 
	 * @param title specifies the title/subject of this post
	 * 
	 * @param body specifies the main content/body of this post
	 * 
	 * @param timestamp specifies the date and time this post was created
	 * 
	 * @param isPinned specifies whether this post is pinned/highlighted (TRUE or FALSE)
	 * 
	 */
	// Constructor to initialize a new Post object with all attributes.
	public Post(int postId, String authorUsername, String title, String body, String timestamp, boolean isPinned) {
		this.postId = postId;
		this.authorUsername = authorUsername;
		this.title = title;
		this.body = body;
		this.timestamp = timestamp;
		this.isPinned = isPinned;
	}


	/*****
	 * <p> Method: void setPostId(int newPostId) </p>
	 * 
	 * <p> Description: This setter defines the postId attribute. </p>
	 * 
	 * @param newPostId is an int that specifies the unique identifier for this post.
	 * 
	 */
	// Sets the unique identifier for this post.
	public void setPostId(int newPostId) {
		this.postId = newPostId;
	}


	/*****
	 * <p> Method: void setAuthorUsername(String newAuthorUsername) </p>
	 * 
	 * <p> Description: This setter defines the authorUsername attribute. </p>
	 * 
	 * @param newAuthorUsername is a String that specifies the username of the post's author.
	 * 
	 */
	// Sets the username of the author of this post.
	public void setAuthorUsername(String newAuthorUsername) {
		this.authorUsername = newAuthorUsername;
	}


	/*****
	 * <p> Method: void setTitle(String newTitle) </p>
	 * 
	 * <p> Description: This setter defines the title attribute. </p>
	 * 
	 * @param newTitle is a String that specifies the title/subject of this post.
	 * 
	 */
	// Sets the title of this post.
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}


	/*****
	 * <p> Method: void setBody(String newBody) </p>
	 * 
	 * <p> Description: This setter defines the body attribute. </p>
	 * 
	 * @param newBody is a String that specifies the main content of this post.
	 * 
	 */
	// Sets the body content of this post.
	public void setBody(String newBody) {
		this.body = newBody;
	}


	/*****
	 * <p> Method: void setTimestamp(String newTimestamp) </p>
	 * 
	 * <p> Description: This setter defines the timestamp attribute. </p>
	 * 
	 * @param newTimestamp is a String that specifies the date and time this post was created.
	 * 
	 */
	// Sets the timestamp of this post.
	public void setTimestamp(String newTimestamp) {
		this.timestamp = newTimestamp;
	}


	/*****
	 * <p> Method: void setIsPinned(boolean newIsPinned) </p>
	 * 
	 * <p> Description: This setter defines the isPinned attribute. </p>
	 * 
	 * @param newIsPinned is a boolean that specifies whether this post is pinned (TRUE or FALSE).
	 * 
	 */
	// Sets the pinned status of this post.
	public void setIsPinned(boolean newIsPinned) {
		this.isPinned = newIsPinned;
	}


	/*****
	 * <p> Method: int getPostId() </p>
	 * 
	 * <p> Description: This getter returns the postId. </p>
	 * 
	 * @return an int of the postId
	 *
	 */
	// Gets the unique identifier of this post.
	public int getPostId() {
		return this.postId;
	}


	/*****
	 * <p> Method: String getAuthorUsername() </p>
	 * 
	 * <p> Description: This getter returns the authorUsername. </p>
	 * 
	 * @return a String of the authorUsername
	 *
	 */
	// Gets the username of the author of this post.
	public String getAuthorUsername() {
		return this.authorUsername;
	}


	/*****
	 * <p> Method: String getTitle() </p>
	 * 
	 * <p> Description: This getter returns the title. </p>
	 * 
	 * @return a String of the title
	 *
	 */
	// Gets the title of this post.
	public String getTitle() {
		return this.title;
	}


	/*****
	 * <p> Method: String getBody() </p>
	 * 
	 * <p> Description: This getter returns the body. </p>
	 * 
	 * @return a String of the body
	 *
	 */
	// Gets the body content of this post.
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
	// Gets the timestamp of this post.
	public String getTimestamp() {
		return this.timestamp;
	}


	/*****
	 * <p> Method: boolean getIsPinned() </p>
	 * 
	 * <p> Description: This getter returns the value of the isPinned attribute. </p>
	 * 
	 * @return true if this post is pinned, else false
	 *
	 */
	// Gets the pinned status of this post.
	public boolean getIsPinned() {
		return this.isPinned;
	}

}