package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: PostList Class </p>
 * 
 * <p> Description: This PostList class represents a collection of Post entities in the system.
 * It supports storing all current posts as well as any subset of posts (e.g., a subset reflecting
 * the results of a search). The subset may be empty, contain one or more elements, or be 
 * arbitrarily large. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Weiye (Richard) Zhang
 * 
 * @version 1.00	2026-06-15 
 */
public class PostList {

	/*
	 * These are the private attributes for this collection object
	 */
	private List<Post> posts;


	/*****
	 * <p> Method: PostList() </p>
	 * 
	 * <p> Description: This default constructor initializes an empty list of posts. Used when
	 * building a list incrementally by adding posts one at a time. </p>
	 */
	// Default constructor initializes an empty post list.
	public PostList() {
		this.posts = new ArrayList<>();
	}


	/*****
	 * <p> Method: PostList(List&lt;Post&gt; posts) </p>
	 * 
	 * <p> Description: This constructor initializes the post list with an existing list of Post
	 * objects. Used when populating the list from a database query result. </p>
	 * 
	 * @param posts specifies an existing List of Post objects to initialize this collection with
	 * 
	 */
	// Constructor to initialize the post list with an existing list of posts.
	public PostList(List<Post> posts) {
		this.posts = posts;
	}


	/*****
	 * <p> Method: void addPost(Post post) </p>
	 * 
	 * <p> Description: This method adds a Post object to the end of the list. </p>
	 * 
	 * @param post is the Post object to be added to this list.
	 * 
	 */
	// Adds a post to the list.
	public void addPost(Post post) {
		this.posts.add(post);
	}


	/*****
	 * <p> Method: void removePost(int postId) </p>
	 * 
	 * <p> Description: This method removes the first Post object whose postId matches the
	 * specified value. Iterates forward and breaks on the first match to avoid index skipping
	 * after removal. </p>
	 * 
	 * @param postId is an int that specifies the unique identifier of the post to be removed.
	 * 
	 */
	// Removes the post with the matching postId from the list.
	public void removePost(int postId) {
		for (int i = 0; i < this.posts.size(); ++i) {
			if (this.posts.get(i).getPostId() == postId) {
				posts.remove(i);
				break;
			}
		}
	}


	/*****
	 * <p> Method: Post getPost(int postId) </p>
	 * 
	 * <p> Description: This method searches the list for a Post object whose postId matches
	 * the specified value and returns it. Returns an empty Post object if no match is found. </p>
	 * 
	 * @param postId is an int that specifies the unique identifier of the post to retrieve.
	 * 
	 * @return the Post object with the matching postId, or an empty Post if not found.
	 *
	 */
	// Returns the post with the matching postId, or an empty Post if not found.
	public Post getPost(int postId) {
		Post fetchPost = new Post();

		for (int i = 0; i < this.posts.size(); ++i) {
			if (this.posts.get(i).getPostId() == postId) {
				fetchPost = posts.get(i);
				break;
			}
		}

		return fetchPost;
	}


	/*****
	 * <p> Method: List&lt;Post&gt; getAllPosts() </p>
	 * 
	 * <p> Description: This getter returns the full list of Post objects stored in this
	 * collection. </p>
	 * 
	 * @return a List of all Post objects in this collection.
	 *
	 */
	// Returns the full list of posts.
	public List<Post> getAllPosts() {
		return this.posts;
	}


	/*****
	 * <p> Method: void clear() </p>
	 * 
	 * <p> Description: This method clears all Post objects from the list, resetting it to an
	 * empty state. Used when refreshing the list from the database. </p>
	 * 
	 */
	// Clears all posts from the list.
	public void clear() {
		this.posts = new ArrayList<>();
	}


	/*****
	 * <p> Method: int size() </p>
	 * 
	 * <p> Description: This method returns the number of Post objects currently stored in
	 * this collection. </p>
	 * 
	 * @return an int representing the number of posts in this list.
	 *
	 */
	// Returns the number of posts in the list.
	public int size() {
		return this.posts.size();
	}

}