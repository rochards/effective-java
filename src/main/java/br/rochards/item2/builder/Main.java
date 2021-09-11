package br.rochards.item2.builder;

public class Main {
    public static void main(String[] args) {
        var cocaCola = new NutritionFacts.Builder(240, 0)
                .calories(10)
                .sodium(35)
                .build();

        System.out.println(cocaCola);
    }
}
