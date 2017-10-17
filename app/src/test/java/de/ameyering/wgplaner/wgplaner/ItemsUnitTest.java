package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;

import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.structure.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

@RunWith(MockitoJUnitRunner.class)
public class ItemsUnitTest {

    @Test
    public void testConstructor(){
        User user = Mockito.mock(User.class);

        Item item = new Item("name", user, user);
        Assert.assertEquals(new Item("name", user, user), item);

        item.buy(new Money(1, 1));

        Item mock = new Item("name", user, user);

        Assert.assertEquals(mock, item);
    }

    @Test
    public void testBuy() {
            User requestedFor = new User("1", "me");

            Item item = new Item("name", Mockito.mock(User.class), requestedFor);
            item.buy(new Money(1, 1));
            Assert.assertTrue(item.equals(new Item("name", Mockito.mock(User.class), requestedFor)));

            item.buy(new Money(1, 101));
            Assert.assertEquals(item, new Item("name", Mockito.mock(User.class), requestedFor));
    }

    @Test
    public void testEquals() {
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
    public void testClone(){
        User requestedFor = new User("1", "me");
        Item item = new Item("name", requestedFor, requestedFor);
        Item clone = null;
        try {
            clone = item.clone();
        }
        catch (CloneNotSupportedException e){
            Assert.assertTrue(false);
        }

        Assert.assertEquals(item, clone);
        clone.buy(new Money(1,1));
        Assert.assertEquals(item, clone);
    }

    @Test
    public void testGetBougthOn(){
        Item item = new Item("name", Mockito.mock(User.class), Mockito.mock(User.class));
        Money price = new Money(1, 1);
        item.buy(price);

        Assert.assertEquals(DataContainer.Items.getDateFormat().format(Calendar.getInstance().getTime()), item.getBougthOn());
    }

    @Test
    public void testGetPrice(){
        Item item = new Item("name", Mockito.mock(User.class), Mockito.mock(User.class));
        Money price = new Money(1, 1);
        item.buy(price);

        Assert.assertEquals(price, item.getPrice());
    }
}
