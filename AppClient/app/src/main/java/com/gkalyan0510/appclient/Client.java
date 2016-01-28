package com.gkalyan0510.appclient;

import android.content.Intent;
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

public class Client extends Activity {

    private Socket socket;

    private static final int SERVERPORT = 6000;
    private static  String SERVER_IP = "127.0.0.1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //new Thread(new ClientThread()).start();
        //Toast.makeText(getApplicationContext(),"Thread Started",Toast.LENGTH_LONG).show();
        new opensocket().execute();
        Button but = (Button)findViewById(R.id.openconn);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SERVER_IP = ((TextView) findViewById(R.id.ip)).getText().toString().trim();
                new opensocket().execute();
            }
        });
        Button slp = (Button)findViewById(R.id.slp);
        slp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Client.this, SensorClient.class);

                startActivity(intent);
            }
        });
    }
    class Data implements Serializable {
        String posture;
        int x,y,z;
        Data(String s,float X,float Y,float Z){
            posture=s;
            x=(int)X; y=(int)Y; z=(int)Z;
        }

    }
    public void onClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.EditText01);
            String str = et.getText().toString();
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            Gson gson = new Gson();
            str = gson.toJson(new Data(str,0,0,0));
            // String json = ow.writeValueAsString(new Data(getposnformcoords((int)X,(int)Y,(int)Z),X,Y,Z));

//            ((TextView)findViewById(R.id.json)).setText(str);
            str = str.replace('\n','☺');
            out.println(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {



        }

    }

    class opensocket extends AsyncTask<Void,String,String >{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String p;
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
               // publishProgress("Stuck after getting ");
                socket = new Socket(serverAddr, SERVERPORT);
               // Toast.makeText(getApplicationContext(),"Initialised sOCKET",Toast.LENGTH_LONG).show();
                //System.out.println("<----------------------Initialised sOCKET-------------------------->");
                p="connection successful";

            } catch (UnknownHostException e1) {
               // Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();e1.printStackTrace();
                //System.out.println("<---------------------Error 1------------------------->");
                p="Error connecting Please provide correct IP";//+e1.getMessage()+"<-->"+e1.toString();
            } catch (IOException e1) {
              //  Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();e1.printStackTrace();
             //   System.out.println("<----------------------Error 2-------------------------->");
                p="Error connecting Please provide correct IP";
                //p="Second-> "+e1.getMessage()+"<-->"+e1.toString();
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