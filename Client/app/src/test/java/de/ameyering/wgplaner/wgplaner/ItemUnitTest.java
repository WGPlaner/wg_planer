package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Random;

import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

@RunWith(MockitoJUnitRunner.class)
public class ItemUnitTest {

    @Test
    public void testBuy(){
        Random random = new Random();

        float price = random.nextFloat() * 2 - 1;
        String date = DataContainer.Item.getDateFormat().format(Calendar.getInstance().getTime());

        Item item = new Item("Milch", new User("1", "Arne"), new User("1", "Arne"));

        boolean success = item.buy(price);

        if(price > 0){
            Assert.assertEquals(price, item.getPrice(), 0.01f);
            Assert.assertEquals(date, item.getBougthOn());
        }
        else{
            Assert.assertEquals(false, success);
            Assert.assertEquals(null, item.getBougthOn());
        }
    }

    @Test
    public void testEquals(){
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
}
