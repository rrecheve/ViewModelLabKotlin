package es.unex.giiis.asee.viewmodellabkotlin.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.unex.giiis.asee.executorslabkotlin.R
import es.unex.giiis.asee.viewmodellabkotlin.AppContainer
import es.unex.giiis.asee.viewmodellabkotlin.MyApplication
import es.unex.giiis.asee.viewmodellabkotlin.data.RepoRepository
import es.unex.giiis.asee.viewmodellabkotlin.data.model.Repo
import es.unex.giiis.asee.viewmodellabkotlin.data.network.OnReposLoadedListener


class MainActivity : AppCompatActivity(), MyAdapter.OnListInteractionListener{

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MyAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var mProgressBar: ProgressBar? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mUsername = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSwipeRefreshLayout = findViewById<View>(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        mRecyclerView = findViewById<View>(R.id.repoList) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = MyAdapter(emptyList(), this)
        mRecyclerView!!.adapter = mAdapter

        val searchBox = findViewById<EditText>(R.id.searchBox)
        val searchButton = findViewById<Button>(R.id.searchButton)
        mProgressBar = findViewById(R.id.progressBar)

        val appContainer: AppContainer = (application as MyApplication).appContainer!!
        val mViewModel =
            ViewModelProvider(this, appContainer.factory)[MainActivityViewModel::class.java]

        val reposObserver = Observer<List<Repo>> {
            mAdapter!!.swap(it)
            // Show the repo list or the loading screen based on whether the repos data exists and is loaded
            if (it.isNotEmpty()) showReposDataView()
            else showLoading()
        }
        mViewModel.repos!!.observe(this, reposObserver)

        searchButton.setOnClickListener { view: View? ->
            mViewModel?.setUsername(searchBox.text.toString())
        }

        mSwipeRefreshLayout?.setOnRefreshListener {
            mViewModel?.onRefresh()
        }
    }

    override fun onListInteraction(url: String?) {
        val webpage = Uri.parse(url)
        val webIntent = Intent(Intent.ACTION_VIEW, webpage)
        startActivity(webIntent)
    }

    private fun showLoading() {
        mProgressBar!!.visibility = View.VISIBLE
        mRecyclerView?.setVisibility(View.INVISIBLE)
    }

    private fun showReposDataView() {
        mProgressBar!!.visibility = View.GONE
        mSwipeRefreshLayout!!.isRefreshing = false
        mRecyclerView?.setVisibility(View.VISIBLE)
    }
}
