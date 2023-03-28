package ru.gas.humblr.presentation.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.gas.humblr.R
import ru.gas.humblr.databinding.PostItemBinding
import ru.gas.humblr.domain.model.PostItem
import ru.gas.humblr.domain.utils.AppUtils

class SavedPostsListAdapter(private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<PostListViewHolder>(), AppUtils {

    private var data: List<PostItem> = emptyList()

    fun setData(data: List<PostItem>) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            val comments = getFormattedNumber(item?.numComments ?: 0)
            commentsCount.text = String.format(
                commentsCount.context.getString(R.string.comments), comments
            )
            author.text = item?.author
            created.text = item?.created?.toDate()
            postTitle.text = item?.title

            item?.body?.let {
                fullPost.isVisible = true
                fullPost.text = it
            }

            item?.image.let {
                titleImg.isVisible = true
                Glide.with(titleImg.context).load(it).centerCrop().into(titleImg)
            }
            holder.binding.root.setOnClickListener {
                item?.id?.let {
                    val name = it.substringAfter("t3_")
                    onItemClick(name)
                }
            }

        }

    }


    override fun getItemCount(): Int = data.size
}


class PostListViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)