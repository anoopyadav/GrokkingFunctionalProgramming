import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ch04_ranked_words {
    public static int score(String word) {
        return word.replaceAll("a", "").length();
    }

    public static int bonus(String word) {
        return word.contains("c") ? 5 : 0;
    }

    public static int penalty(String word) {
        return word.contains("s") ? 7 : 0;
    }

    public static List<String> rankedWords(List<String> words, Function<String, Integer> wordScore) {
        Comparator<String> comparator = (w1, w2) -> Integer.compare(wordScore.apply(w2), wordScore.apply(w1));
        return words.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<String> words = Arrays.asList("ada", "haskell", "java", "scala", "rust");
        System.out.println(rankedWords(words, ch04_ranked_words::score));
        System.out.println(rankedWords(words, w -> score(w) + bonus(w)));
        System.out.println(rankedWords(words, w -> score(w) + bonus(w) - penalty(w)));
    }
}
