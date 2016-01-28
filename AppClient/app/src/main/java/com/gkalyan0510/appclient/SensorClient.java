package com.gkalyan0510.appclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.w3c.dom.Text;

public class SensorClient extends Activity {
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    boolean getprox(int a,int v){
        if(a<v+2&&a>v-2)
            return true;
        else return false;
    }
    String getposnformcoords(int x,int y,int z){
        if(getprox(x,10)&&getprox(y,0)&&getprox(z,0))
            return "Landscape";
        else if(getprox(x,0)&&getprox(y,10)&&getprox(z,0))
            return "Potrait";
        else if(getprox(x,0)&&getprox(y,0)&&getprox(z,10))
            return "flat";
        else
            return "not determined";

    }
    int mod(int a){
        if(a<0)
            return -a;
        else return a;
    }
    String getorientn(int x,int y,int z){
        if(mod(x)>3&&getprox(y,0)&&mod(z)<9)
            return "landscape mode";
        if(mod(y)>3&&getprox(x,0)&&mod(z)<9)
            return "potrait mode";
        else
            return "not determined";
    }
    class Data implements Serializable{
        String posture;
        int x,y,z;
        Data(String s,float X,float Y,float Z){
            posture=s;
            x=(int)X; y=(int)Y; z=(int)Z;
        }

    }
    private final SensorEventListener mSensorListener = new SensorEventListener() {

        float X,Y,Z;
        public void onSensorChanged(SensorEvent event) {

            try {


                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                X=event.values[0]; Y=event.values[1]; Z=event.values[2];
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                Gson gson = new Gson();
                String json = gson.toJson(new Data(getorientn((int) X, (int) Y, (int) Z),X,Y,Z));
               // String json = ow.writeValueAsString(new Data(getposnformcoords((int)X,(int)Y,(int)Z),X,Y,Z));

                ((TextView)findViewById(R.id.json)).setText(json);
                json = json.replace('\n','☺');      // replacing new lines with (char)1 (alt+1) to make the string in single line
                out.println(json + "");             // to avoid reading multiple line on Servers side
                xc.setText(event.values[0]+"");
                yc.setText(event.values[1]+"");
                zc.setText(event.values[2]+"");

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private Socket socket;
    TextView xc,yc,zc;
    private static final int SERVERPORT = 6000;
    private static  String SERVER_IP = "127.0.0.1";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sensor_client);
        xc= (TextView)findViewById(R.id.xc);
        yc= (TextView)findViewById(R.id.yc);
        zc= (TextView)findViewById(R.id.zc);
        Button but = (Button)findViewById(R.id.setip);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SERVER_IP = ((TextView) findViewById(R.id.ipip)).getText().toString().trim();
                new opensocket().execute();
            }
        });
        //new Thread(new ClientThread()).start();
        //Toast.makeText(getApplicationContext(),"Thread Started",Toast.LENGTH_LONG).show();
        new opensocket().execute();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 1000000);
    }



    class opensocket extends AsyncTask<Void,String,String >{                 // Asunchronous task for making conection with server
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Toast.makeText(getApplicationContext(),"Starting asynctask",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String p;
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    publishProgress("Stuck after getting ");
                socket = new Socket(serverAddr, SERVERPORT);
                // Toast.makeText(getApplicationContext(),"Initialised sOCKET",Toast.LENGTH_LONG).show();
                //System.out.println("<----------------------Initialised sOCKET-------------------------->");
                p="Connection Successful";

            } catch (UnknownHostException e1) {
                // Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();e1.printStackTrace();
                //System.out.println("<---------------------Error 1------------------------->");
                //
                p="Error connecting Please provide correct IP";
                // p="First-> "+e1.getMessage()+"<-->"+e1.toString();
            } catch (IOException e1) {
                //  Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();e1.printStackTrace();
                //   System.out.println("<----------------------Error 2-------------------------->");
                p="Error connecting Please provide correct IP";
               // p="Second-> "+e1.getMessage()+"<-->"+e1.toString();
            }
            return p;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            Toast.makeText(getApplicationContext(),str+" ",Toast.LENGTH_LONG).show();

        }
    }





}