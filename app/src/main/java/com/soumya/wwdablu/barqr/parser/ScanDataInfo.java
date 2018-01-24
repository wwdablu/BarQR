package com.soumya.wwdablu.barqr.parser;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ScanDataInfo {

    public abstract String scanData();
    public abstract @ScanData.Type String scanDataType();
    public abstract String scanDataTypeFriendlyName();
    public abstract @ScanData.Scan String scanType();
}
