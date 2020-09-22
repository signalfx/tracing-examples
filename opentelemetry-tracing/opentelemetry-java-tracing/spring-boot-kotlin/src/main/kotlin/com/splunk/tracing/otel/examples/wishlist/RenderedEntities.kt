package com.splunk.tracing.otel.examples.wishlist

import java.time.format.DateTimeFormatter

data class RenderedItem(
        val name: String,
        val description: String,
        val url: String,
        val reason: String,
        val addedAt: String,
        val wisherId: Long?,
        val itemId: Long?
)

fun Item.render() = RenderedItem(
        name, description, url, reason,
        addedAt.format(DateTimeFormatter.ISO_DATE_TIME),
        wisher?.id, id
)

data class RenderedWisher(
        val firstName: String,
        val lastName: String,
        val items: List<RenderedItem>,
        val addedAt: String,
        val wisherId: Long?
)

fun Wisher.render() = RenderedWisher(
        firstName, lastName,
        items.sortedWith(compareBy{it.addedAt}).map { it -> it.render() },
        addedAt.format(DateTimeFormatter.ISO_DATE_TIME),
        id
)
