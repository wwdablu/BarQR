package com.soumya.wwdablu.barqr.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.soumya.wwdablu.barqr.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScanData {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SCAN_TYPE_QR,
            SCAN_TYPE_BAR
    })
    public @interface Scan {}
    public static final String SCAN_TYPE_QR = "scan.type.qr";
    public static final String SCAN_TYPE_BAR = "scan.type.bar";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
        TYPE_WEB_URL,
        TYPE_PLAIN_TEXT,
        TYPE_VCARD,
        TYPE_EMAIL,
        TYPE_SMS,
        TYPE_PHONE,
        TYPE_UNKNOWN
    })
    public @interface Type {}

    public static final String TYPE_WEB_URL = "type.web.url";
    public static final String TYPE_PLAIN_TEXT = "type.plain.text";
    public static final String TYPE_VCARD = "type.vcard";
    public static final String TYPE_EMAIL = "type.email";
    public static final String TYPE_SMS = "type.sms";
    public static final String TYPE_PHONE = "type.phone";
    public static final String TYPE_UNKNOWN = "type.unknown";

    //Data Identifiers
    private static final String VCARD_BEGIN = "BEGIN:VCARD";
    private static final String VCARD_END = "END:VCARD";
    private static final String MAIL_IDENTIFIER = "MATMSG:";
    private static final String MESSAGE_IDENTIFIER = "SMSTO:";
    private static final String PHONE_IDENTIFIER = "TEL:";

    //Internal class used to store intermediate scan data parsing information
    private class ScanResolveInfo {
        String scanData;
        @Type String scanDataType;
        String friendlyName;
    }

    /**
     * Resolves the type of the scanned data and returns accordingly. This
     * can be used to perform specific action based on the data type, for
     * example, if Web URL, then the browser can be launched.
     * @param scanPayload Data obtained after scanning
     * @return Data content information
     */
    public synchronized ScanDataInfo resolveDataContent(@NonNull Context context, @NonNull String scanPayload) {

        ScanResolveInfo scanResolveInfo = new ScanResolveInfo();

        //Resolve the information
        resolveScanDataType(context, scanPayload, scanResolveInfo);

        return ImmutableScanDataInfo.builder()
                .scanData(scanPayload)
                .scanDataType(scanResolveInfo.scanDataType)
                .scanDataTypeFriendlyName(scanResolveInfo.friendlyName)
                .build();
    }

    /*
     * Get the type of data that has been found after the scan has been done.
     */
    private void resolveScanDataType(Context context, String rawScanData, ScanResolveInfo scanResolveInfo) {

        //Guard check
        if(TextUtils.isEmpty(rawScanData)) {
            scanResolveInfo.friendlyName = context.getString(R.string.unknown);
            scanResolveInfo.scanDataType = TYPE_UNKNOWN;
        }

        //Check if the rawScanData if a Web URL
        else if(URLUtil.isValidUrl(rawScanData)) {
            scanResolveInfo.friendlyName = context.getString(R.string.website);
            scanResolveInfo.scanDataType = TYPE_WEB_URL;
        }

        //Check if the rawScanData is VCard
        else if(rawScanData.contains(VCARD_BEGIN) && rawScanData.contains(VCARD_END)) {
            scanResolveInfo.friendlyName = context.getString(R.string.vcard);
            scanResolveInfo.scanDataType = TYPE_VCARD;
        }

        //Check if the rawScanData is Email
        else if (rawScanData.length() >= 6 &&
                rawScanData.substring(0, 7).toUpperCase().contentEquals(MAIL_IDENTIFIER)) {

            scanResolveInfo.friendlyName = context.getString(R.string.email);
            scanResolveInfo.scanDataType = TYPE_EMAIL;
        }

        //Check if the rawScanData is SMS
        else if (rawScanData.length() >= 6 &&
                rawScanData.substring(0, 6).toUpperCase().contentEquals(MESSAGE_IDENTIFIER)) {

            scanResolveInfo.friendlyName = context.getString(R.string.message);
            scanResolveInfo.scanDataType = TYPE_SMS;
        }

        //Check if the rawScanData is Phone
        else if (rawScanData.length() >= 4 &&
                rawScanData.substring(0, 4).toUpperCase().contentEquals(PHONE_IDENTIFIER)) {

            scanResolveInfo.friendlyName = context.getString(R.string.phone);
            scanResolveInfo.scanDataType = TYPE_PHONE;
        }

        else {

            scanResolveInfo.friendlyName = context.getString(R.string.plain_text);
            scanResolveInfo.scanDataType = TYPE_PLAIN_TEXT;
        }
    }
}
