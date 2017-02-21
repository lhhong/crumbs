class Record:
	def __init__(self,date,quantity):
		self.date = date
		self.quantity = int(quantity)

	def printDetails(self):
		print self.date
		print self.quantity

class ShipmentRecord(Record):
	def __init__(self,date,quantity,doe):
		self.expiry = doe