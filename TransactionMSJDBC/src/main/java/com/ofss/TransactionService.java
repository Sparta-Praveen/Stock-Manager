package com.ofss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionDAO transactionDAO;

    public boolean performTransaction(Transaction t) {
        try {
            transactionDAO.saveTransaction(t);
            return true;
        } catch(Exception e) {
            // Optionally log exception
            return false;
        }
    }
}
