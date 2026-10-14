package lt.teksto.skaiciuokle.model;

import java.io.Serializable;

/**
 * Modelis, reprezentuojantis vieną atlikto skaičiavimo įrašą programėlės veikimo metu (runtime).
 */
public class HistoryItem implements Serializable {
    private final String timestamp;
    private final String textSnippet;
    private final String fullText;
    private final TextMetrics metrics;

    public HistoryItem(String timestamp, String fullText, TextMetrics metrics) {
        this.timestamp = timestamp;
        this.fullText = fullText;
        this.metrics = metrics;

        // Sukuriame trumpą peržiūrą (snippet)
        if (fullText != null) {
            String singleLine = fullText.replaceAll("\\s+", " ").trim();
            if (singleLine.length() > 60) {
                this.textSnippet = singleLine.substring(0, 57) + "…";
            } else {
                this.textSnippet = singleLine;
            }
        } else {
            this.textSnippet = "";
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTextSnippet() {
        return textSnippet;
    }

    public String getFullText() {
        return fullText;
    }

    public TextMetrics getMetrics() {
        return metrics;
    }
}
