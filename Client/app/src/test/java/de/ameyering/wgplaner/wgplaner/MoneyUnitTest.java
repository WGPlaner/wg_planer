package de.ameyering.wgplaner.wgplaner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import de.ameyering.wgplaner.wgplaner.structure.Money;

@RunWith(MockitoJUnitRunner.class)
public class MoneyUnitTest {

    @Test
    public void testToString(){
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            int preDecimal = random.nextInt(11);
            int decimal = random.nextInt(100);
            Money test = new Money(preDecimal, decimal);

            if(decimal < 10){
                String expected = preDecimal + "," + "0" + decimal;
                Assert.assertEquals(expected, test.toString());
            } else {
                String expected = preDecimal + "," + decimal;
                Assert.assertEquals(expected, test.toString());
            }
        }
    }

    @Test
    public void testSum(){
        ArrayList<Money> test = new ArrayList<>();

        int expectedPreDecimal = 0;
        int expectedDecimal = 0;

        Random random = new Random();
        for(int i = 0; i < 20; i++){
            int preDecimal = random.nextInt(11);
            int decimal = random.nextInt(100);
            Money money = new Money(preDecimal, decimal);
            test.add(money);

            expectedPreDecimal = expectedPreDecimal + preDecimal;
            expectedDecimal = expectedDecimal + decimal;
            if(expectedDecimal >= 100){
                expectedDecimal = expectedDecimal - 100;
                expectedPreDecimal = expectedPreDecimal + 1;
            }
        }

        Assert.assertEquals(new Money(expectedPreDecimal, expectedDecimal), Money.sum(test));
    }
}
