package com.soumya.wwdablu.barqr.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.soumya.wwdablu.barqr.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DataHandler {

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

    public @interface ScanType {}
    public static final String TYPE_WEB_URL = "type.web.url";
    public static final String TYPE_PLAIN_TEXT = "type.plain.text";
    public static final String TYPE_VCARD = "type.vcard";
    public static final String TYPE_EMAIL = "type.email";
    public static final String TYPE_SMS = "type.sms";
    public static final String TYPE_PHONE = "type.phone";
    public static final String TYPE_UNKNOWN = "type.unknown";

    //VCard Identifier
    private static final String VCARD_BEGIN = "BEGIN:VCARD";
    private static final String VCARD_END = "END:VCARD";

    //Mail Identifier
    private static final String MAIL_TAG = "MATMSG:";

    //SMS Identifier
    private static final String SMS_TAG = "SMSTO:";

    private static final String PHONE_TAG = "TEL:";

    /**
     * Get the type of data that is being stored
     * @param rawScanData Data obtained after scanning
     * @return
     */
    public static @ScanType String getScanTypeFrom(String rawScanData) {

        //Guard check
        if(TextUtils.isEmpty(rawScanData)) {
            return TYPE_UNKNOWN;
        }

        //Check if the rawScanData if a Web URL
        if(URLUtil.isValidUrl(rawScanData)) {
            return TYPE_WEB_URL;
        }

        //Check if the rawScanData is VCard
        else if(rawScanData.contains(VCARD_BEGIN) && rawScanData.contains(VCARD_END)) {
            return TYPE_VCARD;
        }

        //Check if the rawScanData is Email
        else if (rawScanData.length() >= 6 &&
                rawScanData.substring(0, 7).toUpperCase().contentEquals(MAIL_TAG)) {
            return TYPE_EMAIL;
        }

        //Check if the rawScanData is SMS
        else if (rawScanData.length() >= 6 &&
                rawScanData.substring(0, 6).toUpperCase().contentEquals(SMS_TAG)) {
            return TYPE_SMS;
        }

        //Check if the rawScanData is Phone
        else if (rawScanData.length() >= 4 &&
                rawScanData.substring(0, 4).toUpperCase().contentEquals(PHONE_TAG)) {
            return TYPE_PHONE;
        }

        return TYPE_PLAIN_TEXT;
    }

    public static String getScanTypeFriendlyName(String rawScanData) {

        @ScanType String resolvedScanType = getScanTypeFrom(rawScanData);
        String friendlyName = "Plain Text";

        switch(resolvedScanType) {

            case TYPE_WEB_URL:
                friendlyName = "Website";
                break;

            case TYPE_EMAIL:
                friendlyName = "E-Mail";
                break;

            case TYPE_SMS:
                friendlyName = "SMS";
                break;

            case TYPE_PHONE:
                friendlyName = "Phone";
                break;

            case TYPE_VCARD:
                friendlyName = "VCard";
                break;
        }

        return friendlyName;
    }

    public static String getScanDataInFriendlyFormat(String rawScanData) {

        @ScanType String resolvedScanType = getScanTypeFrom(rawScanData);
        String friendlyName = rawScanData;

        switch(resolvedScanType) {

            case TYPE_WEB_URL:
                friendlyName = "Click to launch, " + rawScanData;
                break;

            case TYPE_EMAIL:
                friendlyName = "Click to send email at, " + getEMailRecipientAddress(rawScanData);
                break;

            case TYPE_SMS:
                friendlyName = "Click to send a SMS to, " + rawScanData.split(":")[1];
                break;

            case TYPE_PHONE:
                friendlyName = "Click to place a call at, " + rawScanData.split(":")[1];
                break;

            case TYPE_VCARD:
                friendlyName = "Click to import contact from VCard";
                break;
        }

        return friendlyName;
    }

    /**
     * Perform the action based on the content of the scan data
     * @param context
     * @param rawScanData
     */
    public static void performActionOn(Context context, String rawScanData) {

        @ScanType String resolvedScanType = getScanTypeFrom(rawScanData);

        switch(resolvedScanType) {

            case TYPE_WEB_URL:

                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(rawScanData));
                context.startActivity(browserIntent);
                break;

            case TYPE_EMAIL:

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                parseEmailDataFrom(emailIntent, rawScanData);
                context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.choose_email_app)));
                break;

            case TYPE_SMS:

                String[] smsData = rawScanData.split(":");

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:" + Uri.encode(smsData[1])));
                smsIntent.putExtra("sms_body", smsData[2]);
                context.startActivity(Intent.createChooser(smsIntent, context.getString(R.string.choose_sms_app)));
                break;

            case TYPE_PHONE:

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse(rawScanData));
                context.startActivity(Intent.createChooser(phoneIntent, context.getString(R.string.choose_phone_app)));
                break;
        }
    }

    private static void parseEmailDataFrom(Intent mailIntent, String rawScanData) {

        String strippedMatMsg = rawScanData.substring(MAIL_TAG.length(), rawScanData.length());
        String[] mailDataInfo = strippedMatMsg.split(";");

        for (String data : mailDataInfo) {

            String[] dataSplit = data.split(":");

            switch (dataSplit[0].toUpperCase()) {

                case "TO":
                    mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{dataSplit[1]});
                    break;

                case "SUB":
                    mailIntent.putExtra(Intent.EXTRA_SUBJECT, dataSplit[1]);
                    break;

                case "BODY":
                    mailIntent.putExtra(Intent.EXTRA_TEXT, dataSplit[1]);
                    break;
            }
        }
    }

    private static String getEMailRecipientAddress(String rawScanData) {

        String strippedMatMsg = rawScanData.substring(MAIL_TAG.length(), rawScanData.length());
        String[] mailDataInfo = strippedMatMsg.split(";");

        for (String data : mailDataInfo) {

            String[] dataSplit = data.split(":");

            switch (dataSplit[0].toUpperCase()) {

                case "TO":
                    return dataSplit[1];
            }
        }

        return "";
    }
}
