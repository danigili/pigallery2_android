package com.example.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.data.ApiMedia

class MediaPagingSource(
    private val data: List<ApiMedia>
) : PagingSource<Int, ApiMedia>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ApiMedia> {
        val position = params.key ?: 0
        val pageSize = params.loadSize
        
        val end = (position + pageSize).coerceAtMost(data.size)
        val chunk = data.subList(position, end)
        
        return LoadResult.Page(
            data = chunk,
            prevKey = if (position == 0) null else position - pageSize,
            nextKey = if (end == data.size) null else end
        )
    }

    override fun getRefreshKey(state: PagingState<Int, ApiMedia>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}
