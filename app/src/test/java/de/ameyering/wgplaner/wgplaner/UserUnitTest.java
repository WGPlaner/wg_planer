package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.ameyering.wgplaner.wgplaner.structure.User;

@RunWith(MockitoJUnitRunner.class)
public class UserUnitTest {

    @Test
    public void testEquals() {
        String uid = "1";
        String name = "2";
        String fail = "3";

        Assert.assertEquals(new User(uid, name), new User(uid, name));
        Assert.assertEquals(new User(uid, name), new User(uid, fail));

        Assert.assertTrue(!new User(uid, name).equals(new User(fail, name)));
    }

    @Test
    public void testGetUid() {
        String uid = "1";

        User user = new User(uid, "");

        Assert.assertEquals(uid, user.getUid());
    }

    @Test
    public void testGetDisplayName() {
        String name = "1";

        User user = new User("", name);

        Assert.assertEquals(name, user.getDisplayName());
    }
}
