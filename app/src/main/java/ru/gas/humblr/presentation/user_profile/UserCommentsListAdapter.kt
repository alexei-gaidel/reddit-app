package ru.gas.humblr.presentation.user_profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gas.humblr.databinding.CommentItemBinding
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.utils.AppUtils

class UserCommentsListAdapter(private val onSaveClick: (String?) -> Unit) :
    PagingDataAdapter<CommentListItem, UserCommentsListViewHolder>(DiffutilCallback()), AppUtils {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCommentsListViewHolder {
        val binding =
            CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserCommentsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserCommentsListViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            replyAuthor.text = item?.author
            replyCreated.text = item?.created?.toDate()
            replyText.text = item?.body

            saveComment.setOnClickListener {
                item?.name?.let { onSaveClick(it) }
            }
        }
    }
}


class UserCommentsListViewHolder(val binding: CommentItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class DiffutilCallback : DiffUtil.ItemCallback<CommentListItem>() {
    override fun areItemsTheSame(oldItem: CommentListItem, newItem: CommentListItem): Boolean =
        oldItem.body == newItem.body

    override fun areContentsTheSame(oldItem: CommentListItem, newItem: CommentListItem): Boolean =
        oldItem == newItem

}
