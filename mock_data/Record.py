class Record:
	def __init__(self,dateStamp,quantity):
		self.dateStamp = dateStamp
		self.quantity = int(quantity)

	def printDetails(self):
		print self.dateStamp
		print self.quantity

class ShipmentRecord(Record):
	def __init__(self,dateStamp,quantity,doe):
		self.expiry = doe