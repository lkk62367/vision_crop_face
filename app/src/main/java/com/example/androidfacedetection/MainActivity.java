package com.example.androidfacedetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int RQS_LOADIMAGE = 1;
    private Button btnLoad, btnDetFace;
    private ImageView imgView;
    private Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad = (Button)findViewById(R.id.btnLoad);
        btnDetFace = (Button)findViewById(R.id.btnDetectFace);
        imgView = (ImageView)findViewById(R.id.imgview);

        btnLoad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, RQS_LOADIMAGE);
            }
        });

        btnDetFace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(myBitmap == null){
                    Toast.makeText(MainActivity.this,
                            "myBitmap == null",
                            Toast.LENGTH_LONG).show();
                }else{
                    detectFace();
                    Toast.makeText(MainActivity.this,
                            "Done",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_LOADIMAGE
                && resultCode == RESULT_OK){

            if(myBitmap != null){
                myBitmap.recycle();
            }

            try {
                InputStream inputStream =
                        getContentResolver().openInputStream(data.getData());
                myBitmap = BitmapFactory.decodeStream(inputStream);

                inputStream.close();
                imgView.setImageBitmap(myBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    reference:
    https://search-codelabs.appspot.com/codelabs/face-detection
     */
    public void detectFace(){

        //Create a Paint object for drawing with
/*        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.GREEN);
        myRectPaint.setStyle(Paint.Style.STROKE);*/

        //Create a Canvas object for drawing on
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Detect the Faces
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).build();

        //!!!
        //Cannot resolve method setTrackingEnabled(boolean)
        //skip for now
        //faceDetector.setTrackingEnabled(false);

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        int nx1 = 0;
        int ny1 = 0;
        int nw1 = 0;
        int nh1 = 0;
        //Draw Rectangles on the Faces
        for(int i=0; i<faces.size(); i++) {
            Log.e("William" , "faces.size()" + faces.size());
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            float w1 = thisFace.getWidth();
            float h1 = thisFace.getHeight();
            nx1 = (int)x1;
            ny1 = (int)y1;
            nw1 = (int)w1;
            nh1 = (int)h1;
            //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 10, 10, myRectPaint);
        }

        Bitmap te = Bitmap.createBitmap(myBitmap, nx1, ny1, nw1, nh1);

        imgView.setImageBitmap(te);


    }
}
