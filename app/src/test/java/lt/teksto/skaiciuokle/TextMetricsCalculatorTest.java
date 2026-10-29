package lt.teksto.skaiciuokle;

import lt.teksto.skaiciuokle.calculator.TextMetricsCalculator;
import lt.teksto.skaiciuokle.model.TextMetrics;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Teksto metrikų skaičiuoklės vienetų (JUnit) testai,
 * padengiantys 100% klasių, metodų ir sąlygų šakų.
 */
public class TextMetricsCalculatorTest {

    @Test
    public void testPrivateConstructorThrowsExceptionViaReflection() throws Exception {
        Constructor<TextMetricsCalculator> constructor =
                TextMetricsCalculator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            constructor.newInstance();
            fail("Privatus konstruktorius privalo mesti UnsupportedOperationException");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof UnsupportedOperationException);
            assertEquals("TextMetricsCalculator yra paslaugų klasė.", e.getCause().getMessage());
        }
    }

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
    public void testMultipleSentencesAndEllipsis() {
        String text = "Pirmas sakinys… Antras sakinys! Trečias sakinys? Ketvirtas be taško";
        TextMetrics result = TextMetricsCalculator.calculate(text);

        assertEquals(9, result.getWordCount());
        assertEquals(4, result.getSentenceCount());
    }

    @Test
    public void testSentenceWithPunctuationOnly() {
        // Tekstas neturintis jokių raidžių ar skaičių
        assertEquals(0, TextMetricsCalculator.countSentences("... ! ?? ---"));
        assertEquals(0, TextMetricsCalculator.countWords("--- ..."));
        assertEquals(0, TextMetricsCalculator.countNumbers("--- ..."));
    }

    @Test
    public void testSentenceSplitWithTrailingPunctuationSegment() {
        // Sakinys, po kurio seka skyrybos ženklai be raidžių
        String text = "Pirmas sakinys. ... Antras sakinys!";
        assertEquals(2, TextMetricsCalculator.countSentences(text));
    }

    @Test
    public void testMultilineTextAndLineEndings() {
        // Tikriname \n, \r\n, \r atskirai ir \r pabaigoje
        String textLf = "A\nB\nC";
        assertEquals(3, TextMetricsCalculator.countLines(textLf));

        String textCrLf = "A\r\nB\r\nC";
        assertEquals(3, TextMetricsCalculator.countLines(textCrLf));

        String textCrOnly = "A\rB\rC";
        assertEquals(3, TextMetricsCalculator.countLines(textCrOnly));

        String textTrailingCr = "A\r";
        assertEquals(1, TextMetricsCalculator.countLines(textTrailingCr));
    }

    @Test
    public void testIndividualMethodsWithNullAndEmpty() {
        assertEquals(0, TextMetricsCalculator.countWords(null));
        assertEquals(0, TextMetricsCalculator.countWords("   "));
        assertEquals(0, TextMetricsCalculator.countNumbers(null));
        assertEquals(0, TextMetricsCalculator.countNumbers("   "));
        assertEquals(0, TextMetricsCalculator.countSentences(null));
        assertEquals(0, TextMetricsCalculator.countSentences("   "));
        assertEquals(0, TextMetricsCalculator.countCharactersWithSpaces(null));
        assertEquals(0, TextMetricsCalculator.countCharactersWithoutSpaces(null));
        assertEquals(0, TextMetricsCalculator.countLines(null));
        assertEquals(0, TextMetricsCalculator.countLines(""));
    }
}
