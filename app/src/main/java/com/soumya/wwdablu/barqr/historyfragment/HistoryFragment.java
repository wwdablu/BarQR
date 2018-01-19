package com.soumya.wwdablu.barqr.historyfragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private FragmentHistoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        historyHelper = HistoryHelper.getInstance(getActivity().getApplicationContext());

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history,
                container, false);

        binding.fabScanNew.setOnClickListener(fabClickListener);

        binding.rvHistory.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        historyAdapter = new HistoryAdapter();
        binding.rvHistory.setAdapter(historyAdapter);

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinkedList<HistoryPojo> list = historyHelper.getHistoryData();
        handleGetStartedInfo(list.size());

        for(HistoryPojo pojo : list) {
            historyAdapter.addHistory(pojo);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case SCAN_REQ_CODE:

                if(Activity.RESULT_OK == resultCode) {

                    HistoryPojo historyPojo = ImmutableHistoryPojo.builder()
                            .rawScanData(data.getStringExtra("rawScanData"))
                            .rawScanType(data.getStringExtra("rawScanType"))
                            .build();

                    historyHelper.addHistoryData(historyPojo);
                    historyAdapter.addHistory(historyPojo);
                    handleGetStartedInfo(1);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.hist_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_clear:
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.clear_history)
                    .setMessage(R.string.clear_history_msg)
                    .setPositiveButton(R.string.action_continue, (dialogInterface, i) -> {
                        HistoryHelper.getInstance(getActivity().getApplicationContext()).clearAllHistory();
                        historyAdapter.clearList();
                        historyAdapter.notifyDataSetChanged();
                        handleGetStartedInfo(0);
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .create()
                    .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener fabClickListener = view -> {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(intent, SCAN_REQ_CODE);
    };

    private void handleGetStartedInfo(int dataCount) {

        binding.tvNoScanHistory.setVisibility(0 == dataCount ? View.VISIBLE : View.GONE);
    }
}
