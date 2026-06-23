package entityClasses;

/*******
 * <p> Title: DiscussionReply Class </p>
 *
 * <p> Description: Represents a reply to a DiscussionPost.
 * Replies are text-only and belong to exactly one parent post. </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00   2026-06-22 
 */
public class DiscussionReply {

    private final int    id;
    private final int    postId;
    private final String author;
    private final String body;
    private final String createdAt;

    /*******
     * <p> Method: DiscussionReply </p>
     *
     * <p> Description: Full constructor used by Database.getRepliesForPost(). </p>
     *
     * @param id        primary key from the replies table
     * @param postId    FK to the parent post
     * @param author    username of the replier
     * @param body      reply text
     * @param createdAt timestamp string from H2
     */
    public DiscussionReply(int id, int postId, String author, String body, String createdAt) {
        this.id        = id;
        this.postId    = postId;
        this.author    = author;
        this.body      = body;
        this.createdAt = createdAt;
    }

    /** @return the reply's primary key */
    public int    getId()        { return id; }

    /** @return the parent post's id */
    public int    getPostId()    { return postId; }

    /** @return the username of the replier */
    public String getAuthor()    { return author; }

    /** @return the reply body text */
    public String getBody()      { return body; }

    /** @return H2 timestamp string */
    public String getCreatedAt() { return createdAt; }
}