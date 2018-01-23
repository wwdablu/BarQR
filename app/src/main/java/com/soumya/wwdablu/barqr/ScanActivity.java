package com.soumya.wwdablu.barqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {

    public static final String KEY_SCAN_TYPE = "rawScanType";
    public static final String KEY_SCAN_DATA = "rawScanData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Intent resultIntent = new Intent();

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {

                resultIntent.putExtra(KEY_SCAN_TYPE, "");
                resultIntent.putExtra(KEY_SCAN_DATA, "");
                resultCode = RESULT_CANCELED;

            } else {

                resultIntent.putExtra(KEY_SCAN_TYPE, result.getFormatName());
                resultIntent.putExtra(KEY_SCAN_DATA, result.getContents());
                resultCode = RESULT_OK;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        setResult(resultCode, resultIntent);
        finish();
    }
}
