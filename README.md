# Effective Java
Anotações e exemplos do livro Effective Java de Joshua Bloch.

### Item 2: considere utilizar builders quando o construtor tiver muitos parâmetros

Esta estratégia é muito adequada para os casos em que a classe possui vários atributos opcionais na sua composição. 
Algumas outras alternativas são:
* Utilizar o padrão *telescoping constructor*: quando você cria vários construtores na classe, dando mais opções de 
  inicialização. Ex.:
  ```java
  // ...
  public NutritionFacts(int servingSize, int servings) {
    this(servingSizes, servings, 0) // chama um construtor com 3 parâmetros
  }
  
  public NutritionFacts(int servingSize, int servings, int calories) {
    this(servingSizes, servings, calories, 0) // chama um construtor com 4 parâmetros
  }
  // ...
  ```