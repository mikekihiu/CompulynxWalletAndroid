package com.compulynx.droid.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Customer;
import com.compulynx.droid.db.Database;
import com.compulynx.droid.network.HttpApi;
import com.compulynx.droid.view.util.ViewUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_customer_id) TextInputEditText etCustomerId;
    @BindView(R.id.et_pin) TextInputEditText etPin;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.progress) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    void tappedLogin() {
        if (ViewUtil.isBlank(etCustomerId)) {
            ViewUtil.showToast(this, "Please enter your customer ID", true);
            return;
        }
        if (ViewUtil.isBlank(etPin)) {
            ViewUtil.showToast(this, "Please enter your PIN", true);
            return;
        }
        if (HttpApi.BASE_URL.isEmpty()) {
            ViewUtil.showSnackbar(btnLogin, "Please configure API BASE_URL before running the app", true);
            return;
        }
        toggleProgress();
        JsonObject body = new JsonObject();
        body.addProperty("customerId", etCustomerId.getText().toString());
        body.addProperty("pin", etPin.getText().toString());
        HttpApi.getInstance().post(HttpApi.ENDPOINT_LOGIN, body)
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
                        Customer customer = new Gson().fromJson(resp.toString(), Customer.class);
                        Database.writeExecutor.execute(() -> Database.getInstance(this).customerDao().insert(customer));
                        finish();
                        startActivity(new Intent(this, HomeActivity.class));
                    } else
                        ViewUtil.showToast(this, getString(R.string.login_failed), false);
                }).subscribe();
    }

    void toggleProgress() {
        ViewUtil.toggleProgress(progressBar, etCustomerId, etPin, btnLogin);
    }
}
