package com.soumya.wwdablu.barqr.parser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.soumya.wwdablu.barqr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        TYPE_GEOLOCATION,
        TYPE_WIFI,
        TYPE_UNKNOWN
    })
    @interface Type {}

    public static final String TYPE_WEB_URL = "type.web.url";
    public static final String TYPE_PLAIN_TEXT = "type.plain.text";
    public static final String TYPE_VCARD = "type.vcard";
    public static final String TYPE_EMAIL = "type.email";
    public static final String TYPE_SMS = "type.sms";
    public static final String TYPE_PHONE = "type.phone";
    public static final String TYPE_GEOLOCATION = "type.geo";
    public static final String TYPE_WIFI = "type.wifi";
    public static final String TYPE_UNKNOWN = "type.unknown";

    //Data Identifiers
    private static final String VCARD_BEGIN = "BEGIN:VCARD";
    private static final String VCARD_END = "END:VCARD";
    private static final String MAIL_IDENTIFIER = "MATMSG:";
    private static final String MESSAGE_IDENTIFIER = "SMSTO:";
    private static final String PHONE_IDENTIFIER = "TEL:";
    private static final String GEOLOCATION_IDENTIFIER = "GEO:";
    private static final String WIFI_IDENTIFIER = "WIFI:";

    /**
     * Resolves the type of the scanned data and returns accordingly. This
     * can be used to perform specific action based on the data type, for
     * example, if Web URL, then the browser can be launched.
     * @param scanPayload Data obtained after scanning
     * @return Data content information
     */
    public static ScanDataInfo resolveDataContent(@NonNull Context context, @Scan String scanFormat, @NonNull String scanPayload) {

        return ImmutableScanDataInfo.builder()
            .scanData(scanPayload)
            .scanDataType(getScanTypeFrom(scanPayload))
            .scanDataTypeFriendlyName(getScanTypeFriendlyName(context, scanPayload))
            .scanType(scanFormat)
            .build();
    }

    /**
     * Get the type of data that has been scanned
     * @param rawScanData Data obtained after scanning
     * @return Type of the scan, like URL, Phone, VCard
     */
    public static @Type String getScanTypeFrom(String rawScanData) {

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
        else if (rawScanData.length() >= 7 &&
                rawScanData.substring(0, 7).toUpperCase().contentEquals(MAIL_IDENTIFIER)) {
            return TYPE_EMAIL;
        }

        //Check if the rawScanData is SMS
        else if (rawScanData.length() >= 6 &&
                rawScanData.substring(0, 6).toUpperCase().contentEquals(MESSAGE_IDENTIFIER)) {
            return TYPE_SMS;
        }

        //Check if the rawScanData is Phone
        else if (rawScanData.length() >= 4 &&
                rawScanData.substring(0, 4).toUpperCase().contentEquals(PHONE_IDENTIFIER)) {
            return TYPE_PHONE;
        }

        //Check if the rawScanData is geolocation
        else if (rawScanData.length() >= 4 &&
                rawScanData.substring(0, 4).toUpperCase().contentEquals(GEOLOCATION_IDENTIFIER)) {
            return TYPE_GEOLOCATION;

            //Check if the rawScanData is WiFi
        } else if (rawScanData.length() >= 5 &&
                rawScanData.substring(0, 5).toUpperCase().contentEquals(WIFI_IDENTIFIER)) {
            return TYPE_WIFI;

        }

        return TYPE_PLAIN_TEXT;
    }

    /**
     * Provide user friendly name of the type of data scanned
     * @param rawScanData Data received from the scanner
     * @return String of the scan type in human readable format
     */
    public static String getScanTypeFriendlyName(Context context, String rawScanData) {

        @Type String resolvedScanType = getScanTypeFrom(rawScanData);
        String friendlyName = context.getString(R.string.plain_text);

        switch(resolvedScanType) {

            case TYPE_WEB_URL:
                friendlyName = context.getString(R.string.website);
                break;

            case TYPE_EMAIL:
                friendlyName = context.getString(R.string.email);
                break;

            case TYPE_SMS:
                friendlyName = context.getString(R.string.message);
                break;

            case TYPE_PHONE:
                friendlyName = context.getString(R.string.phone);
                break;

            case TYPE_VCARD:
                friendlyName = context.getString(R.string.vcard);
                break;

            case TYPE_GEOLOCATION:
                friendlyName = context.getString(R.string.location);
                break;

            case TYPE_WIFI:
                friendlyName = context.getString(R.string.wifi);
                break;
        }

        return friendlyName;
    }

    /**
     * Provides the possible action that can be perform on the data
     * that has been scanned.
     * @param rawScanData Data received from the scanner
     * @return String describing the action
     */
    public static String getActionInFriendlyText(Context context, String rawScanData) {

        String resolvedScanType = getScanTypeFrom(rawScanData);
        String friendlyName = rawScanData;

        switch(resolvedScanType) {

            case TYPE_WEB_URL:
                friendlyName = context.getString(R.string.click_to_launch) + rawScanData;
                break;

            case TYPE_EMAIL:
                friendlyName = context.getString(R.string.click_to_send_email) + getEMailRecipientAddress(rawScanData);
                break;

            case TYPE_SMS:
                friendlyName = context.getString(R.string.click_to_send_sms) + rawScanData.split(":")[1];
                break;

            case TYPE_PHONE:
                friendlyName = context.getString(R.string.click_to_place_call) + rawScanData.split(":")[1];
                break;

            case TYPE_VCARD:
                friendlyName = context.getString(R.string.click_to_import_cards);
                break;

            case TYPE_GEOLOCATION:
                friendlyName = context.getString(R.string.click_to_launch_maps);
                break;

            case TYPE_WIFI:
                friendlyName = context.getString(R.string.click_to_view_info);
                break;
        }

        return friendlyName;
    }

    /**
     * Perform the action based on the content of the scan data
     * @param context Context to access resources
     * @param rawScanData Data received from the scanner
     */
    public static void performActionOn(Context context, String rawScanData) {

        @Type String resolvedScanType = getScanTypeFrom(rawScanData);

        switch(resolvedScanType) {

            case TYPE_WEB_URL:

                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(rawScanData));
                context.startActivity(browserIntent);
                break;

            case TYPE_EMAIL:

                new AlertDialog.Builder(context)
                    .setTitle("Send EMail")
                    .setMessage(String.format("Would you like to send Email to %1s", getEMailRecipientAddress(rawScanData)))
                    .setCancelable(false)
                    .setPositiveButton("Send", (dialogInterface, i) -> {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        parseEmailDataFrom(emailIntent, rawScanData);
                        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.choose_email_app)));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                break;

            case TYPE_SMS:

                final String[] smsData = rawScanData.split(":");

                new AlertDialog.Builder(context)
                    .setTitle("Send Message")
                    .setMessage(String.format("Would you like to send SMS to %1s", smsData[1]))
                    .setCancelable(false)
                    .setPositiveButton("Send", (dialogInterface, i) -> {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("sms:" + Uri.encode(smsData[1])));
                        smsIntent.putExtra("sms_body", smsData[2]);
                        context.startActivity(Intent.createChooser(smsIntent, context.getString(R.string.choose_sms_app)));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                break;

            case TYPE_PHONE:

                String phoneData = rawScanData.substring(PHONE_IDENTIFIER.length(), rawScanData.length());

                new AlertDialog.Builder(context)
                    .setTitle("Place Call")
                    .setMessage(String.format("Would you like to place call to %1s", phoneData))
                    .setCancelable(false)
                    .setPositiveButton("Call", (dialogInterface, i) -> {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                        phoneIntent.setData(Uri.parse(rawScanData));
                        context.startActivity(Intent.createChooser(phoneIntent, context.getString(R.string.choose_phone_app)));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                break;

            case TYPE_GEOLOCATION:

                Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rawScanData));
                if(null != mapsIntent.resolveActivity(context.getPackageManager())) {
                    context.startActivity(mapsIntent);
                } else {
                    Toast.makeText(context, R.string.maps_app_not_found, Toast.LENGTH_LONG).show();
                }
                break;

            case TYPE_VCARD:
                File vcard = null;
                FileOutputStream vcardFos = null;
                try {
                    vcard = new File(context.getExternalFilesDir("barqr").getPath() + File.pathSeparator + "vcard.vcf");
                    vcardFos = new FileOutputStream(vcard);
                    OutputStreamWriter vcardWriter = new OutputStreamWriter(vcardFos);
                    vcardWriter.write(rawScanData);
                    vcardWriter.flush();
                    vcardWriter.close();
                    vcardFos.close();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri vcardUri = FileProvider.getUriForFile(context,
                            context.getApplicationContext().getPackageName() + ".provider", vcard);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(vcardUri,"text/x-vcard");
                    context.startActivity(intent);

                    vcardFos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case TYPE_WIFI:

                String wifiDetails = showWiFiDetails(rawScanData);

                new AlertDialog.Builder(context)
                    .setTitle("SSID Details")
                    .setMessage(wifiDetails)
                    .setCancelable(false)
                    .setPositiveButton("Close", null)
                    .show();
                break;
        }
    }

    private static void parseEmailDataFrom(Intent mailIntent, String rawScanData) {

        String strippedMatMsg = rawScanData.substring(MAIL_IDENTIFIER.length(), rawScanData.length());
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

        String strippedMatMsg = rawScanData.substring(MAIL_IDENTIFIER.length(), rawScanData.length());
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

    private static String showWiFiDetails(String rawScanData) {

        StringBuilder sb = new StringBuilder();

        String data = rawScanData.substring(WIFI_IDENTIFIER.length(), rawScanData.length());
        String wifiInfo[] = data.split(";");

        for(String s : wifiInfo) {

            String splitData[] = s.split(":");

            switch (splitData[0].toUpperCase()) {

                case "T":
                    sb.append("SSID Type: ").append(splitData[1]).append("\n");
                    break;

                case "S":
                    sb.append("SSID Name: ").append(splitData[1]).append("\n");
                    break;

                case "P":
                    sb.append("Password: ").append(splitData[1]).append("\n");
                    break;
            }
        }

        return sb.toString();
    }
}
