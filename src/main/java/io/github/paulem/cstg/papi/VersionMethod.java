package io.github.paulem.cstg.papi;

import org.bukkit.Bukkit;

public enum VersionMethod {
    BUKKIT(Bukkit.getBukkitVersion()),
    SERVER(Bukkit.getVersion());

    final String version;

    private VersionMethod(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }
}
