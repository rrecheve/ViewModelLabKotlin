package es.unex.giiis.asee.viewmodellabkotlin.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.unex.giiis.asee.viewmodellabkotlin.data.RepoRepository
import es.unex.giiis.asee.viewmodellabkotlin.data.model.Repo

internal class MainActivityViewModel(repository: RepoRepository) : ViewModel() {
    private val mRepository: RepoRepository
    private val mRepos: LiveData<List<Repo>>
    private var mUsername = ""

    init {
        mRepository = repository
        mRepos = mRepository.currentRepos
    }

    fun setUsername(username: String) {
        mUsername = username
        mRepository.setUsername(username)
    }

    fun onRefresh() {
        mRepository.doFetchRepos(mUsername)
    }

    val repos: LiveData<List<Repo>>
        get() = mRepos
}
