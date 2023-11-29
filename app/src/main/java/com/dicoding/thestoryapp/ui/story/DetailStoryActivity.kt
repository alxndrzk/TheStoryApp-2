package com.dicoding.thestoryapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.thestoryapp.databinding.ActivityDetailStoryBinding
import com.dicoding.thestoryapp.ui.auth.LoginActivity
import com.dicoding.thestoryapp.ui.story.viewmodel.StoryViewModel
import com.dicoding.thestoryapp.util.Result
import com.dicoding.thestoryapp.util.changeFormatDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryActivity : AppCompatActivity() {
    companion object{
        const val STORY_ID = "STORY_ID"
    }
    private lateinit var viewbinding: ActivityDetailStoryBinding
    private val detailstoryViewModel: StoryViewModel by viewModels()

    private var id: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        val view = viewbinding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = "Detail Story"

        if (intent.extras != null) {
            id = intent.getStringExtra(STORY_ID).toString()
        }

        isLoading(true)
        setDetailStoryData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDetailStoryData() {
        detailstoryViewModel.getDetailStory(id).observe(this){ detailStoryResponse ->
            when (detailStoryResponse) {
                is Result.Loading -> {
                    isLoading(true)
                }
                is Result.Success -> {
                    isLoading(false)
                    with(viewbinding) {
                        Glide.with(this@DetailStoryActivity)
                            .load(detailStoryResponse.data?.story?.photoUrl)
                            .into(img)
                        date.text = "Date created: ${changeFormatDate(detailStoryResponse.data?.story?.createdAt as String)}"
                        createdBy.text = detailStoryResponse.data.story.name
                        description.text = detailStoryResponse.data.story.description
                    }
                }
                else -> {
                    isLoading(false)
                    if (detailStoryResponse.code == 401) {
                        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    Toast.makeText(this@DetailStoryActivity, detailStoryResponse.message, Toast.LENGTH_SHORT).show()
                    finish()

                }
            }
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