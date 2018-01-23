package com.soumya.wwdablu.barqr.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ScanDataInfo {

    public abstract @ScanData.Type String scanDataType();
    public abstract String scanData();
    public abstract String scanDataTypeFriendlyName();
    public abstract @ScanData.Scan String scanType();
}
