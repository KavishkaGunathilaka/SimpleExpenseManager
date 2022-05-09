package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.suitebuilder.annotation.LargeTest;

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

@RunWith(JUnit4.class)
@LargeTest
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
    public void testAddAccount() {
        String accNo = "56789Z";
        String bankName = "Ja Ja Bank";
        String name = "Luke Skywalker";
        double balance = 99999.0;
        Account dummyAcct = new Account(accNo, bankName, name, balance);

        persistentAccountDAO.addAccount(dummyAcct);

        String query = "SELECT * FROM " + PersistentAccountDAO.ACCOUNT_TABLE + " WHERE " + PersistentAccountDAO.ACCOUNT_NO + " = \"" + accNo + "\"";

        SQLiteDatabase db = persistentAccountDAO.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        assertTrue(cursor.moveToFirst());

        assertEquals( dummyAcct.getAccountNo(), cursor.getString(0));
        assertEquals(dummyAcct.getBankName(), cursor.getString(1));
        assertEquals(dummyAcct.getAccountHolderName(), cursor.getString(2));
        assertEquals(dummyAcct.getBalance(), cursor.getDouble(3));

        cursor.close();
        db.close();

    }

    @Test
    public void testGetAccount() throws InvalidAccountException {
        String accNo = "56789Z";
        String bankName = "Ja Ja Bank";
        String name = "Luke Skywalker";
        double balance = 99999.0;

        Account acc = persistentAccountDAO.getAccount(accNo);

        assertNotNull(acc);

        assertEquals(accNo, acc.getAccountNo());
        assertEquals(bankName, acc.getBankName());
        assertEquals(name, acc.getAccountHolderName());
        assertEquals(balance, acc.getBalance());
    }

    @Test
    public void testGetAccountsList() {
        List<Account> accountList = persistentAccountDAO.getAccountsList();

        assertEquals(1, accountList.size());
    }

    @Test
    public void testGetAccountNumbersList() {
        List<String> accountNumbersList = persistentAccountDAO.getAccountNumbersList();
        String accNo = "56789Z";

        assertEquals(1, accountNumbersList.size());
        assertEquals(accNo, accountNumbersList.get(0));
    }
}