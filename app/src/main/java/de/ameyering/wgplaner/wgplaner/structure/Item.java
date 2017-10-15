package de.ameyering.wgplaner.wgplaner.structure;

import java.util.Calendar;

import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

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
        new Item(name, requestedBy, requestedFor);
        this.price = price;
        this.bougthOn = bougthOn;
    }

    public boolean buy(float price) {
        if (price > 0) {
            this.price = price;
            this.bougthOn = DataContainer.Items.getDateFormat().format(
                    Calendar.getInstance().getTime()); //Formats actual Time to String
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && this.name.equals(((Item) obj).name) &&
            this.requestedFor.equals(((Item) obj).requestedFor);
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