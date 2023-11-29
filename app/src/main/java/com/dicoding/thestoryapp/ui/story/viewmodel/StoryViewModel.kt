package com.dicoding.thestoryapp.ui.story.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.thestoryapp.data.StoryRepository
import com.dicoding.thestoryapp.model.ResponseDetailStory
import com.dicoding.thestoryapp.model.ResponseGeneral
import com.dicoding.thestoryapp.model.ResponseListStory
import com.dicoding.thestoryapp.model.Story
import com.dicoding.thestoryapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
@Suppress("DEPRECATION")
class StoryViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    fun createStory(desc: String, file: File): LiveData<Result<ResponseGeneral>> = repository.createStory(desc, file)

    fun getListStory(): LiveData<PagingData<Story>> = repository.getListStory().cachedIn(viewModelScope)

    fun getListStoryLocation(): LiveData<Result<ResponseListStory>> = repository.getListStoryLocation()

    fun getDetailStory(id: String): LiveData<Result<ResponseDetailStory>> = repository.getDetailStory(id)


}