class Tx:
	def __init__(self,seller,price,item,quantity,expiry,txDate,toSell,accepter):
		self.seller = seller
		self.price = price
		self.item = item
		self.quantity = quantity
		self.expiry = expiry
		self.txDate = txDate
		self.toSell = toSell
		self.accepter = accepter
