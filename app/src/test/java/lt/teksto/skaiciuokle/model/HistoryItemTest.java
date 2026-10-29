package lt.teksto.skaiciuokle.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class HistoryItemTest {

    @Test
    public void testHistoryItemWithShortText() {
        TextMetrics metrics = new TextMetrics(3, 15, 13, 1, 1, 0);
        HistoryItem item = new HistoryItem("12:00:00", "Trumpas tekstas", metrics);

        assertEquals("12:00:00", item.getTimestamp());
        assertEquals("Trumpas tekstas", item.getFullText());
        assertEquals("Trumpas tekstas", item.getTextSnippet());
        assertSame(metrics, item.getMetrics());
    }

    @Test
    public void testHistoryItemWithLongTextTruncatesSnippet() {
        String longText = "Šis tekstas yra labai ilgas ir viršija šešiasdešimt simbolių ribą, todėl turi būti sutrumpintas!";
        TextMetrics metrics = new TextMetrics(13, 98, 86, 1, 1, 0);
        HistoryItem item = new HistoryItem("12:05:00", longText, metrics);

        assertEquals("12:05:00", item.getTimestamp());
        assertEquals(longText, item.getFullText());
        assertTrue(item.getTextSnippet().endsWith("…"));
        assertEquals(58, item.getTextSnippet().length()); // 57 simboliai + 1 daugataškis '…'
    }

    @Test
    public void testHistoryItemWithNullText() {
        HistoryItem item = new HistoryItem("12:10:00", null, TextMetrics.empty());

        assertEquals("12:10:00", item.getTimestamp());
        assertNull(item.getFullText());
        assertEquals("", item.getTextSnippet());
        assertNotNull(item.getMetrics());
    }

    @Test
    public void testHistoryItemWithMultipleSpacesAndNewlines() {
        String multilineText = "  Eilutė   vienas\n\n   Eilutė   du   ";
        HistoryItem item = new HistoryItem("12:15:00", multilineText, TextMetrics.empty());

        assertEquals(multilineText, item.getFullText());
        // Patikriname, kad daugialypiai tarpai ir naujos eilutės suspausti į vieną tarpą
        assertEquals("Eilutė vienas Eilutė du", item.getTextSnippet());
    }
}
