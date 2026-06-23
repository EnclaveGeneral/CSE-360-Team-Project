package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: ReplyList Class </p>
 * 
 * <p> Description: This ReplyList class represents a collection of Reply entities in the system.
 * It supports storing all replies to all stored posts as well as any subset of replies (e.g., a 
 * subset reflecting the results of a search or all replies belonging to a specific post). The 
 * subset may be empty, contain one or more elements, or be arbitrarily large. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Weiye (Richard) Zhang
 * 
 * @version 1.00	2026-06-15 
 */
public class ReplyList {

	/*
	 * These are the private attributes for this collection object
	 */
	private List<Reply> replies;


	/*****
	 * <p> Method: ReplyList() </p>
	 * 
	 * <p> Description: This default constructor initializes an empty list of replies. Used when
	 * building a list incrementally by adding replies one at a time. </p>
	 */
	// Default constructor initializes an empty reply list.
	public ReplyList() {
		this.replies = new ArrayList<>();
	}


	/*****
	 * <p> Method: ReplyList(List&lt;Reply&gt; replies) </p>
	 * 
	 * <p> Description: This constructor initializes the reply list with an existing list of Reply
	 * objects. Used when populating the list from a database query result. </p>
	 * 
	 * @param replies specifies an existing List of Reply objects to initialize this collection with
	 * 
	 */
	// Constructor to initialize the reply list with an existing list of replies.
	public ReplyList(List<Reply> replies) {
		this.replies = replies;
	}


	/*****
	 * <p> Method: void addReply(Reply reply) </p>
	 * 
	 * <p> Description: This method adds a Reply object to the end of the list. </p>
	 * 
	 * @param reply is the Reply object to be added to this list.
	 * 
	 */
	// Adds a reply to the list.
	public void addReply(Reply reply) {
		this.replies.add(reply);
	}


	/*****
	 * <p> Method: void removeReply(int replyId) </p>
	 * 
	 * <p> Description: This method removes the first Reply object whose replyId matches the
	 * specified value. Iterates forward and breaks on the first match to avoid index skipping
	 * after removal. </p>
	 * 
	 * @param replyId is an int that specifies the unique identifier of the reply to be removed.
	 * 
	 */
	// Removes the reply with the matching replyId from the list.
	public void removeReply(int replyId) {
		for (int i = 0; i < this.replies.size(); ++i) {
			if (this.replies.get(i).getReplyId() == replyId) {
				this.replies.remove(i);
				break;
			}
		}
	}


	/*****
	 * <p> Method: Reply getReply(int replyId) </p>
	 * 
	 * <p> Description: This method searches the list for a Reply object whose replyId matches
	 * the specified value and returns it. Returns an empty Reply object if no match is found. </p>
	 * 
	 * @param replyId is an int that specifies the unique identifier of the reply to retrieve.
	 * 
	 * @return the Reply object with the matching replyId, or an empty Reply if not found.
	 *
	 */
	// Returns the reply with the matching replyId, or an empty Reply if not found.
	public Reply getReply(int replyId) {
		Reply correctReply = new Reply();

		for (int i = 0; i < this.replies.size(); ++i) {
			if (this.replies.get(i).getReplyId() == replyId) {
				correctReply = this.replies.get(i);
				break;
			}
		}

		return correctReply;
	}


	/*****
	 * <p> Method: List&lt;Reply&gt; getRepliesByPostId(int postId) </p>
	 * 
	 * <p> Description: This method returns a subset of replies whose parentPostId matches the
	 * specified postId. This supports the requirement to retrieve any subset of replies, such as
	 * all replies belonging to a specific post. The returned subset may be empty if no replies
	 * exist for the given post. </p>
	 * 
	 * @param postId is an int that specifies the unique identifier of the parent post.
	 * 
	 * @return a List of Reply objects whose parentPostId matches the specified postId.
	 *
	 */
	// Returns a subset of replies that belong to the specified parent post.
	public List<Reply> getRepliesByPostId(int postId) {
		List<Reply> subset = new ArrayList<>();

		for (int i = 0; i < this.replies.size(); ++i) {
			if (this.replies.get(i).getParentPostId() == postId) {
				subset.add(this.replies.get(i));
			}
		}

		return subset;
	}


	/*****
	 * <p> Method: List&lt;Reply&gt; getAllReplies() </p>
	 * 
	 * <p> Description: This getter returns the full list of Reply objects stored in this
	 * collection. </p>
	 * 
	 * @return a List of all Reply objects in this collection.
	 *
	 */
	// Returns the full list of replies.
	public List<Reply> getAllReplies() {
		return this.replies;
	}


	/*****
	 * <p> Method: void clear() </p>
	 * 
	 * <p> Description: This method clears all Reply objects from the list, resetting it to an
	 * empty state. Used when refreshing the list from the database. </p>
	 * 
	 */
	// Clears all replies from the list.
	public void clear() {
		this.replies = new ArrayList<>();
	}


	/*****
	 * <p> Method: int size() </p>
	 * 
	 * <p> Description: This method returns the number of Reply objects currently stored in
	 * this collection. </p>
	 * 
	 * @return an int representing the number of replies in this list.
	 *
	 */
	// Returns the number of replies in the list.
	public int size() {
		return this.replies.size();
	}

}