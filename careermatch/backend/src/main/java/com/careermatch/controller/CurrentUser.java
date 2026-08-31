package com.careermatch.controller;

import com.careermatch.model.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/** Small helper to pull the authenticated User (set by JwtAuthFilter) out of the security context. */
public class CurrentUser {
    public static User get() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long id() {
        return get().getId();
    }

    public static Long idOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User u)) return null;
        return u.getId();
    }
}
