package com.compulynx.droid.view.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Customer;
import com.compulynx.droid.db.Database;
import com.compulynx.droid.db.Transaction;
import com.compulynx.droid.network.HttpApi;
import com.compulynx.droid.view.util.ViewUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SendMoneyActivity extends AppCompatActivity {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.et_account_to) TextInputEditText etAccountTo;
    @BindView(R.id.et_amount) TextInputEditText etAmount;
    @BindView(R.id.btn_send) Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_send)
    void tappedSend() {
        if (ViewUtil.isBlank(etAccountTo)) {
            ViewUtil.showToast(this, "Please enter recipient's account", true);
            return;
        }
        if (ViewUtil.isBlank(etAmount)) {
            ViewUtil.showToast(this, "Please enter an amount", true);
            return;
        }

        Transaction transaction = new Transaction("TXN".concat(String.valueOf(System.currentTimeMillis())),
                Integer.parseInt(etAmount.getText().toString()));
        Observable.just(transaction)
                .subscribeOn(Schedulers.io())
                .map(transaction1 -> {
                    Database db = Database.getInstance(this);
                    db.transactionDao().insert(transaction1);
                    Customer customer = db.customerDao().getFirst();
                    JsonObject body = new JsonObject();
                    body.addProperty("customerId", customer.customerId);
                    body.addProperty("accountFrom", customer.accountNo);
                    body.addProperty("amount", transaction1.amount);
                    return body;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::callSendMoneyApi)
                .subscribe();
    }

    void toggleProgress() {
        ViewUtil.toggleProgress(progressBar, etAccountTo, etAmount, btnSend);
    }

    void callSendMoneyApi(JsonObject body) {
        toggleProgress();
        body.addProperty("accountTo", etAccountTo.getText().toString());
        HttpApi.getInstance().post(HttpApi.ENDPOINT_SEND_MONEY, body)
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
                    if (resp.size() > 0)
                        new AlertDialog.Builder(this)
                                .setTitle("Transaction successful")
                                .setMessage("Your transaction was completed successfully")
                                .setNeutralButton("OK", ((dialogInterface, i) -> finish()))
                                .show();
                    else {
                        ViewUtil.showToast(this, getString(R.string.txn_failed), false);
                        finish();
                    }
                }).subscribe();
    }

}
