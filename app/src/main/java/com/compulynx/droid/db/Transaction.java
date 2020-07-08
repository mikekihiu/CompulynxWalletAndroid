package com.compulynx.droid.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transaction")
public class Transaction {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "transaction_id")
    public String transactionId;
    public int amount;

    public Transaction(String transactionId, int amount) {
        this.transactionId = transactionId;
        this.amount = amount;
    }
}
