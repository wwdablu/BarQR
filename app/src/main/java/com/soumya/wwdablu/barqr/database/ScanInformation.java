package com.soumya.wwdablu.barqr.database;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class ScanInformation extends RealmObject {

    @PrimaryKey
    @Index
    public long scanId;            //Store the id of the scan

    @Index
    public String scanType;        //Stores the type of the scan for example QR or BAR

    public String scanData;        //Data retrieved after the scan has been done

    public String scanTime;        //Time when the scan was performed

    public String scanDate;        //Date when the scan was performed
}
