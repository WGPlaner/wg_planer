package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.structure.Money;

@RunWith(MockitoJUnitRunner.class)
public class MoneyUnitTest {


    @Test
    public void testInitialize() {
        Money.initialize(new Locale("de", "DE"));
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(new Money(-1, -1), new Money(1, 100));
        Assert.assertEquals(new Money(-1, -1), new Money(1, -1));
        Assert.assertEquals(new Money(1, 50), new Money(1, 50));
        Assert.assertNotEquals(new Money(2, 50), new Money(1, 50));
        Assert.assertNotEquals(new Money(1, 50), new Money(1, 49));
    }
    @Test
    public void testToString() {
        int i = 0;

        while (i <= 18) {
            int preDecimal = 1;
            int decimal = i;
            Money test = new Money(preDecimal, decimal);

            if (decimal == 0) {
                String expected = preDecimal + ",-";
                Assert.assertEquals(expected, test.toString());

            } else if (decimal < 10) {
                String expected = preDecimal + "," + "0" + decimal;
                Assert.assertEquals(expected, test.toString());

            } else {
                String expected = preDecimal + "," + decimal;
                Assert.assertEquals(expected, test.toString());
            }

            i = i + 9;
        }

        Locale locale = new Locale("de", "DE");
        Money.initialize(locale);
        i = 0;

        while (i <= 18) {
            int preDecimal = 1;
            int decimal = i;
            Money test = new Money(preDecimal, decimal);

            if (decimal == 0) {
                String expected = preDecimal + ",-" + "EUR";
                Assert.assertEquals(expected, test.toString());

            } else if (decimal < 10) {
                String expected = preDecimal + "," + "0" + decimal + "EUR";
                Assert.assertEquals(expected, test.toString());

            } else {
                String expected = preDecimal + "," + decimal + "EUR";
                Assert.assertEquals(expected, test.toString());
            }

            i = i + 9;
        }
    }

    @Test
    public void testSum() {
        ArrayList<Money> test = new ArrayList<>();

        int expectedPreDecimal = 0;
        int expectedDecimal = 0;

        for (int i = 0; i < 100; i++) {
            int preDecimal = 1;
            Money money = new Money(preDecimal, i);
            test.add(money);

            expectedPreDecimal = expectedPreDecimal + preDecimal;
            expectedDecimal = expectedDecimal + i;

            if (expectedDecimal >= 100) {
                expectedDecimal = expectedDecimal - 100;
                expectedPreDecimal = expectedPreDecimal + 1;
            }
        }

        Assert.assertEquals(new Money(expectedPreDecimal, expectedDecimal), Money.sum(test));
    }

    @Test
    public void testIsValid() {
        Money money = new Money(-1, -1);
        Assert.assertFalse(money.isValid());

        money = new Money(1, 2);
        Assert.assertTrue(money.isValid());
    }
}
