package com.dicoding.thestoryapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.thestoryapp.R
import com.dicoding.thestoryapp.databinding.ActivityListStoryBinding
import com.dicoding.thestoryapp.ui.auth.LoginActivity
import com.dicoding.thestoryapp.ui.story.adapter.LoadingStateAdapter
import com.dicoding.thestoryapp.ui.story.adapter.StoryAdapter
import com.dicoding.thestoryapp.ui.story.viewmodel.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListStoryActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityListStoryBinding
    private val listStoryViewModel: StoryViewModel by viewModels()
    @Inject lateinit var listStoryAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityListStoryBinding.inflate(layoutInflater)
        val view = viewbinding.root
        setContentView(view)
        supportActionBar?.title = "List Story"
        initRecycleview()


        setData()

        viewbinding.swipeRefresh.setOnRefreshListener {
            viewbinding.swipeRefresh.isRefreshing = false
            isLoading(true)
            setData()

        }

        viewbinding.AddStory.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }

        viewbinding.fabMaps.setOnClickListener{
            startActivity(Intent(this, MapsStoryActivity::class.java))
        }

        listStoryAdapter.setOnItemClicked(object : StoryAdapter.OnItemClickListener{
            override fun onItemClicked(id: String) {
                val intent = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.STORY_ID, id)
                startActivity(intent)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setData(){
        isLoading(true)
        listStoryViewModel.getListStory().observe(this) { responseListStory ->
            listStoryAdapter.submitData(lifecycle, responseListStory)
            listStoryAdapter.addLoadStateListener { listener ->
                if (listener.refresh != LoadState.Loading) {
                    isLoading(false)
                }
                if (listener.refresh is LoadState.Error) {
                    val data = listener.refresh as LoadState.Error
                    if (data.error.message.equals("HTTP 401 Unauthorized")) {
                        Toast.makeText(this@ListStoryActivity, "Your token expired, please relogin!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ListStoryActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        PreferenceManager.getDefaultSharedPreferences(this@ListStoryActivity).edit().clear().apply()
                        startActivity(intent)
                        finish()
                    } else {
                        viewbinding.llError.isVisible = true
                        viewbinding.tvRetry.setOnClickListener {
                            viewbinding.llError.isVisible = false
                            setData()
                        }
                    }
                    Log.e(ListStoryActivity::class.java.simpleName, "Error activity ${data.error.message}")
                    Log.e(ListStoryActivity::class.java.simpleName, "Error activity localized ${data.error.localizedMessage}")
                }

            }
        }
    }

    private fun initRecycleview() {
        with(viewbinding) {
            rvListStory.layoutManager = LinearLayoutManager(this@ListStoryActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = listStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listStoryAdapter.retry()
                }
            )
        }


    }

    private fun isLoading(isL: Boolean) {
        if (isL) {
            viewbinding.rlLoading.visibility = View.VISIBLE
        } else {
            viewbinding.rlLoading.visibility = View.GONE
        }
    }

}