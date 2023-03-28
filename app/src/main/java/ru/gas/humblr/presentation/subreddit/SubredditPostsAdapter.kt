package ru.gas.humblr.presentation.subreddit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.gas.humblr.databinding.PostsListItemBinding
import ru.gas.humblr.domain.model.SubredditPostsItem


class SubredditPostsAdapter(
    private val onClick: (SubredditPostsItem) -> Unit,
) : RecyclerView.Adapter<SubredditPostsViewHolder>() {

    private var data: List<SubredditPostsItem> = emptyList()

    fun setData(data: List<SubredditPostsItem>) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditPostsViewHolder {
        val binding =
            PostsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubredditPostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubredditPostsViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            author.text = item?.author
            subredditTitle.text = item?.title



            if (item?.img != null) {
                subredditPreview.isVisible = true
                Glide.with(subredditPreview.context).load(item.img).centerCrop()
                    .into(subredditPreview)
            }


            holder.binding.root.setOnClickListener {
                item?.let {
                    onClick(item)
                }
            }

        }
    }


    override fun getItemCount(): Int = data.size
}

class SubredditPostsViewHolder(val binding: PostsListItemBinding) :
    RecyclerView.ViewHolder(binding.root)