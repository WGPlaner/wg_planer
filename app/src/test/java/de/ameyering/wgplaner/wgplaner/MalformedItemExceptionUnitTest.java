package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.ameyering.wgplaner.wgplaner.exception.MalformedItemException;

@RunWith(MockitoJUnitRunner.class)
public class MalformedItemExceptionUnitTest {

    @Test
    public void testConstructor() {
        MalformedItemException exception = new MalformedItemException();
        Assert.assertEquals(null, exception.getMessage());
        Assert.assertEquals(null, exception.getCause());

        exception = new MalformedItemException("message");
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertEquals(null, exception.getCause());

        Throwable cause = new Throwable();
        exception = new MalformedItemException("message", cause);
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testGetMessage() {
        MalformedItemException exception = new MalformedItemException();
        Assert.assertEquals(null, exception.getMessage());

        exception = new MalformedItemException("message");
        Assert.assertEquals("message", exception.getMessage());
    }

    @Test
    public void testGetCause() {
        MalformedItemException exception = new MalformedItemException();
        Assert.assertEquals(null, exception.getCause());

        Throwable cause = new Throwable();
        exception = new MalformedItemException("message", cause);
        Assert.assertEquals(cause, exception.getCause());
    }
}
