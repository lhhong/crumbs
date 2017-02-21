from Product import *
from Record import *
import pandas as pd
import urllib2
import urllib
import jsonpickle
import datetime

url = "http://localhost:8080/import"

def addSalesRecords(product):
	filename = "spreadsheets/" + product.name + "_sales.csv"
	data = pd.read_csv(filename, parse_dates=['Date'], usecols = ['Date','Sales'], nrows = 468)
	for row in data.itertuples():
		timestamp = row[1].to_datetime()
		epoch_time = int((timestamp - datetime.datetime(1970,1,1)).total_seconds()) * 1000
		try: product.addSales(Record(epoch_time,row[2]))
		except: break
def addShipmentRecords(product):
	filename = "spreadsheets/" + product.name + "_shipments.csv"
	data = pd.read_csv(filename, parse_dates=['Date'], usecols = ['Date','Quantity'])
	for row in data.itertuples():
		try: product.addShipment(ShipmentRecord(row[1],row[2],row[3]))
		except: break

def main():
	#populate a list of products
	products = []
	product_names = [[ "Marigold Milk", "Dairy product", 4.50 ],
					 [ "Parmesan Cheese", "Dairy product", 6.00 ],
					 [ "Onion", "Vegetables", 2,50 ],
					 [ "Carrot", "Vegetables", 4.00],
					 [ "Sardines", "Canned foods", 3.50 ],
					 [ "Corn", "Canned foods", 4.00]
					 ]
	for itemInfo in product_names:
		products.append( Product(itemInfo[0],itemInfo[1],itemInfo[2]) )
	for item in products:
		addSalesRecords(item)
	for product in products:
		#data = urllib.urlencode(product)
		print jsonpickle.encode(product)
		urllib2.urlopen(urllib2.Request(url=url, data=jsonpickle.encode(product), headers={'Content-Type':'application/json'})).read() 

main()
