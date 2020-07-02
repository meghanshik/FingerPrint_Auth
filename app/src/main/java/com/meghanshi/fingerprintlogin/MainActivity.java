package com.meghanshi.fingerprintlogin;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;
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

    public class MainActivity extends AppCompatActivity implements FingerPrintAuthCallback {

        TextView textView;
        ImageView imageView;
        FingerPrintAuthHelper mFingerPrintAuthHelper;
        String currentDateTimeString;
        String MAC;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            textView = (TextView) this.<View>findViewById(R.id.textView2);
            imageView = findViewById(R.id.imageView);
            String wifiname = "\"KANCHAN\"";
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wifiMgr != null;
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String name = wifiInfo.getSSID();
            boolean trueFalse = wifiname.equals(name);
        if(!trueFalse)
        {
            textView.setText("Please Connect your phone to KANCHAN wifi");
            Toast.makeText(getApplicationContext(),"Connect to 'WIFI that one must connect to login' for Login",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
            mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        Toast.makeText(this, "Android version is lower:)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoFingerPrintRegistered() {

    }

    @Override
    public void onBelowMarshmallow() {
        Toast.makeText(this, "Upgrade your android version!", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("HardwareIds")
    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.ic_done);
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        TextView textView = (TextView)findViewById(R.id. textView2);
        textView.setText("Done.");
        MAC = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(currentDateTimeString,MAC);

    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                Toast.makeText(this, "Can't recognize you", Toast.LENGTH_SHORT).show();

                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //This is not recoverable error. Try other options for user authentication. like pin, password.
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                //Any recoverable error. Display message to the user.
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("StaticFieldLeak")
    class BackgroundTask extends AsyncTask<String,Void,String>
    {
        String add_url;
        @Override
        protected void onPreExecute() {
            add_url = "https://bhargjoshi.000webhostapp.com/Insert.php";

        }

        @Override
        protected String doInBackground(String... args) {
            String date = args[0];
            String MAC = args[1];

            try {
                URL url = new URL(add_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data_string = URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8")+"&"+
                        URLEncoder.encode("MAC","UTF-8")+"="+URLEncoder.encode(MAC,"UTF-8");
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
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            }
        }






}
