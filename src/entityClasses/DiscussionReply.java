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
	private final String  createdAt;
	private boolean 	  read;
	private boolean 	  quality;


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
	 * @param createdAt  specifies the timestamp string from H2
	 * 
	 * @param read 		 specifies whether reply read or not
	 * 
	 * @param quality	 specifies whether reply quality is valid/invalid
	 *
	 */
	public DiscussionReply(int id, int postId, String author, String body, String createdAt, boolean read, boolean quality) {
		this.id         = id;
		this.postId     = postId;
		this.author     = author;
		this.body       = body;
		this.createdAt  = createdAt;
		this.read 		= read;
		this.quality    = quality;
	}


	/*******
	 * <p> Method: getId </p>
	 * <p> Description: Gets the reply's primary key </p>
	 *
	 * @return int id
	 */
	public int     getId()         		  { return id; }

	
	/*******
	 * <p> Method: getPostId </p>
	 * <p> Description: Gets the parent post's id </p>
	 *
	 * @return int postId
	 */
	public int     getPostId()     		  { return postId; }

	
	/*******
	 * <p> Method: getAuthor </p>
	 * <p> Description: Gets the username of the replier </p>
	 *
	 * @return String author
	 */
	public String  getAuthor()     		  { return author; }

	
	/*******
	 * <p> Method: getBody </p>
	 * <p> Description: Gets the reply body text </p>
	 *
	 * @return String body
	 */
	public String  getBody()       		  { return body; }
	
	
	/*******
	 * <p> Method: getRead </p>
	 * <p> Description: Gets the read status </p>
	 *
	 * @return boolean read
	 */
	public boolean  getRead()       	  { return read; }
	
	
	/*******
	 * <p> Method: setRead </p>
	 * <p> Description: Sets read to input </p>
	 *
	 * @param boolean input, the boolean to set read to
	 */
	public void  setRead(boolean input)   { read = input; }
	
	
	/*******
	 * <p> Method: getCreatedAt </p>
	 * <p> Description: Gets the creation timestamp </p>
	 *
	 * @return String createdAt, the H2 timestamp string
	 */
	public String  getCreatedAt()  		  { return createdAt; }
	
	
	/*******
	 * <p> Method: getQuality </p>
	 * <p> Description: Gets the boolean quality flag, for answer quality check </p>
	 *
	 * @return boolean quality
	 */
	public boolean getQuality() 		  { return quality; }
	
	
	/*******
	 * <p> Method: setQuality </p>
	 * <p> Description: Sets quality to the input </p>
	 *
	 * @param boolean input, the boolean to set quality to
	 */
	public void setQuality(boolean input) { quality = input; } 
}
