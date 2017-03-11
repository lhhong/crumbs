package com.crumbs.components;

import com.crumbs.entities.CrumbsContract;
import org.ethereum.core.*;
import org.ethereum.listener.EthereumListener;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.message.Message;
import org.ethereum.net.p2p.HelloMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.ethereum.util.ByteUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by low on 25/2/17 12:52 AM.
 * Specialised Listener with only onBlock that needs to be implemented for easy lambda expression use
 */
public interface OnBlockListener extends EthereumListener{
	@Override
	default void onTransactionExecuted(TransactionExecutionSummary summary) {
	}

	@Override
	default void onPendingTransactionUpdate(TransactionReceipt txReceipt, PendingTransactionState state, Block block) {
	}

	@Override
	default void onPendingStateChanged(PendingState pendingState) {
	}

	@Override
	default void onSyncDone(SyncState state) {
	}

	@Override
	default void trace(String s) {

	}

	@Override
	default void onNodeDiscovered(Node node) {

	}

	@Override
	default void onHandShakePeer(Channel channel, HelloMessage helloMessage) {

	}

	@Override
	default void onEthStatusUpdated(Channel channel, StatusMessage statusMessage) {

	}

	@Override
	default void onRecvMessage(Channel channel, Message message) {

	}

	@Override
	default void onSendMessage(Channel channel, Message message) {

	}

	@Override
	default void onPeerDisconnect(String s, long l) {

	}

	@Override
	default void onPendingTransactionsReceived(List<Transaction> list) {

	}

	@Override
	default void onNoConnections() {

	}

	@Override
	default void onVMTraceCreated(String s, String s1) {

	}

	@Override
	default void onPeerAddedToSyncPool(Channel channel) {

	}
}
