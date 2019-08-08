package com.signalfx.tracing.example.wishlist

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.OneToMany
import javax.persistence.CascadeType
import javax.persistence.FetchType
import javax.persistence.GenerationType
import javax.persistence.JoinColumn
import javax.validation.constraints.NotBlank

@Entity
class Item(
        @get: NotBlank var name: String = "",
        var description: String = "",
        @get: NotBlank var url: String = "",
        var reason: String = "",

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wisherId")
        var wisher: Wisher?,

        var addedAt: LocalDateTime = LocalDateTime.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)


@Entity
class Wisher(
        @get: NotBlank var firstName: String = "",
        @get: NotBlank var lastName: String = "",

        @OneToMany(cascade = [CascadeType.REMOVE], orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "wisher")
        var items: MutableSet<Item> = LinkedHashSet(),

        var addedAt: LocalDateTime = LocalDateTime.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)
