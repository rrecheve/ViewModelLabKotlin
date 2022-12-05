package es.unex.giiis.asee.viewmodellabkotlin.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import es.unex.giiis.asee.viewmodellabkotlin.AppExecutors
import es.unex.giiis.asee.viewmodellabkotlin.data.model.Repo
import es.unex.giiis.asee.viewmodellabkotlin.data.network.RepoNetworkDataSource
import es.unex.giiis.asee.viewmodellabkotlin.data.roombd.RepoDao
import java.util.*

class RepoRepository private constructor(
    repoDao: RepoDao,
    repoNetworkDataSource: RepoNetworkDataSource
) {
    private val mRepoDao: RepoDao
    private val mRepoNetworkDataSource: RepoNetworkDataSource
    private val userFilterLiveData = MutableLiveData<String>()
    private val lastUpdateTimeMillisMap: MutableMap<String, Long> = HashMap()

    init {
        mRepoDao = repoDao
        mRepoNetworkDataSource = repoNetworkDataSource
        // LiveData that fetches repos from network
        val networkData: LiveData<Array<Repo>> = mRepoNetworkDataSource.currentRepos
        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        networkData.observeForever { newReposFromNetwork: Array<Repo> ->
            AppExecutors.instance?.diskIO()?.execute {

                // Deleting cached repos of user
                if (newReposFromNetwork.isNotEmpty()) {
                    mRepoDao.deleteReposByUser(newReposFromNetwork[0].owner.login)
                }
                // Insert our new repos into local database
                mRepoDao.bulkInsert(Arrays.asList(*newReposFromNetwork))
                Log.d(LOG_TAG, "New values inserted in Room")
            }
        }
    }

    fun setUsername(username: String) {
        userFilterLiveData.value = username
        AppExecutors.instance?.diskIO()?.execute {
            if (isFetchNeeded(username)) {
                doFetchRepos(username)
            }
        }
    }

    fun doFetchRepos(username: String) {
        Log.d(LOG_TAG, "Fetching Repos from Github")
        AppExecutors.instance?.diskIO()?.execute {
            mRepoDao.deleteReposByUser(username)
            mRepoNetworkDataSource.fetchRepos(username)
            lastUpdateTimeMillisMap.put(username, System.currentTimeMillis())
        }
    }

    /**
     * Database related operations
     */
    val currentRepos: LiveData<List<Repo>>  =
          Transformations.switchMap(userFilterLiveData){
            mRepoDao.getReposByOwner(it)}

    /**
     * Checks if we have to update the repos data.
     * @return Whether a fetch is needed
     */
    private fun isFetchNeeded(username: String): Boolean {
        var lastFetchTimeMillis = lastUpdateTimeMillisMap[username]
        lastFetchTimeMillis = lastFetchTimeMillis ?: 0L
        val timeFromLastFetch = System.currentTimeMillis() - lastFetchTimeMillis
        return timeFromLastFetch > MIN_TIME_FROM_LAST_FETCH_MILLIS || mRepoDao.getNumberReposByUser(username) == 0
    }

    companion object {
        private val LOG_TAG = RepoRepository::class.java.simpleName

        // For Singleton instantiation
        private var sInstance: RepoRepository? = null
        private const val MIN_TIME_FROM_LAST_FETCH_MILLIS: Long = 30000
        @Synchronized
        fun getInstance(dao: RepoDao, nds: RepoNetworkDataSource): RepoRepository? {
            Log.d(LOG_TAG, "Getting the repository")
            if (sInstance == null) {
                sInstance = RepoRepository(dao, nds)
                Log.d(LOG_TAG, "Made new repository")
            }
            return sInstance
        }
    }
}