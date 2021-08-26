# Effective Java
Anotações e exemplos do livro Effective Java de Joshua Bloch.

### Item 2: considere utilizar builders quando o construtor tiver muitos parâmetros

Considere a classe `NutritionFacts` abaixo que em seu cenário real é composta por 2 atributos obrigatórios e 24
opcionais. Por simplicidade vamos representar apenas 4 atributos.
```java
public class NutritionFacts {

    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;

    // Construtor com os atributos obrigatórios
    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
    }
}
```
acima representamos apenas 5 dos atributos da classe. Imagine o trabalho para codificar os demais construtores. Isso 
se tornaria contraprodutivo.

Instanciando um objeto de `NutritionFacts`:
```java
NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35);
```
perceba que não queríamos setar a propriedade `fat`, por isso atribuímos 0 a ela, ou seja, mesmo disponibilizando 
vários construtores ainda assim não conseguimos uma flexibilidade total para setarmos apenas os atributos necessários.