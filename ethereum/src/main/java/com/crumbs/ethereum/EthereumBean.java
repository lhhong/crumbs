package com.crumbs.ethereum;

import com.alibaba.fastjson.JSON;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.mine.MinerListener;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;


public class EthereumBean{

    Ethereum ethereum;
    public final Logger logger = LoggerFactory.getLogger(EthereumBean.class);

    public void start(){
        this.ethereum = EthereumFactory.createEthereum();
        this.ethereum.addListener(new EthereumListener(ethereum));
        this.ethereum.getBlockMiner().startMining();
    }


    public String getBestBlock(){
        return "" + ethereum.getBlockchain().getBestBlock().getNumber();
    }

    public void sendMockTx(String sender, String receiver) {
	    byte[] senderPrivateKey = ByteUtil.hexStringToBytes(sender);
	    byte[] receiveAddress = ByteUtil.hexStringToBytes(receiver);
	    byte[] fromAddress = ECKey.fromPrivate(senderPrivateKey).getAddress();
	    byte[] data = {(byte) 0x3e};
	    BigInteger nonce = ethereum.getRepository().getNonce(fromAddress);
	    Transaction tx = new Transaction(
			    ByteUtil.bigIntegerToBytes(nonce),
			    ByteUtil.longToBytesNoLeadZeroes(ethereum.getGasPrice()),
			    ByteUtil.longToBytesNoLeadZeroes(200000),
			    receiveAddress,
			    ByteUtil.bigIntegerToBytes(BigInteger.valueOf(1)),  // 1_000_000_000 gwei, 1_000_000_000_000L szabo, 1_000_000_000_000_000L finney, 1_000_000_000_000_000_000L ether
			    data,
			    ethereum.getChainIdForNextBlock());

	    tx.sign(ECKey.fromPrivate(senderPrivateKey));
	    logger.info("<=== Sending transaction: " + tx);
	    ethereum.submitTransaction(tx);
    }

    public BigInteger getAccountBal(String addr) {
    	return ethereum.getRepository().getBalance(ByteUtil.hexStringToBytes(addr));
    }

    public String getAdminInfo() {
        return JSON.toJSONString(ethereum.getAdminInfo());
    }

}
