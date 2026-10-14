package lt.teksto.skaiciuokle.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lt.teksto.skaiciuokle.R;
import lt.teksto.skaiciuokle.databinding.ItemHistoryBinding;
import lt.teksto.skaiciuokle.model.HistoryItem;
import lt.teksto.skaiciuokle.model.TextMetrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapteris skaičiavimų istorijos atvaizdavimui RecyclerView sąraše.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnHistoryItemClickListener {
        void onItemClick(HistoryItem item);
    }

    private static final int MAX_HISTORY_ITEMS = 30;
    private final List<HistoryItem> items = new ArrayList<>();
    private final OnHistoryItemClickListener listener;

    public HistoryAdapter(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(HistoryItem item) {
        // Įterpiame naujausią įrašo pradžioje
        items.add(0, item);
        notifyItemInserted(0);

        // Apribojame istorijos dydį, kad išvengtume TransactionTooLargeException saugant būseną
        if (items.size() > MAX_HISTORY_ITEMS) {
            int lastIndex = items.size() - 1;
            items.remove(lastIndex);
            notifyItemRemoved(lastIndex);
        }
    }

    public void clearItems() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public List<HistoryItem> getItems() {
        return items;
    }

    public void setItems(List<HistoryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryBinding binding;

        public HistoryViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryItem item, OnHistoryItemClickListener listener) {
            binding.tvHistoryTimestamp.setText(item.getTimestamp());

            TextMetrics metrics = item.getMetrics();
            String formattedSnippet = "„" + item.getTextSnippet() + "“ => " +
                    metrics.getSentenceCount() + ", " +
                    metrics.getWordCount() + ", " +
                    metrics.getCharCountWithSpaces() + ", " +
                    metrics.getNumberCount();
            binding.tvHistorySnippet.setText(formattedSnippet);

            String summary = itemView.getContext().getString(
                    R.string.history_item_summary,
                    metrics.getSentenceCount(),
                    metrics.getWordCount(),
                    metrics.getCharCountWithSpaces(),
                    metrics.getNumberCount()
            );
            binding.tvHistorySummary.setText(summary);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
