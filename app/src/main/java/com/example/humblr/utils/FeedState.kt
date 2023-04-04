package com.example.humblr.utils

sealed class FeedState {
    object NewFeed: FeedState()
    object HotFeed: FeedState()
}