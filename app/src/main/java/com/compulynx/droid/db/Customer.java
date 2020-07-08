package com.compulynx.droid.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customer")
public class Customer  {

    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "first_name")
    public String firstName;
    @ColumnInfo(name = "last_name")
    public String lastName;
    public String email;
    @ColumnInfo(name = "customer_id")
    public String customerId;
    @ColumnInfo(name = "account_no")
    public String accountNo;
}
