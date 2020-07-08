package com.compulynx.droid.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Customer.class, Transaction.class}, version = 1)
public abstract class Database extends RoomDatabase {

    private static volatile Database instance;
    public abstract CustomerDao customerDao();
    public abstract TransactionDao transactionDao();
    public static final ExecutorService writeExecutor =
            Executors.newFixedThreadPool(2);

    public static Database getInstance(Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null)
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "wallet-db")
                            .build();
            }
        }
        return instance;
    }
}
