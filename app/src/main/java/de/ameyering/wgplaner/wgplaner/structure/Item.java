package de.ameyering.wgplaner.wgplaner.structure;

import java.util.Calendar;

import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

public class Item {
    public String name;
    public User requestedBy;
    public User requestedFor;
    private Money price;
    private String bougthOn;

    public Item(String name, User requestedBy, User requestedFor) {
        this.name = name;
        this.requestedBy = requestedBy;
        this.requestedFor = requestedFor;
    }

    private Item(String name, User requestedBy, User requestedFor, Money price, String bougthOn) {
        this(name, requestedBy, requestedFor);
        this.price = price;
        this.bougthOn = bougthOn;
    }

    public void buy(Money price) {
        this.price = price;
        this.bougthOn = DataContainer.Items.getDateFormat().format(
                Calendar.getInstance().getTime()); //Formats actual Time to String
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && this.name.equals(((Item) obj).name) &&
            this.requestedFor.equals(((Item) obj).requestedFor);
    }

    public Money getPrice() {
        return price;
    }

    public String getBougthOn() {
        return bougthOn;
    }

    @Override
    public Item clone() throws CloneNotSupportedException {
        String name = this.name;
        User requestedBy = this.requestedBy;
        User requestedFor = this.requestedFor;
        Money price = this.price;
        String bougthOn = this.bougthOn;

        return new Item(name, requestedBy, requestedFor, price, bougthOn);
    }
}
