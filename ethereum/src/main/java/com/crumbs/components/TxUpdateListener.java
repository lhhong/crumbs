package com.crumbs.components;

import org.ethereum.core.*;
import org.ethereum.listener.EthereumListener;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.message.Message;
import org.ethereum.net.p2p.HelloMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;

import java.util.List;

/**
 * Created by low on 18/3/17 12:35 PM.
 */
public interface TxUpdateListener extends EthereumListener{

	@Override
	default void onTransactionExecuted(TransactionExecutionSummary summary) {
	}

	@Override
	default void onPendingStateChanged(PendingState pendingState) {
	}

	@Override
	default void onSyncDone(SyncState state) {
	}

	@Override
	default void onBlock(BlockSummary block) {

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
