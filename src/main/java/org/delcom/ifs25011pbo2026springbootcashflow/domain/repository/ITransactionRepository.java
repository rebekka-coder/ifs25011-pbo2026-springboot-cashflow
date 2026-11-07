package org.delcom.ifs25011pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;

/**
 * Port spesifik transaksi yang memperluas {@link IRepository}.
 * Menambahkan pembuatan transaksi baru dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {

    /**
     * Menyimpan transaksi pemasukan baru.
     *
     * @param description keterangan transaksi
     * @param amount      nominal transaksi
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction saveIncome(String description, BigDecimal amount);

    /**
     * Menyimpan transaksi pengeluaran baru.
     *
     * @param description keterangan transaksi
     * @param amount      nominal transaksi
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction saveExpense(String description, BigDecimal amount);
}
