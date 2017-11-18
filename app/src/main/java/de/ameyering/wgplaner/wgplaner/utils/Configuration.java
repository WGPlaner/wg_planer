package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import de.ameyering.wgplaner.wgplaner.R;

public class Configuration implements Serializable {
    public static Configuration singleton;
    public static final long serialVersionUID =
        7526471155622776137L; //Needed for Serialization (ObjectOutputStream)
    private static ConfigFileHandler handler;

    private HashMap<Type, String> configs; //Stores configurations
    private byte[] profilePicture;


    public static void initConfig(Context context) {
        handler = new ConfigFileHandler(context);
        Configuration configuration = handler.readConfig();

        if (configuration != null) {
            singleton = configuration;

        } else {
            singleton = new Configuration();
        }
    }

    private Configuration() {
        configs = new HashMap<>();
    }

    public void addConfig(Type type, String value) {
        if(value != null) {
            configs.put(type, value);
            handler.writeConfig(singleton);
        } else {
            configs.remove(type);
        }
    }

    public String getConfig(Type type) {
        if (configs.containsKey(type)) {
            return configs.get(type);
        }

        return null;
    }

    public void setProfilePicture(byte[] byteArray) {
        this.profilePicture = byteArray;
        handler.writeConfig(singleton);
    }

    public Bitmap getProfilePicture(Context context) {
        if (profilePicture != null) {
            return BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length);

        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_picture_dummy);
        }
    }

    /*
    Configuration.Type Enum
    Needed to identify configurations
     */

    public enum Type {
        USER_UID,
        USER_DISPLAY_NAME,
        USER_PHOTO_URL,
        USER_GROUP_ID,
        USER_EMAIL_ADDRESS
    }

    /*
    FileHandler-class for Configuration
    Uses ObjectInputStream and ObjectOutputStream to convert read data easily into objects
     */

    private static class ConfigFileHandler {
        private static final String fileName = ".config";
        private final File config;

        private ConfigFileHandler(Context context) {
            config = new File(context.getFilesDir(), fileName);

            if (!config.exists()) {
                try {
                    config.createNewFile();

                } catch (Exception e) {}
            }
        }

        private Configuration readConfig() {
            FileInputStream fileInputStream = null;
            ObjectInputStream objectInputStream = null;
            Configuration configuration = null;

            try {
                fileInputStream = new FileInputStream(config);
                objectInputStream = new ObjectInputStream(fileInputStream);
                configuration = (Configuration) objectInputStream.readObject();

            } catch (ClassNotFoundException e) {
                return new Configuration();

            } catch (NotSerializableException e) {
                return new Configuration();

            } catch (IOException e) {
                return new Configuration();

            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();

                    } catch (IOException e) {}
                }

                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();

                    } catch (IOException e) {}
                }
            }

            return configuration;
        }

        private void writeConfig(Configuration configuration) {
            FileOutputStream fileOutputStream = null;
            ObjectOutputStream objectOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(config);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(configuration);

            } catch (Exception e) {

            } finally {
                try {
                    fileOutputStream.close();

                } catch (IOException e) {

                }

                try {
                    objectOutputStream.close();

                } catch (IOException e) {

                }
            }
        }
    }
}
