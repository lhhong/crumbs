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
		uint x; //x, y coordinate of member
		uint y;
	}
	struct MemberList {
		mapping(address => Member) members;
		MemKey[] keys;
		uint nextKey;
	}
	struct Tx {
		string uuid;
		Member from;
		uint256 price;
		string item;
		uint32 quantity;
		bool toSell;
		Member accepter;
		uint256 transportPrice;
		bool pending;
	}
	struct TxList {
		mapping(string => Tx) txs;
		TxKey[] keys;
		uint nextKey;
	}
	TxList list;
	MemberList memList;

	function register(string _name, uint _x, uint _y) {
		uint key = 0;
		while (key < memList.nextKey && !memList.keys[key].deleted && !(memList.keys[key].addr == msg.sender)) {
			key++;
		}
		if (key == memList.nextKey) {
			list.nextKey++;
		}
		memList.keys[key].addr = msg.sender;
		memList.keys[key].deleted = false;
		memList.members[memList.keys[key].addr].addr = msg.sender;
		memList.members[memList.keys[key].addr].name = _name;
		memList.members[memList.keys[key].addr].x = _x;
		memList.members[memList.keys[key].addr].y = _y;
	}

	function newOffer(string _uuid, uint256 _price, string _item, uint32 _quantity, bool _toSell) {
		if (memList.members[msg.sender].x == 0) {
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
		list.txs[list.keys[key].uuid].toSell = _toSell;    
	}

	function strConcat(string _a, string _b) internal returns (string){
		bytes memory _ba = bytes(_a);
		bytes memory _bb = bytes(_b);
		string memory ab = new string(_ba.length + _bb.length);
		bytes memory ba = bytes(ab);
		uint k = 0;
		for (uint i = 0; i < _ba.length; i++) ba[k++] = _ba[i];
		for (i = 0; i < _bb.length; i++) ba[k++] = _bb[i];
		return string(ba);
	}

	function getAllAvailKey() constant returns (string all) {
		all = "";
		uint key = 0;
		while (key < list.nextKey) {
			if (!list.keys[key].deleted) {
				if (!list.txs[list.keys[key].uuid].pending) {
					all = strConcat(all, list.keys[key].uuid);
				}
			}
		}
	}

	function getTxById(string _uuid) constant returns (string _name, uint _x, uint _y, uint256 _price, string _item, uint32 _quantity, bool _toSell) {
		if (list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) {
			throw;
		}
		_name = list.txs[_uuid].from.name;
		_x = list.txs[_uuid].from.x;
		_y = list.txs[_uuid].from.y;
		_price = list.txs[_uuid].price;
		_item = list.txs[_uuid].item;
		_quantity = list.txs[_uuid].quantity;
		_toSell = list.txs[_uuid].toSell;
	}

	function checkStatus(string _uuid) constant returns (bool _pending, string _name, uint _x, uint _y, uint256 _transportPrice) {
		if (list.txs[_uuid].quantity == 0) {
			throw;
		}
		_pending = list.txs[_uuid].pending;
		_name = list.txs[_uuid].accepter.name;
		_x = list.txs[_uuid].accepter.x;
		_y = list.txs[_uuid].accepter.y;
		_transportPrice = list.txs[_uuid].transportPrice;
	}

	function accept(string _uuid, uint256 _transportPrice) payable {
		if (list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) {
			throw;
		}
		if (list.txs[_uuid].toSell) {
			if (msg.value < (list.txs[_uuid].price + _transportPrice)) {
				throw;
			}
		}	
		Member _accepter = memList.members[msg.sender];
		list.txs[_uuid].accepter = _accepter;
		list.txs[_uuid].pending = true;
		list.txs[_uuid].transportPrice = _transportPrice;
	}

	function agree(string _uuid) payable {
		if (!list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) {
			throw;
		}
		uint256 totalPrice = list.txs[_uuid].price + list.txs[_uuid].transportPrice;
		if (!list.txs[_uuid].toSell) {
			if (msg.value < totalPrice) {
				throw;
			}
			list.txs[_uuid].from.addr.call.gas(2000000).value(totalPrice);
		}	
		list.txs[_uuid].accepter.addr.call.gas(2000000).value(totalPrice);
		//TODO delete Tx
	}
}	


