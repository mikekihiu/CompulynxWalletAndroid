package com.compulynx.droid.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Transaction;
import com.compulynx.droid.network.HttpApi;
import com.compulynx.droid.view.adapter.TransactionAdapter;
import com.compulynx.droid.view.util.ViewUtil;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StatementActivity extends AppCompatActivity {

    public static final String CUSTOMER_ID = "customerID";

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.tv_bottom_right) TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        fetchTransactions();
    }

    void fetchTransactions() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject body = new JsonObject();
        body.addProperty("customerId", getIntent().getStringExtra(CUSTOMER_ID));
        HttpApi.getInstance().fetchLast100Txns(body)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onErrorReturn(err -> {
                    Log.e(getClass().getSimpleName(), err.toString());
                    return null;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(resp -> {
                    progressBar.setVisibility(View.GONE);
                    //Log.w("resp", resp.toString());
                    if (resp != null) {
                        //Type type = new TypeToken<ArrayList<Transaction>>(){}.getType();
                        double sum = 0;
                        for (Transaction transaction : resp)
                            sum += transaction.amount;
                        recyclerView.setAdapter(new TransactionAdapter(resp));
                        tvTotal.setText(String.valueOf(sum));
                    } else
                        ViewUtil.showToast(this, getString(R.string.sth_went_wrong), false);
                }).subscribe();
    }
}
