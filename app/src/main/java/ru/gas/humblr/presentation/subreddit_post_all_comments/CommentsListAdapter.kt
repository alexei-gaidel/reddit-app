package ru.gas.humblr.presentation.subreddit_post_all_comments

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.gas.humblr.databinding.CommentItemBinding
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.utils.AppUtils

class CommentsListAdapter(
    private val onUserNameClick: (String) -> Unit, private val onSaveClick: (String?) -> Unit
) : RecyclerView.Adapter<CommentsListViewHolder>(), AppUtils {

    private var data: List<CommentListItem> = emptyList()

    fun setData(data: List<CommentListItem>) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsListViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsListViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            replyAuthor.paintFlags = replyAuthor.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            replyAuthor.text = item?.author
            replyCreated.text = item?.created?.toDate()
            replyText.text = item?.body

            saveComment.setOnClickListener {
                item?.name?.let { onSaveClick(it) }
            }

            replyAuthor.setOnClickListener {
                item?.author?.let {
                    onUserNameClick(it)
                }
            }
        }

    }

    override fun getItemCount(): Int = data.size
}

class CommentsListViewHolder(val binding: CommentItemBinding) :
    RecyclerView.ViewHolder(binding.root)