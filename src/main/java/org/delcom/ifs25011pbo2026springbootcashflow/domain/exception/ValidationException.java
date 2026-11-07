package org.delcom.ifs25011pbo2026springbootcashflow.domain.exception;

/**
 * Exception yang dilempar saat validasi aturan bisnis atau input gagal.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}
