package es.unex.giiis.asee.viewmodellabkotlin.data.network

import es.unex.giiis.asee.viewmodellabkotlin.data.model.Repo


interface OnReposLoadedListener {
    fun onReposLoaded(repos: List<Repo>)
}
