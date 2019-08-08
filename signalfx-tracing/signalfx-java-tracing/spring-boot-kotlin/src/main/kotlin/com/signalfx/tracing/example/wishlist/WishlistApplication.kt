package com.signalfx.tracing.example.wishlist

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class WishlistApplication

fun main(args: Array<String>) {
    runApplication<WishlistApplication>(*args)
}

@Configuration
class WishlistConfiguration {

    @Bean
    fun loadWishlistEntries(wisherRepository: WisherRepository,
                            itemRepository: ItemRepository) = ApplicationRunner {
        val aaronson = wisherRepository.save(Wisher("A.", "Aaronson"))
        itemRepository.save(Item(
                "Pentax K1000",
                "35mm camera",
                "http://example.com",
                "For general photos.",
                aaronson))
        itemRepository.save(Item(
                "Marantz PMD-221",
                "Cassette recorder",
                "http://example.com",
                "Portably records audio.",
                aaronson))
        itemRepository.save(Item(
                "Tops Legal Pads",
                "Canary yellow",
                "http://example.com",
                "For taking notes.",
                aaronson))

        val zowkowski = wisherRepository.save(Wisher("Mr.", "Zowkowski"))
        itemRepository.save(Item(
                "ARP Pro Soloist",
                "A synthesizer",
                "http://example.com",
                "Generates and modifies tones.",
                zowkowski))
        itemRepository.save(Item(
                "Ludwig Supraphonic",
                "Bronze snare drum",
                "http://example.com",
                "Keeps beat.",
                zowkowski))
        itemRepository.save(Item(
                "Kramer 450G",
                "Electric guitar",
                "http://example.com",
                "Makes intriguing noises.",
                zowkowski))
    }
}