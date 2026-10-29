package lt.teksto.skaiciuokle.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextMetricsTest {

    @Test
    public void testFullConstructorAndGetters() {
        TextMetrics metrics = new TextMetrics(10, 50, 42, 3, 2, 5);

        assertEquals(10, metrics.getWordCount());
        assertEquals(50, metrics.getCharCountWithSpaces());
        assertEquals(42, metrics.getCharCountWithoutSpaces());
        assertEquals(3, metrics.getSentenceCount());
        assertEquals(2, metrics.getLineCount());
        assertEquals(5, metrics.getNumberCount());
    }

    @Test
    public void testFiveParamConstructorDefaultsNumberCountToZero() {
        TextMetrics metrics = new TextMetrics(4, 20, 17, 1, 1);

        assertEquals(4, metrics.getWordCount());
        assertEquals(20, metrics.getCharCountWithSpaces());
        assertEquals(17, metrics.getCharCountWithoutSpaces());
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(1, metrics.getLineCount());
        assertEquals(0, metrics.getNumberCount());
    }

    @Test
    public void testEmptyFactoryMethod() {
        TextMetrics empty = TextMetrics.empty();

        assertEquals(0, empty.getWordCount());
        assertEquals(0, empty.getCharCountWithSpaces());
        assertEquals(0, empty.getCharCountWithoutSpaces());
        assertEquals(0, empty.getSentenceCount());
        assertEquals(0, empty.getLineCount());
        assertEquals(0, empty.getNumberCount());
    }

    @Test
    public void testEqualsAndHashCode() {
        TextMetrics m1 = new TextMetrics(5, 25, 20, 2, 1, 1);
        TextMetrics m2 = new TextMetrics(5, 25, 20, 2, 1, 1);

        // Tas pats objektas (this == o)
        assertEquals(m1, m1);

        // Lygūs objektai
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());

        // null ir kitos klasės objektas
        assertNotEquals(m1, null);
        assertNotEquals(m1, "StringObject");

        // Skirtumai kiekviename lauke
        assertNotEquals(m1, new TextMetrics(99, 25, 20, 2, 1, 1));
        assertNotEquals(m1, new TextMetrics(5, 99, 20, 2, 1, 1));
        assertNotEquals(m1, new TextMetrics(5, 25, 99, 2, 1, 1));
        assertNotEquals(m1, new TextMetrics(5, 25, 20, 99, 1, 1));
        assertNotEquals(m1, new TextMetrics(5, 25, 20, 2, 99, 1));
        assertNotEquals(m1, new TextMetrics(5, 25, 20, 2, 1, 99));
    }

    @Test
    public void testToString() {
        TextMetrics metrics = new TextMetrics(2, 12, 10, 1, 1, 0);
        String str = metrics.toString();

        assertNotNull(str);
        assertTrue(str.contains("wordCount=2"));
        assertTrue(str.contains("charCountWithSpaces=12"));
        assertTrue(str.contains("charCountWithoutSpaces=10"));
        assertTrue(str.contains("sentenceCount=1"));
        assertTrue(str.contains("lineCount=1"));
        assertTrue(str.contains("numberCount=0"));
    }
}
