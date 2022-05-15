package com.neoastra.task;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Uri selectedImage=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        Switch sw=(Switch) findViewById(R.id.switchtheme);
        Button imagebtn=(Button) findViewById(R.id.imagebtn);
        Button savebtn=(Button) findViewById(R.id.savebtn);
        CardView crop=(CardView) findViewById(R.id.card1);
        CardView vertical=(CardView) findViewById(R.id.card2);
        CardView horizontal=(CardView) findViewById(R.id.card3);
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                sw.setChecked(true);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                sw.setChecked(false);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                sw.setChecked(false);
                break;
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) //Line A
            {
                if(isChecked)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        });

        savebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(Intent.createChooser(i, "Choose directory"), 3);
                    }
                } else{
                Toast.makeText(MainActivity.this, "Please pick the Image", Toast.LENGTH_SHORT).show();
            }
            }
        });

        imagebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        crop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage != null)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customview, viewGroup, false);
                    builder.setView(dialogView);
                    Button submit=(Button) dialogView.findViewById(R.id.buttonOk);
                    Button defaultbtn=(Button) dialogView.findViewById(R.id.buttonOk2);
                    EditText aspectx=(EditText) dialogView.findViewById(R.id.et1);
                    EditText aspecty=(EditText) dialogView.findViewById(R.id.et2);
                    EditText wifthx=(EditText) dialogView.findViewById(R.id.et3);
                    EditText heighty=(EditText) dialogView.findViewById(R.id.et4);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String ax=aspectx.getText().toString();
                            String ay=aspecty.getText().toString();
                            String wx=wifthx.getText().toString();
                            String wy=heighty.getText().toString();
                            if(ax.equals("") || ax.equals("0"))
                            {
                                aspectx.setError("please valid input");
                            }else  if(ay.equals("") || ay.equals("0"))
                            {
                                aspecty.setError("please valid input");
                            }else  if(wx.equals("") || wx.equals("0"))
                            {
                                wifthx.setError("please valid input");
                            }else  if(wy.equals("") || wy.equals("0"))
                            {
                                heighty.setError("please valid input");
                            }else{
                                alertDialog.dismiss();
                                Uri imgUri= selectedImage;

                                Intent cropIntent = new Intent("com.android.camera.action.CROP");

                                cropIntent.setDataAndType(imgUri, "image/*");
                                cropIntent.putExtra("crop", true);
                                cropIntent.putExtra("outputX", Integer.parseInt(wx));
                                cropIntent.putExtra("outputY", Integer.parseInt(wy));
                                cropIntent.putExtra("aspectX", Integer.parseInt(ax));
                                cropIntent.putExtra("aspectY", Integer.parseInt(ay));
                                cropIntent.putExtra("scaleUpIfNeeded", true);
                                cropIntent.putExtra("return-data", true);
                                startActivityForResult(cropIntent, 2);
                            }
                        }
                    });
                    defaultbtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                                 Uri imgUri= selectedImage;
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");

                    cropIntent.setDataAndType(imgUri, "image/*");
                    cropIntent.putExtra("crop", true);
                    cropIntent.putExtra("outputX", 360);
                    cropIntent.putExtra("outputY", 360);
                    cropIntent.putExtra("aspectX", 3);
                    cropIntent.putExtra("aspectY", 3);
                    cropIntent.putExtra("scaleUpIfNeeded", true);
                    cropIntent.putExtra("return-data", true);
                    startActivityForResult(cropIntent, 2);

                        }
                    });





                }else{
                    Toast.makeText(MainActivity.this, "Please pick the Image", Toast.LENGTH_SHORT).show();
                }


            }
        });
        vertical.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage != null)
                {
                    Bitmap bitmap2 = null;
                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        Bitmap bm= createFlippedBitmap(bitmap2, false,true);
                        ImageView image=(ImageView) findViewById(R.id.image);
                        image.setImageBitmap(bm);
                        selectedImage=getImageUri(getApplicationContext(),bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }else{
                    Toast.makeText(MainActivity.this, "Please pick the Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        horizontal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage != null)
                {
                    Bitmap bitmap2 = null;
                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                      Bitmap bm=  createFlippedBitmap(bitmap2, true,false);
                        ImageView image=(ImageView) findViewById(R.id.image);
                        image.setImageBitmap(bm);
                        selectedImage=getImageUri(getApplicationContext(),bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Please pick the Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{permission.WRITE_EXTERNAL_STORAGE},
                4);
    }

    public static Bitmap createFlippedBitmap(Bitmap source, boolean xFlip, boolean yFlip) {
        Matrix matrix = new Matrix();
        matrix.postScale(xFlip ? -1 : 1, yFlip ? -1 : 1, source.getWidth() / 2f, source.getHeight() / 2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ImageView image=(ImageView) findViewById(R.id.image);
                    image.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    ImageView image=(ImageView) findViewById(R.id.image);
                    image.setImageURI(selectedImage);
                    Uri uri=selectedImage; // the URI you've received from the other app
                    InputStream in = null;
                    try {
                        in = getContentResolver().openInputStream(uri);
                        ExifInterface exifInterface = new ExifInterface(in);
                        String lat=exifInterface.getAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE);
                        String lon=exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        String flash=exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                        String Size=exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)+" * "+exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                        String datetime=exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                        String textDisplay="";
                        if(!(Objects.equals(datetime, null)) )
                        {
                            textDisplay=datetime+" \n\t";
                        }
                        if(!(Objects.equals(lat, null)) && !(Objects.equals(lon, null))){
                            textDisplay=textDisplay+"Lat : "+lat+" Long : "+lon+" \n";
                        }
                        if(flash == "0") {
                            textDisplay=textDisplay+"Flash : No \n";
                        }else if(flash == "1" ){
                            textDisplay=textDisplay+"Flash : Yes \n";
                        }


                        textDisplay=textDisplay+"Resolution : "+Size+" \n";
                        TextView text =(TextView) findViewById(R.id.text);


                        text.setText(uri.getPath()+" \n"+textDisplay);
                        Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                      ItAndSave(textDisplay,bitmap2);

                    } catch (IOException | NullPointerException e) {
                        // Handle any errors
                       e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException ignored) {}
                        }
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    ImageView image=(ImageView) findViewById(R.id.image);
                    Bundle bundle = imageReturnedIntent.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");

                    image.setImageBitmap(bitmap);
                    //image.setImageURI(selectedImage);
                    Uri uri=getImageUri(this,bitmap); // the URI you've received from the other app
                    InputStream in = null;
                    try {
                        in = getContentResolver().openInputStream(uri);
                        ExifInterface exifInterface = new ExifInterface(in);
                        String lat=exifInterface.getAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE);
                        String lon=exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        String flash=exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                        String Size=exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)+" * "+exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                        String textDisplay=exifInterface.getAttribute(ExifInterface.TAG_DATETIME)+" \n\t";
                        if(!(Objects.equals(lat, null)) && !(Objects.equals(lon, null))){
                            textDisplay=textDisplay+"Lat : "+lat+" Long : "+lon+" \n";
                        }
                        if(flash == "0") {
                            textDisplay=textDisplay+"Flash : No \n";
                        }else if(flash == "1" ){
                            textDisplay=textDisplay+"Flash : Yes \n";
                        }


                        textDisplay=textDisplay+"Resolution : "+Size+" \n";
                        TextView text =(TextView) findViewById(R.id.text);


                        text.setText(uri.getPath()+" \n"+textDisplay);
                        Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                      ItAndSave(textDisplay,bitmap2);

                    } catch (IOException | NullPointerException e) {
                        // Handle any errors
                       e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException ignored) {}
                        }
                    }
                }
                break;
        case 3:
                Log.i("Test", "Result URI " + imageReturnedIntent.getData().getPath());
            try {
                OutputStream fOut = null;
            Integer counter = 0;
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                Uri treeUri = imageReturnedIntent.getData();
                //String sPath = treeUri.getPath();

               // Log.d("","Chosen path = "+ sPath);
                Uri docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri,
                        DocumentsContract.getTreeDocumentId(treeUri));

                String sPath = getPath(this, docUri);
                Log.d("","Chosen path = "+ sPath);
            File file = new File(sPath, "NeoAstra"+ts+".png"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            fOut = new FileOutputStream(file);

            Bitmap pictureBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage); // obtaining the Bitmap
            pictureBitmap.compress(CompressFormat.PNG, 100, fOut);
            fOut.flush(); // Not really required

                fOut.close(); // do not forget to close the stream

           // MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            break;
        }
    }

    @SuppressLint("ResourceAsColor")
    public void ItAndSave(String text, Bitmap toEdit){
       Bitmap dest= toEdit.copy(Bitmap.Config.ARGB_8888, true);

        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(80f);
        tPaint.setColor(Color.RED);
        tPaint.setStyle(Style.FILL);
        tPaint.setTextAlign(Paint.Align.RIGHT);
        tPaint.setColor(Color.WHITE);// text shadow
        tPaint.setShadowLayer(100f, 50f, 50f, Color.WHITE);
        tPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        float height = dest.getHeight()-100;
       // cs.drawColor(R.color.transparentBlack);
        for (String line: text.split("\n")) {
            cs.drawText(line, dest.getWidth(), height, tPaint);
            height += tPaint.descent() - 200;
        }
        //cs.drawText(text, dest.getWidth(), dest.getHeight()-100, tPaint);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dest.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ImageView picView2 = (ImageView)findViewById(R.id.image);
        picView2.setImageBitmap(dest);
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}