package com.zhmenko.web.nac.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super("Роль с названием \""+roleName+"\" не найдена!");
    }
}
