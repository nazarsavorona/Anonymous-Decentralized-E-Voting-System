package ua.knu.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ua.knu.backend.entities.Transaction;

@Component
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Transaction findByTransactionID(String transactionID);
}
