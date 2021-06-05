import json
import base64
from google.cloud import storage
import numpy as np
import cv2
from keras.preprocessing import image
from keras.preprocessing.image import img_to_array
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

#Function to convert images to array
def convert_image_to_array(img_path):
    try:
        image = cv2.imread(img_path)
        default_image_size = tuple((299, 299))
        if image is not None :
            image = cv2.resize(image, default_image_size)
            return img_to_array(image)
        else :
            return np.array([])
    except Exception as e:
        print("Error : {e}")
        return None


# Function to predict
def get_prediction(img_path):
    CATEGORIES = ["aloperia areata", "beau's lines", "bluish nail", "clubbing", "darier's disease", "eczema", "koilonychia", "leukonychia", "lindsay's nails", "muehrck-e's lines", "normal", "onycholycis", "pale nail", "red lunula", "splinter hemmorrage", "terry's nail", "white nail", "yellow nails"]

    # make prediction
    path = img_path
    im=convert_image_to_array(path)
    np_image_li = np.array(im, dtype=np.float16) / 299
    npp_image = np.expand_dims(np_image_li, axis=0)

    prediction=model.predict(npp_image)
    itemindex = np.where(prediction==np.max(prediction))

    # get disease name and percentage
    res = "{} | {:2.0f}%".format(CATEGORIES[itemindex[1][0]], 100*np.max(prediction))

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