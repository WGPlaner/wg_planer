package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

/**
 * Created by D067867 on 06.10.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class DataContainerUnitTest {

    @Test
    public void testAdd(){
        Item item = new Item("Milch", Mockito.mock(User.class), Mockito.mock(User.class));

        DataContainer.Item.addItem(item);

        Assert.assertEquals(item, DataContainer.Item.getItem(0));
        Assert.assertEquals(1, DataContainer.Item.getSize());

        DataContainer.Item.removeItem(item);
    }

    @Test
    public void testRemove(){
        Item item = new Item("Milch", new User("1", "Arne"), Mockito.mock(User.class));

        int size = DataContainer.Item.getSize();

        DataContainer.Item.addItem(item);
        DataContainer.Item.removeItem(item);

        Assert.assertEquals(size, DataContainer.Item.getSize());

        size = DataContainer.Item.getSize();

        DataContainer.Item.addItem(item);
        DataContainer.Item.removeItem(new Item("Milch", new User("1", "Arne"), Mockito.mock(User.class)));

        Assert.assertEquals(size, DataContainer.Item.getSize());
    }
}
