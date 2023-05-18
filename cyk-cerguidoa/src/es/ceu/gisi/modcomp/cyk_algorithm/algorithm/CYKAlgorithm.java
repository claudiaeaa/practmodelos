package es.ceu.gisi.modcomp.cyk_algorithm.algorithm;

import es.ceu.gisi.modcomp.cyk_algorithm.algorithm.exceptions.CYKAlgorithmException;
import es.ceu.gisi.modcomp.cyk_algorithm.algorithm.interfaces.CYKAlgorithmInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;



/**
 * Esta clase contiene la implementación de la interfaz CYKAlgorithmInterface
 * que establece los métodos necesarios para el correcto funcionamiento del
 * proyecto de programación de la asignatura Modelos de Computación.
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class CYKAlgorithm implements CYKAlgorithmInterface {
	ArrayList<Character> nonTerminals = new ArrayList<Character>();
    	ArrayList<Character> Terminals = new ArrayList<Character>();
    	char startSymbol;
    	Map<Character, Set<String>> productions = new HashMap<>();

 

    @Override
    /**
     * Método que añade los elementos no terminales de la gramática.
     *
     * @param nonterminal Por ejemplo, 'S'
     * @throws CYKAlgorithmException Si el elemento no es una letra mayúscula.
     */
    public void addNonTerminal(char nonterminal) throws CYKAlgorithmException {
        if (!Character.isUpperCase(nonterminal)) {
            throw new CYKAlgorithmException();
        }
        if (nonTerminals.contains(nonterminal)) {
            throw new CYKAlgorithmException();
        }
        nonTerminals.add(nonterminal);
    }

    @Override
    /**
     * Método que añade los elementos terminales de la gramática.
     *
     * @param terminal Por ejemplo, 'a'
     * @throws CYKAlgorithmException Si el elemento no es una letra minúscula.
     */
    public void addTerminal(char terminal) throws CYKAlgorithmException {
       if (!Character.isLowerCase(terminal)) {
            throw new CYKAlgorithmException();
        }
        if (terminals.contains(terminal)) {
            throw new CYKAlgorithmException();
        }else{
        terminals.add(terminal);
    }
    }

    @Override
    /**
     * Método que indica, de los elementos no terminales, cuál es el axioma de
     * la gramática.
     *
     * @param nonterminal Por ejemplo, 'S'
     * @throws CYKAlgorithmException Si el elemento insertado no forma parte del
     * conjunto de elementos no terminales.
     */
    public void setStartSymbol(char nonterminal) throws CYKAlgorithmException {
         if (!nonTerminals.contains(nonterminal)) {
            throw new CYKAlgorithmException();
        }else{
        startSymbol = nonterminal;
    }
    }


    @Override
    /**
     * Método utilizado para construir la gramática. Admite producciones en FNC,
     * es decir de tipo A::=BC o A::=a
     *
     * @param nonterminal A
     * @param production "BC" o "a"
     * @throws CYKAlgorithmException Si la producción no se ajusta a FNC o está
     * compuesta por elementos (terminales o no terminales) no definidos
     * previamente.
     */
    public void addProduction(char nonterminal, String production) throws CYKAlgorithmException {
        
	   if (!nonTerminals.contains(nonterminal)) {
       		 throw new CYKAlgorithmException();
    }
  	  if (production.length() == 2 && Character.isUpperCase(production.charAt(0)) && Character.isUpperCase(production.charAt(1)) && nonTerminals.contains(production.charAt(0)) && nonTerminals.contains(production.charAt(1)) && nonterminal != production.charAt(0)) {
        Set<String> nonterminalProductions = productions.getOrDefault(nonterminal, new HashSet<>());
        if (nonterminalProductions.contains(production)) {
           	 throw new CYKAlgorithmException();
        }
        nonterminalProductions.add(production);
        productions.put(nonterminal, nonterminalProductions);
    } else if (production.length() == 1 && Character.isLowerCase(production.charAt(0)) && Terminals.contains(production.charAt(0))) {
        Set<String> nonterminalProductions = productions.getOrDefault(nonterminal, new HashSet<>());
        if (nonterminalProductions.contains(production)) {
            	throw new CYKAlgorithmException();
        }
        nonterminalProductions.add(production);
        productions.put(nonterminal, nonterminalProductions);
    } else {
        	throw new CYKAlgorithmException();
    }
}

    @Override
    /**
     * Método que indica si una palabra pertenece al lenguaje generado por la
     * gramática que se ha introducido.
     *
     * @param word La palabra a verificar, tiene que estar formada sólo por
     * elementos no terminales.
     * @return TRUE si la palabra pertenece, FALSE en caso contrario
     * @throws CYKAlgorithmException Si la palabra proporcionada no está formada
     * sólo por terminales, si está formada por terminales que no pertenecen al
     * conjunto de terminales definido para la gramática introducida, si la
     * gramática es vacía o si el autómata carece de axioma.
     */
    public boolean isDerived(String word) throws CYKAlgorithmException {
       int n = word.length();
    if (n == 0) {
        throw new CYKAlgorithmException();
    }
    if (nonTerminals.isEmpty() || startSymbol == '\u0000') {
        throw new CYKAlgorithmException();
    }

    Set<Character>[][] table = new HashSet[n][n];

    for (int i = 0; i < n; i++) {
        char terminal = word.charAt(i);
        if (!Terminals.contains(terminal)) {
            throw new CYKAlgorithmException();
        }
        table[i][i] = getNonterminalsFromTerminal(terminal);
    }

    for (int l = 2; l <= n; l++) {
        for (int i = 0; i <= n - l; i++) {
            int j = i + l - 1;
            table[i][j] = new HashSet<>();
            for (int k = i; k < j; k++) {
                Set<Character> nonterminalsA = table[i][k];
                Set<Character> nonterminalsB = table[k + 1][j];
                for (char nonterminal : nonTerminals) {
                    for (String production : productions.getOrDefault(nonterminal, Collections.emptySet())) {
                        if (production.length() == 2) {
                            char symbolA = production.charAt(0);
                            char symbolB = production.charAt(1);
                            if (nonterminalsA.contains(symbolA) && nonterminalsB.contains(symbolB)) {
                                table[i][j].add(nonterminal);
                            }
                        }
                    }
                }
            }
        }
    }

    return table[0][n - 1].contains(startSymbol);
}



    @Override
    /**
     * Método que, para una palabra, devuelve un String que contiene todas las
     * celdas calculadas por el algoritmo (la visualización debe ser similar al
     * ejemplo proporcionado en el PDF que contiene el paso a paso del
     * algoritmo).
     *
     * @param word La palabra a verificar, tiene que estar formada sólo por
     * elementos no terminales.
     * @return Un String donde se vea la tabla calculada de manera completa,
     * todas las celdas que ha calculado el algoritmo.
     * @throws CYKAlgorithmException Si la palabra proporcionada no está formada
     * sólo por terminales, si está formada por terminales que no pertenecen al
     * conjunto de terminales definido para la gramática introducida, si la
     * gramática es vacía o si el autómata carece de axioma.
     */
    public String algorithmStateToString(String word) throws CYKAlgorithmException {
        if (!isValidWord(word)) {
        throw new CYKAlgorithmException("La palabra no es válida. Debe estar formada solo por elementos no terminales.");
    }

    int n = word.length();
    String[][] table = new String[n][n];

    // Inicializar la tabla con valores vacíos
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            table[i][j] = "";
        }
    }

    // Calcular los valores de la tabla
    for (int i = 0; i < n; i++) {
        char nonTerminal = word.charAt(i);
        StringBuilder derivedSymbols = new StringBuilder();

        for (Map.Entry<Character, Set<String>> entry : productions.entrySet()) {
            char symbol = entry.getKey();
            Set<String> symbolProductions = entry.getValue();

            for (String production : symbolProductions) {
                if (production.equals(Character.toString(nonTerminal))) {
                    derivedSymbols.append(symbol);
                }
            }
        }

        table[i][0] = derivedSymbols.toString();
    }

    for (int j = 1; j < n; j++) {
        for (int i = 0; i < n - j; i++) {
            StringBuilder derivedSymbols = new StringBuilder();

            for (int k = 0; k < j; k++) {
                String symbols1 = table[i][k];
                String symbols2 = table[i + k + 1][j - k - 1];

                for (int m = 0; m < symbols1.length(); m++) {
                    char symbol1 = symbols1.charAt(m);

                    for (int p = 0; p < symbols2.length(); p++) {
                        char symbol2 = symbols2.charAt(p);

                        Set<String> symbolProductions = productions.getOrDefault(symbol1, Collections.emptySet());
                        for (String production : symbolProductions) {
                            if (production.length() == 2 && production.charAt(0) == symbol1 && production.charAt(1) == symbol2) {
                                derivedSymbols.append(symbolProductions);
                            }
                        }
                    }
                }
            }

            table[i][j] = derivedSymbols.toString();
        }
    }

    // Construir el String de visualización de la tabla
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n - i; j++) {
            result.append(String.format("(%d,%d): %s ", i, i + j, table[i][j]));
        }
        result.append("\n");
    }

    return result.toString();
}

   private boolean isValidWord(String word) {
    for (int i = 0; i < word.length(); i++) {
        char symbol = word.charAt(i);
        if (!nonTerminals.contains(symbol)) {
            return false;
        }
    }
    return true;
}

    @Override
    /**
     * Elimina todos los elementos que se han introducido hasta el momento en la
     * gramática (elementos terminales, no terminales, axioma y producciones),
     * dejando el algoritmo listo para volver a insertar una gramática nueva.
     */
    public void removeGrammar() {
    	nonTerminals.clear();
    	terminals.clear();
    	productions.clear();
    	startSymbol = '\0';
    }

    @Override
    /**
     * Devuelve un String que representa todas las producciones que han sido
     * agregadas a un elemento no terminal.
     *
     * @param nonterminal
     * @return Devuelve un String donde se indica, el elemento no terminal, el
     * símbolo de producción "::=" y las producciones agregadas separadas, única
     * y exclusivamente por una barra '|' (no incluya ningún espacio). Por
     * ejemplo, si se piden las producciones del elemento 'S', el String de
     * salida podría ser: "S::=AB|BC".
     */
    public String getProductions(char nonterminal) {
         return productions.entrySet().stream()
            .filter(entry -> entry.getKey() == nonterminal)
            .findFirst()
            .map(entry -> {
                StringBuilder productionsString = new StringBuilder();
                Set<String> value = entry.getValue();
                productionsString.append(entry.getKey()).append("::=");
                List<String> sortedProductions = new ArrayList<>(value);
                Collections.sort(sortedProductions);
                for (int i = 0; i < sortedProductions.size(); i++) {
                    if (i > 0) {
                        productionsString.append("|");
                    }
                    productionsString.append(sortedProductions.get(i));
                }
                return productionsString.toString();
            })
            .orElse("");
}

    @Override
    /**
     * Devuelve un String con la gramática completa.
     *
     * @return Devuelve el agregado de hacer getProductions sobre todos los
     * elementos no terminales.
     */
    public String getGrammar() {
         StringBuilder grammar = new StringBuilder();

    for (char nonTerminal : nonTerminals) {
        if (productions.containsKey(nonTerminal)) {
            grammar.append(nonTerminal).append(" -> ").append(productions.get(nonTerminal)).append("\n");
        }
    }

    return grammar.toString();
}
}

