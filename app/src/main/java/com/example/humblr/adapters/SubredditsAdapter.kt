package com.example.humblr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.humblr.R
import com.example.humblr.models.Post
import com.example.humblr.models.SubredditModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SubredditsAdapter(private val _onSubscribeClick: (SubredditModel) -> Unit) : RVAdapter<SubredditModel, SubredditsAdapter.ViewHolderSubreddits>() {

    inner class ViewHolderSubreddits(view: View) : BaseViewHolder<SubredditModel>(view) {
        @ExperimentalCoroutinesApi

        private val _name: TextView by lazy { itemView.findViewById(R.id.subreddit_title) }
        private val _description: TextView by lazy { itemView.findViewById(R.id.subreddit_description) }
        private val _button: ImageButton by lazy {itemView.findViewById(R.id.subscribe_button)}

        override fun bind(data: SubredditModel) {

            _name.text = data.data.display_name_prefixed
            _description.text = data.data.title

            if (data.data.user_is_subscriber){
                _button.setImageResource(R.drawable.icon_subscribed)
                _name.background = context.resources.getDrawable(R.drawable.text_view_item_subscribed)
            }else{
                _button.setImageResource(R.drawable.icon_unsubscribed)
                _name.background = context.resources.getDrawable(R.drawable.text_view_item_unsubscribed)
            }

            _button.setOnClickListener {
                _onSubscribeClick(data)
                data.data.user_is_subscriber = !data.data.user_is_subscriber
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolderSubreddits(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_subreddit,
                parent,
                false
            )
        )
}