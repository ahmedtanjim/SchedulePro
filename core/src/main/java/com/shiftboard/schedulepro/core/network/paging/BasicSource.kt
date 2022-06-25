package com.shiftboard.schedulepro.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.lang.Exception


abstract class BasicSource<T: Any> : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val key = params.key ?: 1
            val lastKey = (key - (params.loadSize + 1)).takeIf { it >= 0 }
            val items = request(params.key ?: 1, params.loadSize)
            val nextKey = if (items.size == params.loadSize) key + items.size else null

            LoadResult.Page(items, lastKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    abstract suspend fun request(page: Int, batch: Int): List<T>
}

fun <T: Any> simplePagingSource(fetchPage: suspend (page: Int, batch: Int)->List<T>): PagingSource<Int, T> {
    return object : BasicSource<T>() {
        override suspend fun request(page: Int, batch: Int): List<T> {
            return fetchPage(page, batch)
        }

        override fun getRefreshKey(state: PagingState<Int, T>): Int? {
            return state.anchorPosition
        }
    }
}

