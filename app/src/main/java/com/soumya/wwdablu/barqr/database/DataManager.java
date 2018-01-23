package com.soumya.wwdablu.barqr.database;

import com.soumya.wwdablu.barqr.model.ImmutableScanDataInfo;
import com.soumya.wwdablu.barqr.model.ScanDataInfo;
import com.soumya.wwdablu.barqr.util.DataHandler;
import com.soumya.wwdablu.barqr.util.Utils;

import java.util.Date;
import java.util.LinkedList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DataManager {

    private static DataManager mInstance;

    private DataManager() {
        //
    }

    /**
     * Returns the instance of the DataManager to access the scan history information
     * @return
     */
    public static synchronized DataManager getInstance() {

        if(null == mInstance) {
            mInstance = new DataManager();
        }

        return mInstance;
    }

    public Observable<LinkedList<ScanDataInfo>> getAllScanHistory() {

        return Observable.create(emitter -> {

            try (final Realm realm = Realm.getDefaultInstance()) {


                realm.executeTransaction(realmParam -> {

                    RealmResults<ScanInformation> results = realmParam.where(ScanInformation.class).findAll();
                    LinkedList<ScanDataInfo> scanInformationList = new LinkedList<>();

                    for(ScanInformation scanInfo : results) {

                        scanInformationList.add(
                                ImmutableScanDataInfo.builder()
                                        .scanData(scanInfo.scanData)
                                        .scanDataType("")
                                        .scanType(scanInfo.scanType)
                                        .scanDataTypeFriendlyName(DataHandler.getScanTypeFriendlyName(scanInfo.scanData))
                                .build()
                        );
                    }

                    emitter.onNext(scanInformationList);
                    emitter.onComplete();
                });
            }
        });
    }

    public void saveScanData(final ScanDataInfo scanDataInfo) {

        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {

            try (Realm realm = Realm.getDefaultInstance()) {

                realm.executeTransaction(realmParam -> {

                    Number _id = realm.where(ScanInformation.class).max("scanId");
                    int id = (null == _id ? 1 : (_id.intValue() + 1));
                    ScanInformation scanInformation = realm.createObject(ScanInformation.class, id);

                    scanInformation.scanData = scanDataInfo.scanData();
                    scanInformation.scanDate = Utils.getCurrentDate();
                    scanInformation.scanTime = Long.toString(System.currentTimeMillis());
                    scanInformation.scanType = scanDataInfo.scanType();
                });
            }
        })

        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                //
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Could not save scan data because %s", e.getMessage());
            }

            @Override
            public void onComplete() {
                Timber.d("Scan data has been saved.");
            }
        });
    }
}
