package lt.teksto.skaiciuokle.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Duomenų modelis, talpinantis apskaičiuotas teksto metrikas.
 * Ši klasė yra nepriklausoma nuo Android karkaso ir gali būti lengvai perpanaudojama.
 */
public class TextMetrics implements Serializable {
    private final int wordCount;
    private final int charCountWithSpaces;
    private final int charCountWithoutSpaces;
    private final int sentenceCount;
    private final int lineCount;
    private final int numberCount;

    public TextMetrics(int wordCount, int charCountWithSpaces, int charCountWithoutSpaces, int sentenceCount, int lineCount, int numberCount) {
        this.wordCount = wordCount;
        this.charCountWithSpaces = charCountWithSpaces;
        this.charCountWithoutSpaces = charCountWithoutSpaces;
        this.sentenceCount = sentenceCount;
        this.lineCount = lineCount;
        this.numberCount = numberCount;
    }

    public TextMetrics(int wordCount, int charCountWithSpaces, int charCountWithoutSpaces, int sentenceCount, int lineCount) {
        this(wordCount, charCountWithSpaces, charCountWithoutSpaces, sentenceCount, lineCount, 0);
    }

    public static TextMetrics empty() {
        return new TextMetrics(0, 0, 0, 0, 0, 0);
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharCountWithSpaces() {
        return charCountWithSpaces;
    }

    public int getCharCountWithoutSpaces() {
        return charCountWithoutSpaces;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getNumberCount() {
        return numberCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMetrics that = (TextMetrics) o;
        return wordCount == that.wordCount &&
                charCountWithSpaces == that.charCountWithSpaces &&
                charCountWithoutSpaces == that.charCountWithoutSpaces &&
                sentenceCount == that.sentenceCount &&
                lineCount == that.lineCount &&
                numberCount == that.numberCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordCount, charCountWithSpaces, charCountWithoutSpaces, sentenceCount, lineCount, numberCount);
    }

    @Override
    public String toString() {
        return "TextMetrics{" +
                "wordCount=" + wordCount +
                ", charCountWithSpaces=" + charCountWithSpaces +
                ", charCountWithoutSpaces=" + charCountWithoutSpaces +
                ", sentenceCount=" + sentenceCount +
                ", lineCount=" + lineCount +
                ", numberCount=" + numberCount +
                '}';
    }
}
