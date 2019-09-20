package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import com.samsung.android.sdk.coldwallet.*;

/*
public interface ScwErrorCode {
    int ERROR_NONE = 0;
    int ERROR_OP_FAIL = -1;
    int ERROR_OP_INTERRUPTED = -2;
    int ERROR_ILLEGAL_MSG = -3;
    int ERROR_LOAD_TA_FAILED = -4;
    int ERROR_OP_TIMEOUT = -5;
    int ERROR_TNC_NOT_AGREED = -6;
    int ERROR_USER_AUTHENTICATION_FAILED = -7;
    int ERROR_MANDATORY_APP_UPDATE_NEEDED = -8;
    int ERROR_CHECK_INTEGRITY_FAILED = -9;
    int ERROR_CHECK_INTEGRITY_NOT_AVAILABLE = -10;
    int ERROR_INVALID_SCW_APP_ID = -11;
    int ERROR_WALLET_RESET = -12;
    int ERROR_NOT_SUPPORTED_COUNTRY = -13;
    int ERROR_CHECK_APP_VERSION_FAILED = -15;
    int ERROR_INVALID_TRANSACTION_FORMAT = -16;
    int ERROR_INIT_TA_FAILED = -17;
    int ERROR_EXTERNAL_DISPLAY_NOT_ALLOWED = -18;
    int ERROR_OP_NOT_SUPPORTED = -19;
    int ERROR_OUT_OF_BOUND_VALUE = -20;
    int ERROR_OUT_OF_OUTPUT_COUNT = -21;
    int ERROR_INVALID_INPUT_UTXO = -22;
    int ERROR_OUT_OF_INPUT_UTXO_SIZE = -23;
    int ERROR_EXCEED_NUMBER_OF_DEVICES = -24;
    int ERROR_INVALID_HD_PATH = -25;
    int ERROR_WALLET_NOT_CREATED = -26;
    int ERROR_NETWORK_NOT_AVAILABLE = -27;
    int ERROR_NETWORK_FAILED = -28;
    int ERROR_SERVER_FAILED = -29;
    int ERROR_NEGATIVE_VALUE = -30;
}
 */

public class SamsungErc20WalletModule extends ReactContextBaseJavaModule {

    private boolean mInitialized = false;

    private String mSeedHash = null;

    private final ReactApplicationContext reactContext;

    public SamsungErc20WalletModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "SamsungErc20Wallet";
    }

    @ReactMethod
    public void init(ReadableMap params, Callback callback) {
        mSeedHash = ScwService.getInstance().getSeedHash();


        callback.invoke(null);
    }

    @ReactMethod
    public void needToUpdate(Callback callback) {
        callback.invoke(null);
    }

    @ReactMethod
    public void getAddr(Callback callback) {
        callback.invoke(null);
    }

    @ReactMethod
    public void getBalance(Callback callback) {
        callback.invoke(null);
    }

    @ReactMethod
    public void transfer(ReadableMap params, Callback callback) {
        callback.invoke(null);
    }
}

