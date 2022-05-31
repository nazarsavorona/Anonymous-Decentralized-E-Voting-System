package ua.knu.backend.validator;

import org.springframework.stereotype.Component;
import ua.knu.backend.entities.Transaction;

@Component
public class TransactionPuller implements Runnable {
    private Validator validator;
    private TransactionProvider provider;

    public TransactionPuller(Validator validator, TransactionProvider provider) {
        this.validator = validator;
        this.provider = provider;
    }

    @Override
    public void run() {
        Transaction transaction = null;
        boolean isNeeded = true;

        while (true) {
            if (isNeeded) {
                transaction = this.provider.getTransaction();
            }

            if (transaction != null) {
                isNeeded = this.validator.setCurrentTransaction(transaction);
            }
        }
    }
}
