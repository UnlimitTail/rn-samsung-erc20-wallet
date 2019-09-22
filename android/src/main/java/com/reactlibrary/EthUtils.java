package com.reactlibrary;

import android.util.Log;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

final class EthUtils {
    private static String LOG_ID = "SamsungErc20WalletModule";

    public static byte[] createRawTransaction(Web3j web3,
                                               String addrContract,
                                               String fromAddress,
                                               String toAddress,
                                               BigInteger amount,
                                               BigInteger gasPrice,
                                               BigInteger gasLimit
    ) {
        try {
            //* when you send ERC20 Token
            Function function = new Function(
                    "transfer",
                    Arrays.<Type>asList(
                            new Address(toAddress),
                            new Uint256(amount)),
                    Collections.<TypeReference<?>>emptyList()
            );

            final String data = FunctionEncoder.encode(function);

            BigInteger nonce = getNonce(web3, fromAddress);

            RawTransaction t = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    addrContract,
                    data
            );

            return TransactionEncoder.encode(t);
        }
        catch (Exception e) {
            Log.e(LOG_ID, e.toString());
            return new byte[0];
        }
    }

    public static BigInteger getNonce(Web3j web3, String fromAddress) throws InterruptedException, ExecutionException {
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get();

        return ethGetTransactionCount.getTransactionCount();
    }
}
