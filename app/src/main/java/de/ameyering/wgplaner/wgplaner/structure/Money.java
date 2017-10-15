package de.ameyering.wgplaner.wgplaner.structure;

import android.support.annotation.NonNull;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class Money {
    private static Currency currency = null;

    private int preDecimal = -1;
    private int decimal = -1;

    public static void initialize(Locale locale) {
        currency = Currency.getInstance(locale);
    }

    public static Money sum(@NonNull List<Money> money) {
        int preDecimal = 0;
        int decimal = 0;

        for (int i = 0; i < money.size(); i++) {
            Money current = money.get(i);
            preDecimal = preDecimal + current.preDecimal;
            decimal = decimal + current.decimal;

            if (decimal >= 100) {
                decimal = decimal - 100;
                preDecimal = preDecimal + 1;
            }
        }

        return new Money(preDecimal, decimal);
    }

    public Money(int preDecimal, int decimal) {
        if (decimal >= 100 || decimal < 0 || preDecimal < 0) {
            return;

        } else {
            this.preDecimal = preDecimal;
            this.decimal = decimal;
        }
    }

    @Override
    public String toString() {
        if (currency == null) {
            if (decimal == 0) {
                String out = "%s,-";
                return String.format(out, this.preDecimal);

            } else {
                String out = "%s,%02d";
                return String.format(out, preDecimal, decimal);
            }

        } else {
            if (decimal == 0) {
                String out = "%s,-" + currency.getSymbol();
                return String.format(out, this.preDecimal);

            } else {
                String out = "%s,%2d" + currency.getSymbol();
                return String.format(out, preDecimal, decimal);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((Money) obj).preDecimal == this.preDecimal &&
            ((Money) obj).decimal ==  this.decimal;
    }
}
