package br.rochards.item2.builder;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class Pizza {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }

    final Set<Topping> toppings;

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }

    abstract static class Builder<T extends Builder<T>> {
        /*
        * <T extends Builder<T>> -> recursive type parameter. Assim restringe a liberdade do programador que nunca
        * vai poder fazer um public static class Builder extends Pizza.Builder<Integer> { ... }, que não faz sentido
        * algum.
        * A estratégia de usar generics foi adotada para não ser preciso fazer casts quando for inicializar uma pizza.
        *    Ex.: NyPizza pizza = (NyPizza) new NyPizza.Builder(SMALL).build();
        * */
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class); // forma de criar uma empty collection de Topping

        public T topping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        // subclasses devem implementar o método abaixo e retornar this.
        protected abstract T self();
    }
}
