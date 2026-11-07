package org.delcom.ifs25011pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain, sehingga bebas dari urusan tampilan maupun penyimpanan.
 */
public class Transaction {

    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Keterangan atau deskripsi transaksi. */
    private String description;

    /** Nominal transaksi; selalu bernilai positif. */
    private BigDecimal amount;

    /** Jenis transaksi (pemasukan / pengeluaran), tidak berubah setelah dibuat. */
    private final TransactionType type;

    /** Waktu transaksi dicatat. */
    private final LocalDateTime createdAt;

    /** Membuat transaksi baru yang dicatat pada waktu sekarang. */
    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    /** Membuat transaksi dengan waktu pencatatan yang sudah ditentukan. */
    public Transaction(int id, String description, BigDecimal amount, TransactionType type,
                       LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Menyatakan apakah transaksi ini adalah pemasukan. */
    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    /** Pengaruh transaksi terhadap saldo: positif untuk pemasukan, negatif untuk pengeluaran. */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }

    /** Mengubah keterangan transaksi. */
    public void changeDescription(String description) {
        this.description = description;
    }

    /** Mengubah nominal transaksi. */
    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
