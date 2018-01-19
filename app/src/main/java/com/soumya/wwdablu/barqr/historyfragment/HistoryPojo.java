package com.soumya.wwdablu.barqr.historyfragment;

import org.immutables.value.Value;

@Value.Immutable
public abstract class HistoryPojo {

    public abstract String rawScanType();
    public abstract String rawScanData();
}
