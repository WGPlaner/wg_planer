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
    public void testInitialize(){
        Money.initialize(new Locale("de", "DE"));
    }

    @Test
    public void testConstructor(){
        for(int i = -2; i < 101; i++){
            Money money = new Money(1, i);

            if(i < 0 || i >= 100){
                Assert.assertEquals(new Money(-1, -1), money);
            }
            else{
                Assert.assertEquals(new Money(1, i), new Money(1, i));
            }
        }
    }
    @Test
    public void testToString() {
        for (int i = 0; i < 100; i++) {
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
        }
        Locale locale = new Locale("de", "DE");
        Money.initialize(locale);

        for (int i = 0; i < 100; i++) {
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
        }
    }

    @Test
    public void testSum() {
        ArrayList<Money> test = new ArrayList<>();

        int expectedPreDecimal = 0;
        int expectedDecimal = 0;

        for (int i = 0; i < 100; i++) {
            int preDecimal = 1;
            int decimal = i;
            Money money = new Money(preDecimal, decimal);
            test.add(money);

            expectedPreDecimal = expectedPreDecimal + preDecimal;
            expectedDecimal = expectedDecimal + decimal;

            if (expectedDecimal >= 100) {
                expectedDecimal = expectedDecimal - 100;
                expectedPreDecimal = expectedPreDecimal + 1;
            }
        }

        Assert.assertEquals(new Money(expectedPreDecimal, expectedDecimal), Money.sum(test));
    }
}
