package org.rippleosi.users.model;

import java.util.ArrayList;
import java.util.List;

public class UserPermissions {

    private String role;

    public UserPermissions(final String role) {
        this.role = role;
    }

    public List<String> loadUserPermissions() {
        final List<String> permissions = new ArrayList<>();
        final String role = getRole();

        switch (role.toUpperCase()) {
            case "IDCR": {
                permissions.add("READ");
                permissions.add("WRITE");
                break;
            }
            case "PHR": {
                permissions.add("READ");
                break;
            }
            case "ADMIN": {
                permissions.add("ADMIN");
                break;
            }
            case "IG": {
                permissions.add("IG");
                break;
            }
            case "NONE": {
                permissions.add("NONE");
                break;
            }
            case "": {
                permissions.add("NONE");
                break;
            }
        }

        if (!permissions.contains("NONE")) {
            addPermissions(permissions);
        }

        return permissions;
    }

    protected List<String> addPermissions(final List<String> permissions) {
        return permissions;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
