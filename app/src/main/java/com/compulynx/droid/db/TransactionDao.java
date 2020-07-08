package com.compulynx.droid.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction txn);

    @Query("SELECT * FROM `transaction`")
    List<Transaction> getAll();

    @Query("DELETE FROM `transaction`")
    void deleteAll();
}
