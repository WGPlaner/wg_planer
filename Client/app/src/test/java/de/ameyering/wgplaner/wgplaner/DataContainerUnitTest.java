package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;

@RunWith(MockitoJUnitRunner.class)
public class DataContainerUnitTest {

    @Test
    public void testAdd(){
        Item item = new Item("Milch", Mockito.mock(User.class), Mockito.mock(User.class));

        DataContainer.Items.addItem(item);

        Assert.assertEquals(item, DataContainer.Items.getItem(0));
        Assert.assertEquals(1, DataContainer.Items.getSize());

        DataContainer.Items.removeItem(item);
    }

    @Test
    public void testRemove(){
        Item item = new Item("Milch", new User("1", "Arne"), Mockito.mock(User.class));

        int size = DataContainer.Items.getSize();

        DataContainer.Items.addItem(item);
        DataContainer.Items.removeItem(item);

        Assert.assertEquals(size, DataContainer.Items.getSize());

        size = DataContainer.Items.getSize();

        DataContainer.Items.addItem(item);
        DataContainer.Items.removeItem(new Item("Milch", new User("1", "Arne"), Mockito.mock(User.class)));

        Assert.assertEquals(size + 1, DataContainer.Items.getSize());
    }
}
