package de.ameyering.wgplaner.wgplaner.structure;

public class User {
    private String uid = null;
    private String displayName = null;
    private String groupId = null;
    private String photoUrl = null;
    private String emailAddress = null;

    public User() {}

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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
