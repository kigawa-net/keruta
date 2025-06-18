package net.kigawa.keruta.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controller for handling the welcome page.
 * Redirects to the admin dashboard.
 */
@Controller
@RequestMapping("/welcome")
class RootController {

    /**
     * Redirects to the admin dashboard.
     *
     * @return The admin dashboard view name
     */
    @GetMapping
    fun root(): String {
        return "admin/dashboard"
    }
}
