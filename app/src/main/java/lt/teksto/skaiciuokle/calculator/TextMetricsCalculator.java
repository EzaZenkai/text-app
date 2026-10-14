package lt.teksto.skaiciuokle.calculator;

import lt.teksto.skaiciuokle.model.TextMetrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Universali, atskira ir nepriklausoma teksto metrikų skaičiavimo klasė.
 * Gali būti laisvai perpanaudojama bet kokiame Java / Android projekte.
 * Nenaudoja jokių specifinių Android karkaso priklausomybių.
 */
public final class TextMetricsCalculator {

    // Iš anksto sukompiliuoti šablonai našumui užtikrinti
    private static final Pattern LETTER_PATTERN = Pattern.compile("\\p{L}");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern CONTENT_PATTERN = Pattern.compile("[\\p{L}\\p{N}]");
    private static final Pattern SENTENCE_SPLIT_PATTERN = Pattern.compile("(?<=[.!?…])\\s+");

    // Neleidžiame kurti klasės egzempliorių, nes visi metodai yra statiniai
    private TextMetricsCalculator() {
        throw new UnsupportedOperationException("TextMetricsCalculator yra paslaugų klasė.");
    }

    /**
     * Apskaičiuoja visas teksto metrikas ir grąžina TextMetrics objektą.
     *
     * @param text analizuojamas tekstas
     * @return TextMetrics objektas su visais skaičiavimų rezultatais
     */
    public static TextMetrics calculate(String text) {
        if (text == null || text.isEmpty()) {
            return TextMetrics.empty();
        }

        int words = countWords(text);
        int charsWithSpaces = countCharactersWithSpaces(text);
        int charsWithoutSpaces = countCharactersWithoutSpaces(text);
        int sentences = countSentences(text);
        int lines = countLines(text);
        int numbers = countNumbers(text);

        return new TextMetrics(words, charsWithSpaces, charsWithoutSpaces, sentences, lines, numbers);
    }

    /**
     * Suskaičiuoja visus simbolius, įskaitant tarpus ir naujas eilutes.
     */
    public static int countCharactersWithSpaces(String text) {
        if (text == null) {
            return 0;
        }
        return text.length();
    }

    /**
     * Suskaičiuoja simbolius be jokių tarpų (whitespace).
     */
    public static int countCharactersWithoutSpaces(String text) {
        if (text == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Suskaičiuoja žodžius tekste.
     * Žodžiu laikoma simbolių seka, atskirta tarpais ir turinti bent vieną raidę (\p{L}).
     * Pavieniai skaičiai (pvz., „15“, „2026“) ar skyrybos ženklai nelaikomi žodžiais.
     * Teisingai palaiko lietuviškas ir kitas Unicode raides.
     */
    public static int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String[] tokens = text.trim().split("\\s+");
        int count = 0;
        for (String token : tokens) {
            if (LETTER_PATTERN.matcher(token).find()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Suskaičiuoja skaičius tekste.
     * Skaičiumi laikomas atskiras žodžio/elemento fragmentas, kuriame nėra raidžių,
     * tačiau yra bent vienas skaitmuo (pvz., „1“, „15“, „2026“, „3.14“).
     */
    public static int countNumbers(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String[] tokens = text.trim().split("\\s+");
        int count = 0;
        for (String token : tokens) {
            if (!LETTER_PATTERN.matcher(token).find() && DIGIT_PATTERN.matcher(token).find()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Suskaičiuoja sakinius tekste.
     * Sakiniai atskiriami tašku, šauktuku, klaustuku arba daugataškiu.
     * Taip pat teisingai traktuoja nebaigtą sakinį (tekstą be pabaigos skyrybos ženklo).
     */
    public static int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        // Jei tekste nėra jokių raidžių ar skaičių (pvz., tik "..." ar "---"), sakinių nėra
        if (!CONTENT_PATTERN.matcher(text).find()) {
            return 0;
        }

        // Sakinių skirtukai: vienas ar daugiau . ! ? … po kurių seka tarpas, eilutės pabaiga arba teksto pabaiga
        String trimmed = text.trim();
        String[] sentenceArray = SENTENCE_SPLIT_PATTERN.split(trimmed);

        int sentenceCount = 0;
        for (String sentence : sentenceArray) {
            if (CONTENT_PATTERN.matcher(sentence).find()) {
                sentenceCount++;
            }
        }

        return sentenceCount;
    }

    /**
     * Suskaičiuoja eilučių skaičių tekste be masyvų allokavimo.
     */
    public static int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int lines = 1;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                lines++;
            } else if (c == '\r') {
                // Jei po \r neseka \n, traktuojame kaip atskirą eilutę
                if (i + 1 < text.length() && text.charAt(i + 1) != '\n') {
                    lines++;
                }
            }
        }
        return lines;
    }
}
