package lt.teksto.skaiciuokle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lt.teksto.skaiciuokle.R;
import lt.teksto.skaiciuokle.databinding.ItemHistoryBinding;
import lt.teksto.skaiciuokle.model.HistoryItem;
import lt.teksto.skaiciuokle.model.TextMetrics;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class HistoryAdapterTest {

    private HistoryAdapter.OnHistoryItemClickListener mockListener;
    private HistoryAdapter adapter;

    @Before
    public void setUp() throws Exception {
        mockListener = mock(HistoryAdapter.OnHistoryItemClickListener.class);
        adapter = new HistoryAdapter(mockListener);

        // Android unit test stub Observable initializes mObservers to null.
        // We set it to an empty list so notifyItem* calls succeed.
        java.lang.reflect.Field observableField = androidx.recyclerview.widget.RecyclerView.Adapter.class.getDeclaredField("mObservable");
        observableField.setAccessible(true);
        Object observable = observableField.get(adapter);
        java.lang.reflect.Field mObserversField = android.database.Observable.class.getDeclaredField("mObservers");
        mObserversField.setAccessible(true);
        mObserversField.set(observable, new ArrayList<>());
    }

    @Test
    public void testInitialState() {
        assertEquals(0, adapter.getItemCount());
        assertNotNull(adapter.getItems());
        assertTrue(adapter.getItems().isEmpty());
    }

    @Test
    public void testAddItemAddsToBeginning() {
        HistoryItem item1 = new HistoryItem("10:00:00", "Pirmas tekstas", TextMetrics.empty());
        HistoryItem item2 = new HistoryItem("10:01:00", "Antras tekstas", TextMetrics.empty());

        adapter.addItem(item1);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Pirmas tekstas", adapter.getItems().get(0).getFullText());

        adapter.addItem(item2);
        assertEquals(2, adapter.getItemCount());
        assertEquals("Antras tekstas", adapter.getItems().get(0).getFullText());
        assertEquals("Pirmas tekstas", adapter.getItems().get(1).getFullText());
    }

    @Test
    public void testAddItemLimitsToMaxThirtyItems() {
        // Pridedame 35 elementus ir tikriname, ar dydis neviršija 30
        for (int i = 1; i <= 35; i++) {
            HistoryItem item = new HistoryItem("10:00:" + i, "Tekstas " + i, TextMetrics.empty());
            adapter.addItem(item);
        }

        assertEquals(30, adapter.getItemCount());
        // Naujausias įrašas turi būti 35
        assertEquals("Tekstas 35", adapter.getItems().get(0).getFullText());
        // Paskutinis įrašas (seniausias iš likusių 30) turi būti 6
        assertEquals("Tekstas 6", adapter.getItems().get(29).getFullText());
    }

    @Test
    public void testClearItems() {
        adapter.addItem(new HistoryItem("10:00:00", "Tekstas 1", TextMetrics.empty()));
        adapter.addItem(new HistoryItem("10:01:00", "Tekstas 2", TextMetrics.empty()));
        assertEquals(2, adapter.getItemCount());

        adapter.clearItems();
        assertEquals(0, adapter.getItemCount());
        assertTrue(adapter.getItems().isEmpty());
    }

    @Test
    public void testSetItems() {
        List<HistoryItem> list = new ArrayList<>();
        list.add(new HistoryItem("10:00:00", "A", TextMetrics.empty()));
        list.add(new HistoryItem("10:01:00", "B", TextMetrics.empty()));

        adapter.setItems(list);
        assertEquals(2, adapter.getItemCount());
        assertEquals("A", adapter.getItems().get(0).getFullText());

        // Tikriname su null sąrašu
        adapter.setItems(null);
        assertEquals(0, adapter.getItemCount());
    }

    private ItemHistoryBinding createMockBinding(Context mockContext,
                                                 com.google.android.material.card.MaterialCardView mockRootView,
                                                 TextView mockTimestamp,
                                                 TextView mockSnippet,
                                                 TextView mockSummary) throws Exception {
        when(mockRootView.getContext()).thenReturn(mockContext);
        java.lang.reflect.Constructor<ItemHistoryBinding> constructor =
                ItemHistoryBinding.class.getDeclaredConstructor(
                        com.google.android.material.card.MaterialCardView.class,
                        TextView.class,
                        TextView.class,
                        TextView.class
                );
        constructor.setAccessible(true);
        return constructor.newInstance(mockRootView, mockSnippet, mockSummary, mockTimestamp);
    }

    @Test
    public void testOnCreateViewHolderAndOnBindViewHolder() throws Exception {
        ViewGroup mockParent = mock(ViewGroup.class);
        Context mockContext = mock(Context.class);
        when(mockParent.getContext()).thenReturn(mockContext);

        com.google.android.material.card.MaterialCardView mockRootView =
                mock(com.google.android.material.card.MaterialCardView.class);
        TextView mockTimestamp = mock(TextView.class);
        TextView mockSnippet = mock(TextView.class);
        TextView mockSummary = mock(TextView.class);

        ItemHistoryBinding mockBinding = createMockBinding(mockContext, mockRootView, mockTimestamp, mockSnippet, mockSummary);

        when(mockContext.getString(eq(R.string.history_item_summary), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn("1 sak., 2 žodž.");

        try (MockedStatic<ItemHistoryBinding> mockedStatic = mockStatic(ItemHistoryBinding.class);
             MockedStatic<LayoutInflater> layoutInflaterStatic = mockStatic(LayoutInflater.class)) {

            LayoutInflater mockInflater = mock(LayoutInflater.class);
            layoutInflaterStatic.when(() -> LayoutInflater.from(mockContext)).thenReturn(mockInflater);
            mockedStatic.when(() -> ItemHistoryBinding.inflate(mockInflater, mockParent, false))
                    .thenReturn(mockBinding);

            HistoryAdapter.HistoryViewHolder holder = adapter.onCreateViewHolder(mockParent, 0);
            assertNotNull(holder);

            HistoryItem item = new HistoryItem("12:30:00", "Pavyzdinis tekstas", new TextMetrics(2, 17, 16, 1, 1, 0));
            adapter.addItem(item);

            adapter.onBindViewHolder(holder, 0);

            verify(mockTimestamp).setText("12:30:00");
            verify(mockSummary).setText("1 sak., 2 žodž.");
        }
    }

    @Test
    public void testViewHolderBindAndClickListener() throws Exception {
        Context mockContext = mock(Context.class);
        com.google.android.material.card.MaterialCardView mockRootView =
                mock(com.google.android.material.card.MaterialCardView.class);
        TextView mockTimestamp = mock(TextView.class);
        TextView mockSnippet = mock(TextView.class);
        TextView mockSummary = mock(TextView.class);

        ItemHistoryBinding mockBinding = createMockBinding(mockContext, mockRootView, mockTimestamp, mockSnippet, mockSummary);

        when(mockContext.getString(eq(R.string.history_item_summary), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn("Santrauka");

        HistoryAdapter.HistoryViewHolder holder = new HistoryAdapter.HistoryViewHolder(mockBinding);
        HistoryItem item = new HistoryItem("15:00:00", "Tekstas paspaudimui", new TextMetrics(2, 20, 18, 1, 1, 0));

        // 1. Tikriname su klausytoju (listener != null)
        holder.bind(item, mockListener);

        // 2. Tikriname, kai listener == null (neturi mesti NullPointerException)
        holder.bind(item, null);

        ArgumentCaptor<View.OnClickListener> clickCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(mockRootView, times(2)).setOnClickListener(clickCaptor.capture());
        List<View.OnClickListener> allListeners = clickCaptor.getAllValues();

        // Pirmojo klausytojo paspaudimas (su listener != null)
        allListeners.get(0).onClick(mockRootView);
        verify(mockListener, times(1)).onItemClick(item);

        // Antrojo klausytojo paspaudimas (su listener == null)
        allListeners.get(1).onClick(mockRootView);
        verify(mockListener, times(1)).onItemClick(item); // iškvietimų skaičius neturi padidėti
    }
}
