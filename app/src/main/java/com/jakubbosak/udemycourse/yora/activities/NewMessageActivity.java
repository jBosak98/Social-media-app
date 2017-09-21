package com.jakubbosak.udemycourse.yora.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.services.entities.Message;
import com.jakubbosak.udemycourse.yora.services.entities.UserDetails;
import com.jakubbosak.udemycourse.yora.views.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class NewMessageActivity extends BaseAunthenticatedActivity implements View.OnClickListener, Camera.PictureCallback {
    private static final int REQUEST_SEND_MESSAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA/*, Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE*/};
    private static int PHOTOS = 1;




    private static final String TAG = "NewMessageActivity";
    private static final String STATE_CAMERA_INDEX = "STATE_CAMERA_INDEX";

    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";

    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private int currentCameraIndex;
    private CameraPreview cameraPreview;

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        }
    }


    @Override
    protected void onYoraCreate(Bundle savedState) {
        verifyStoragePermissions();
        setContentView(R.layout.activity_new_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(savedState != null){
            currentCameraIndex = savedState.getInt(STATE_CAMERA_INDEX);
        } else {
            currentCameraIndex = 0;
        }

        cameraPreview = new CameraPreview(this);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_new_message_frame);
        frameLayout.addView(cameraPreview, 0);

        findViewById(R.id.activity_new_message_switchCamera).setOnClickListener(this);
        findViewById(R.id.activity_new_message_takePicture).setOnClickListener(this);

    }



    @Override
    public void onResume(){
        super.onResume();
        establishCamera();

    }

    @Override
    public void onPause(){
        super.onPause();

        if(camera != null){
            //camera.stopPreview();
            cameraPreview.setCamera(null, null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(STATE_CAMERA_INDEX, currentCameraIndex);
    }

    private void establishCamera() {
        if (camera != null){
            cameraPreview.setCamera(null, null);
            camera.release();
            camera = null;
        }

        try {
            camera = Camera.open(currentCameraIndex);

        } catch (Exception e){
            Log.e(TAG, "Could not open camera" + currentCameraIndex, e);
            Toast.makeText(this, "Error establishing camera!", Toast.LENGTH_LONG).show();
            return;
        }
        cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraIndex, cameraInfo);
        cameraPreview.setCamera(camera, cameraInfo);

        if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
            Toast.makeText(this,"Using back camera", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Using front camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.activity_new_message_takePicture){
            takePicture();
        } else if(id == R.id.activity_new_message_switchCamera){
            switchCamera();
        }
    }

    private void switchCamera() {
       /* if(camera != null){
            cameraPreview.setCamera(null,null);
            camera.release();
        }*/
        currentCameraIndex = currentCameraIndex +1 < Camera.getNumberOfCameras() ? currentCameraIndex +1 : 0;
        establishCamera();
    }

    private void takePicture() {
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmap = processBitmap(data);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);


        File outputFile = new File(getCacheDir(), "temp-image" + "("+ PHOTOS + ")");
        getCacheDir().deleteOnExit();
        PHOTOS++;
        try {
            FileOutputStream fileOutput = new FileOutputStream(outputFile);
            fileOutput.write(output.toByteArray());
            fileOutput.close();
        } catch (Exception e){
            Log.e(TAG, "Could not save bitmap", e);
            Toast.makeText(this, "Could not save image to temp directory", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(SendMessageActivity.EXTRA_IMAGE_PATH, Uri.fromFile(outputFile));


        UserDetails user = getIntent().getParcelableExtra(EXTRA_CONTACT);
        if (user != null) {
             intent.putExtra(SendMessageActivity.EXTRA_CONTACT, user);
        }
        startActivityForResult(intent, REQUEST_SEND_MESSAGE);
        bitmap.recycle();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Bitmap processBitmap(byte[] data) {
        Bitmap bitmap =  BitmapFactory.decodeByteArray(data, 0, data.length);
        if(bitmap.getHeight() > SendMessageActivity.MAX_IMAGE_HEIGHT){
            float scale = (float) SendMessageActivity.MAX_IMAGE_HEIGHT / bitmap.getWidth();
            int finalWidth = (int) (bitmap.getHeight() * scale);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, SendMessageActivity.MAX_IMAGE_HEIGHT, finalWidth, false);

            if(resizedBitmap != bitmap) {
                bitmap.recycle();
                bitmap = resizedBitmap;
            }
        }

        Matrix matrix = new Matrix();
        if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            Matrix matrixMirror = new Matrix();
            matrixMirror.setValues(new float[] {
                -1, 0, 0,
                0, 1, 0,
                0, 0, 1});
            matrix.postConcat(matrixMirror);
        }

        matrix.postRotate(90);
        Bitmap processedBitmap = Bitmap.createBitmap(bitmap, 0 ,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if(bitmap != processedBitmap) {
            bitmap.recycle();
        }
        return processedBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == REQUEST_SEND_MESSAGE && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();

            Message message = data.getParcelableExtra(SendMessageActivity.RESULT_MESSAGE);

            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }
}
