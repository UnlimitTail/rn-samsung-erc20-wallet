package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import com.samsung.android.sdk.coldwallet.*;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.samsung.android.sdk.coldwallet.ScwErrorCode.ERROR_ILLEGAL_MSG;
import static com.samsung.android.sdk.coldwallet.ScwErrorCode.ERROR_WALLET_RESET;

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
    private Integer mTokenDecimal = null;
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
    public void init(final ReadableMap params, final Callback callback) {
        if (mInitialized) {
            callback.invoke(null);
            return;
        }

        ScwService scw = ScwService.getInstance();
        if (null == scw) {
            callback.invoke(new Exception("Samsung keystore not supported this device"));
            return;
        }

        mContractAddress = params.getString("contractAddress");
        mChainnetUrl = params.getString("chainnetUrl");
        mTokenDecimal = params.getInt("tokenDecimal");

        if (
                null == mContractAddress ||
                null == mChainnetUrl ||
                null == mTokenDecimal
        ) {
            callback.invoke(new Exception("Invalid parameters"));
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
                        callback.invoke(new Exception("Empty erc20 wallet"));
                        return;
                    }


                    mErc20Address = addressList.get(0);

                    mInitialized = true;
                    callback.invoke(null);
                }

                @Override
                public void onFailure(int errorCode) {
                    callback.invoke(new Exception("Failed to init : " + errorCode));
                }
            };


        final String hdpath4 = "m/44'/60'/0'/0/0";

        ArrayList<String> hdPathList = new ArrayList<>();
        hdPathList.add(hdpath4);

        ScwService.getInstance().getAddressList(cb, hdPathList);
    }

    @ReactMethod
    public void needToUpdate(final Callback callback) {
        final int keystoreApiLevel = ScwService.getInstance().getKeystoreApiLevel();
        final boolean res =  keystoreApiLevel > 0;
        callback.invoke(null, res);
    }

    @ReactMethod
    public void getAddress(final Callback callback) {
        if (!mInitialized) {
            callback.invoke(new Exception("Not initialized"));
            return;
        }

        callback.invoke(null, mErc20Address);
    }

    @ReactMethod
    public void getBalance(final Callback callback) {
        if (!mInitialized) {
            callback.invoke(new Exception("Not initialized"));
            return;
        }

        callback.invoke(null);
    }

    @ReactMethod
    public void transfer(final ReadableMap params, final Callback callback) {
        if (!mInitialized) {
            callback.invoke(new Exception("Not initialized"));
            return;
        }

        final String toAddress = params.getString("toAddress");
        final Integer amount = params.getInt("amount");
        final Integer gasLimit = params.getInt("gasLimit");

        final String hdPath = "m/44'/60'/0'/0/0";

        final BigInteger gasPrice = Convert.toWei(mTokenDecimal.toString(), Convert.Unit.GWEI).toBigInteger();

        final byte[] encodedUnsignedEthTx = EthUtils.createRawTransaction(
                mWeb3,
                mContractAddress,
                mErc20Address,
                toAddress,
                BigInteger.valueOf(amount),
                gasPrice,
                BigInteger.valueOf(gasLimit)
        );

        ScwService.ScwSignEthTransactionCallback cb =
                new ScwService.ScwSignEthTransactionCallback() {
                    @Override
                    public void onSuccess(byte[] signedEthTransaction) {
                        final String hex = Numeric.toHexString(signedEthTransaction);

                        try {
                            EthSendTransaction t = mWeb3.ethSendRawTransaction(hex).sendAsync().get();
                            if (null != t.getError()) {
                                Log.e(LOG_ID, "EthSendTransaction Err : " +  t.getError().getMessage());
                                callback.invoke(new Exception("Failed to transfer : " + t.getError().getCode()));
                                return;
                            }

                            callback.invoke(null, t.getResult());
                        }
                        catch (Exception e) {
                            callback.invoke(e);
                        }
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        callback.invoke(new Exception("Failed to transfer : " + errorCode));
                    }
                };

        ScwService.getInstance().signEthTransaction(cb, encodedUnsignedEthTx, hdPath);
    }
}
