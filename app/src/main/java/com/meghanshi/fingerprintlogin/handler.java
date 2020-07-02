package com.meghanshi.fingerprintlogin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.M)
public class handler extends FingerprintManager.AuthenticationCallback {

    Context context;
    String currentDateTimeString;

    public handler(Context context)
    {
        this.context = context;
    }

        public void StartAuth (FingerprintManager fingerprintManager,FingerprintManager.CryptoObject cryptoObject)
        {
            CancellationSignal cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject,cancellationSignal ,0,this,null);

        }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        try {
            this.update("There is an error. "+ errString , false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        try {
            this.update("Auth fail . ",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void update(String s, boolean b) throws JSONException {
        TextView textView = (TextView)((Activity)context).findViewById(R.id.textView2);
         currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        textView.setText(currentDateTimeString);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(currentDateTimeString);

    }

    class BackgroundTask extends AsyncTask<String,Void,String>
    {
        String add_url;
        @Override
        protected void onPreExecute() {
            add_url = "http://192.168.1.13/Webs/Insert.php";
             }

        @Override
        protected String doInBackground(String... args) {
            String date = args[0];

            try {
                URL url = new URL(add_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data_string = URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8");
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
                return "Present";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context,result,Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        try {
            this.update("Error :"+helpString,false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        try {
            this.update("welcome",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}



