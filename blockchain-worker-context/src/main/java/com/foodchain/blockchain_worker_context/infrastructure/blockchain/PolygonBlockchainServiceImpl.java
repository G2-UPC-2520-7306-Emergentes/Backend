// infrastructure/blockchain/PolygonBlockchainServiceImpl.java
package com.foodchain.blockchain_worker_context.infrastructure.blockchain;

import com.foodchain.blockchain_worker_context.application.outbound.blockchain.BlockchainService;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.util.Collections;

@Service
public class PolygonBlockchainServiceImpl implements BlockchainService {
    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;
    private Long chainId;

    public PolygonBlockchainServiceImpl(Web3j web3j,
                                        @Value("${blockchain.private-key}") String privateKey,
                                        @Value("${blockchain.contract-address}") String contractAddress) {
        this.web3j = web3j;
        this.credentials = Credentials.create(privateKey);
        this.contractAddress = contractAddress;
        this.chainId = null;
    }

    @Override
    public String anchorHash(String hash) throws Exception {
        if (this.chainId == null) {
            EthChainId ethChainId = web3j.ethChainId().send();
            this.chainId = ethChainId.getChainId().longValue();
        }

        byte[] hashBytes = Hex.decode(hash.startsWith("0x") ? hash.substring(2) : hash);
        Function function = new Function("anchorHash", Collections.singletonList(new Bytes32(hashBytes)), Collections.emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        gasPrice = gasPrice.multiply(BigInteger.valueOf(110)).divide(BigInteger.valueOf(100)); // +10%

        BigInteger gasLimit;
        try {
            org.web3j.protocol.core.methods.request.Transaction estimateTransaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(credentials.getAddress(), contractAddress, encodedFunction);
            EthEstimateGas estimateGasResponse = web3j.ethEstimateGas(estimateTransaction).send();
            gasLimit = estimateGasResponse.getAmountUsed().multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100));
        } catch (Exception e) {
            gasLimit = BigInteger.valueOf(100_000);
        }

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, this.chainId, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        if (ethSendTransaction.hasError()) {
            throw new RuntimeException("Error al enviar la transacción: " + ethSendTransaction.getError().getMessage());
        }
        return ethSendTransaction.getTransactionHash();
    }
}