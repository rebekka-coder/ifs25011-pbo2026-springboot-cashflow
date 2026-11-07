package org.delcom.ifs25011pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Presenter yang memformat data dari use case menjadi teks siap tampil.
 * Memisahkan logika tampilan dari entity, use case, dan view.
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Memformat uang dengan pemisah ribuan koma dan dua desimal, contoh: Rp 1,000,000.00. */
    public String formatCurrency(BigDecimal amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    /** Label jenis transaksi untuk ditampilkan. */
    public String formatType(TransactionType type) {
        return switch (type) {
            case INCOME -> "Pemasukan";
            case EXPENSE -> "Pengeluaran";
        };
    }

    /** Memformat waktu pencatatan transaksi. */
    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Memformat satu transaksi menjadi baris teks. */
    public String format(Transaction transaction) {
        return String.format("[%d] %s | %s | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                formatCurrency(transaction.getAmount()),
                formatType(transaction.getType()));
    }

    /** Memformat saldo saat ini. */
    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatCurrency(balance);
    }

    /** Label opsi pengurutan untuk ditampilkan. */
    public String formatSortOption(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    /** Judul daftar seluruh transaksi. */
    public String showAllTitle() {
        return "Daftar Transaksi";
    }

    /** Judul hasil pencarian berdasarkan kata kunci. */
    public String showSearchTitle(String keyword) {
        return "Hasil Pencarian: \"" + keyword + "\"";
    }

    /** Judul daftar yang sudah diurutkan. */
    public String showSortTitle(SortOption option) {
        return "Daftar Transaksi (Urut: " + formatSortOption(option) + ")";
    }

    /** Pesan saat belum ada transaksi. */
    public String showEmptyMessage() {
        return "- Data transaksi belum tersedia!";
    }

    /** Pesan saat pencarian tidak menemukan hasil. */
    public String showNotFoundMessage() {
        return "- Transaksi tidak ditemukan!";
    }

    /** Pesan sukses setelah menambah pemasukan. */
    public String showAddIncomeSuccess(Transaction transaction) {
        return "Berhasil menambah pemasukan: " + format(transaction);
    }

    /** Pesan sukses setelah menambah pengeluaran. */
    public String showAddExpenseSuccess(Transaction transaction) {
        return "Berhasil menambah pengeluaran: " + format(transaction);
    }

    /** Pesan sukses setelah menghapus transaksi. */
    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    /** Pesan gagal saat menghapus transaksi. */
    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    /** Pesan validasi dari domain. */
    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
