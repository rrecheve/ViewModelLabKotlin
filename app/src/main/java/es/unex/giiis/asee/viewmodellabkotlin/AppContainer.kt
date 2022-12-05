package es.unex.giiis.asee.viewmodellabkotlin

import android.content.Context
import es.unex.giiis.asee.viewmodellabkotlin.data.RepoRepository
import es.unex.giiis.asee.viewmodellabkotlin.data.network.RepoNetworkDataSource
import es.unex.giiis.asee.viewmodellabkotlin.data.roombd.RepoDatabase
import es.unex.giiis.asee.viewmodellabkotlin.ui.MainViewModelFactory

class AppContainer(context: Context?) {
    private val database: RepoDatabase
    private val networkDataSource: RepoNetworkDataSource
    var repository: RepoRepository
    var factory: MainViewModelFactory

    init {
        database = RepoDatabase.getInstance(context)!!
        networkDataSource = RepoNetworkDataSource.instance!!
        repository = RepoRepository.getInstance(database.repoDao(), networkDataSource)!!
        factory = MainViewModelFactory(repository)
    }
}