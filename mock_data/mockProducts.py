from Product import *
from Record import *
import pandas as pd
import urllib2
import urllib
import jsonpickle
import datetime
import csv

url = "http://localhost:8080/import"

def getFileRows(filename):
	with open(filename,"r") as f:
			reader = csv.reader(f,delimiter = ",")
			data = list(reader)
			row_count = len(data)
	return row_count

def getEpochTime(timestamp):
	py_timestamp = timestamp.to_datetime() 	#Convert to python datetime
	epoch_time = int( (py_timestamp-datetime.datetime(1970,1,1)).total_seconds()*1000 ) #Convert to epoch
	return epoch_time


def addSalesRecords(product):
	filename = "spreadsheets/" + product.name + "_sales.csv"
	row_count = getFileRows(filename)

	data = pd.read_csv(filename, parse_dates=['Date'], usecols = ['Date','Sales'],  nrows = row_count-1)
	for row in data.itertuples():
		try:
			sale_epoch = getEpochTime(row[1])
			product.addSales(Record(sale_epoch,row[2]))
		except:
			break

def addShipmentRecords(product):
	filename = "spreadsheets/" + product.name + "_shipments.csv"
	row_count = getFileRows(filename)

	data = pd.read_csv(filename, parse_dates=['Date','DOE'], usecols = ['Date','Quantity','DOE'],  nrows = row_count-1)
	for row in data.itertuples():
		try:
			shipment_epoch = getEpochTime(row[1])
			doe_epoch = getEpochTime(row[3])
			product.addShipment(ShipmentRecord(shipment_epoch,row[2],doe_epoch))
		except:
			break

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
		addShipmentRecords(item)
		#data = urllib.urlencode(item)
		print jsonpickle.encode(item)
		urllib2.urlopen(urllib2.Request(url=url, data=jsonpickle.encode(item), headers={'Content-Type':'application/json'})).read()

main()
