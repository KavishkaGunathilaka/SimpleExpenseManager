package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class PersistentExpenseManager extends ExpenseManager{

    public PersistentExpenseManager() {
        setup();
    }

    @Override
    public void setup() {
        PersistentTransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(MainActivity.getContext());
        setTransactionsDAO(persistentTransactionDAO);

        PersistentAccountDAO persistentAccountDAO = new PersistentAccountDAO(MainActivity.getContext());
        setAccountsDAO(persistentAccountDAO);
        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        persistentAccountDAO.addAccount(dummyAcct1);
        persistentAccountDAO.addAccount(dummyAcct2);
    }
}
