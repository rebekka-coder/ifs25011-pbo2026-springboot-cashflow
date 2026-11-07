package org.delcom.ifs25011pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;

/**
 * Implementasi repository transaksi di atas {@link InMemoryRepository}.
 * Data hanya disimpan di memori dan hilang saat aplikasi dihentikan.
 */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {

    /** Penghitung ID otomatis, bertambah setiap kali transaksi baru disimpan. */
    private int idCounter = 0;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction saveIncome(String description, BigDecimal amount) {
        return create(description, amount, TransactionType.INCOME);
    }

    @Override
    public Transaction saveExpense(String description, BigDecimal amount) {
        return create(description, amount, TransactionType.EXPENSE);
    }

    private Transaction create(String description, BigDecimal amount, TransactionType type) {
        return save(new Transaction(nextId(), description, amount, type));
    }

    /** Menghasilkan ID unik berikutnya. */
    private int nextId() {
        return ++idCounter;
    }
}
