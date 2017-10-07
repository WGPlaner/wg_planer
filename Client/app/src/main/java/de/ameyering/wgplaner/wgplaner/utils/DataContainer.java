package de.ameyering.wgplaner.wgplaner.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.structure.Item;

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
}
