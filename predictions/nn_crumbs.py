from flask import Flask, jsonify, request #import objects from the Flask model
app = Flask(__name__) #define app using Flask

import numpy as np
import pandas as pd
import datetime

from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error

import numpy as np
import pandas as pd

@app.route('/predict', methods=['POST'])
def predict():
	# get sales
	sales = request.json['sales']
	m = len(sales)
	# instantiate random dates
	dates = []
	dummy_date = datetime.datetime(2015,12,26)
	for i in range(m):
		dummy_date += datetime.timedelta(days=1)
		dates.append(dummy_date)

	# arrange data in a dataframe
	data = pd.DataFrame(data=sales,index=dates)
	data.columns = ['Sales']
	dataset = data["Sales"] 

	# log transformation
	dataset_scaled = np.log(dataset)

	# normalize the dataset
	scaler = MinMaxScaler(feature_range=(0, 1))
	dataset_scaled = scaler.fit_transform(dataset_scaled)

	# split into train and test sets
	train_size = int(len(dataset_scaled) * 0.67)
	test_size = len(dataset_scaled) - train_size
	train, test = dataset_scaled[0:train_size], dataset_scaled[train_size:len(dataset_scaled)]

	# create dataset by the amount of lookback
	look_back = 7
	trainX, trainY = create_dataset(train, look_back)
	testX, testY = create_dataset(test, look_back)

	# reshape input to be [samples, time steps, features]
	trainX = np.reshape(trainX, (trainX.shape[0], 1, trainX.shape[1]))
	testX = np.reshape(testX, (testX.shape[0], 1, testX.shape[1]))

	# create and fit the LSTM network
	model = Sequential()
	model.add(LSTM(4, input_dim=look_back))
	model.add(Dense(1))
	model.compile(loss='mean_squared_error', optimizer='adam')
	model.fit(trainX, trainY, nb_epoch=10, batch_size=1, verbose=2)

	# generate predictions for next k days
	last_n = np.array(dataset_scaled[len(dataset_scaled)-look_back:])
	pred_horizon = 8
	predictions = predictNextK(last_n,pred_horizon,model)
	predictions = np.exp(scaler.inverse_transform(predictions))
	predictions = [int(i) for i in predictions]

	return jsonify({ 'predictions' : predictions })


# convert an array of values into a dataset matrix
def create_dataset(dataset, look_back=1):
    dataX, dataY = [], []
    for i in range(len(dataset)-look_back):
        a = dataset[i:(i+look_back)]
        dataX.append(a)
        dataY.append(dataset[i + look_back])
    return np.array(dataX), np.array(dataY)

# generate predictions for the next k days
def predictNextK(seed,k,model):
    predictions = np.empty(k)
    for i in range(k):
        seed = np.reshape(seed,(1,1,k))
        nextPeriod = model.predict(seed)
        seed = np.append(seed,nextPeriod)
        seed = np.delete(seed, 0, axis=0)
        predictions[i] = nextPeriod
    return predictions

if __name__ == '__main__':
	app.run(debug=True, port=5000) #run app on port 5000 in debug mode