package lk.ac.mrt.cse.dbs.simpleexpensemanager;


import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
@SmallTest
public class PersistentAccountDAOTest {

    private PersistentAccountDAO persistentAccountDAO;

    @Before
    public void setUp(){
        persistentAccountDAO = new PersistentAccountDAO(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void finish() {
        persistentAccountDAO.close();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(persistentAccountDAO);
    }

    @Test
    public void testAddAccount() throws InvalidAccountException {
        String accNo = "56789Z";
        String bankName = "Jar Jar Bank";
        String name = "Luke Skywalker";
        double balance = 99999.0;
        Account dummyAcct = new Account(accNo, bankName, name, balance);

        persistentAccountDAO.addAccount(dummyAcct);

        Account acc = persistentAccountDAO.getAccount(accNo);

        assertNotNull(acc);

        assertEquals(accNo, acc.getAccountNo());
        assertEquals(bankName, acc.getBankName());
        assertEquals(name, acc.getAccountHolderName());
        assertEquals(balance, acc.getBalance());

    }


    @Test
    public void testGetAccountsList() {
        Account dummyAcct1 = new Account("56789Z", "Jar Jar Bank", "Luke Skywalker", 99999.0);
        Account dummyAcct2 = new Account("76590V", "Vader Bank", "Yoda", 7467.0);

        persistentAccountDAO.addAccount(dummyAcct1);
        persistentAccountDAO.addAccount(dummyAcct2);

        List<Account> accountList = persistentAccountDAO.getAccountsList();

        assertFalse(accountList.isEmpty());
        assertEquals(2, accountList.size());

        assertEquals(dummyAcct1.getAccountNo(), accountList.get(0).getAccountNo());
        assertEquals(dummyAcct2.getAccountNo(), accountList.get(1).getAccountNo());

    }

    @Test
    public void testGetAccountNumbersList() {

        Account dummyAcct1 = new Account("56789Z", "Jar Jar Bank", "Luke Skywalker", 99999.0);
        Account dummyAcct2 = new Account("76590V", "Vader Bank", "Yoda", 7467.0);

        persistentAccountDAO.addAccount(dummyAcct1);
        persistentAccountDAO.addAccount(dummyAcct2);

        List<String> accountNumbersList = persistentAccountDAO.getAccountNumbersList();

        assertFalse(accountNumbersList.isEmpty());
        assertEquals(2, accountNumbersList.size());

        assertEquals(dummyAcct1.getAccountNo(), accountNumbersList.get(0));
        assertEquals(dummyAcct2.getAccountNo(), accountNumbersList.get(1));

    }
}