from Record import *
class Product:
	def __init__(self,name,category,price):
		self.name = name
		self.category = category
		self.price = price
		self.salesRecord = []
		self.shipmentsRecord = []

	def addSales(self,sales):
		self.salesRecord.append(sales)
	
	def addShipment(self,shipment):
		self.shipmentsRecord.append(shipment)

	def printDetails(self):
		print self.name + ", " + self.category + ", " + str(self.price)
		for sales in self.salesRecord:
			sales.printDetails()
		for shipment in self.shipmentsRecord:
			shipment.printDetails()

	def getNumOfSales(self):
		return len(self.salesRecord)




