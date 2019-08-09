package com.signalfx.tracing.example.wishlist

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Long> {
    fun findByNameContainingIgnoreCase(name: String): Iterable<Item>
}

@Repository
interface WisherRepository : JpaRepository<Wisher, Long> {
    fun findByLastNameContainingIgnoreCase(lastName: String): Iterable<Wisher>
    fun findByFirstNameContainingIgnoreCase(lastName: String): Iterable<Wisher>
    fun findByFirstNameAndLastNameContainingIgnoreCase(firstName: String, lastName: String): Iterable<Wisher>
}