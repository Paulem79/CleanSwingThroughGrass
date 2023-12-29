package io.github.paulem.cstg.papi;

public class Version {
    private final int major;
    private final int minor;
    private final int revision;

    public Version(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getRevision() {
        return this.revision;
    }

    public static Version getVersion(VersionMethod versionMethod) {
        int major;
        int minor;
        int revision;
        String version;
        if (versionMethod == VersionMethod.BUKKIT) {
            version = VersionMethod.BUKKIT.getVersion();
            String[] parts = version.split("-")[0].split("\\.");
            major = Integer.parseInt(parts[0]);
            minor = Integer.parseInt(parts[1]);

            try {
                revision = Integer.parseInt(parts[2]);
            } catch (NumberFormatException var7) {
                revision = 0;
            }
        } else {
            if (versionMethod != VersionMethod.SERVER) {
                throw new IllegalArgumentException("Invalid VersionMethod enum value");
            }

            version = VersionMethod.SERVER.getVersion();
            String mcVersion = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);
            String[] mcParts = mcVersion.split("\\.");
            major = Integer.parseInt(mcParts[0]);
            minor = Integer.parseInt(mcParts[1]);
            revision = Integer.parseInt(mcParts[2]);
        }

        return new Version(major, minor, revision);
    }

    public String toString() {
        int var10000 = this.getMajor();
        return "" + var10000 + "." + this.getMinor() + "." + this.getRevision();
    }
}
