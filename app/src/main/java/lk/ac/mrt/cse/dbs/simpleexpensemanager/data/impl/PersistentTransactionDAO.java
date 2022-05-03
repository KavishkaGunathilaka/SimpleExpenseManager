package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public static final String AMOUNT = "amount";
    public static final String TRANSACTION_DATE = "date";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String EXPENSE_TYPE_ID = "expenseTypeId";
    public static final String TRANSACTION_DETAILS_TABLE = "TransactionDetails";

    public PersistentTransactionDAO(@Nullable Context context) {
        super(context, "190205R.db", null, 1);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TRANSACTION_DATE, transaction.getDate().toString());
        cv.put(ACCOUNT_NO, transaction.getAccountNo());
        cv.put(EXPENSE_TYPE_ID, transaction.getExpenseType().ordinal()+1);
        cv.put(AMOUNT, transaction.getAmount());

        db.insert(TRANSACTION_DETAILS_TABLE, null, cv);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionsList = new ArrayList<>();

        String query = "SELECT * FROM " + TRANSACTION_DETAILS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do{
                String accountNo = cursor.getString(1);
                ExpenseType expenseType = ExpenseType.values()[cursor.getInt(2) - 1];
                double amount = cursor.getDouble(3);
                Date date = new Date(cursor.getString(4));

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactionsList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return transactionsList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionList = getAllTransactionLogs();

        int size = transactionList.size();
        if (size <= limit) {
            return transactionList;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactionList.subList(size - limit, size);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
