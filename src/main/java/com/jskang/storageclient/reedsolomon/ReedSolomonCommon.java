package com.jskang.storageclient.reedsolomon;

import java.io.IOException;

public interface ReedSolomonCommon {

    int DATA_SHARDS = 6;
    int PARITY_SHARDS = 3;
    int TOTAL_SHARDS = 9;
    int BYTES_IN_INT = 4;
}
