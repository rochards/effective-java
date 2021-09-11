package br.rochards.item2.builder;

import static br.rochards.item2.builder.NyPizza.Size.SMALL;
import static br.rochards.item2.builder.Pizza.Topping.*;

public class Main {
    public static void main(String[] args) {
        var cocaCola = new NutritionFacts.Builder(240, 0)
                .calories(10)
                .sodium(35)
                .build();

        System.out.println(cocaCola);

        NyPizza nyPizza = new NyPizza.Builder(SMALL)
                .topping(SAUSAGE)
                .topping(ONION)
                .build();
        System.out.println(nyPizza);

        Calzone calzone = new Calzone.Builder()
                .sauceInside()
                .topping(SAUSAGE)
                .topping(ONION)
                .topping(PEPPER)
                .build();
        System.out.println(calzone);
    }
}
