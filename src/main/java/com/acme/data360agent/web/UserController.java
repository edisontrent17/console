package com.acme.data360agent.web;

import com.acme.data360agent.security.AppUser;
import com.acme.data360agent.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final CurrentUserService users;

    public UserController(CurrentUserService users) {
        this.users = users;
    }

    @GetMapping("/me")
    public AppUser me() {
        return users.currentUser();
    }
}
