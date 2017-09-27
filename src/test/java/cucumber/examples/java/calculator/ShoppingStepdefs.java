package cucumber.examples.java.calculator;

import cucumber.api.Transformer;
import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import cucumber.deps.com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShoppingStepdefs {
    private RpnCalculator calc = new RpnCalculator();

    @Дано("^следующие товары:$")
    public void followingGroceries(List<Grocery> groceries) throws Throwable {
        for (Grocery grocery : groceries) {
            calc.push(grocery.price.value);
            calc.push("+");
        }
    }

    @Когда("^я плачу (\\d+) рублей$")
    public void i_pay(int amount) {
        calc.push(amount);
        calc.push("-");
    }

    @Тогда("^сдача должна быть равна (\\d+)$")
    public void my_change_should_be_(int change) {
        assertEquals(-calc.value().intValue(), change);
    }


    public static class Grocery {
        public String name;
        @XStreamConverter(Price.Converter.class)
        public Price price;

        public Grocery() {
            super();
        }
    }

    public static class Price {
        public int value;

        public Price(int value) {
            this.value = value;
        }

        public static class Converter extends Transformer<Price> {
            @Override
            public Price transform(String value) {
                return new Price(Integer.parseInt(value));
            }
        }
    }
}
