package com.soumya.wwdablu.barqr.historyfragment;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soumya.wwdablu.barqr.R;
import com.soumya.wwdablu.barqr.databinding.RowScanResultBinding;
import com.soumya.wwdablu.barqr.parser.ScanData;
import com.soumya.wwdablu.barqr.parser.ScanDataInfo;

import java.util.LinkedList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private LinkedList<ScanDataInfo> scanDataInfoList;

    public HistoryAdapter() {
        scanDataInfoList = new LinkedList<>();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowScanResultBinding binder = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.row_scan_result, parent, false);

        return new HistoryViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return null == scanDataInfoList ? 0 : scanDataInfoList.size();
    }

    void addHistory(ScanDataInfo scanDataInfo) {

        scanDataInfoList.add(scanDataInfo);
        notifyItemInserted(scanDataInfoList.size() - 1);
    }

    void clearList() {
        scanDataInfoList.clear();
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private RowScanResultBinding binder;

        public HistoryViewHolder(RowScanResultBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }

        public void bind(int position) {

            ScanDataInfo scanDataInfo = scanDataInfoList.get(position);

            //Show the type of data (it is inferred from the data)
            binder.tvScanType.setText(scanDataInfo.scanDataTypeFriendlyName());

            //Show the action that can be performed on the data
            binder.tvScanData.setText(ScanData.getActionInFriendlyText(
                    binder.getRoot().getContext(), scanDataInfo.scanData()));

            //Show the data source accordingly
            if(scanDataInfo.scanType().toLowerCase().contains(ScanData.SCAN_TYPE_QR)) {
                binder.ivScanType.setImageResource(R.drawable.qrcode_scan);
            }

            this.binder.getRoot().setOnClickListener(clickListener);
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ScanData.performActionOn(binder.getRoot().getContext(),
                        scanDataInfoList.get(getAdapterPosition()).scanData());
            }
        };
    }
}
