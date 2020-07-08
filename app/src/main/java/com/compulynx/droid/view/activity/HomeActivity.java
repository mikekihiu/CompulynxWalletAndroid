package com.compulynx.droid.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Database;
import com.compulynx.droid.network.HttpApi;
import com.compulynx.droid.view.util.ViewUtil;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.btn_balance) Button btnBalance;
    @BindView(R.id.btn_send_money) Button btnSendMoney;
    @BindView(R.id.btn_view_statement) Button btnViewStatement;
    @BindView(R.id.btn_last_txn) Button btnLastTxn;
    @BindView(R.id.btn_profile) Button btnProfile;
    @BindView(R.id.btn_logout) Button btnLogout;
    @BindView(R.id.tv_welcome) TextView tvWelcome;

    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .map(context -> Database.getInstance(this).customerDao().getFirst())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(customer -> {
                    if (customer != null) {
                        tvWelcome.setText(String.format(getString(R.string.welcome_template),
                                customer.firstName.toUpperCase()));
                        customerId = customer.customerId;
                    }
                }).subscribe();
    }

    @OnClick(R.id.btn_balance)
    public void tappedBalance() {
        toggleProgress();
        JsonObject body = new JsonObject();
        body.addProperty("customerId", customerId);
        HttpApi.getInstance().post(HttpApi.ENDPOINT_ACCOUNT_BALANCE, body)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onErrorReturn(err -> {
                    Log.e(getClass().getSimpleName(), err.toString());
                    return new JsonObject();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(resp -> {
                    toggleProgress();
                    //Log.w("resp", resp.toString());
                    if (resp.size() > 0) {
                        String balance = resp.get("balance").getAsString();
                        new AlertDialog.Builder(this)
                                .setTitle("Account balance")
                                .setMessage("Your account balance is "+balance)
                                .setNeutralButton("OK", (dialogInterface, i) -> {})
                                .show();
                    } else
                        ViewUtil.showToast(this, getString(R.string.sth_went_wrong), false);
                }).subscribe();
    }

    @OnClick(R.id.btn_send_money)
    void tappedSendMoney() {
        startActivity(new Intent(this, SendMoneyActivity.class));
    }

    @OnClick(R.id.btn_view_statement)
    void tappedViewStatement() {
        startActivity(new Intent(this, StatementActivity.class)
        .putExtra(StatementActivity.CUSTOMER_ID, customerId));
    }

    @OnClick(R.id.btn_last_txn)
    void tappedLastTransactions() {
        startActivity(new Intent(this, LastTxnActivity.class));
    }

    @OnClick(R.id.btn_profile)
    void tappedProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @OnClick(R.id.btn_logout)
    void tappedLogout() {
        Database.writeExecutor.execute(() -> {
            Database.getInstance(this).customerDao().deleteAll();
            Database.getInstance(this).transactionDao().deleteAll();
        });
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    void toggleProgress() {
        ViewUtil.toggleProgress(progressBar, btnBalance, btnSendMoney, btnViewStatement, btnLastTxn, btnProfile, btnLogout);
    }
}
