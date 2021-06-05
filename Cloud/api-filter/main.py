import requests
import json
import base64
from google.cloud import storage
import numpy as np
from keras.preprocessing import image
import keras.models
from keras.models import model_from_json


# create remote client to bucket
storage_client = storage.Client()
bucket = storage_client.get_bucket('model-bucket-nails')

# download model.json and model.h5 to local machine
blob = bucket.blob('model-2/model.json')
blob.download_to_filename('/tmp/model2.json')
blob2 = bucket.blob('model-2/model.h5')
blob2.download_to_filename('/tmp/model2.h5')

# get model from model.json
json_file = open('/tmp/model2.json', 'r')
model_json = json_file.read()
json_file.close()
model = model_from_json(model_json)

# load weights from model.h5 from bucket into new model
model.load_weights("/tmp/model2.h5")
print("Load weights into new model")

# function to predict
def filtering(request):
    if request.method == 'GET':
        return "Just try send POST request"
    elif request.method == 'POST':
        # get data from request
        img_data = request.get_json()
        print("Data image from request:")
        print(img_data)

        # get data keys
        for i in img_data.keys():
            filename = i
        print(filename)

        # encode utf-8 string to base64
        img_val = img_data[filename].encode('utf-8')
        print("Image value in Base64:")
        print(img_val)

        # decode base64 to bytes
        img_val_to_bytes = base64.decodebytes(img_val)
        print("Image value in bytes:")
        print(img_val_to_bytes)

        # change img_data values
        img_data[filename] = img_val_to_bytes
        print("Fixed Image Data")
        print(img_data)

        # create image from bytes
        with open('/tmp/'+filename, 'wb') as f:
            f.write(img_val_to_bytes)

        CATEGORIES = ['nails', 'notnails']

        # make prediction
        print("Make prediction:")
        path = '/tmp/'+filename
        img = image.load_img(path, target_size=(256, 256))
        img = np.array(img)
        img = img/255.
        xy = img
        xy = np.expand_dims(img, axis=0)
        
        images = np.vstack([xy])
        prediction = model.predict(images, batch_size=32)
        
        print(path)
        print(str(prediction))

        # check for nails or not
        res = "{} | {:2.0f}%".format(CATEGORIES[int(prediction[0][0])],
                             100*np.max(prediction))

        res = res.split(' | ')
        name = res[0]
        percent = res[1]

        print(name + " " + percent)

        data = {}
        
        # check if the data can pass to next api
        if name == "notnails":
          data['is_nail'] = False
          data['is_disease_match'] = False
          data['name'] = ""
          data['percent'] = ""
          data['desc'] = ""
          data['treat'] = ""

        elif name == "nails":
          print("next api")
          # convert img_data to base64 string
          img_data[filename] = base64.encodebytes(img_val_to_bytes).decode('utf-8')

          url = 'https://asia-southeast2-continual-block-312109.cloudfunctions.net/get-predict'
          s = requests.post(url, json=img_data)
          data = s.json()

        return data
