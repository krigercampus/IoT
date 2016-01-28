package com.gkalyan0510.appserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gkalyan0510.appserver.R;
import com.google.gson.Gson;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class Server extends Activity {

    private ServerSocket serverSocket;
    boolean canupdate;
    Handler updateConversationHandler;
    String messg="";
    Thread serverThread = null;

    private TextView text;

    public static final int SERVERPORT = 6000;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text2);

        updateConversationHandler = new Handler();

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canupdate = !canupdate;
                Toast.makeText(getApplicationContext(),messg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class Data{
        String posture;
        float x,y,z;
        Data(String s,float X,float Y,float Z){
            posture=s;
            x=X; y=Y; z=Z;
        }

    }
    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    System.out.println("ServerRunning" + serverSocket.getInetAddress() + "lloc->" + serverSocket.getLocalSocketAddress() + "*************************");
                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.print("ServerRunning" + serverSocket.toString() + "*************************");
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                System.out.print("comm thread running"+serverSocket.toString()+"*************************");
               /* try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                try {

                    String read = input.readLine();//+input.readLine()+input.readLine()+input.readLine()+input.readLine()+input.readLine();
                    read = read.replace('☺','\n');

                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            //ObjectMapper mapper = new ObjectMapper();
                messg = msg;
            text.setText(msg);

                Data dt;// = mapper.readValue(msg,Data.class);
               /* ((TextView)findViewById(R.id.post)).setText(dt.posture+"");
                ((TextView)findViewById(R.id.xx)).setText(dt.x+"");
                ((TextView)findViewById(R.id.yy)).setText(dt.y+"");
                ((TextView)findViewById(R.id.zz)).setText(dt.z+"");*/
                //JSONObject json = (JSONObject)new JSONParser().parse("{\"name\":\"MyNode\", \"width\":200, \"height\":100}");

                Gson gson = new Gson();
                dt = gson.fromJson(msg,Data.class);
                ((TextView)findViewById(R.id.post)).setText(dt.posture + "");
                ((TextView)findViewById(R.id.xx)).setText(dt.x+"");
                ((TextView)findViewById(R.id.yy)).setText(dt.y + "");
                ((TextView)findViewById(R.id.zz)).setText(dt.z+"");

            // dt.getPosture()+"\n\n"+dt.getX()+"\n"+dt.getY()+"\n"+dt.getZ() );
                //System.out.print(msg);

        }
    }
}