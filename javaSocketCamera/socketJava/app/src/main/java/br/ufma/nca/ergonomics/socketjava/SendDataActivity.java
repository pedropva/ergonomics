package br.ufma.nca.ergonomics.socketjava;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;

public class SendDataActivity extends AppCompatActivity {
    ImageView image;
    TextView response;
    Button buttonCapture;
    String ServerAddress = "192.168.200.71";
    String ServerPort = "30000";
    Integer fileCount = 0;
    int TAKE_PHOTO_CODE = 0;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public static void verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CAMERA
            );
        }
    }

    private static final String TAG = SendDataActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        //File directory  = getFilesDir();
        verifyStoragePermissions(this);
        verifyCameraPermissions(this);
        final String dir = "/sdcard/picFolder/";//directory.getPath()+"/picFolder";
        Log.d("CameraDemo", dir);
        /*
        File imageDir = new File(dir);
        if (imageDir.exists()){
            Log.d("CameraDemo", "Dir exists");
        }else {
            Log.d("CameraDemo", "Dir doesnt exist");
        }
        File newdir = new File(dir);
        newdir.mkdirs();
        */
        File mediaStorageDir = new File(dir);

        if (!mediaStorageDir.exists()) {
            Log.d("CameraDemo", "Dir doesnt exist");
            if (!mediaStorageDir.mkdirs()) {
                Log.e("App", "failed to create directory");
            }
        }else{
            Log.d("CameraDemo", "Dir exists");
        }
        fileCount = (mediaStorageDir.list().length-1);



        buttonCapture = (Button) findViewById(R.id.btnCapture);
        response = (TextView) findViewById(R.id.responseTextView);
        image = (ImageView) findViewById(R.id.imageView);


        buttonCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                String file = dir+fileCount+".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                    Log.e("CameraDemo", e.getMessage());
                }

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });

        /*
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                response.setText("");
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            byte[] imgbyte = new byte[0];
            String filepath =  "/sdcard/picFolder/"+fileCount+".jpg";
            Log.d("CameraDemo", filepath);
            image.setImageBitmap(BitmapFactory.decodeFile(filepath));
            File imagefile = new File(filepath);
            if (imagefile.exists()){
                Log.d("CameraDemo", "File exists");
            }else {
                Log.d("CameraDemo", "File doesnt exist");
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                imgbyte = new byte[fis.available()];
                fis.read(imgbyte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Bitmap bm = BitmapFactory.decodeStream(fis);
            //imgbyte = getBytesFromBitmap(bm);

            Log.d("CameraDemo", "Pic saved");
            Log.d("CameraDemo", "Tamanho do buffer enviado:" + Integer.toString(imgbyte.length));
            if(imgbyte.length>0){
                Client myClient = new Client(ServerAddress
                        , Integer.parseInt(ServerPort)
                        , response
                        , imgbyte);
                myClient.execute();
            }
        }
    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}

