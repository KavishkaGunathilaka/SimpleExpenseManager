package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    public static final String EXPENSE_TYPE_TABLE = "ExpenseType";
    public static final String ACCOUNT_TABLE = "Account";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String BANK_NAME = "bankName";
    public static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final String BALANCE = "balance";
    public static final String AMOUNT = "amount";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_DATE = "date";
    public static final String EXPENSE_TYPE_ID = "expenseTypeId";
    public static final String TRANSACTION_DETAILS_TABLE = "TransactionDetails";

    public PersistentAccountDAO(@Nullable Context context) {
        super(context, "190205R.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createExpenseType = "CREATE TABLE " + EXPENSE_TYPE_TABLE + " (" + EXPENSE_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EXPENSE_TYPE + " VARCHAR(10))";
        db.execSQL(createExpenseType);

        String createAccount = "CREATE TABLE " + ACCOUNT_TABLE + " (" + ACCOUNT_NO + " VARCHAR(10) PRIMARY KEY NOT NULL,  " + BANK_NAME + " VARCHAR(30) NOT NULL, " + ACCOUNT_HOLDER_NAME + " VARCHAR(30) NOT NULL, " + BALANCE + " REAL NOT NULL)";
        db.execSQL(createAccount);

        String createTransactionDetails = "CREATE TABLE " + TRANSACTION_DETAILS_TABLE + " (" + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCOUNT_NO + " VARCHAR(10) NOT NULL, " + EXPENSE_TYPE_ID + " INTEGER NOT NULL, " + AMOUNT + " REAL NOT NULL, " + TRANSACTION_DATE + " VARCHAR(30) NOT NULL, foreign key (" + EXPENSE_TYPE_ID + ") references " + EXPENSE_TYPE_TABLE + " (" + EXPENSE_TYPE_ID + ") on delete set null on up" + TRANSACTION_DATE + " cascade, foreign key (" + ACCOUNT_NO + ") references " + ACCOUNT_TABLE + "(" + ACCOUNT_NO + ") on delete set null on up" + TRANSACTION_DATE + " cascade)";
        db.execSQL(createTransactionDetails);

        String addExpenseTypes = "INSERT INTO " + EXPENSE_TYPE_TABLE  + " (" + EXPENSE_TYPE + ") VALUES ('EXPENSE')";
        db.execSQL(addExpenseTypes);

        addExpenseTypes = "INSERT INTO " + EXPENSE_TYPE_TABLE  + " (" + EXPENSE_TYPE + ") VALUES ('INCOME')";
        db.execSQL(addExpenseTypes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TYPE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_DETAILS_TABLE);

        // create new tables
        onCreate(db);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbersList = new ArrayList<>();

        List<Account> accountList = getAccountsList();

        for (Account account: accountList) {
            accountNumbersList.add(account.getAccountNo());
        }

        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountsList = new ArrayList<>();

        String query = "SELECT * FROM " + ACCOUNT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do{
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);

                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accountsList.add(account);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String query = "SELECT * FROM " + ACCOUNT_TABLE + " WHERE " + ACCOUNT_NO + " = \"" + accountNo + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Account account;

        if (cursor.moveToFirst()){
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);

            account = new Account(accountNo, bankName, accountHolderName, balance);
            cursor.close();
            db.close();

            return account;
        } else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }


    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ACCOUNT_NO, account.getAccountNo());
        cv.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        cv.put(BANK_NAME, account.getBankName());
        cv.put(BALANCE, account.getBalance());

        db.insertWithOnConflict(ACCOUNT_TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) {

        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(ACCOUNT_TABLE, ACCOUNT_NO + " = \"" + accountNo + "\"", null);

        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Account account = getAccount(accountNo);

        SQLiteDatabase db = this.getReadableDatabase();

        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }

        ContentValues cv = new ContentValues();
        cv.put(BALANCE, account.getBalance());

        db.update(ACCOUNT_TABLE, cv, ACCOUNT_NO +  " = \"" + accountNo + "\"",null);

        db.close();
    }
}
