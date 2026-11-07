package org.delcom.ifs25011pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi.
 * Setiap opsi membawa comparator-nya sendiri sehingga aturan pengurutan
 * terpusat di domain, bukan tersebar sebagai angka ajaib di view atau use case.
 */
public enum SortOption {
    /** Nominal terkecil lebih dulu. */
    AMOUNT_ASC(Comparator.comparing(Transaction::getAmount)
            .thenComparing(Transaction::getId)),

    /** Nominal terbesar lebih dulu. */
    AMOUNT_DESC(Comparator.comparing(Transaction::getAmount).reversed()
            .thenComparing(Transaction::getId)),

    /** Pemasukan ditampilkan lebih dulu. */
    INCOME_FIRST(Comparator.comparing(Transaction::getType)
            .thenComparing(Transaction::getId)),

    /** Pengeluaran ditampilkan lebih dulu. */
    EXPENSE_FIRST(Comparator.comparing(Transaction::getType).reversed()
            .thenComparing(Transaction::getId));

    /** Comparator yang digunakan untuk mengurutkan daftar transaksi. */
    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    /** Mengembalikan comparator yang sesuai dengan opsi ini. */
    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
