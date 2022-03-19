package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.PostAdapter
import com.example.parstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class FeedFragment : Fragment() {

    lateinit var postRecyclerView: RecyclerView

    lateinit var adapter: PostAdapter

    lateinit var swipeContainer: SwipeRefreshLayout

    var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is where we set up our views and click listeners

        postRecyclerView = view.findViewById(R.id.postRecyclerView)

        // Steps to populate RecyclerView
        // 1. Create layout for each row in list
        // 2. Create data source for each row (this is the Post class)
        // 3. Create adapter that will  bridge data and row layout
        // 4. Set adapter on RecyclerView
        adapter = PostAdapter(requireContext(), allPosts)
        postRecyclerView.adapter = adapter

        // 5. Set layout manager on RecyclerView
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            Log.i(TAG, "Get data")
            queryPosts()
        }
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        queryPosts()
    }

    //Query for all posts in our server
    open fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post Objects
        query.include(Post.KEY_USER)
        // Return posts in descending order: ie newer posts will appear first
        query.addDescendingOrder("createdAt")

        // Only return the most recent 20 posts
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
                                MainActivity.TAG, "Post: " + Post.getDescription() + " , username: " +
                                    Post.getUser()?.username)
                        }

                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                        swipeContainer.setRefreshing(false)
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "FeedFragment"
    }
}