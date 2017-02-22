from flask import Flask, jsonify, request #import objects from the Flask model
app = Flask(__name__) #define app using Flask

import pandas as pd
import numpy as np
import datetime

from statsmodels.tsa.stattools import adfuller
from statsmodels.tsa.arima_model import ARIMA

@app.route('/predict', methods=['POST'])
def predict():
	# Get dates and sales
	sales = request.json['sales']
	m = len(sales)
	# Instantiate random dates
	dates = []
	dummy_date = datetime.datetime(2015,12,26)
	for i in range(m):
		dummy_date += datetime.timedelta(days=1)
		dates.append(dummy_date)

	# Arrange them in a dataframe
	data = pd.DataFrame(data=sales,index=dates)
	data.columns = ['Sales']
	ts = data["Sales"]


	#test_stationarity(ts)
	ts_log = np.log(ts)
	ts_log_diff = ts_log - ts_log.shift(7)                 #Shift by a season 7 days
	ts_log_diff.dropna(inplace=True)
	#test_stationarity(ts_log_diff)

	# Obtain optimal p,q values for ARIMA model
	p,q = grid(ts_log,ts)

	# Predict 7 days in advance
	model = ARIMA(ts_log, order=(p, 1, q))
	results_ARIMA = model.fit(disp=-1)
	m = len(ts_log)
	prediction_len = 8
	predict_diff = results_ARIMA.predict(start = m, end= m+prediction_len-1, dynamic= True)
	diff_cumsum = np.array(predict_diff.cumsum())
	base = np.ones(prediction_len) * ts_log.ix[m-1]
	log_predictions = base + diff_cumsum
	real_predictions = np.exp(log_predictions)              #Scale predictions back to normal

	return jsonify({'predictions' : real_predictions.tolist()})

def test_stationarity(timeseries):
	#Determing rolling statistics
	rolmean = pd.rolling_mean(timeseries, window=12)
	rolstd = pd.rolling_std(timeseries, window=12)

	#Perform Dickey-Fuller test:
	#print('Results of Dickey-Fuller Test:')
	dftest = adfuller(timeseries, autolag='AIC')
	dfoutput = pd.Series(dftest[0:4], index=['Test Statistic','p-value','#Lags Used','Number of Observations Used'])
	for key,value in dftest[4].items():
		dfoutput['Critical Value (%s)'%key] = value
	#print(dfoutput)

# Returns the mse of a particular arima model
def fitARIMA(p,q,y,realdata):
	import math
	from statsmodels.tsa.arima_model import ARIMA
	m = len(realdata)
	start_index = int(math.floor(len(realdata)*0.95)) #start predicting from this point
	model = ARIMA(y[:start_index], order=(p, 1, q))
	results_ARIMA = model.fit(disp=-1)

	#predict out of sample
	z = results_ARIMA.predict(start = start_index, end= len(realdata), dynamic= True)
	diff_cumsum = z.cumsum()
	d = np.array(diff_cumsum)
	base = np.ones(len(d)) * y.ix[start_index-1]
	log_predictions = base + d
	real_predictions = np.exp(log_predictions)
	return np.sqrt(sum((real_predictions-realdata[start_index-1:])**2)/len(real_predictions))

# Finds the p and q values that result in the lowest validation mse
def grid(y,realdata):
	p_values = [0,1,2,3,4]
	q_values = [0,1]
	cv_results = []
	min_cv = 50000000
	for p in p_values:
		for q in q_values:
			result = fitARIMA(p,q,y,realdata)
			#print result
			cv_results.append(result)
			if result < min_cv:
				min_cv = result
				min_p = p
				min_q = q
	return min_p,min_q

if __name__ == '__main__':
	app.run(debug=True, port=5000) #run app on port 5000 in debug mode

