package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(JUnit4.class)
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
public class PersistentTransactionDAOTest {

        private PersistentTransactionDAO persistentTransactionDAO;

        @Before
        public void setUp(){
            persistentTransactionDAO = new PersistentTransactionDAO(InstrumentationRegistry.getTargetContext());
        }

        @After
        public void finish() {
            persistentTransactionDAO.close();
        }

        @Test
        public void test1_PreConditions() {
            assertNotNull(persistentTransactionDAO);
        }

        @Test
        public void test2_LogTransaction() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2022, 5, 11);
            Date date = calendar.getTime();
            String accNo = "56789Z";

            PersistentAccountDAO persistentAccountDAO = new PersistentAccountDAO(InstrumentationRegistry.getTargetContext());
            Account dummyAcct = new Account(accNo, "Jar Jar Bank", "Luke Skywalker", 99999.0);
            persistentAccountDAO.addAccount(dummyAcct);

            persistentTransactionDAO.logTransaction(date, accNo, ExpenseType.INCOME, 1000);
            persistentTransactionDAO.logTransaction(date, accNo, ExpenseType.EXPENSE, 500);

            List<Transaction> transactionList = persistentTransactionDAO.getAllTransactionLogs();

            for (Transaction t:
                    transactionList) {
                Log.v("Kav", t.getAccountNo() + " " + t.getAmount());
            }

            assertEquals(2, transactionList.size());

            assertEquals(accNo, transactionList.get(0).getAccountNo());
            assertEquals(ExpenseType.INCOME, transactionList.get(0).getExpenseType());
            assertEquals(1000.0, transactionList.get(0).getAmount());

            assertEquals(accNo, transactionList.get(1).getAccountNo());
            assertEquals(ExpenseType.EXPENSE, transactionList.get(1).getExpenseType());
            assertEquals(500.0, transactionList.get(1).getAmount());

        }


        @Test
        public void test3_GetPaginatedTransactionLogs() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2022, 5, 11);
            Date date = calendar.getTime();
            String accNo = "56789Z";

            PersistentAccountDAO persistentAccountDAO = new PersistentAccountDAO(InstrumentationRegistry.getTargetContext());
            Account dummyAcct = new Account(accNo, "Jar Jar Bank", "Luke Skywalker", 99999.0);
            persistentAccountDAO.addAccount(dummyAcct);

            persistentTransactionDAO.logTransaction(date, accNo, ExpenseType.INCOME, 5000);
            persistentTransactionDAO.logTransaction(date, accNo, ExpenseType.INCOME, 1000);
            persistentTransactionDAO.logTransaction(date, accNo, ExpenseType.EXPENSE, 500);
            persistentTransactionDAO.logTransaction(date, accNo, ExpenseType.EXPENSE, 3000);

            List<Transaction> transactionList = persistentTransactionDAO.getPaginatedTransactionLogs(1);
            assertEquals(1, transactionList.size());

            transactionList = persistentTransactionDAO.getPaginatedTransactionLogs(2);
            assertEquals(2, transactionList.size());

            transactionList = persistentTransactionDAO.getPaginatedTransactionLogs(3);
            assertEquals(3, transactionList.size());

            transactionList = persistentTransactionDAO.getPaginatedTransactionLogs(4);
            assertEquals(4, transactionList.size());

        }

}
