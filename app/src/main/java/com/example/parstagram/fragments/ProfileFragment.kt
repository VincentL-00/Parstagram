package com.example.parstagram.fragments

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment : FeedFragment() {

    override fun queryPosts() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects
        query.include(Post.KEY_USER)
        // Only return posts from currently signed in user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())
        // Return posts in descending order: ie newer posts will appear first
        query.addDescendingOrder("createdAt")
        // Return most recent 20 posts
        query.limit = 20
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something has went wrong
                    Log.e(MainActivity.TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        for (Post in posts) {
                            Log.i(
                                MainActivity.TAG,
                                "Post: " + Post.getDescription() + " , username: " +
                                        Post.getUser()?.username
                            )
                        }

                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}