package es.unex.giiis.asee.viewmodellabkotlin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.unex.giiis.asee.viewmodellabkotlin.data.RepoRepository

class MainViewModelFactory(repository: RepoRepository) : ViewModelProvider.NewInstanceFactory() {
    private val mRepository: RepoRepository

    init {
        mRepository = repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(mRepository) as T
    }
}