package de.ameyering.wgplaner.wgplaner.structure;

/**
 * Created by D067867 on 06.10.2017.
 */

public class User {
    private String uid;
    private String displayName;

    public User(String uid, String displayName) {
        this.uid = uid;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && this.uid.equals(((User) obj).uid);
    }
}
