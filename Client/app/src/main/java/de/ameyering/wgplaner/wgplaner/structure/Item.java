package de.ameyering.wgplaner.wgplaner.structure;

import java.util.Calendar;

import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

/**
 * Created by D067867 on 06.10.2017.
 */

public class Item {
    public String name;
    public User requestedBy;
    public User requestedFor;
    private float price;
    private String bougthOn;

    public Item(String name, User requestedBy, User requestedFor) {
        this.name = name;
        this.requestedBy = requestedBy;
        this.requestedFor = requestedFor;
    }

    private Item(String name, User requestedBy, User requestedFor, float price, String bougthOn) {
        this.name = name;
        this.requestedBy = requestedBy;
        this.requestedFor = requestedFor;
        this.price = price;
        this.bougthOn = bougthOn;
    }

    public boolean buy(float price){
        if(price > 0){
            this.price = price;
            this.bougthOn = DataContainer.Item.getDateFormat().format(Calendar.getInstance().getTime()); //Formats actual Time to String
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == this.getClass()){
            Item other = (Item) obj; // Object gets parsed into Item Structure

            if(this.name.equals(other.name) && this.requestedBy.equals(other.requestedBy)){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    public float getPrice() {
        return price;
    }

    public String getBougthOn() {
        return bougthOn;
    }

    @Override
    public Item clone() throws CloneNotSupportedException {
        return new Item(this.name, this.requestedBy, this.requestedFor, this.price, this.bougthOn);
    }
}
