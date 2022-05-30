package ua.knu.backend.validator;

import lombok.extern.slf4j.Slf4j;
import ua.knu.backend.entities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TransactionProvider {
    private List<Transaction> transactions;

    private final Object lock = new Object();

    public TransactionProvider(Validator validator) {
        transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        synchronized (lock) {
            transactions.add(transaction);
        }
    }

    public Transaction getTransaction() {
        synchronized (lock) {
            if (!transactions.isEmpty()) {
                return transactions.remove(0);
            }
            return null;
        }
    }
}
