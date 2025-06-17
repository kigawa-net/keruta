package net.kigawa.keruta.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Controller for login page.
 */
@Controller
class LoginController {

    /**
     * Login page.
     *
     * @return The login page
     */
    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }
}