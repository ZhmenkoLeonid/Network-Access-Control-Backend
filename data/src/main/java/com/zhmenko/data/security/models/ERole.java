package com.zhmenko.data.security.models;

import lombok.Getter;

@Getter
public enum ERole {
    ROLE_CLIENT("ROLE_CLIENT"),

    ROLE_ADMIN("ROLE_ADMIN");

    private final String roleName;
    ERole(String roleName) {
        this.roleName = roleName;
    }
}
