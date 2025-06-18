package net.kigawa.keruta.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controller for handling the root path.
 */
@Controller
@RequestMapping("/")
class RootController {

    /**
     * Redirects the root path to the admin dashboard.
     *
     * @return The admin dashboard view name
     */
    @GetMapping
    fun root(): String {
        return "admin/dashboard"
    }
}