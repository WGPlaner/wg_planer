package de.ameyering.wgplaner.wgplaner.structure;

import java.util.Calendar;

import de.ameyering.wgplaner.wgplaner.exception.MalformedItemException;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

public class Item {
    public String name;
    public User requestedBy;
    public User requestedFor;
    private Money price;
    private String boughtOn;

    public Item(String name, User requestedBy, User requestedFor) throws MalformedItemException {

        if (name == null) {
            throw new MalformedItemException("name should not be null");
        }

        if (requestedBy == null) {
            throw new MalformedItemException("requestedBy should not be null");
        }

        if (requestedFor == null) {
            throw new MalformedItemException("requestedFor should not be null");
        }

        if (name.equals("")) {
            throw new MalformedItemException("name should not be empty");
        }

        this.name = name;
        this.requestedBy = requestedBy;
        this.requestedFor = requestedFor;
    }

    private Item(String name, User requestedBy, User requestedFor, Money price,
        String boughtOn) throws MalformedItemException {
        this(name, requestedBy, requestedFor);

        if (!price.isValid()) {
            throw new MalformedItemException("price should be valid");
        }

        this.price = price;
        this.boughtOn = boughtOn;
    }

    /*
    public void buy(Money price) {
        this.price = price;
        this.boughtOn = DataContainer.Items.getDateFormat().format(
                Calendar.getInstance().getTime()); //Formats actual Time to String
    }
    */

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && this.name.equals(((Item) obj).name) &&
            this.requestedFor.equals(((Item) obj).requestedFor);
    }

    public Money getPrice() {
        return price;
    }

    public String getBoughtOn() {
        return boughtOn;
    }

    @Override
    public Item clone() throws CloneNotSupportedException {
        String name = this.name;
        User requestedBy = this.requestedBy;
        User requestedFor = this.requestedFor;
        Money price = this.price;
        String bougthOn = this.boughtOn;

        if (price != null && bougthOn != null) {
            try {
                Item item = new Item(name, requestedBy, requestedFor, price, bougthOn);
                return item;

            } catch (MalformedItemException e) {
                throw new CloneNotSupportedException();
            }

        } else {
            try {
                Item item = new Item(name, requestedBy, requestedFor);
                return  item;

            } catch (MalformedItemException e) {
                throw new CloneNotSupportedException();
            }
        }
    }
}
