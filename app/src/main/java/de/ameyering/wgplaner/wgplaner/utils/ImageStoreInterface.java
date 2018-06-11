package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

public abstract class ImageStoreInterface {

    public static ImageStoreInterface getInstance(Context context) {
        return new ImageStore(context);
    }

    public abstract void setGroupPicture(Bitmap picture);

    public abstract void setGroupPicture(byte[] picture);

    public abstract Bitmap getGroupPicture();

    public abstract File getGroupPictureFile();

    public abstract void setGroupMemberPicture(String uid, Bitmap picture);

    public abstract void setGroupMemberPicture(String uid, byte[] picture);

    public abstract Bitmap getGroupMemberPicture(String uid);

    public abstract File getGroupMemberPictureFile(String uid);

    public abstract void deleteGroupMemberPicture(String uid);
}
