package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by D067867 on 06.10.2017.
 */

public class Configuration implements Serializable{
    public static Configuration singleton;
    public static final long serialVersionUID = 7526471155622776137L; //Needed for Serialization (ObjectOutputStream)
    private static ConifgFileHandler handler;

    private HashMap<Type, String> configs; //Stores configurations


    public static void initConfig(Context context) {
        handler = new ConifgFileHandler(context);
        Configuration configuration = handler.readConfig();

        if(configuration != null){
            singleton = configuration;
        }
        else {
            singleton = new Configuration();
        }
    }

    private Configuration(){
        configs = new HashMap<>();
    }

    public void addConfig(Type type, String value){
        configs.put(type, value);
        handler.writeConfig(singleton);
    }

    public String getConfig(Type type){
        return configs.get(type);
    }

    /*
    Configuration.Type Enum
    Needed to identify configurations
     */

    public enum Type{
        //Declare Config-Tye here
        NAME;
    }

    /*
    FileHandler-class for Configuration
    Uses ObjectInputStream and ObjectOutputStream to convert read data easily into objects
     */

    private static class ConifgFileHandler{
        private static final String fileName = ".config";
        private final File config;

        private ConifgFileHandler(Context context){
            config = new File(context.getFilesDir(), fileName);

            if(!config.exists()){
                try {
                    config.createNewFile();
                } catch (Exception e){}
            }
        }

        private Configuration readConfig(){
            FileInputStream fileInputStream = null;
            ObjectInputStream objectInputStream = null;
            Configuration configuration = null;

            try{
                fileInputStream = new FileInputStream(config);
                objectInputStream = new ObjectInputStream(fileInputStream);
                configuration = (Configuration) objectInputStream.readObject();
            }
            catch(ClassNotFoundException e){
            }
            catch(NotSerializableException e){
            }
            catch (IOException e){
            }
            finally{
                if(fileInputStream != null){
                    try {
                        fileInputStream.close();
                    }
                    catch(IOException e){}
                }
                if(objectInputStream != null){
                    try{
                        objectInputStream.close();
                    }
                    catch(IOException e){}
                }
                return configuration;
            }
        }

        private void writeConfig(Configuration configuration){
            FileOutputStream fileOutputStream = null;
            ObjectOutputStream objectOutputStream = null;

            try{
                fileOutputStream = new FileOutputStream(config);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(configuration);
            } catch (Exception e){

            } finally {
                try{
                    fileOutputStream.close();
                } catch(IOException e){

                }
                try{
                    objectOutputStream.close();
                } catch (IOException e){

                }
            }
        }
    }
}
