class Record(object):
	def __init__(self,dateStamp,quantity):
		self.dateStamp = dateStamp
		self.quantity = int(quantity)

	def printDetails(self):
		print self.dateStamp
		print self.quantity

class ShipmentRecord(object):
	def __init__(self,dateStamp,quantity,doe):
		self.dateStamp = dateStamp
		self.quantity = int(quantity)
		self.expiry = doe