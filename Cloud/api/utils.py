import json
import base64
from google.cloud import storage
import numpy as np
from keras.preprocessing import image
import keras.models
from keras.models import model_from_json

storage_client = storage.Client()
bucket = storage_client.get_bucket('model-bucket-nails')

blob = bucket.blob('model-2/model.json')
blob.download_to_filename('/tmp/model2.json')
blob2 = bucket.blob('model-2/model.h5')
blob2.download_to_filename('/tmp/model2.h5')

json_file = open('/tmp/model2.json', 'r')
model_json = json_file.read()
json_file.close()
model = model_from_json(model_json)
model.load_weights("/tmp/model2.h5")

def get_prediction(img_path):
    CATEGORIES = ["aloperia areata", "beau's lines", "bluish nail", "clubbing", "darier's disease", "eczema", "koilonychia", "leukonychia", "lindsay's nails", "muehrck-e's lines", "normal", "onycholycis", "pale nail", "red lunula", "splinter hemmorrage", "terry's nail", "white nail", "yellow nails"]

    # make prediction
    path = img_path
    img = image.load_img(path, target_size=(150, 150))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)

    images = np.vstack([x])
    prediction = model.predict(images, batch_size=10)

    # get disease name and percentage
    res = "{} | {:2.0f}%".format(CATEGORIES[np.argmax(prediction)], 100*np.max(prediction))

    res = res.split(' | ')
    name = res[0]
    percent = res[1]

    data = {}
    
    # check for unhealthy nails
    if name != 'normal':
        # get the details from bucket
        blob3 = bucket.blob('data/'+ name + '.txt')
        details = blob3.download_as_string()
        details = details.decode('utf-8')
        details = details.split(';;')

        data['is_nail'] = True
        data['is_disease_match'] = True
        data['name'] = details[0]
        data['accuracy'] = percent
        data['desc'] = details[1]
        data['treat'] = details[2]
    
    # check for healthy nails
    elif name == 'normal':
        data['is_nail'] = True
        data['is_disease_match'] = False
        data['name'] = ""
        data['accuracy'] = ""
        data['desc'] = ""
        data['treat'] = ""

    return data