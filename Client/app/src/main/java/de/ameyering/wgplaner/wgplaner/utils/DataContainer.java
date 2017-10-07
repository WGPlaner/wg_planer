package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.User;

public abstract class DataContainer {

    public static abstract class Item{
        private static ArrayList<de.ameyering.wgplaner.wgplaner.structure.Item> items = new ArrayList<>();
        private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        public static ArrayList<de.ameyering.wgplaner.wgplaner.structure.Item> getItems() {
            return items;
        }

        public static void addItem(de.ameyering.wgplaner.wgplaner.structure.Item item){
            items.add(item);
        }

        public static de.ameyering.wgplaner.wgplaner.structure.Item getItem(int i){
            return items.get(i);
        }

        public static void removeItem(de.ameyering.wgplaner.wgplaner.structure.Item item){
            items.remove(item);
        }

        public static int getSize(){
            return items.size();
        }

        public static SimpleDateFormat getDateFormat(){
            return dateFormat;
        }
    }

    public static abstract class User {
        private static ArrayList<de.ameyering.wgplaner.wgplaner.structure.User> users = new ArrayList<>();

        public static ArrayList<de.ameyering.wgplaner.wgplaner.structure.User> getUser() {
            return users;
        }

        protected static void addUser(de.ameyering.wgplaner.wgplaner.structure.User user){
            users.add(user);
        }

        @Nullable
        protected static String getDisplayNameByUid(String uid){
            de.ameyering.wgplaner.wgplaner.structure.User user = new de.ameyering.wgplaner.wgplaner.structure.User(uid, "");
            if(users.contains(user)){
                int index = users.indexOf(user);
                return users.get(index).getDisplayName();
            }
            return null;
        }
    }
}
