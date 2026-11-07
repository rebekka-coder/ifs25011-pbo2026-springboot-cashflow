package org.delcom.ifs25011pbo2026springbootcashflow.domain.entity;

/**
 * Jenis transaksi keuangan.
 * Urutan deklarasi dipakai sebagai urutan alami: pemasukan lebih dulu dari pengeluaran.
 */
public enum TransactionType {
    /** Uang masuk; menambah saldo. */
    INCOME,

    /** Uang keluar; mengurangi saldo. */
    EXPENSE
}
