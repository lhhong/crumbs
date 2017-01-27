package com.crumbs.ethereum;

import com.alibaba.fastjson.JSON;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;


public class EthereumBean {

    Ethereum ethereum;

    public void start(){
        this.ethereum = EthereumFactory.createEthereum();
        this.ethereum.addListener(new EthereumListener(ethereum));
    }


    public String getBestBlock(){
        return "" + ethereum.getBlockchain().getBestBlock().getNumber();
    }

    public void sendMockTx() {
    }

    public BigInteger getAccount(String addr) {
    	return ethereum.getRepository().getBalance(ByteUtil.hexStringToBytes(addr));
    }

    public String getAdminInfo() {
        return JSON.toJSONString(ethereum.getAdminInfo());
    }
}
