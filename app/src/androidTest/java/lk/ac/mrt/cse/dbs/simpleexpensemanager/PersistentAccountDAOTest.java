package lk.ac.mrt.cse.dbs.simpleexpensemanager;


import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
        String bankName = "Ja Ja Bank";
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
}