package entityClasses;

import javafx.scene.image.Image;

/*******
 * <p> Title: DiscussionPost Class </p>
 *
 * <p> Description: Represents a single post in the unified discussion board.
 * A post is either a text post (type = "text") or an image post (type = "image").
 * Image posts carry a JavaFX Image and the original filename; text posts leave those null. </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00   2026-06-22 
 */
public class DiscussionPost {

    private final int    id;
    private final String author;
    private final String title;
    private final String body;
    private final String postType;       // "text" | "image"
    private final String imageFilename;
    private final Image  image;
    private final String createdAt;

    /*******
     * <p> Method: DiscussionPost </p>
     *
     * <p> Description: Full constructor used by Database.getAllPosts(). </p>
     *
     * @param id             primary key from the posts table
     * @param author         username of the poster
     * @param title          post title or image caption
     * @param body           body text (null for image posts)
     * @param postType       "text" or "image"
     * @param imageFilename  original filename (null for text posts)
     * @param image          decoded JavaFX Image (null for text posts)
     * @param createdAt      timestamp string from H2
     */
    public DiscussionPost(int id, String author, String title, String body,
                          String postType, String imageFilename, Image image, String createdAt) {
        this.id            = id;
        this.author        = author;
        this.title         = title;
        this.body          = body;
        this.postType      = postType;
        this.imageFilename = imageFilename;
        this.image         = image;
        this.createdAt     = createdAt;
    }

    /** @return the post's primary key */
    public int    getId()            { return id; }

    /** @return the username of the author */
    public String getAuthor()        { return author; }

    /** @return the post title or image caption */
    public String getTitle()         { return title; }

    /** @return the body text, or null for image posts */
    public String getBody()          { return body; }

    /** @return "text" or "image" */
    public String getPostType()      { return postType; }

    /** @return the original image filename, or null for text posts */
    public String getImageFilename() { return imageFilename; }

    /** @return the decoded JavaFX Image, or null for text posts */
    public Image  getImage()         { return image; }

    /** @return H2 timestamp string */
    public String getCreatedAt()     { return createdAt; }

    /** @return true if this is an image post */
    public boolean isImagePost()     { return "image".equals(postType); }
}