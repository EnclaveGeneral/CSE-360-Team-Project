/**
 * the junit for the read only access
 * 
 * @author josh sprague
 */
package junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import applicationMain.FoundationsMain;
import guiDiscussion.ControllerDiscussion;
import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;

@TestMethodOrder(OrderAnnotation.class)
class read_only {
	private ControllerDiscussion con;
	private Database data_base = FoundationsMain.database;
	
	public ArrayList<DiscussionReply> add_reply(){
		return new ArrayList<>(data_base.getRepliesForPost(1));
	}

	/*******
	 * <p> Method: setUp() </p>
	 *
	 * <p> Description: initialize stuff </p>
	 *
	 */
	@BeforeEach
	void setUp() throws Exception {
		con = new ControllerDiscussion();
		data_base = new Database();
		data_base.connectToDatabase();
		data_base.saveTextPost("josh", "anime", "example body", "a b c");
	}
	
	/*******
	 * <p> Method: resetReadFlag() </p>
	 *
	 * <p> Description: reset flag to false after every test </p>
	 *
	 */
	@AfterEach
    void resetReadFlag() {
        ControllerDiscussion.resetReadFlag();
    }

	/*******
	 * <p> Method: read_test() </p>
	 *
	 * <p> Description: checks if read works </p>
	 *
	 */
	@Test
	@Order(1)
	void read_test() {
		System.out.println("test 1");
		assertEquals(con.get_read(), false);
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
	}
	
	/*******
	 * <p> Method: add_post() </p>
	 *
	 * <p> Description: add a post to database </p>
	 *
	 */
	@Test
	@Order(2)
	void add_post() {
		System.out.println("test 2");
		
		// make sure list is 1
		ArrayList<DiscussionPost> new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.size(), 1);
		
		// turn read on
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
		
		// try to make another post which fails
		ControllerDiscussion.performCreatePost();
		new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.size(), 1);
	}
	
	/*******
	 * <p> Method: update_post() </p>
	 *
	 * <p> Description: update a post to database </p>
	 *
	 */
	@Test
	@Order(3)
	void update_post() {
		System.out.println("test 3");
		
		// turn read on
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
		
		// try to update
		ControllerDiscussion.performUpdatePost();
		
		// make sure it didnt update
		ArrayList<DiscussionPost> new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.get(0).getId(), 1);
		assertEquals(new_list.get(0).getAuthor(), "josh");
		assertEquals(new_list.get(0).getTitle(), "anime");
		assertEquals(new_list.get(0).getBody(), "example body");
		assertEquals(new_list.get(0).getTags(), "a b c");
		
		// turn read off
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), false);
		
		// try to update
		data_base.updatePost(1, "josh", "anime", "new body", "a b c d");
		
		// make sure list is 1
		new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.size(), 1);
		
		// check the info
		assertEquals(new_list.get(0).getId(), 1);
		assertEquals(new_list.get(0).getAuthor(), "josh");
		assertEquals(new_list.get(0).getTitle(), "anime");
		assertEquals(new_list.get(0).getBody(), "new body");
		assertEquals(new_list.get(0).getTags(), "a b c d");
		
	}
	
	/*******
	 * <p> Method: delete_post() </p>
	 *
	 * <p> Description: delete a post to database </p>
	 *
	 */
	@Test
	@Order(4)
	void delete_post() {
		System.out.println("test 4");
		
		// turn read on
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
		
		// try to delete
		ControllerDiscussion.performDeletePost();
		
		// make sure list is 1
		ArrayList<DiscussionPost> new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.size(), 1);
		
		// turn read off
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), false);
		
		// try to delete
		data_base.deletePost(1);
		
		// make sure list is 0
		new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.size(), 0);
	}
	
	/*******
	 * <p> Method: add_reply_test() </p>
	 *
	 * <p> Description: add a reply to database </p>
	 *
	 */
	@Test
	@Order(5)
	void add_reply_test() {
		System.out.println("test 5");
		
		ArrayList<DiscussionPost> new_list = new ArrayList<>(data_base.getAllPosts());
		assertEquals(new_list.size(), 1);
		
		// add reply
		data_base.addReply(1, "josh", "example reply");
		ArrayList<DiscussionReply> reply_list = add_reply();
		// double check the info
		assertEquals(reply_list.get(0).getAuthor(), "josh");
		assertEquals(reply_list.get(0).getBody(), "example reply");
		
		// and size
		assertEquals(reply_list.size(), 1);
		
		// turn read on
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
		
		// try to make another
		ControllerDiscussion.performCreateReply();
		
		// size should still be 1
		assertEquals(reply_list.size(), 1);
	}
	
	/*******
	 * <p> Method: update_reply() </p>
	 *
	 * <p> Description: update a reply to database </p>
	 *
	 */
	@Test
	@Order(6)
	void update_reply() {
		System.out.println("test 6");
		
		// add reply
		data_base.addReply(1, "josh", "example reply");
		ArrayList<DiscussionReply> reply_list = add_reply();
		
		// make sure its added
		assertEquals(reply_list.size(), 1);
		
		// update reply
		data_base.updateReply(1, "new reply");
		reply_list = new ArrayList<>(data_base.getRepliesForPost(1));
		
		// check info
		assertEquals(reply_list.get(0).getAuthor(), "josh");
		assertEquals(reply_list.get(0).getBody(), "new reply");
		
		// turn read on
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
		
		// try to update reply
		ControllerDiscussion.performUpdateReply();
		
		// check info for the preivous one
		assertEquals(reply_list.get(0).getAuthor(), "josh");
		assertEquals(reply_list.get(0).getBody(), "new reply");
	}
	
	/*******
	 * <p> Method: delete_reply() </p>
	 *
	 * <p> Description: delete a reply to database </p>
	 *
	 */
	@Test
	@Order(7)
	void delete_reply() {
		System.out.println("test 7");
		
		// add reply
		data_base.addReply(1, "josh", "example reply");
		ArrayList<DiscussionReply> reply_list = add_reply();	
		
		// turn read on
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), true);
		
		// try to delete reply
		ControllerDiscussion.performDeleteReply();
		
		// check for 1 replies
		reply_list = new ArrayList<>(data_base.getRepliesForPost(1));
		assertEquals(reply_list.size(), 1);
		
		// turn read off
		ControllerDiscussion.performRead();
		assertEquals(con.get_read(), false);
		
		// delete reply
		data_base.deleteReply(1);
		
		// check for 0 replies
		reply_list = new ArrayList<>(data_base.getRepliesForPost(1));
		assertEquals(reply_list.size(), 0);
	}

}
