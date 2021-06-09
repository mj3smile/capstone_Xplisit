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
      Cloud Build API, Cloud Functions API, Cloud Storage, Cloud Scheduler API
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
      <li>For model 1 <code>on ML/model/model - deteksi kuku atau bukan/</code>
        <blockquote>Make sure you store the model.json and model.h5</blockquote>
      </li>
      <li>For model 2 <code>on ML/model/model/</code>
        <blockquote>Make sure you store the model.json and model.h5</blockquote>
      </li>
    </ol>
  </li>
</ol>
<h4>Cloud Functions</h4>
<ol type="1">
    <li>On Cloud Functions, click <code>CREATE FUNCTION</code></li>
    <li>Give name of the function</li>
    <li>Specify the location</li>
    <blockquote>For latency, choose the nearest location to where you want the Functions will be called </blockquote>
    <li>Choose the trigger type</li>
    <blockquote>For this project, choose <code>HTTP</code></blockquote>
    <li>For the <code>Authentication</code>, select <code>Allow unauthenticated invocations</code></li>
    <li>Check the <code>Require HTTPS</code> checkbox</li>
    <li>Open the <code>RUNTIME, BUILD AND CONNECTION SETTINGS</code></li>
    <li>For the <code>Memory allocated</code>, choose the one that suits your needs</li>
    <li>Click <code>NEXT</code></li>
    <li>For the <code>Runtime</code>, choose <code>Python 3.7</code></li>
    <li>For the <code>Source code</code>, choose <code>Inline Editor</code></li>
    <li>For the <code>main.py</code>, copy the code on <code>Cloud/api/main.py</code></li>
    <li>For the <code>requirements.txt</code>, copy the library or packages needed on <code>Cloud/api/requirements.txt</code></li>
    <li>For the <code>utils.py</code>, copy the code on <code>Cloud/api/utils.py</code></li>
    <li>Last but not least, for the <code>Entry point</code>, fill it with the function on the main.py that will be run by Cloud Functions</li>
    <blockquote>Based on code on main.py, you should fill the Entry point with <code>"index"</code> -> without <code>""</code></blockquote>
    <li>Click <code>DEPLOY</code></li>
    <li>Wait until the Functions is ready to use</li>
    <blockquote>If there is a green checkmark beside the Functions name, then the Functions is successfully deployed</blockquote>
</ol>
<h4>Cloud Scheduler</h4>
<ol type="1">
  <li>Click <code>CREATE JOB</code></li>
  <li>Give name of the job</li>
  <li>Add description if necessary</li>
  <li>For <code>Frequency</code>, fill it with <code>"*/5 * * * *"</code> -> without <code>""</code>
    <blockquote>This will run the job every 5 minutes, this is necessary because Cloud Function has a cold start which means if the Functions is not used for a certain period of time, it will take time to make the Functions run.<br> For more information, you can check link below:<br>https://mikhail.io/serverless/coldstarts/big3/#:~:text=Instances%20of%20cloud%20functions%20are%20added%20and%20removed,in%20Serverless%20Functions.%20When%20Does%20Cold%20Start%20Happen%3F</blockquote>
  </li>
  <li>For <code>Timezone</code>, it's up to you or you can adjust it with your timezone</li>
  <li>For <code>Target type</code>, choose <code>HTTP</code></li>
  <li>For <code>URL</code>, fill it with the url link on your Cloud Functions-><code>TRIGGER</code> tab</li>
  <li>For <code>HTTP method</code>, choose <code>POST</code></li>
  <li>For <code>HTTP headers</code>, click <code>ADD A HEADER</code></li>
  <li>For the <code>Name</code> and <code>Value</code>, fill it with <code>User-Agent</code> and <code>Google-Cloud-Scheduler</code></li>
  <li>For <code>Max retry attempts</code>, choose <code>1</code></li>
  <li>Click <code>CREATE</code></li>
  <li>Wait until the <code>State</code> is <code>Enabled</code></li>
</ol>
<h4>How the Android app call the API</h4>
<ul>
  <li>Open your Cloud Functions
  <li>On the <code>TRIGGER</code> tab, you will see a url link. For example:</li>
  <blockquote>https://asia-southeast2-continual-block-87495.cloudfunctions.net/YOUR_FUNCTION_NAME</blockquote>
  <li>Use the url link for the code on Android app</li>
</ul>

<!-- buat ML -->
<h2>Machine Learning</h2>

