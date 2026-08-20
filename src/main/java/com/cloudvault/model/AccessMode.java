package com.cloudvault.model;

/**
 * Access mode for shared files
 */
public enum AccessMode {
    DOWNLOAD("Users can view AND download the file"),
    VIEW_ONLY("Users can only view the file, no download");

    private final String description;

    AccessMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
