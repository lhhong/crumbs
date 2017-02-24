from Tx import *
def main():
	# Populate a list of transactions
	transactions = []
	# (seller, price, item, quantity, expiry, txDate, sell, accepter)
	tx_names = [[ "NTUC Bedok Mall", 1500.00, "Parmesan Cheese", 180, 28-02-17, 24-02-17, 1, "Giant Tampines Mall" ],
				[ "Giant Tampines Mall", 550.00, "Onion", 500, 27-02-17, 24-02-17, 1, "NTUC Bedok Mall" ],
				[ "NTUC Bedok Mall", 400.00, "Sardines", 430, 01-03-17, 24-07-16, 1, "Cold Storage Katong V" ],
				[ "Giant Tampines Mall", 600.00, "Marigold Milk", 350, 27-02-17, 24-02-17, 1, "Cold Storage Katong V" ],
				[ "Cold Storage Katong V", 250.00, "Carrot", 200, 26-02-17, 24-02-17, 1, "Lee Minimart Katong V" ],
				[ "NTUC Bedok Mall", 420.00, "Corn", 300, 02-03-17, 24-02-17, 1, "Giant Tampines Mall" ],
				[ "NTUC Bedok Mall", 10.00, "Carrot", 300, 26-02-17, 24-02-17, 1, "Bedok Givers Charity" ],
				[ "NTUC Bedok Mall", 1300.00, "Parmesan Cheese ", 150, 03-03-17, NULL, 1, NULL ],
				[ "Giant Tampines Mall", 550.00, "Onion", 500, 02-03-17, NULL, 1, NULL],
				[ "Lee Minimart Katong V", 350.00, "Marigold Milk", 200, NULL, NULL, 0, NULL ],
				]