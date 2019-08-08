package com.signalfx.tracing.example.wishlist

import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@Controller
class WishlistController {

    @GetMapping("/")
    fun rootPath(model: Model): String {
        return "redirect:/wishlist"
    }

    @GetMapping("/wishlist")
    fun wishlist(model: Model): String {
        model["title"] = "My Spring Boot Wishlist"
        return "wishlist"
    }

    @GetMapping("/error")
    fun errorPath(model: Model): String {
        model["title"] = "Something's wrong"
        model["error"] = "We are experiencing an issue."
        return "error"
    }

}

@Controller
class WisherController(val repository: WisherRepository) {

    @PostMapping("/wishers")
    fun createWisher(@Valid wisher: Wisher, result: BindingResult, response: HttpServletResponse, model: Model): String {
        if (result.hasErrors()) {
            model["error"] = result.allErrors.toString()
            response.status = HttpServletResponse.SC_BAD_REQUEST
        } else {
            val created = repository.save(wisher)
            model["wishers"] = created.render()
        }
        return "wishlist"
    }

    @GetMapping("/wishers")
    fun getWisher(wisher: Wisher, result: BindingResult, response: HttpServletResponse, model: Model): String {
        var wishers: List<RenderedWisher>

        val id = wisher.id
        if (id != null) {
            wishers = listOf<RenderedWisher>(repository.findById(id).get().render())
        } else {
            val firstName = wisher.firstName
            val lastName = wisher.lastName
            if (!firstName.isNullOrEmpty()) {
                if (!lastName.isNullOrEmpty()) {
                    wishers = repository.findByFirstNameAndLastNameContainingIgnoreCase(firstName, lastName).map() { it.render() }
                } else {
                    wishers = repository.findByFirstNameContainingIgnoreCase(firstName).map() { it.render() }
                }
            } else if (!lastName.isNullOrEmpty()) {
                wishers = repository.findByLastNameContainingIgnoreCase(lastName).map { it.render() }
            } else {
                wishers = repository.findAll().map { it.render() }
            }
        }

        if (wishers.isEmpty()) {
            response.status = HttpServletResponse.SC_NOT_FOUND
        }

        model["wishers"] = wishers.sortedWith(compareBy{it.lastName})
        return "wishlist"
    }

    @PostMapping("/wishers/delete/{wisherId}")
    fun deleteById(@PathVariable wisherId: Long, response: HttpServletResponse, model: Model): String {
        var wisher : Wisher
        try {
            wisher = repository.findById(wisherId).get()
            repository.delete(wisher)
        } catch (e: NoSuchElementException) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            model["error"] = e.toString()
        }
        return "wishlist"
    }
}

@Controller
class ItemController(val itemRepository: ItemRepository, val wisherRepository: WisherRepository) {

    @PostMapping("/items")
    fun createByName(@Valid item: Item, result: BindingResult, response: HttpServletResponse, model: Model): String {
        if (result.hasErrors()) {
            model["error"] = result.allErrors.toString()
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return "wishlist"
        }

        val name = item.name
        val description = item.description
        val url = item.url
        val reason = item.reason

        var wisher = item.wisher

        itemRepository.save(Item(
                name,
                description,
                url,
                reason,
                wisher
        ))

        model["wishers"] = List(1) { wisher?.render() }
        return "wishlist"
    }

    @PostMapping("/items/delete/{itemId}")
    fun deleteById(@PathVariable itemId: Long, response: HttpServletResponse, model: Model): String {
        var item : Item
        try {
            item = itemRepository.findById(itemId).get()
            val wisher = item?.wisher
            itemRepository.delete(item)
            wisher?.items?.remove(item)
            model["wishers"] = List(1) { wisher?.render() }
        } catch (e: NoSuchElementException) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            model["error"] = e.toString()
        }
        return "wishlist"
    }
}