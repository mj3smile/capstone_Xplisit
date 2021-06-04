<h1>CAPSTONE PROJECT / B21-CAP0339</h1>
<h3>Xplicit Team</h3>
<hr>
<!-- buat Android -->
<h2>Android</h2>



<!-- buat Cloud -->
<h2>Cloud</h2>
<h4>Setup and Requirements</h4>
<ol>
  <li>Create a GCP project</li>
  <li>Connect the project to your billing account</li>
  <li>Enable necessary APIs and Services. 
    <code>
      Cloud Build API, Cloud Functions API, Cloud Storage
    </code>
  </li>
</ol>
<h4>Cloud Storage</h4>
<ol>
  <li>Create the bucket</li>
  <li>Create 3 folder on the bucket
    <ol type="a">
      <li>For disease data <code>on Cloud/data</code>
      </li>
      <li>For model 1 <code>on Cloud/model-filter/</code>
        <blockquote>Make sure you store the model.json and model.h5</blockquote>
      </li>
      <li>For model 2 <code>on Cloud/model-predict</code>
        <blockquote>Make sure you store the model.json and model.h5</blockquote>
      </li>
    </ol>
  </li>
</ol>
<h4>Cloud Functions</h4>
We will create 2 API with each model
<ul>
  <li><h5>Create Functions to connect with first model</h5>
    <blockquote>This Functions will check whether the image is nail or not based on the model prediction result</blockquote>
    <ol type="1">
      <li>On Cloud Functions, click <code>CREATE FUNCTION</code></li>
      <li>Give name of the function</li>
      <li>Specify the location</li>
      <blockquote>For latency, choose the nearest location to where you want the function will be called </blockquote>
      <li>Choose the trigger type</li>
      <blockquote>For this project, choose <code>HTTP</code></blockquote>
      <li>For the <code>Authentication</code>, select <code>Allow unauthenticated invocations</code></li>
      <li>Open the <code>RUNTIME, BUILD AND CONNECTION SETTINGS</code></li>
      <li>For the <code>Memory allocated</code>, choose the one that suits your needs</li>
      <li>Click <code>NEXT</code></li>
      <li>For the <code>Runtime</code>, choose <code>Python 3.7</code></li>
      <li>For the <code>Source code</code>, choose <code>Inline Editor</code></li>
      <li>For the <code>main.py</code>, copy the code on <code>Cloud/api-filter/main.py</code></li>
      <li>For the <code>requirements.txt</code>, copy the library or packages needed on <code>Cloud/api-filter/requirements.txt</code></li>
      <li>Last but not least, for the <code>Entry point</code>, fill it with the function on the code that will be run by Cloud Functions</li>
      <blockquote>Based on code on main.py, you should fill the Entry point with <code>"filtering"</code> -> without <code>""</code></blockquote>
      <li>Click <code>DEPLOY</code></li>
      <li>Wait until the Functions is ready to use</li>
      <blockquote>If there is a green checkmark beside the Functions name, then the Functions is successfully deployed</blockquote>
    </ol>
  </li>
  <li><h5>Create Functions to connect with second model</h5>
    <blockquote>This Functions will make a prediction of the image based on the model</blockquote>
    <ol type="1">
      <li>Just follow the steps for create the first Functions</li>
      <li>For the <code>main.py</code>, copy the code on <code>Cloud/api-predict/main.py</code></li>
      <li>For the <code>requirements.txt</code>, copy the library or packages needed on <code>Cloud/api-predict/requirements.txt</code></li>
      <li>For the <code>Entry point</code>, fill it with the function on the code that will be run by Cloud Functions</li>
      <blockquote>Based on code on main.py, you should fill the Entry point with <code>"make_prediction"</code> -> without <code>""</code></blockquote>
    </ol>
  </li>
</ul>
<h4>How the Android app call the API</h4>
<ul>
  <li>Open your Cloud Functions that connect to <code>first model</code>
  <li>On the <code>TRIGGER</code> tab, you will see a url link. For example:</li>
  <blockquote>https://asia-southeast2-continual-block-87495.cloudfunctions.net/YOUR_FUNCTION_NAME</blockquote>
  <li>Use the url link for the code on Android app</li>
</ul>

<!-- buat ML -->
<h2>Machine Learning</h2>

