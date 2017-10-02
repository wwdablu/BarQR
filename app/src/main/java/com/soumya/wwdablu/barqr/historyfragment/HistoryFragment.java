package com.soumya.wwdablu.barqr.historyfragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soumya.wwdablu.barqr.R;
import com.soumya.wwdablu.barqr.ScanActivity;
import com.soumya.wwdablu.barqr.database.HistoryHelper;
import com.soumya.wwdablu.barqr.databinding.FragmentHistoryBinding;

import java.util.LinkedList;

public class HistoryFragment extends Fragment {

    private static final int SCAN_REQ_CODE = 1001;

    private HistoryAdapter historyAdapter;
    private HistoryHelper historyHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        historyHelper = HistoryHelper.getInstance(getActivity().getApplicationContext());

        FragmentHistoryBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history,
                container, false);

        binding.fabScanNew.setOnClickListener(fabClickListener);

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyAdapter = new HistoryAdapter();
        binding.rvHistory.setAdapter(historyAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinkedList<HistoryPojo> list = historyHelper.getHistoryData();
        for(HistoryPojo pojo : list) {
            historyAdapter.addHistory(pojo);
        }
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            startActivityForResult(intent, SCAN_REQ_CODE);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case SCAN_REQ_CODE:

                if(Activity.RESULT_OK == resultCode) {

                    HistoryPojo historyPojo = new HistoryPojo();
                    historyPojo.scanType = data.getStringExtra("scanType");
                    historyPojo.scanData = data.getStringExtra("scanData");
                    historyHelper.addHistoryData(historyPojo);
                    historyAdapter.addHistory(historyPojo);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
