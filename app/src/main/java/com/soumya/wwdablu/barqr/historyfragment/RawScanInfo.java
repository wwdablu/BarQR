package com.soumya.wwdablu.barqr.historyfragment;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RawScanInfo {

    public abstract String rawScanType();
    public abstract String rawScanData();
}
