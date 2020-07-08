package com.compulynx.droid.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Database;
import com.compulynx.droid.view.adapter.TransactionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LastTxnActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_txn);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        fetchTransactions();
    }

    void fetchTransactions() {
        Database.writeExecutor.execute(() ->
            recyclerView.setAdapter(new TransactionAdapter(Database.getInstance(this).transactionDao().getAll())));
    }
}
