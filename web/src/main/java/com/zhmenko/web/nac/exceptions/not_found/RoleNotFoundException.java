package com.zhmenko.web.nac.exceptions.not_found;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String roleName) {
        super("Роль с названием \""+roleName+"\" не найдена!");
    }
}
