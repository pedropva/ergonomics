package br.ufma.nca.ergonomics.socketjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.FloatBuffer;

public class SendDataActivity extends AppCompatActivity {
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button enviar = (Button) findViewById(R.id.btUpload);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Example of a call to a native method
                TextView tv = (TextView) findViewById(R.id.reply_server);
                tv.setText("iai");

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        setContentView(R.layout.activity_send_data);
        TextView tv = (TextView) findViewById(R.id.reply_server);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
}
*/
    TextView response;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    String ServerAddress = "192.168.200.94";
    String ServerPort = "30000";
    FloatBuffer testCloud =null;
    Integer nPoints=500000;
    Integer buffSize=nPoints*4;
    private static final String TAG = SendDataActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        response = (TextView) findViewById(R.id.responseTextView);
        testCloud = FloatBuffer.allocate(buffSize);
        fillTestCloud(testCloud);

        buttonConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "Primeiros 5 pontos:" +  printNFirstPoints(testCloud,5));
                Log.i(TAG, "Ultimos 5 pontos:" +  print5LastPoints(testCloud,nPoints));
                byte[] output = float2Byte(testCloud);
                Client myClient = new Client(ServerAddress
                        , Integer.parseInt(ServerPort)
                        , response
                        , output
                        ,buffSize*4
                        ,nPoints
                        , print5LastPoints(testCloud,nPoints));
                myClient.execute();
            }
        });


        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                response.setText("");
            }
        });
        /*
        response.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(!(s.equals("Failed transmitting the message") || s.equals("Success transmitting the message"))) {
                    response.setText(verifyDataSent(s.toString(), print5LastPoints(testCloud, 7500)));
                }
            }
        });
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
    private void fillTestCloud(FloatBuffer pointCloudBuffer) {
        float aux=0.0f;
        for (int i = 0; i < pointCloudBuffer.capacity(); i = i + 1) {
            pointCloudBuffer.put(aux);
            aux += 1.0f;
        }

    }
    public static final byte[] float2Byte(FloatBuffer inData) {
        int j = 0;
        int length = inData.capacity();
        int dataLength = 3000+inData.capacity()-inData.capacity()%3000;
        byte[] outData = new byte[dataLength * 4];
        for (int i = 0; i < length; i++) {
            int d = Float.floatToIntBits(inData.get(i));
            outData[j++] = (byte) (d >>> 24);
            outData[j++] = (byte) (d >>> 16);
            outData[j++] = (byte) (d >>> 8);
            outData[j++] = (byte) (d >>> 0);
        }
        return outData;
    }
    private String print5LastPoints(FloatBuffer pointCloudBuffer, int numPoints) {
        String pointsString = "";
        if (numPoints != 0) {
            int numFloats = 4 * numPoints;
            for (int i = numFloats-20; i < numFloats; i++) {
                pointsString +=" " + pointCloudBuffer.get(i);
            }
        }else{
            pointsString="Não deu pra recuperar a nuvem de pontos :(";
        }
        return pointsString;
    }
    private String printNFirstPoints(FloatBuffer pointCloudBuffer, int numPoints) {
        String pointsString = "";
        if (numPoints != 0) {
            int numFloats = 4 * numPoints;
            for (int i = 0; i < numFloats; i = i + 1) {
                pointsString +=" " + pointCloudBuffer.get(i);
            }
        }else{
            pointsString="Não deu pra recuperar a nuvem de pontos :(";
        }
        return pointsString;
    }

}

