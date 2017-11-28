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

public class ImageStore {
    private static final String profilePictureName = "pImage.jpg";
    private static final String groupPictureName = "gImage.jpg";

    private File profilePicture;
    private File groupPicture;

    private byte[] profilePictureBytes;
    private byte[] groupPictureBytes;

    private static ImageStore singleton;

    static {
        singleton = new ImageStore();
    }

    public static void initialize(Context context){
        singleton.profilePicture = new File(context.getFilesDir(), profilePictureName);
        singleton.groupPicture = new File(context.getFilesDir(), groupPictureName);

        if(singleton.profilePictureBytes == null){
            if(singleton.profilePicture.exists()){
                singleton.loadProfilePicture();
            } else {
                try {
                    singleton.profilePicture.createNewFile();
                } catch (IOException e){
                    Log.e("ProfilePicture", ":FileCreationFailed");
                }
            }
        }
        if(singleton.groupPictureBytes == null){
            if(singleton.groupPicture.exists()){
                singleton.loadGroupPicture();
            } else {
                try{
                    singleton.groupPicture.createNewFile();
                } catch (IOException e){
                    Log.e("GroupPicture", ":FileCreationFailed");
                }
            }
        }
    }

    public static ImageStore getInstance(){
        return singleton;
    }

    public void setProfilePicture(Bitmap profilePicture){
        if(profilePicture != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            profilePicture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            profilePictureBytes = outputStream.toByteArray();

            writeProfilePicture(profilePictureBytes);
        }
    }

    public void setGroupPicture(Bitmap groupPicture){
        if(groupPicture != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            groupPicture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            groupPictureBytes = outputStream.toByteArray();

            writeGroupPicture(groupPictureBytes);
        }
    }

    public Bitmap getProfileBitmap(Context context){
        if(profilePictureBytes != null && profilePictureBytes.length > 0) {
            return BitmapFactory.decodeByteArray(profilePictureBytes, 0, profilePictureBytes.length);
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_picture_dummy);
        }
    }

    public Bitmap getGroupBitmap(){
        return BitmapFactory.decodeByteArray(groupPictureBytes, 0, groupPictureBytes.length);
    }

    public File getProfilePictureFile(){
        return profilePicture;
    }

    public File getGroupPictureFile(){
        return groupPicture;
    }

    private void writeProfilePicture(byte[] profilePictureBytes){
        FileOutputStream fileOutputStream = null;

        try{
            fileOutputStream = new FileOutputStream(profilePicture);
            fileOutputStream.write(profilePictureBytes);
        } catch (FileNotFoundException e){
            profilePicture = null;
        } catch (IOException e){
            this.profilePictureBytes = null;
        } finally {
            try{
                fileOutputStream.close();
            } catch (IOException e){
                Log.e("ProfilePicture", ":OutputStreamCloseFailed");
            }
        }
    }

    private void writeGroupPicture(byte[] groupPictureBytes){
        FileOutputStream fileOutputStream = null;

        try{
            fileOutputStream = new FileOutputStream(groupPicture);
            fileOutputStream.write(groupPictureBytes);
        } catch (FileNotFoundException e){
            groupPicture = null;
        } catch (IOException e){
            this.groupPictureBytes = null;
        } finally {
            try{
                fileOutputStream.close();
            } catch (IOException e){
                Log.e("GroupPicture", ":OutputStreamCloseFailed");
            }
        }
    }

    public boolean writeGroupMemberPicture(String uid, byte[] imageBytes, Context context){
        File image = new File(context.getFilesDir(), uid + ".jpg");

        if(!image.exists()){
            try {
                image.createNewFile();
            } catch (IOException e){
                return false;
            }
        }

        FileOutputStream fileOutputStream = null;
        boolean success = false;

        try{
            fileOutputStream = new FileOutputStream(image);
            fileOutputStream.write(imageBytes);
            success = true;
        } catch (FileNotFoundException e){
            success = false;
        } catch (IOException e){
            success = false;
        } finally {
            try{
                fileOutputStream.close();
            } catch (IOException e){
                Log.e("GroupMemberPicture", ":FailedToCloseOutputStream");
            }
            return success;
        }
    }

    public Bitmap loadGroupMemberPicture(String uid, Context context){
        File image = new File(context.getFilesDir(), uid + ".jpg");

        if(!image.exists()){
            return null;
        }

        FileInputStream fileInputStream = null;
        byte[] imageBytes = null;
        Bitmap bitmapImage = null;

        try{
            fileInputStream = new FileInputStream(image);
            imageBytes = new byte[(int) image.length()];
            fileInputStream.read(imageBytes);
            bitmapImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (FileNotFoundException e){
            bitmapImage = null;
        } catch (IOException e){
            bitmapImage = null;
        } finally {
            try{
                fileInputStream.close();
            } catch (IOException e){
                Log.e("GroupMemberPicture", ":FailedToCloseInputStream");
            }
            return bitmapImage;
        }
    }

    private void loadGroupPicture() {
        FileInputStream fileInputStream = null;

        try{
            fileInputStream = new FileInputStream(groupPicture);
            groupPictureBytes = new byte[(int) groupPicture.length()];
            fileInputStream.read(groupPictureBytes);
        } catch (FileNotFoundException e){
            groupPicture = null;
        } catch (IOException e){
            groupPictureBytes = null;
        } finally {
            try{
                fileInputStream.close();
            } catch (IOException e){
                Log.e("GroupPicture", ":InputStreamCloseFailed");
            }
        }
    }

    private void loadProfilePicture() {
        FileInputStream fileInputStream = null;

        try{
            fileInputStream = new FileInputStream(profilePicture);
            profilePictureBytes = new byte[(int) profilePicture.length()];
            fileInputStream.read(profilePictureBytes);
        } catch (FileNotFoundException e){
            profilePicture = null;
        } catch (IOException e){
            profilePictureBytes = null;
        } finally {
            try{
                fileInputStream.close();
            } catch (IOException e){
                Log.e("ProfilePicture", ":InputStreamCloseFailed");
            }
        }
    }
}
