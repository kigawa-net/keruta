package net.kigawa.keruta.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/go-to-admin")
class RootController {

    @GetMapping
    fun redirectToAdmin(): String {
        return "redirect:/admin"
    }
}
