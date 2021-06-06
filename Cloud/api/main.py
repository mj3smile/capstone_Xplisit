from flask import request, jsonify
import json
import base64
from google.cloud import storage
import numpy as np
from keras.preprocessing import image
import keras.models
from keras.models import model_from_json
import utils
from werkzeug.utils import secure_filename

storage_client = storage.Client()
bucket = storage_client.get_bucket('model-bucket-nails')

blob = bucket.blob('model-1/model.json')
blob.download_to_filename('/tmp/model1.json')
blob2 = bucket.blob('model-1/model.h5')
blob2.download_to_filename('/tmp/model1.h5')

json_file = open('/tmp/model1.json', 'r')
model_json = json_file.read()
json_file.close()
model = model_from_json(model_json)
model.load_weights("/tmp/model1.h5")

def index(request):
    data = {}

    if request.method == 'GET':
        data['message'] = 'you are not allowed to access this resource by this request method'
        return jsonify(data), 405
    elif request.method == 'POST':
        img_data = request.files.get('image')

        if img_data:
            img_filename = secure_filename(img_data.filename)
            img_data.save('/tmp/' + img_filename)

            CATEGORIES = ['nails', 'notnails']
            path = '/tmp/' + img_filename
            img = image.load_img(path, target_size=(256, 256))
            img = np.array(img)
            img = img/255.
            xy = img
            xy = np.expand_dims(img, axis=0)
            
            images = np.vstack([xy])
            prediction = model.predict(images, batch_size=32)
            
            # check for nails or not
            res = "{} | {:2.0f}%".format(CATEGORIES[int(prediction[0][0])],
                                100*np.max(prediction))

            res = res.split(' | ')
            name = res[0]
            percent = res[1]
            
            if name == "notnails":
                data['is_nail'] = False
                data['is_disease_match'] = False
                data['name'] = ""
                data['percent'] = ""
                data['desc'] = ""
                data['treat'] = ""

            elif name == "nails":
                data = utils.get_prediction(path)

        else:
            data['message'] = 'please attach image file in your request'
        
        return jsonify(data)
