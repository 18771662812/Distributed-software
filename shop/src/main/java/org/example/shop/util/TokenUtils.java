package org.example.shop.util;

import org.example.shop.entity.User;

import java.util.UUID;

public final class TokenUtils {

    private TokenUtils() {
    }

    public static String generateToken(User user) {
        return user.getId() + "-" + UUID.randomUUID();
    }
}
