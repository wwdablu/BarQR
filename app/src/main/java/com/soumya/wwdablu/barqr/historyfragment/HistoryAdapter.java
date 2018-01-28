package com.soumya.wwdablu.barqr.historyfragment;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.soumya.wwdablu.barqr.R;
import com.soumya.wwdablu.barqr.databinding.CardGenericDetailsBinding;
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

        CardGenericDetailsBinding binder = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.card_generic_details, parent, false);

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

        scanDataInfoList.add(0, scanDataInfo);
        notifyDataSetChanged();
    }

    void clearList() {
        scanDataInfoList.clear();
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private CardGenericDetailsBinding binder;

        public HistoryViewHolder(CardGenericDetailsBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }

        public void bind(int position) {

            ScanDataInfo scanDataInfo = scanDataInfoList.get(position);

            //Show the action that can be performed on the data
            binder.tvGenericDetails.setText(ScanData.getActionInFriendlyText(
                    binder.getRoot().getContext(), scanDataInfo.scanData()));

            assignScanDataTypeIcon(scanDataInfo, binder.ivGenericIcon);

            //Mark the header accordingly to the scan type
            if(scanDataInfo.scanType().toLowerCase().contains(ScanData.SCAN_TYPE_QR)) {
                binder.vwScanType.setBackgroundColor(binder.getRoot().getContext().getResources().getColor(R.color.qrScanType));
            } else {
                binder.vwScanType.setBackgroundColor(binder.getRoot().getContext().getResources().getColor(R.color.barScanType));
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

    private void assignScanDataTypeIcon(ScanDataInfo scanDataInfo, ImageView imageView) {

        switch (scanDataInfo.scanDataType()) {
            case ScanData.TYPE_EMAIL:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_email));
                break;

            case ScanData.TYPE_GEOLOCATION:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_location));
                break;

            case ScanData.TYPE_PHONE:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_phone));
                break;

            case ScanData.TYPE_PLAIN_TEXT:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_plain_text));
                break;

            case ScanData.TYPE_SMS:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_sms));
                break;

            case ScanData.TYPE_VCARD:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_vcard));
                break;

            case ScanData.TYPE_WEB_URL:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_website));
                break;

            case ScanData.TYPE_WIFI:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_wifi));
                break;

            case ScanData.TYPE_UNKNOWN:
            default:
                imageView.setBackground(imageView.getContext().getResources().getDrawable(R.drawable.ic_unknown));
                break;

        }
    }
}
