package com.soumya.wwdablu.barqr;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.soumya.wwdablu.barqr.historyfragment.HistoryFragment;

public class HistoryActivity extends AppCompatActivity {

    private static final String KEY_HISTORY_FRAGMENT = "historyFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager()
                .findFragmentByTag(KEY_HISTORY_FRAGMENT);

        if(null == historyFragment) {

            historyFragment = new HistoryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fl_history, historyFragment, KEY_HISTORY_FRAGMENT);
            transaction.commit();
        }
    }
}
