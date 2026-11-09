package org.delcom.ifs25011pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25011pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25011pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.util.List;
import java.util.function.Function;

/**
 * Tampilan web Vaadin aplikasi pencatat keuangan.
 * Pengurutan bisa lewat header kolom grid maupun pilihan {@link SortOption};
 * semua aturan bisnis didelegasikan ke {@link FinanceUseCase}.
 */
@Route("")
@PageTitle("Catatan Keuangan")
public class FinanceView extends VerticalLayout {

    /** Aksi penyimpanan transaksi baru (pemasukan atau pengeluaran). */
    @FunctionalInterface
    private interface TransactionSubmitter {
        Transaction submit(String description, String amount) throws ValidationException;
    }

    /** Teks deskripsi halaman untuk SEO (meta description). */
    private static final String PAGE_DESCRIPTION =
            "Aplikasi web pencatat keuangan pribadi: catat pemasukan dan pengeluaran, cari, urutkan, dan pantau saldo.";

    /** Warna tombol solid dengan teks putih; rasio kontras di atas 4.5:1. */
    private static final String INCOME_BUTTON_COLOR = "#166534";
    private static final String EXPENSE_BUTTON_COLOR = "#B91C1C";

    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortField = new ComboBox<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span();

    private final Span transactionCount = new Span("0");
    private final Span incomeTotal = new Span();
    private final Span expenseTotal = new Span();
    private final Span balanceLabel = new Span();

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        configureLandmark();

        configureGrid();
        configureToolbarFields();

        add(new H1("Catatan Keuangan"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    // ---------- Aksesibilitas & SEO ----------

    /** Menjadikan seluruh isi halaman sebagai landmark utama (main) dan menambahkan meta description. */
    private void configureLandmark() {
        getElement().setAttribute("role", "main");
        getElement().setAttribute("aria-label", "Catatan Keuangan");

        addAttachListener(event -> event.getUI().getPage().executeJs(
                "if (!document.querySelector('meta[name=description]')) {"
                        + "const meta = document.createElement('meta');"
                        + "meta.name = 'description';"
                        + "meta.content = $0;"
                        + "document.head.appendChild(meta);"
                        + "}", PAGE_DESCRIPTION));
    }

    // ---------- Ringkasan ----------

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryCard("Jumlah Transaksi", transactionCount, "#DBEAFE", "#1D4ED8", "#1E3A8A"),
                createSummaryCard("Total Pemasukan", incomeTotal, "#DCFCE7", "#16A34A", "#14532D"),
                createSummaryCard("Total Pengeluaran", expenseTotal, "#FEE2E2", "#DC2626", "#7F1D1D"),
                createSummaryCard("Saldo", balanceLabel, "#FEF3C7", "#D97706", "#78350F")
        );
        summary.setWidthFull();
        summary.getStyle().set("align-items", "stretch");
        return summary;
    }

    private Div createSummaryCard(String label, Span value, String background, String accent,
                                  String textColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", textColor);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("color", textColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "0")
                .set("background", background)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        transactionCount.setText(String.valueOf(financeUseCase.getAllTransactions().size()));
        incomeTotal.setText(presenter.formatCurrency(financeUseCase.getTotalIncome()));
        expenseTotal.setText(presenter.formatCurrency(financeUseCase.getTotalExpense()));
        balanceLabel.setText(presenter.formatCurrency(financeUseCase.getBalance()));
    }

    // ---------- Grid ----------

    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID")
                .setKey("id")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Transaction::getId);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan")
                .setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(java.util.Comparator.comparing(
                        Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(transaction -> presenter.formatCurrency(transaction.getAmount()))
                .setHeader("Jumlah")
                .setKey("amount")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setComparator(SortOption.AMOUNT_ASC.comparator());

        grid.addComponentColumn(this::createTypeBadge)
                .setHeader("Tipe")
                .setKey("type")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(SortOption.INCOME_FIRST.comparator());

        grid.addColumn(presenter::formatCreatedAt)
                .setHeader("Waktu")
                .setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(java.util.Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createDeleteButton)
                .setHeader("Tindakan")
                .setKey("actions")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span createTypeBadge(Transaction transaction) {
        Span badge = new Span(presenter.formatType(transaction.getType()));
        badge.getStyle()
                .set("display", "inline-block")
                .set("padding", "0.15rem 0.6rem")
                .set("border-radius", "999px")
                .set("font-size", "0.8125rem")
                .set("font-weight", "600")
                .set("background-color", transaction.isIncome() ? "#DCFCE7" : "#FEE2E2")
                .set("color", transaction.isIncome() ? "#14532D" : "#7F1D1D");
        return badge;
    }

    private Button createDeleteButton(Transaction transaction) {
        Button button = new Button("Hapus", VaadinIcon.TRASH.create(),
                event -> openDeleteDialog(transaction));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.getStyle().set("color", EXPENSE_BUTTON_COLOR);
        return button;
    }

    // ---------- Toolbar ----------

    private void configureToolbarFields() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.addKeyPressListener(Key.ENTER, event -> searchTransactions());

        sortField.setPlaceholder("Urutkan...");
        sortField.setItems(SortOption.values());
        sortField.setItemLabelGenerator(presenter::formatSortOption);
        sortField.addValueChangeListener(event -> {
            if (event.isFromClient() && event.getValue() != null) {
                applySort(event.getValue());
            }
        });
    }

    private HorizontalLayout buildToolbar() {
        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(),
                event -> searchTransactions());
        Button refreshButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(),
                event -> refreshAll());

        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                event -> openAddDialog("Tambah Pemasukan",
                        financeUseCase::addIncome, presenter::showAddIncomeSuccess));
        styleSolidButton(incomeButton, INCOME_BUTTON_COLOR);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                event -> openAddDialog("Tambah Pengeluaran",
                        financeUseCase::addExpense, presenter::showAddExpenseSuccess));
        styleSolidButton(expenseButton, EXPENSE_BUTTON_COLOR);

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, searchButton, sortField, refreshButton, incomeButton, expenseButton);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        return toolbar;
    }

    /** Memberi warna latar gelap dan teks putih agar kontras memenuhi WCAG AA (minimal 4.5:1). */
    private void styleSolidButton(Button button, String background) {
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle()
                .set("--vaadin-button-background", background)
                .set("--vaadin-button-text-color", "#FFFFFF")
                .set("background-color", background)
                .set("color", "#FFFFFF");
    }

    // ---------- Dialog ----------

    private void openAddDialog(String title, TransactionSubmitter submitter,
                               Function<Transaction, String> successMessage) {
        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();

        TextField amountField = new TextField("Jumlah (Rp)");
        amountField.setPlaceholder("contoh: 1000000");
        amountField.setWidthFull();

        VerticalLayout form = new VerticalLayout(descriptionField, amountField);
        form.setPadding(false);

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.setWidth("450px");
        dialog.setMaxWidth("95vw");
        dialog.add(form);

        Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create(), event -> {
            try {
                Transaction created = submitter.submit(descriptionField.getValue(), amountField.getValue());
                showSuccess(successMessage.apply(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(Transaction transaction) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Menghapus Transaksi");
        dialog.setText("Yakin ingin menghapus transaksi \"" + transaction.getDescription()
                + "\" (ID " + transaction.getId() + ")?");
        dialog.setCancelable(true);
        dialog.setCancelText("Batal");
        dialog.setConfirmText("Hapus");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteTransaction(transaction));
        dialog.open();
    }

    // ---------- Aksi ----------

    private void deleteTransaction(Transaction transaction) {
        try {
            financeUseCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        }
        refreshAll();
    }

    private void searchTransactions() {
        String keyword = searchField.getValue();
        if (keyword.isBlank()) {
            refreshAll();
            return;
        }

        List<Transaction> results = financeUseCase.searchTransactions(keyword);
        sortField.clear();
        showTransactions(results, presenter.showSearchTitle(keyword), presenter.showNotFoundMessage());
    }

    private void applySort(SortOption option) {
        searchField.clear();
        grid.sort(List.of());
        showTransactions(financeUseCase.sortTransactions(option),
                presenter.showSortTitle(option), presenter.showEmptyMessage());
    }

    private void refreshAll() {
        searchField.clear();
        sortField.clear();
        grid.sort(List.of());
        showTransactions(financeUseCase.getAllTransactions(),
                presenter.showAllTitle(), presenter.showEmptyMessage());
    }

    private void showTransactions(List<Transaction> transactions, String title, String emptyMessage) {
        emptyState.setText(emptyMessage);
        grid.setItems(transactions);
        statusLabel.setText(transactions.isEmpty() ? title + " " + emptyMessage : title);
        updateSummary();
    }

    // ---------- Notifikasi ----------

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
