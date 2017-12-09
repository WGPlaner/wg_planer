package de.ameyering.wgplaner.wgplaner.structure;

import de.ameyering.wgplaner.wgplaner.exception.MalformedItemException;

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

        name = name.trim();
        if (name.isEmpty()) {
            throw new MalformedItemException("name should not be empty");
        }

        if (requestedBy == null) {
            throw new MalformedItemException("requestedBy should not be null");
        }

        if (requestedFor == null) {
            throw new MalformedItemException("requestedFor should not be null");
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

    public void buy(Money price) {
        this.price = price;
    }

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
        String boughtOn = this.boughtOn;

        if (price != null && boughtOn != null) {
            try {
                return new Item(name, requestedBy, requestedFor, price, boughtOn);

            } catch (MalformedItemException e) {
                throw new CloneNotSupportedException();
            }

        } else {
            try {
                return  new Item(name, requestedBy, requestedFor);

            } catch (MalformedItemException e) {
                throw new CloneNotSupportedException();
            }
        }
    }
}
