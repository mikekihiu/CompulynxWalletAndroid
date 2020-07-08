package com.compulynx.droid.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Customer customer);

    @Query("SELECT * FROM customer LIMIT 1")
    Customer getFirst();

    @Query("DELETE FROM customer")
    void deleteAll();
}
