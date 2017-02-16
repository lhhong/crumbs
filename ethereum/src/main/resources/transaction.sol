pragma solidity ^0.4.9;
contract transaction {
	struct TxKey {
		string uuid;
		bool deleted;
	}
	struct MemKey {
		address addr;
		bool deleted;
	}
	struct Member {
		address addr;
		string name;
		int64 x_loc; //x, y coordinate of member
		int64 y_loc;
	}
	struct MemberList {
		mapping(address => Member) members;
		MemKey[50] keys;
		uint32 nextKey;
	}
	struct Tx {
		string uuid;
		Member from;
		uint64 price;
		string item;
		uint32 quantity;
		uint64 expiry;
		uint64 txDate;
		bool toSell;
		Member accepter;
		uint64 transportPrice;
		bool pending;
		bool done;
	}
	struct TxList {
		mapping(string => Tx) txs;
		TxKey[200] keys;
		uint32 nextKey;
	}

	MemberList memList;
	TxList list;

	function topup() payable {

	}

	function register(string _name, int64 _x, int64 _y) public {
		uint key = 0;
		while (key < memList.nextKey && !memList.keys[key].deleted) {
		    if (memList.keys[key].addr == msg.sender) {
		        throw;
		    }
			key++;
		}
		if (key == memList.nextKey) {
			memList.nextKey++;
		}
		memList.keys[key].addr = msg.sender;
		memList.keys[key].deleted = false;
		memList.members[memList.keys[key].addr].addr = msg.sender;
		memList.members[memList.keys[key].addr].name = _name;
		memList.members[memList.keys[key].addr].x_loc = _x;
		memList.members[memList.keys[key].addr].y_loc = _y;
	}

	function newOffer(string _uuid, uint64 _price, string _item, uint32 _quantity, uint64 _expiry, bool _toSell, uint64 _txDate) public {
		if (memList.members[msg.sender].x_loc == 0) {
			throw;
		}
		uint key = 0;
		while (key < list.nextKey && !list.keys[key].deleted) {
			key++;
		}
		if (key == list.nextKey) {
			list.nextKey++;
		}
		list.keys[key].uuid = _uuid;
		list.keys[key].deleted = false;
		list.txs[list.keys[key].uuid].uuid = _uuid;
		list.txs[list.keys[key].uuid].from = memList.members[msg.sender];
		list.txs[list.keys[key].uuid].price = _price;
		list.txs[list.keys[key].uuid].item = _item;
		list.txs[list.keys[key].uuid].quantity = _quantity;
		list.txs[list.keys[key].uuid].expiry = _expiry;
		list.txs[list.keys[key].uuid].toSell = _toSell;
		list.txs[list.keys[key].uuid].txDate = _txDate;
	}

	function strConcat(string _a, string _b) internal returns (string){
		bytes memory _ba = bytes(_a);
		bytes memory _bb = bytes(_b);
		string memory ab = new string(_ba.length + _bb.length + 1);
		bytes memory ba = bytes(ab);
		byte sc = ';';
		uint k = 0;
		for (uint i = 0; i < _ba.length; i++) ba[k++] = _ba[i];
		ba[k++] = sc;
		for (i = 0; i < _bb.length; i++) ba[k++] = _bb[i];
		return string(ba);
	}

	function getAllKey() constant returns (string all) {
		all = "";
		uint key = 0;
		while (key < list.nextKey) {
			if (!list.keys[key].deleted) {
				all = strConcat(all, list.keys[key].uuid);
			}
			key++;
		}
	}

	function getAllAvailKey() constant returns (string all) {
		all = "";
		uint key = 0;
		while (key < list.nextKey) {
			if (!list.keys[key].deleted) {
				if (!list.txs[list.keys[key].uuid].pending && !list.txs[list.keys[key].uuid].done) {
					all = strConcat(all, list.keys[key].uuid);
				}
			}
			key++;
		}
	}

	function getTxById(string _uuid) constant returns (address _addr, string _name, int64 _x, int64 _y, uint64 _price, string _item, uint32 _quantity, uint64 _expiry, bool _toSell, bool _pending, bool _done, uint64 _txDate) {
		if (list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) {
			throw;
		}
		_addr = list.txs[_uuid].from.addr;
		_name = list.txs[_uuid].from.name;
		_x = list.txs[_uuid].from.x_loc;
		_y = list.txs[_uuid].from.y_loc;
		_price = list.txs[_uuid].price;
		_item = list.txs[_uuid].item;
		_quantity = list.txs[_uuid].quantity;
		_expiry = list.txs[_uuid].expiry;
		_toSell = list.txs[_uuid].toSell;
		_pending = list.txs[_uuid].pending;
		_done = list.txs[_uuid].done;
		_txDate = list.txs[_uuid].txDate;
	}

	function checkPendingStatus(string _uuid) constant returns (bool _pending, address _addr, string _name, int64 _x, int64 _y, uint64 _transportPrice) {
		if (list.txs[_uuid].quantity == 0) {
			throw;
		}
		_pending = list.txs[_uuid].pending;
		_addr = list.txs[_uuid].accepter.addr;
		_name = list.txs[_uuid].accepter.name;
		_x = list.txs[_uuid].accepter.x_loc;
		_y = list.txs[_uuid].accepter.y_loc;
		_transportPrice = list.txs[_uuid].transportPrice;
	}

	function checkDoneStatus(string _uuid) constant returns (bool _done) {
		if (list.txs[_uuid].quantity == 0) {
			throw;
		}
		_done = list.txs[_uuid].done;
	}

	function accept(string _uuid, uint64 _transportPrice) payable public {
		if (list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) {
			throw;
		}
		if (list.txs[_uuid].toSell) {
			if (msg.value < ((uint256) (list.txs[_uuid].price + _transportPrice))*1000000000000000000) {
				throw;
			}
		}
		Member _accepter = memList.members[msg.sender];
		list.txs[_uuid].accepter = _accepter;
		list.txs[_uuid].pending = true;
		list.txs[_uuid].transportPrice = _transportPrice;
	}

	function stringsEqual(string storage _a, string memory _b) internal returns (bool) {
		bytes storage a = bytes(_a);
		bytes memory b = bytes(_b);
		if (a.length != b.length)
			return false;
		for (uint i = 0; i < a.length; i ++)
			if (a[i] != b[i])
				return false;
		return true;
	}

	function agree(string _uuid) payable public {
		if (msg.sender != list.txs[_uuid].from.addr) {
			throw;
		}
		if (!list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) {
			throw;
		}
		uint totalPrice = ((uint256) (list.txs[_uuid].price + list.txs[_uuid].transportPrice))*1000000000000000000;
		if (!list.txs[_uuid].toSell) {
			if (msg.value < totalPrice) {
				throw;
			}
			if (!list.txs[_uuid].accepter.addr.send(totalPrice)) {
				throw;
			}
		}
		else if (!list.txs[_uuid].from.addr.send(totalPrice)) {
			throw;
		}
		list.txs[_uuid].done = true;

	}

	function deleteTx(string _uuid) public {
		if (msg.sender != list.txs[_uuid].from.addr) {
			throw;
		}
		uint key = 0;
		while (!stringsEqual(list.keys[key].uuid, _uuid)) {
			key++;
			if (key == list.nextKey) {
			    throw;
			}
		}

		list.keys[key].deleted = true;
		delete list.txs[_uuid];
	}
}