package com.reactlibrary;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import com.samsung.android.sdk.coldwallet.*;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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

    private static String LOG_ID = "SamsungErc20WalletModule";

    private boolean mInitialized = false;

    private String mSeedHash = null;
    private int[] mSupportedCoinTypes = null;
    private String mErc20Address = null;
    private String mContractAddress = null;
    private String mChainnetUrl = null;
    private Web3j mWeb3 = null;

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
    public void init(final ReadableMap params, final Promise promise) {
        if (mInitialized) {
            promise.resolve(null);
            return;
        }

        ScwService scw = ScwService.getInstance();
        if (null == scw) {
            promise.reject("INVALID_DEVICE", "Samsung keystore not supported this device");
            return;
        }

        mContractAddress = params.getString("contractAddress");
        mChainnetUrl = params.getString("chainnetUrl");

        if (
                null == mContractAddress ||
                null == mChainnetUrl
        ) {
            promise.reject("INVALID_PARAMETERS", "Invalid parameters");
            return;
        }

        mWeb3 = Web3jFactory.build(new InfuraHttpService(mChainnetUrl));

        mSeedHash = ScwService.getInstance().getSeedHash();
        mSupportedCoinTypes = ScwService.getInstance().getSupportedCoins();

        ScwService.ScwGetAddressListCallback cb =
            new ScwService.ScwGetAddressListCallback() {
                @Override
                public void onSuccess(List<String> addressList) {

                    if (addressList.isEmpty()) {
                        promise.reject("NO_WALLET", "Empty erc20 wallet");
                        return;
                    }


                    mErc20Address = addressList.get(0);

                    mInitialized = true;
                    promise.resolve(null);
                }

                @Override
                public void onFailure(int errorCode) {
                    promise.reject("FAILURE", "Failed to init : " + errorCode);
                }
            };


        final String hdpath4 = "m/44'/60'/0'/0/0";

        ArrayList<String> hdPathList = new ArrayList<>();
        hdPathList.add(hdpath4);

        ScwService.getInstance().getAddressList(cb, hdPathList);
    }

    @ReactMethod
    public void needToUpdate(final Promise promise) {
        if (!mInitialized) {
            promise.reject("NOT_INITIALIZED", "NOT_INITIALIZED");
            return;
        }
        
        final int keystoreApiLevel = ScwService.getInstance().getKeystoreApiLevel();
        final boolean res =  keystoreApiLevel > 0;
        promise.resolve(Boolean.valueOf(res));
    }

    @ReactMethod
    public void getAddress(final Promise promise) {
        if (!mInitialized) {
            promise.reject("NOT_INITIALIZED", "NOT_INITIALIZED");
            return;
        }

        promise.resolve(mErc20Address);
    }

    @ReactMethod
    public void getBalance(final Promise promise) {
        if (!mInitialized) {
            promise.reject("NOT_INITIALIZED", "NOT_INITIALIZED");
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL ep = new URL("https://api.tokenbalance.com/balance/" + mContractAddress + "/" + mErc20Address);

                    HttpsURLConnection conn = (HttpsURLConnection)ep.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    if (200 != conn.getResponseCode()) {
                        throw new Exception("Failed to request : " + conn.getResponseCode());
                    }

                    InputStream responseBody = conn.getInputStream();

                    byte[] buf = new byte[4096];
                    int len = responseBody.read(buf);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    out.write(buf, 0, len);

                    promise.resolve(new String(out.toByteArray(), "UTF-8"));
                }
                catch (Exception e) {
                    promise.reject(e);
                }

            }
        });
    }

    @ReactMethod
    public void transfer(final ReadableMap params, final Promise promise) {
        if (!mInitialized) {
            promise.reject("NOT_INITIALIZED", "NOT_INITIALIZED");
            return;
        }

        ScwService.ScwSignEthTransactionCallback cb =
                new ScwService.ScwSignEthTransactionCallback() {
                    @Override
                    public void onSuccess(byte[] signedEthTransaction) {
                        final String hex = Numeric.toHexString(signedEthTransaction);

                        try {
                            EthSendTransaction t = mWeb3.ethSendRawTransaction(hex).sendAsync().get();
                            if (null != t.getError()) {
                                Log.e(LOG_ID, "EthSendTransaction Err : " +  t.getError().getMessage());
                                promise.reject("ERR_TRANSFER", "Failed to transfer : " + t.getError().getCode());
                                return;
                            }

                            promise.resolve(t.getResult());
                        }
                        catch (Exception e) {
                            Log.e(LOG_ID, "EthSendTransaction Err : " +  e.toString());
                            promise.reject("ERR_TRANSFER", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        Log.e(LOG_ID, "EthSendTransaction Err : " +  Integer.valueOf(errorCode).toString());
                        promise.reject("ERR_TRANSFER", Integer.valueOf(errorCode).toString());
                    }
                };

        try {
            final String toAddress = params.getString("toAddress");
            final Integer amount = params.getInt("amount");
            final Integer gasLimit = 100000;
            final Integer gasPriceRaw = EthUtils.getGasPrice();
            Log.d(LOG_ID, "gasPriceRaw : " + gasPriceRaw);
            final BigInteger gasPrice = Convert.toWei(gasPriceRaw.toString(), Convert.Unit.GWEI).toBigInteger();

            final byte[] encodedUnsignedEthTx = EthUtils.createRawTransaction(
                    mWeb3,
                    mContractAddress,
                    mErc20Address,
                    toAddress,
                    BigInteger.valueOf(amount),
                    gasPrice,
                    BigInteger.valueOf(gasLimit)
            );

            final String hdPath = "m/44'/60'/0'/0/0";
            ScwService.getInstance().signEthTransaction(cb, encodedUnsignedEthTx, hdPath);
        }
        catch (Exception e) {
            promise.reject(e);
        }
    }
}
