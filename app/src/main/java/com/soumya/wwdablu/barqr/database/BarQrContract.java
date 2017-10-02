package com.soumya.wwdablu.barqr.database;

import android.provider.BaseColumns;

class BarQrContract {

    private BarQrContract() {
        //
    }

    static class History implements BaseColumns {

        static final String TABLE = "history";
        static final String TYPE = "scan_type";
        static final String DATA = "scan_data";
    }
}
