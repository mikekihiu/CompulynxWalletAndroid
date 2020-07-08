package com.compulynx.droid.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Database;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.et_name) TextInputEditText etName;
    @BindView(R.id.et_id) TextInputEditText etId;
    @BindView(R.id.et_account) TextInputEditText etAccount;
    @BindView(R.id.et_email) TextInputEditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .map(context -> Database.getInstance(this).customerDao().getFirst())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(customer -> {
                    if (customer != null) {
                      etName.setText(customer.firstName.concat(" "+customer.lastName));
                      etId.setText(customer.customerId);
                      etAccount.setText(customer.accountNo);
                      etEmail.setText(customer.email);
                    }
                })
                .subscribe();
    }

    @OnClick(R.id.btn_home)
    void tappedHome() {
        finish();
    }
}
