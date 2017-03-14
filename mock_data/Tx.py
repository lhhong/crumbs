class Tx(object):
	def __init__(self,seller,price,item,quantity,expiry,txDate,sell,accepter):
		self.seller = seller
		self.price = price
		self.item = item
		self.quantity = quantity
		self.expiry = expiry
		self.txDate = txDate
		self.sell = sell
		self.accepter = accepter
