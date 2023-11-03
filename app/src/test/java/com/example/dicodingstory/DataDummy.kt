package com.example.dicodingstory

import com.example.dicodingstory.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "story-akTVeH6SRSwYmaN2",
                name = "Juli",
                description = "test${i + 1}",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1699023315143_LSa50i-E.jpg",
                createdAt = "2023-11-03T14:55:15.144",
                lat = -6.0789447,
                lon = 106.6886646
            )
            items.add(story)
        }
        return items
    }
}