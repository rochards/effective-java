# Effective Java
Anotações e exemplos do livro Effective Java de Joshua Bloch.

### Item 2: considere utilizar builders quando o construtor tiver muitos argumentos

Esta estratégia é muito adequada para os casos em que a classe possui vários atributos opcionais na sua composição. 
Algumas **outras alternativas** são:
* Utilizar o padrão _**telescoping constructor**_ - quando você cria vários construtores na classe, dando mais 
  opções de inicialização. Ex.:
  ```java
  // ... atributos da classe declarados como final
  public NutritionFacts(int servingSize, int servings) {
    this(servingSizes, servings, 0) // chama um construtor com 3 argumentos
  }
  
  public NutritionFacts(int servingSize, int servings, int calories) {
    this(servingSizes, servings, calories, 0) // chama um construtor com 4 argumentos
  }
  
  public NutritionFacts(int servingSize, int servings, int calories, int fat) {
    this.servingSize = servingSize;
    this.servings = servings;
    this.calories = calories;
    this.fat = fat;
  }
  // ...
  ```
  mesmo com uma variedade de construtores, muitas vezes não é possível ter plena flexibilidade para inicializar apenas 
  os atributos desejados. A leitura ainda se tornar difícil para um número muito grande de argumentos.

* Utilizar o padrão _**JavaBeans**_ - na classe haverá apenas o construtor *default* (construtor sem argumentos) e 
  você fará uso dos métodos *setters* para inicializar os atributos desejados, isso facilita a legibilidade.   
  Uma clara desvantagem dessa abordagem é que sua classe precisa ser mutável, ou seja, os atributos de classe não 
  podem ser declarados como `final`.

Utilizando o padrão _builder_ temos as vantagens das duas alternativas citadas acima: classes imutáveis e 
legibilidade ao inicializar objetos. Ex.:
```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 0) // (servingSize, servings)
                .calories(10)
                .sodium(35)
                .build();
```
perceba que em `Builder` forçamos o programador a inicializar os atributos obrigatórios e os opcionais não 
facilmente inseridos. A construção da classe acima pode ser encontrada no pacote `br.rochards.item2.builder`.

O padrão _builder_ também pode ser utilizado com classes abstratas (...)

:bulb: uma alternativa interessante para construir _builders_ é utilizar o projeto
[lombok](https://projectlombok.org/features/Builder).


### Item 4: force classes não instanciáveis com um construtor privado

Essa situação é bastante válida para aquelas classes que contêm apenas métodos e atributos estáticos. Podem ser 
consideradas classes utilitárias.

Lembre-se que o compilador Java fornece um construtor *default* (sem parâmetros) sempre que nenhum construtor é 
explicitamente declarado na classe. Então a estratégia consiste em criar um construtor privado:
```java
// classe utilitária não instanciável
public class UtilityClass {
    // suprime o construtor default com a declaração explícita
    private UtilityClass() {
        throw new AssertionError(); // não é obrigatório, está aqui só pro caso de instanciarem dentro da própria classe
    }
    // ...
}
```
:speech_balloon: nenhuma outra classe conseguiria ser subclasse de `UtilityClass`, pois não teria acesso ao 
construtor dela.

### Item 5: prefira injeção de dependência para integrar recursos

Não é incomum classes dependerem de outras para compor sua lista de atributos. Dependendo da forma como você os 
instancia dentro da classe, pode torná-la inflexível. Vamos ao exemplo:
```java
// uso inapropriado de um singleton - inflexível e não testável
public class SpellChecker {
    private final Lexicon dictionary = new Lexicon("pt-br");
    private SpellChecker() {}
    public static final SpellChecker INSTANCE = new SpellChecker(); // singleton pq só vai existir uma instância na memória
    
    public boolean isValid(String word) { /* ... */ }
    public List<String> suggestions(String typo) { /* ... */ }
}
```
o problema acima é que `SpellChecker` só valida palavras em um único idioma. 

:no_entry_sign: *singletons* e classe estáticas utilitárias não são adequadas para construir classes cujo 
comportamento depende de outros recursos (neste caso outra classe).

A recomendação é passar o recurso necessário pelo construtor ao criar uma nova instância da classe, é a chamada 
**injeção de dependência**:
```java
// injeção de dependência fornece flexibilidade e facilidade para testar
public class SpellChecker {
  private final Lexicon dictionary;

  private SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }

  public boolean isValid(String word) { /* ... */ }
  public List<String> suggestions(String typo) { /* ... */ }
}
```