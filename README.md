# Effective Java
Anotações e exemplos do livro Effective Java de Joshua Bloch.

## Capítulo 2: Criando e destruindo objetos

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

  public SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }

  public boolean isValid(String word) { /* ... */ }
  public List<String> suggestions(String typo) { /* ... */ }
}
```


### Item 6: evite criar objetos desnecessariamente

Dê preferência em reutilizar a criar novas instâncias em se tratando de objetos imutáveis. Um exemplo seria:
```java
String s = new String("foo bar");
```
a linha acima sempre cria uma nova `String` cada vez que é executada, ou seja, uma nova instância desse objeto será 
criado na memória. Considere fazer:
```java
String s = "foo bar";
```
agora a mesma instância será utilizada todas as vezes que a linha acima for executada. **Essa abordagem pode melhorar a 
performance da sua aplicação**, por exemplo, dentro de um *loop*.

:warning: essa estratégia é segura porque uma `String` é um objeto imutável no Java.

Como mais um exemplo, vamos analisar o método abaixo que calcula a soma de todos os inteiros positivos:
```java
public static long sum(){
    Long sum = 0L;
    for(long i = 0; i <= Integer.MAX_VALUE; i++){
        sum += i; // é criada uma nova instância de sum a cada iteração pq é um Long
    }
    return sum;
}
```
se `sum` tivesse sido declarado como `long` em vez de `Long` a performance seria melhor para esse *loop*.

### Item 7: elimine referências a objetos obsoletos

Quando trabalhamos com Java, erroneamente pensamos que não devemos nos preocupar com gerenciamento de memória, uma 
vez que temos o *garbage collector* para fazer esse trabalho. No entanto, existem algumas situações em que o 
programador deve proativamente eliminar a referência a objetos obsoletos. Veja o exemplo da classe `Stack` no pacote 
`br.rochards.item7`, o problema se encontra no método `pop`:
```java
public Object pop() {
    if (size == 0)
        throw new EmptyStackException();
    return elements[--nextFreePosition];
}
```
`elements` é um *array* de objetos, e precisamos lembrar que esses tipos de *arrays* guardam apenas referências para 
objetos que estão alocados em algum lugar na memória. Quando utilizamos o método `pop` acima estamos apenas 
decrementando um contador para indicar que aquela posição agora está livre, mas as referências não foram removidas, 
como pode ser visto na figura abaixo
![stack de objetos](images/item7-stack.png)
perceba na figura acima que as posições 6 e 7 ainda possuem referências para objetos, portanto continuam ocupando 
espaço na memória e não serão removidos pelo *garbage collector*. Uma forma de resolver esse problema seria
```java
public Object pop() {
    if (size == 0)
        throw new EmptyStackException();
    Object result = elements[--nextFreePosition];
    elements[nextFreePosition] = null; // elimina a referência obsoleta
    return result;
}
```
De uma forma geral, **sempre que uma classe gerencia seu próprio espaço ocupado na memória, como a `Stack` acima, o 
programador deve estar alerta para vazamentos de memória (_memory leak_)**.

### Item 8: evite *finalizers* e *cleaners*

*Finalizers* são imprevisíveis, muitas vezes perigosos e geralmente desnecessários, então como uma regra geral você 
deve evitar seu uso. A partir do Java 9, *finalizers* forão descontinuados e substituídos por *cleaners*. *Cleaners* 
são menos perigosos do que *finalizers*, mas também são lentos e geralmente desnecessários.

Um defeito de *finalizers* e *cleaners* é que não há garantias que eles serão executados imediatamente. Isso 
significa que **você não deve executar nada crítico no tempo em um _finalizer_ ou _cleaner_**, como por exemplo 
fechar arquivos, pois *file descriptors* são recursos limitados e se deixados abertos pode impedir que outros 
programas abram arquivos. Um outro exemplo é não depender de *finalizers* e *cleaners* para atualizar informações em 
um banco de dados. 

(completar...)

### Item 9: prefira *try-with-resources* a *try-finally* 

Há muitos recursos Java que precisam ser explicitamente fechados invocando um método `close`. Um exemplo é a classe 
`java.io.BufferedReader`. No entanto, muitas vezes o programador esquece de fazer isso, o que pode acarretar 
problemas de desempenho na aplicação.

Vamos a um exemplo utilizando *try-finally*:
```java
public static String readFirstLineOfFile(String path) throws IOException {
    var br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine(); // pode lançar IOException
    } finally {
        br.close(); // pode lançar IOException
    }
}
```
ambas as linhas marcadas acima podem lançar a exceção indicada, acontece que se as duas linhas a lançarem, aparecerá 
no *stack trace* apenas a exceção de `br.close()`, dificultando o *debugging*.

O tratamento das exceções acima pode ser melhorado com *try-with-resources*, basta que a classe ou a sua superclasse 
implemente a interface **`AutoCloseable`**:
```java
public static String readFirstLineOfFile(String path) throws IOException {
    try (var br = new BufferedReader(new FileReader(path))) {
        return br.readLine(); // pode lançar IOException
    }
}
```
*try-with-resources* melhora a legibilidade do código e o método `close` é chamado de forma implícita. Se ambos os 
métodos `readLine` e `close` lançarem exceções, a última é suprimida para facilitar o diagnóstico do problema, mas 
ainda assim indicada na *stack trace*.

:nerd_face: em `br.rochards.item9.File` há mais exemplos de como utilizar o *try-with-resources* pode melhorar 
a legibilidade do código. 

:memo: Prefira *try-with-resources* a *try-finally* quando estiver trabalhando com recursos que devem ser fechados.


## Capítulo 3: Métodos comuns para todos os objetos

### Item 10: obedeça ao contrato geral quando sobrescrever o método `equals`

A forma mais fácil para evitar problemas é não sobrescrever o `equals`, mas há situações específicas em que isso 
melhor se aplica.

Em se tratando das situações que é apropriado sobrescrever o `equals`, são para casos em que a noção de **_logical 
equality_** difere de meros objetos e uma superclasse ainda não sobrescreveu o método. São os casos das **_value 
classes_** e tais classes representam um valor, como a `Integer` ou `String`. Assim, o programador muitas vezes está a 
procura de saber se esses valores são logicamente iguais, e não se apontam para o mesmo objeto na memória. Então 
`Integer` e `String` são exemplos de classes que sobrescrevem o método `equals` herdado de `Object`.

O livro traz muitas informações sobre este item, mas o resumo e recomendações dados para `equals` são:
- Utilizar o *framework open-source* da Google, **AutoValue**;
- Deixar a IDE gerar automaticamente e incluir todos os atributos que precisam fazer parte das comparações.


### Item 11: sempre sobrescreva o `hashCode` quando sobrescrever o `equals`

Se você falhar em fazer isso, estará violando o contrato geral para o `hashCode`, e *collections* como `HashMap` e 
`HashSet` não funcionarão da forma adequada. O ponto é:
* Se **dois objetos** são iguais de acordo com o método `equals`, então chamar o `hashCode` nesses mesmos objetos 
  necessariamente deve resultar no mesmo inteiro. **OBS.:** o método `hashCode` retorna um `int`;

Vamos exemplificar com uma classe chamada `PhoneNumber` que sobrescreveu apenas o `equals`:
```java
Map<PhoneNumber, String> map = new HashMap<>();
map.put(new PhoneNumber(707, 867, 5309), "Jenny");
```
você poderia esperar que fazendo `map.get(new PhoneNumber(707, 867, 5309))` obteria `Jenny`, mas retornou 
`null`. Pelo `equals` são obviamente os mesmos objetos, mas pelo `hashCode` não, justamente porque você violou o contrato.

:warning: objetos iguais devem possuir o mesmo `hashCode`.

O livro mostra um passo a passo de como gerar um bom `hashCode`, mas hoje as IDEs fazem isso para você. A dica é: 
**inclua no `hashCode` os mesmos atributos de classe utilizados no `equals`.**. Existem algumas IDEs, no entanto, 
que podem gerar um `hashCode` de baixa performance, quando utilizam o método `hash` da classe `Objects`. Ex.:
```java
// one-line hashCode - performance baixa
@Override
public int hashCode() {
    return Objects.hash(lineNum, prefix, areaCode); // considerando que a classe possui esses atributos    
}
```
o problema acima é que internamente o método cria um *array* e faz *boxing* e *unboxing* quando os atributos são 
tipos primitivos. Uma abordagem mais performática seria você escrever:
```java
@Override
public int hashCode() {
    // considerando que os atributos areaCode, prefix e lineNum são do tipo short
    int result = Short.hashCode(areaCode); // substituível apenas por: areaCode
    result = 31 * result + Short.hashCode(prefix); // substituível por: 31 * result + (int) prefix;
    result = 31 * result + Short.hashCode(lineNum); // substituível por: 31 * result + (int) lineNum;
    return result;
}
```


### Item 12: sempre sobrescreva o `toString`

Isso é mais uma recomendação do que obrigação, pois não é uma implementação crítica como o `equals` e `hashCode` já 
citados anteriormente. Forcener uma boa implementação do `toString` torna a sua classe mais legível ao imprimir, pois, 
lembre-se que esse método é automaticamente invocado nas utilizações de `println`, `printf`, *debuggers*, etc. 


### Item 13: sobrescreva o `clone` com sensatez

A interface `Cloneable` existe para determinar o comportamento do método `clone`, que é *protected*, da classe `Object`.
Se uma classe implementa a `Cloneable`, então o método `clone` de `Object` retorna uma cópia de cada atributo do objeto
* lembre-se que toda classe em Java implicitamente *extends* de `Object`.
a forma que `Cloneable` funciona é estranho para os padrões no Java, pois está modificando o comportamento de um método 
*protected* da superclasse.

(**... incompleto ...**)


### Item 14: considere implementar `Comparable`

A interface `Comparable` possui o método `compareTo`, que permite comparação para ordenação. Para ordenar um *array* de 
objetos que implementam a interface `Comparable` basta fazer: `Arrays.sort(array)`. Observe abaixo o contrato da 
interface `Comparable`
```java
public interface Comparable<T> {
    int compareTo(T t);
}
```
classes que originam objetos, que enventualmente precisarão ser ordenados, deveriam implementar `Comparable`.

Supondo que os objetos `x` e `y` foram orginados por uma classe que implementa `Comparable`, então a expressão `x.
compareTo(y)` deverá produzir os possíveis resultados abaixo:
* Retornar `-1` se `x` for menor que `y`;
* Retornar `0` se `x` for igual a `y`;
* Retornar `1` se `x` for maior `y`;
violar o contrato acima poderá impedir que estruturas do Java que dependam do `compareTo`, como `TreeSet` e `TreeMap` 
funcionem corretamente.

Quando a classe tem mais de um atributo que deve ser levado em conta na comparação, comece considerando primeiro o 
mais significativo. Se o resultado da comparação for zero, compare o próximo mais significativo e repita o processo 
para os demais, senão retorne o resultado. Ex.:
```java
@Override
public int compareTo(PhoneNumber phoneNumber) {
    /*
     * evite fazer cálculos aritméticos para retornar os valores: 
     * Ex.: int result = areaCode - phoneNumber.areaCode;
     * 
     * evite operadores relacionais ou ternários:
     * Ex.: int result = areaCode > phoneNumber.areaCode ? 1 : areaCode < phoneNumber.areaCode ? -1 : 0;
     * 
     * dê preferência aos métodos estáticos das classes como na implementação abaixo
     * */
    int result = Short.compare(areaCode, phoneNumber.areaCode);
    if (result == 0) {
        result = Short.compare(prefix, phoneNumber.prefix);
        if (result == 0)
            return Short.compare(lineNum, phoneNumber.lineNum);
    }
    return result;
}
```
a implementação acima foi retirada da classe `br.rochards.item14.PhoneNumber`.


## Capítulo 4: Classes e interfaces

### Item 15: restrinja a acessibilidade de classes e membros 

Um componente bem projetado/escrito/desenvolvido esconde seus detalhes de implementação, assim a comunicação se dá 
apenas pela sua API (ex.: a assinatura de um método me diz o que ele recebe e o que retorna). O resultado é que o 
sistema cresce desacoplado, e os componentes podem ser desenvolvidos, testados, otimizados, compreendidos e 
modificados de forma isolada. 

Adote como regra geral **tornar cada membro da classe o mais inacessível possível**. Antes de avançarmos nas 
discussões vamos relembrar alguns conceitos sobre modificadores de acesso em Java:
* **private** - o membro (atributo, método, classes aninhadas) da classe é acessível apenas dentro da classe;
* **protected** - o membro pode ser acessado pelas subclasses da classe em que foi declarado e por classes que 
  pertecem ao mesmo pacote;
* **public** - o membro é acessado de qualquer lugar;
* **package-private** - por último, não é um modificador de acesso no Java, mas é a visibilidade padrão de um membro 
  quando não explicitamente marcado por uns dos três citados acima. Nesse caso, o membro é acessível por qualquer 
  classe dentro do mesmo pacote.

Alguns conselhos interessantes trazidos pelo livro:
* Se uma determinada classe ou interface puder ser *package-private*, então deveria. Assim você poderia modificá-la, 
  substituí-la, ou até mesmo excluí-la em uma próxima *release* sem temer quebrar o código de clientes (consumidores 
  da classe);
* Se uma *package-private* classe ou interface é utilizada apenas por uma única classe, então considere escrevê-la 
  com modificador de acesso *private* dentro do mesmo arquivo da classe que a utiliza.
* Atributos de classes *public* deveriam ser *private*, dessa forma você tem controle sobre seus valores. Classes com 
  atributos *public* e mutáveis geralmente não são *thread-safe*;
* Você pode expor constantes via `public static final`. O importante é que esses atributos sejam de tipos primitivos 
  ou referências para objetos imutáveis.
  * Ex1.: `public static final int[] VALUES = { ... }` - os valores de um *array* podem ser modificados, por isso 
    exportá-los como variáveis públicas pode ser um furo de segurança;
  * Ex2.: para resolver o problema acima da mutabilidade do *array*, você poderia tomar a abordagem abaixo
  ```java
  private static final int[] PRIVATE_VALUES = { ... };
  public static final int[] values() {
        return PRIVATE_VALUES.clone(); // retorna uma cópia exata do array deixando PRIVATE_VALUES imune a modificações
  }
  ```

**(...voltar para falar sobre os módulos...)**


### Item 16: em classes públicas utilize métodos de acesso e não atributos públicos

Não é incomum em projetos termos classes que apenas agregam atributos, que o livro se refere como **_degenerate 
classes_**, como no exemplo abaixo:
```java
// Degenerate classes não deveriam ser públicas
class Point {
    public double x;
    public double y;
}
```
algumas recomendações para esses tipos de classes são:
* Se a classe é acessível fora do pacote em que foi declarada, então utilize método de acesso, *getters* e *setters*, 
  em vez de deixar os atributos como públicos, pois assim você pode foçar certos comportamentos;
  * Ex.:
  ```java
    public void setAge(int age) {
        if (age < 0) 
            throw new IllegalArgumentException("age " + age);
        this.age = age;
    }
  ```
* Em casos em que a classe *package-private*, ou uma classe aninhada (*nested class*), não há tanto problema em 
  deixar os atributos expostos, considerando que essa classe não será exposta para clientes (entenda clientes como 
  outros programadores que consumirão o seu código);
* Caso o atributo em questão seja `final` então é aceitável, pois às vezes você está querendo expor uma constante.


### Item 17: minimize a mutabilidade

Uma classe imutável é aquela que os valores dos seus atributos não podem ser modificados após inicializados. Alguns 
exemplos de classes imutáveis no Java são `String`, `BigInteger` e `BigDecimal`. Trabalhar com esses tipos de 
classes torna seu código menos suscetivel a erros.

Como construir uma classe imutável:
* **Não forneça métodos que modifique o estado do objeto.** Ex.: *setters*;
* **Não permita que a classe seja estendida**. Você pode fazer isso marcando a classe como `final`;
* **Declare todos os atributos como `final`**;
* **Declare todos os atributos como `private`**;
* **Não forneça aos clientes (programadores que usam sua classe) acesso à referência de objetos mutáveis**. Nunca 
  inicialize esses objetos com a referência fornecida pelo cliente:
  * Ex.:
  ```java
  public class SomeClass {
    private final List<String> names;

    public SomeClass(List<String> names) {
        /* sua lista names vai ter os mesmos valores passados pelo construtor, mas referências diferentes. Assim, se 
  o cliente modificar essa lista, names de SomeClass não será afetada */
        this.names = new ArrayList<>(names);
        /* this.names = List.copyOf(names); -> outra alternativa, a diferença é que names agora é imutável. */
    }
    public List<String> getNames() {
        /* abaixo é retornada uma cópia */
        return new ArrayList<>(names);
    }
  }
  ```

Vantagens de trabalhar com objetos imutáveis:
* **São simples** — já que possuirão um único estado (falando dos valores das proprieadades internas) enquanto 
  existir em memória;
* **São _thread-safe_; não requerem sincronização** — já que o estado do objeto não pode ser modificado, então o 
  programador fica despreocupado em saber se o objeto possui o estado mais atualizado pela *thread*;
* **Facilitam a composição de objetos** — imutáveis ou mutáveis. Fica fácil você criar e manter uma classe em que os
  seus atributos são objetos imutáveis;
* **São ótimos para serem utilizados em _collections_ como `Map` e `Set`** — usados como chaves de `Map` você sabe 
  que o valor não mudará; 

Desvantagems de trabalhar com objetos imutáveis:
* **requerem um novo objeto para cada novo valor** — pode ser custoso, acarretando problemas de performance. A dica é 
  sempre que possível reutilizar as instâncias criadas.

:memo: Como regra adote que **uma classe deveria ser imutável, a não ser quer haja uma boa razão para não ser**.  
:memo: **Se uma classe não puder ser imutável, limite sua mutabilidade o máximo possível**.