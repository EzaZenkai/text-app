package lt.teksto.skaiciuokle;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import lt.teksto.skaiciuokle.adapter.HistoryAdapter;
import lt.teksto.skaiciuokle.calculator.TextMetricsCalculator;
import lt.teksto.skaiciuokle.databinding.ActivityMainBinding;
import lt.teksto.skaiciuokle.model.HistoryItem;
import lt.teksto.skaiciuokle.model.TextMetrics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryItemClickListener {

    private static final String KEY_HISTORY = "extra_runtime_history";

    private final int[] sampleResIds = new int[] {
            R.string.sample_text_1,
            R.string.sample_text_2,
            R.string.sample_text_3,
            R.string.sample_text_4
    };
    private int sampleIndex = 0;

    private ActivityMainBinding binding;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupListeners();

        // Atstatome istoriją po ekrano pasukimo (configuration change)
        if (savedInstanceState != null) {
            ArrayList<HistoryItem> savedList;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                savedList = (ArrayList<HistoryItem>) savedInstanceState.getSerializable(KEY_HISTORY, ArrayList.class);
            } else {
                @SuppressWarnings("deprecation")
                ArrayList<HistoryItem> legacyList = (ArrayList<HistoryItem>) savedInstanceState.getSerializable(KEY_HISTORY);
                savedList = legacyList;
            }
            if (savedList != null) {
                historyAdapter.setItems(savedList);
            }
        }
        updateHistoryVisibility();
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter(this);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(historyAdapter);
    }

    private void setupListeners() {
        // Skaičiavimo mygtukas
        binding.btnCalculate.setOnClickListener(v -> calculateMetrics());

        // Pavyzdinio teksto įterpimo mygtukas
        binding.btnSampleText.setOnClickListener(v -> insertSampleText());

        // Įvesties ir rezultatų išvalymo mygtukas
        binding.btnClear.setOnClickListener(v -> clearInputAndResults());

        // Istorijos išvalymo mygtukas
        binding.btnClearHistory.setOnClickListener(v -> clearHistory());

        // Išvalome klaidą, kai vartotojas pradeda rašyti
        binding.etInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.tilInputText.getError() != null) {
                    binding.tilInputText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Vykdo įvesties patikrą ir atlieka teksto metrikų skaičiavimą.
     */
    private void calculateMetrics() {
        CharSequence inputSequence = binding.etInputText.getText();
        String rawText = inputSequence != null ? inputSequence.toString() : "";

        // Reikalavimas: patikrinti ar įvesties laukas nėra tuščias, informuoti vartotoją
        if (rawText.trim().isEmpty()) {
            binding.tilInputText.setError(getString(R.string.error_empty_input));
            binding.etInputText.requestFocus();
            Toast.makeText(this, R.string.toast_empty_input, Toast.LENGTH_SHORT).show();
            return;
        }

        binding.tilInputText.setError(null);

        // Skaičiavimas atliekamas atskiroje perpanaudojamoje klasėje
        TextMetrics metrics = TextMetricsCalculator.calculate(rawText);
        displayMetrics(metrics);

        // Pridedame įrašą į skaičiavimų istoriją (runtime)
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        HistoryItem historyItem = new HistoryItem(timestamp, rawText, metrics);
        historyAdapter.addItem(historyItem);
        binding.rvHistory.scrollToPosition(0);

        updateHistoryVisibility();
    }

    /**
     * Atvaizduoja apskaičiuotas metrikas vartotojo sąsajoje.
     */
    private void displayMetrics(TextMetrics metrics) {
        binding.tvWordCount.setText(String.valueOf(metrics.getWordCount()));
        binding.tvSentenceCount.setText(String.valueOf(metrics.getSentenceCount()));
        binding.tvCharCountWithSpaces.setText(String.valueOf(metrics.getCharCountWithSpaces()));
        binding.tvNumberCount.setText(String.valueOf(metrics.getNumberCount()));
        binding.tvCharCountWithoutSpaces.setText(String.valueOf(metrics.getCharCountWithoutSpaces()));
        binding.tvLineCount.setText(String.valueOf(metrics.getLineCount()));
    }

    /**
     * Sugeneruoja / įterpia paruoštus testavimo sakinius (rotuoja pavyzdžius).
     */
    private void insertSampleText() {
        int currentIndex = sampleIndex % sampleResIds.length;
        int resId = sampleResIds[currentIndex];
        String sampleText = getString(resId);
        sampleIndex++;

        binding.etInputText.setText(sampleText);
        binding.etInputText.setSelection(sampleText.length());
        binding.tilInputText.setError(null);
        Toast.makeText(this, getString(R.string.toast_sample_inserted, currentIndex + 1), Toast.LENGTH_SHORT).show();
    }

    /**
     * Išvalo įvesties lauką ir atstato rezultatų reikšmes į nulius.
     */
    private void clearInputAndResults() {
        binding.etInputText.setText("");
        binding.tilInputText.setError(null);

        binding.tvWordCount.setText(R.string.default_metric_value);
        binding.tvSentenceCount.setText(R.string.default_metric_value);
        binding.tvCharCountWithSpaces.setText(R.string.default_metric_value);
        binding.tvNumberCount.setText(R.string.default_metric_value);
        binding.tvCharCountWithoutSpaces.setText(R.string.default_metric_value);
        binding.tvLineCount.setText(R.string.default_metric_value);

        Toast.makeText(this, R.string.toast_cleared, Toast.LENGTH_SHORT).show();
    }

    /**
     * Išvalo skaičiavimų istoriją.
     */
    private void clearHistory() {
        historyAdapter.clearItems();
        updateHistoryVisibility();
        Toast.makeText(this, R.string.toast_history_cleared, Toast.LENGTH_SHORT).show();
    }

    /**
     * Valdo istorijos sąrašo ir tuščios būsenos matomumą.
     */
    private void updateHistoryVisibility() {
        boolean hasItems = historyAdapter.getItemCount() > 0;
        binding.rvHistory.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        binding.btnClearHistory.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        binding.tvHistoryEmpty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemClick(HistoryItem item) {
        // Paspaudus ant istorijos kortelės, įkeliamas pilnas tekstas ir atstatomi jo rezultatai
        binding.etInputText.setText(item.getFullText());
        binding.etInputText.setSelection(item.getFullText().length());
        binding.tilInputText.setError(null);
        displayMetrics(item.getMetrics());

        Toast.makeText(this, R.string.toast_history_loaded, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Išsaugome runtime istoriją pasukus ekraną
        outState.putSerializable(KEY_HISTORY, new ArrayList<>(historyAdapter.getItems()));
    }
}
