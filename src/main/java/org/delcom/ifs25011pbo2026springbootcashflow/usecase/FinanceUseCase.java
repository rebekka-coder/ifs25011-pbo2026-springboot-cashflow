package org.delcom.ifs25011pbo2026springbootcashflow.usecase;

import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Use case yang menangani logika bisnis pencatatan keuangan.
 * Tidak melakukan I/O; hanya memproses data dan melempar checked exception
 * domain jika input tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {

    private static final int AMOUNT_SCALE = 2;

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambahkan pemasukan baru setelah validasi. */
    public Transaction addIncome(String description, String amount) throws ValidationException {
        String validDescription = validateDescription(description);
        BigDecimal validAmount = parseAmount(amount);
        return transactionRepository.saveIncome(validDescription, validAmount);
    }

    /** Menambahkan pengeluaran baru setelah validasi. */
    public Transaction addExpense(String description, String amount) throws ValidationException {
        String validDescription = validateDescription(description);
        BigDecimal validAmount = parseAmount(amount);
        return transactionRepository.saveExpense(validDescription, validAmount);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException {
        if (!transactionRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return transactionRepository.findAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan transaksi sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(SortOption option) {
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghitung total seluruh pemasukan. */
    public BigDecimal getTotalIncome() {
        return transactionRepository.findAll().stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Menghitung total seluruh pengeluaran. */
    public BigDecimal getTotalExpense() {
        return transactionRepository.findAll().stream()
                .filter(transaction -> !transaction.isIncome())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Menghitung saldo: total pemasukan dikurangi total pengeluaran. */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return description.trim();
    }

    private BigDecimal parseAmount(String amount) throws ValidationException {
        if (amount == null || amount.isBlank()) {
            throw new ValidationException("Jumlah harus berupa angka");
        }

        BigDecimal parsed;
        try {
            parsed = new BigDecimal(amount.trim()).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Jumlah harus berupa angka");
        }

        if (parsed.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
        return parsed;
    }
}
