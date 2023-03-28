package ru.gas.humblr.presentation.my_profile

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.gas.humblr.databinding.FriendItemBinding
import ru.gas.humblr.domain.model.Friend
import ru.gas.humblr.domain.utils.AppUtils

class FriendsListAdapter(private val onUserNameClick: (String) -> Unit) :
    RecyclerView.Adapter<FriendsListViewHolder>(), AppUtils {

    private var data: List<Friend?> = emptyList()

    fun setData(data: List<Friend?>) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListViewHolder {
        val binding = FriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            userName.text = item?.name
            created.text = String.format(
                created.context.getString(ru.gas.humblr.R.string.since), item?.created?.toDate()
            )

            userName.paintFlags = userName.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            Glide.with(userIcon.context).load(item?.icon).centerCrop().into(userIcon)

            userName.setOnClickListener {
                item?.name?.let {
                    onUserNameClick(it)
                }
            }
        }
    }


    override fun getItemCount(): Int = data.size
}


class FriendsListViewHolder(val binding: FriendItemBinding) : RecyclerView.ViewHolder(binding.root)