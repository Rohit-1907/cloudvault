package com.cloudvault.model;

public enum FileStatus {
    ACTIVE("Active"),
    EXPIRED("Expired"),
    DELETED("Deleted");

    private final String displayName;

    FileStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
