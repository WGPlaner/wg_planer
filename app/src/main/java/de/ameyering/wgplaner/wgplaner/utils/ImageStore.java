package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.ameyering.wgplaner.wgplaner.R;

public class ImageStore extends ImageStoreInterface {
    private static final String groupPictureName = "gImage.jpg";

    private File filesDir;

    protected ImageStore(Context context) {
        filesDir = context.getFilesDir();
    }

    public void setGroupPicture(Bitmap groupPicture) {
        if (groupPicture != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            groupPicture.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
            byte[] image = outputStream.toByteArray();

            writeGroupPicture(image);
        }
    }

    public void setGroupPicture(byte[] groupPicture) {
        if (groupPicture != null) {
            writeGroupPicture(groupPicture);
        }
    }

    public Bitmap getGroupPicture() {
        byte[] image =  loadGroupPicture();

        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }

        return null;
    }

    public File getGroupPictureFile() {
        return new File(filesDir, groupPictureName);
    }

    public void setGroupMemberPicture(String uid, Bitmap bitmap) {
        if (bitmap != null && uid != null && !uid.trim().isEmpty()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);

            byte[] bytes = outputStream.toByteArray();

            writeGroupMemberPicture(uid, bytes);
        }
    }

    public void setGroupMemberPicture(String uid, byte[] bytes) {
        if (bytes != null && uid != null && !uid.trim().isEmpty()) {
            writeGroupMemberPicture(uid, bytes);
        }
    }

    public Bitmap getGroupMemberPicture(String uid) {
        if (uid != null && !uid.trim().isEmpty()) {
            byte[] image = loadGroupMemberPicture(uid);

            if (image != null) {
                return BitmapFactory.decodeByteArray(image, 0, image.length);
            }
        }

        return null;
    }

    public File getGroupMemberPictureFile(String uid) {
        if (uid != null && !uid.trim().isEmpty()) {
            return new File(filesDir, uid + ".jpg");
        }

        return null;
    }

    public void writeGroupPicture(byte[] groupPictureBytes) {
        FileOutputStream fileOutputStream = null;
        File groupPicture = new File(filesDir, groupPictureName);

        try {
            fileOutputStream = new FileOutputStream(groupPicture);
            fileOutputStream.write(groupPictureBytes);

        } catch (IOException e) {
            //do nothing
        } finally {
            try {
                fileOutputStream.close();

            } catch (IOException e) {
                Log.e("GroupPicture", ":OutputStreamCloseFailed");
            }
        }
    }

    private void writeGroupMemberPicture(String uid, byte[] imageBytes) {
        File image = new File(filesDir, uid + ".jpg");

        if (!image.exists()) {
            try {
                image.createNewFile();

            } catch (IOException e) {
                return;
            }
        }

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(image);
            fileOutputStream.write(imageBytes);

        } catch (IOException e) {
            //do nothing
        } finally {
            try {
                fileOutputStream.close();

            } catch (IOException e) {
                Log.e("GroupMemberPicture", ":FailedToCloseOutputStream");
            }
        }
    }

    public void deleteGroupMemberPicture(String uid) {
        File image = new File(filesDir, uid + ".jpg");
        image.delete();
    }

    private byte[] loadGroupMemberPicture(String uid) {
        File image = new File(filesDir, uid + ".jpg");

        if (!image.exists()) {
            return null;
        }

        FileInputStream fileInputStream = null;
        byte[] imageBytes = new byte[(int) image.length()];

        try {
            fileInputStream = new FileInputStream(image);
            fileInputStream.read(imageBytes);

        } catch (IOException e) {
            imageBytes = null;

        } finally {
            try {
                fileInputStream.close();

            } catch (IOException e) {
                Log.e("GroupMemberPicture", ":FailedToCloseInputStream");
            }
        }

        return imageBytes;
    }

    private byte[] loadGroupPicture() {
        FileInputStream fileInputStream = null;
        File groupPicture = new File(filesDir, groupPictureName);
        byte[] groupPictureBytes = new byte[(int) groupPicture.length()];

        try {
            fileInputStream = new FileInputStream(groupPicture);
            fileInputStream.read(groupPictureBytes);

        } catch (IOException e) {
            groupPictureBytes = null;

        } finally {
            try {
                fileInputStream.close();

            } catch (IOException e) {
                Log.e("GroupPicture", ":InputStreamCloseFailed");
            }
        }

        return groupPictureBytes;
    }
}
