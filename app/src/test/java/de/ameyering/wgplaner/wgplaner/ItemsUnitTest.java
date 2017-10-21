package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;

import de.ameyering.wgplaner.wgplaner.exception.MalformedItemException;
import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.structure.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

@RunWith(MockitoJUnitRunner.class)
public class ItemsUnitTest {

    @Test
    public void testConstructor() throws MalformedItemException {
        User user = Mockito.mock(User.class);

        Item item = new Item("name", user, user);
        Assert.assertEquals(new Item("name", user, user), item);

        item.buy(new Money(1, 1));

        Item mock = new Item("name", user, user);

        Assert.assertEquals(mock, item);

        try {
            new Item(null, new User("1", "2"), new User("1", "2"));
            Assert.assertTrue(false);

        } catch (MalformedItemException e) {
            String message = e.getMessage();
            Assert.assertEquals("name should not be null", message);
        }

        try {
            new Item("name", null, new User("1", "2"));
            Assert.assertTrue(false);

        } catch (MalformedItemException e) {
            String message = e.getMessage();
            Assert.assertEquals("requestedBy should not be null", message);
        }

        try {
            new Item("name", new User("1", "2"), null);
            Assert.assertTrue(false);

        } catch (MalformedItemException e) {
            String message = e.getMessage();
            Assert.assertEquals("requestedFor should not be null", message);
        }

        try {
            new Item("", new User("1", "2"), new User("1", "2"));
            Assert.assertTrue(false);

        } catch (MalformedItemException e) {
            String message = e.getMessage();
            Assert.assertEquals("name should not be empty", message);
        }

        try {
            item = new Item("name", new User("1", "2"), new User("1", "2"));
            item.buy(new Money(-1, -1));
            item.clone();
            Assert.assertTrue(false);

        } catch (CloneNotSupportedException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testBuy() throws MalformedItemException {
        User requestedFor = new User("1", "me");

        Item item = new Item("name", Mockito.mock(User.class), requestedFor);
        item.buy(new Money(1, 1));
        Assert.assertTrue(item.equals(new Item("name", Mockito.mock(User.class), requestedFor)));

        item.buy(new Money(1, 101));
        Assert.assertEquals(item, new Item("name", Mockito.mock(User.class), requestedFor));
    }

    @Test
    public void testEquals() throws MalformedItemException {
        String nameEq = "Milch";
        User userEq = new User("1", "Arne");
        String nameUeq = "Schoki";
        User userUeq = new User("2", "Andre");

        Item item1 = new Item(nameEq, userUeq, userEq);
        Item item2 = new Item(nameEq, userEq, userEq);

        Item item3 = new Item(nameEq, userEq, userUeq);
        Item item4 = new Item(nameUeq, userEq, userUeq);

        Item item5 = new Item(nameEq, userEq, userUeq);
        Item item6 = new Item(nameEq, userUeq, userEq);

        Assert.assertEquals(item1, item2);
        Assert.assertEquals(false, item3.equals(item4));
        Assert.assertEquals(false, item5.equals(item6));
        Assert.assertEquals(item3.equals(item6), item6.equals(item3));
    }

    @Test
    public void testClone() throws MalformedItemException {
        User requestedFor = new User("1", "me");
        Item item = new Item("name", requestedFor, requestedFor);
        Item clone = null;

        try {
            clone = item.clone();

        } catch (CloneNotSupportedException e) {
            Assert.assertTrue(false);
        }

        Assert.assertEquals(item, clone);
        clone.buy(new Money(1, 1));
        Assert.assertEquals(item, clone);

        item.buy(new Money(-1, -1));

        try {
            item.clone();
            Assert.assertTrue(false);

        } catch (CloneNotSupportedException e) {
            Assert.assertTrue(true);
        }

        try {
            item = new Item("", null, null);
            item.clone();
            Assert.assertTrue(false);

        } catch (CloneNotSupportedException e) {
            Assert.assertTrue(true);

        } catch (MalformedItemException e) {
            Assert.assertTrue(true);
        }

        item = new Item("name", new User("1", "2"), new User("1", "2"));
        item.buy(new Money(2, 4));

        try {
            item.clone();
            Assert.assertTrue(true);

        } catch (CloneNotSupportedException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGetBoughtOn() throws MalformedItemException {
        Item item = new Item("name", Mockito.mock(User.class), Mockito.mock(User.class));
        Money price = new Money(1, 1);
        item.buy(price);

        Assert.assertEquals(DataContainer.Items.getDateFormat().format(Calendar.getInstance().getTime()),
            item.getBoughtOn());
    }

    @Test
    public void testGetPrice() throws MalformedItemException {
        Item item = new Item("name", Mockito.mock(User.class), Mockito.mock(User.class));
        Money price = new Money(1, 1);
        item.buy(price);

        Assert.assertEquals(price, item.getPrice());
    }
}
