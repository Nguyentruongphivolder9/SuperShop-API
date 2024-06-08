package com.project.supershop.common.enums;

public enum Roles {
    ADMIN("ADMIN"),
    USER("USER"),
    SELLER("SELLER");

    private final String Role;

    //Enum constructure
    private Roles(String role){
        this.Role = role;
    }

    public String getRole(){
        return this.Role;
    }
}
