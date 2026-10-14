package lt.teksto.skaiciuokle;

import lt.teksto.skaiciuokle.calculator.TextMetricsCalculator;
import lt.teksto.skaiciuokle.model.TextMetrics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Teksto metrikų skaičiuoklės vienetų (JUnit) testai.
 */
public class TextMetricsCalculatorTest {

    @Test
    public void testNullAndEmptyInput() {
        TextMetrics nullResult = TextMetricsCalculator.calculate(null);
        assertNotNull(nullResult);
        assertEquals(0, nullResult.getWordCount());
        assertEquals(0, nullResult.getCharCountWithSpaces());
        assertEquals(0, nullResult.getCharCountWithoutSpaces());
        assertEquals(0, nullResult.getSentenceCount());
        assertEquals(0, nullResult.getLineCount());
        assertEquals(0, nullResult.getNumberCount());

        TextMetrics emptyResult = TextMetricsCalculator.calculate("");
        assertEquals(0, emptyResult.getWordCount());
        assertEquals(0, emptyResult.getCharCountWithSpaces());
        assertEquals(0, emptyResult.getCharCountWithoutSpaces());
        assertEquals(0, emptyResult.getSentenceCount());
        assertEquals(0, emptyResult.getLineCount());
        assertEquals(0, emptyResult.getNumberCount());
    }

    @Test
    public void testWhitespaceOnlyInput() {
        String text = "   \n\t  ";
        TextMetrics result = TextMetricsCalculator.calculate(text);
        assertEquals(0, result.getWordCount());
        assertEquals(7, result.getCharCountWithSpaces());
        assertEquals(0, result.getCharCountWithoutSpaces());
        assertEquals(0, result.getSentenceCount());
        assertEquals(2, result.getLineCount());
        assertEquals(0, result.getNumberCount());
    }

    @Test
    public void testRubricExample1() {
        // „Labas rytas“ => 1, 2, 11, 0
        String text = "Labas rytas";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(1, result.getSentenceCount());
        assertEquals(2, result.getWordCount());
        assertEquals(11, result.getCharCountWithSpaces());
        assertEquals(0, result.getNumberCount());
    }

    @Test
    public void testRubricExample2() {
        // „Rugsėjo 1 prasidėjo karas. Liūdna“ => 2, 4, 33, 1
        String text = "Rugsėjo 1 prasidėjo karas. Liūdna";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(2, result.getSentenceCount());
        assertEquals(4, result.getWordCount());
        assertEquals(33, result.getCharCountWithSpaces());
        assertEquals(1, result.getNumberCount());
    }

    @Test
    public void testNumbersAreNotCountedAsWords() {
        String text = "Liepos 15 lyjo lietus";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        // „Liepos“, „lyjo“, „lietus“ yra žodžiai, o „15“ yra skaičius
        assertEquals(3, result.getWordCount());
        assertEquals(1, result.getNumberCount());
    }

    @Test
    public void testStandaloneNumbersAndPunctuation() {
        String text = "2026 metais buvo 100 procentų.";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        // Tik „metais“, „buvo“, „procentų“ turi raides -> 3 žodžiai
        assertEquals(3, result.getWordCount());
        // „2026“ ir „100“ yra skaičiai -> 2 skaičiai
        assertEquals(2, result.getNumberCount());
    }

    @Test
    public void testSimpleSentence() {
        String text = "Labas pasauli!";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(2, result.getWordCount());
        assertEquals(14, result.getCharCountWithSpaces());
        assertEquals(13, result.getCharCountWithoutSpaces());
        assertEquals(1, result.getSentenceCount());
        assertEquals(1, result.getLineCount());
    }

    @Test
    public void testLithuanianCharacters() {
        String text = "Ąžuolas žaliuoja, šviesa šviečia į kambarį.";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(6, result.getWordCount());
        assertEquals(43, result.getCharCountWithSpaces());
        assertEquals(38, result.getCharCountWithoutSpaces());
        assertEquals(1, result.getSentenceCount());
    }

    @Test
    public void testMultipleSentences() {
        String text = "Pirmas sakinys. Antras sakinys! Trečias sakinys? Ketvirtas be taško";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(9, result.getWordCount());
        assertEquals(4, result.getSentenceCount());
    }

    @Test
    public void testMultilineText() {
        String text = "Pirmoji eilutė.\nAntroji eilutė.\nTrečioji eilutė.";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(6, result.getWordCount());
        assertEquals(3, result.getSentenceCount());
        assertEquals(3, result.getLineCount());
    }

    @Test
    public void testIndividualMethods() {
        String text = "Vienas du trys.";
        assertEquals(3, TextMetricsCalculator.countWords(text));
        assertEquals(15, TextMetricsCalculator.countCharactersWithSpaces(text));
        assertEquals(13, TextMetricsCalculator.countCharactersWithoutSpaces(text));
        assertEquals(1, TextMetricsCalculator.countSentences(text));
        assertEquals(1, TextMetricsCalculator.countLines(text));
        assertEquals(0, TextMetricsCalculator.countNumbers(text));
    }
}
