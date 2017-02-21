from products import *
import pandas as pd
import numpy as np
from datetime import date
def main():
	p1 = Product("Apples","Fruits",5)
	p1.printDetails()
	data = pd.read_csv('Bread.csv', parse_dates=['Date'], usecols = ['Date','Sales'])
	for row in data.itertuples():
	 	p1.addSalesRecord(Record(row[1],row[2]))
	p1.printDetails()

main()