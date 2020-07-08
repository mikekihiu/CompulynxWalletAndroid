package com.compulynx.droid.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compulynx.droid.R;
import com.compulynx.droid.db.Transaction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> dataSet;

    public TransactionAdapter(List<Transaction> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_txn, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setUp(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_left) TextView tvLeft;
        @BindView(R.id.tv_right) TextView tvRight;
        @BindView(R.id.top_divider) View topDivider;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setUp(Transaction transaction) {
            if (getAdapterPosition() == 0)
                topDivider.setVisibility(View.VISIBLE);
            tvLeft.setText(transaction.transactionId);
            tvRight.setText(String.valueOf(transaction.amount));
        }
    }
}
