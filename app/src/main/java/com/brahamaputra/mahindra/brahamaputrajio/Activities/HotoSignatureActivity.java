package com.brahamaputra.mahindra.brahamaputrajio.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brahamaputra.mahindra.brahamaputrajio.Data.HotoDigitalSignatureData;
import com.brahamaputra.mahindra.brahamaputrajio.Data.HotoTransactionData;
import com.brahamaputra.mahindra.brahamaputrajio.Data.MediaData;
import com.brahamaputra.mahindra.brahamaputrajio.R;
import com.brahamaputra.mahindra.brahamaputrajio.SignaturePad.SignaturePad;
import com.brahamaputra.mahindra.brahamaputrajio.Utils.SessionManager;
import com.brahamaputra.mahindra.brahamaputrajio.baseclass.BaseActivity;
import com.brahamaputra.mahindra.brahamaputrajio.commons.GlobalMethods;
import com.brahamaputra.mahindra.brahamaputrajio.commons.OfflineStorageWrapper;
import com.brahamaputra.mahindra.brahamaputrajio.helper.OnSpinnerItemClick;
import com.brahamaputra.mahindra.brahamaputrajio.helper.SearchableSpinnerDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class HotoSignatureActivity extends BaseActivity {

    //Link of Reference:https://github.com/gcacace/android-signaturepad

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePadOfHandOver;
    private Button mClearButtonOfHandOver;
    private SignaturePad mSignaturePadOfTakeOver;
    private Button mClearButtonOfTakeOver;
    private Button mSaveButton;


    //final Calendar myCalendar = Calendar.getInstance();
    //String str_typeOfMedia;

    //private TextView mMediaTextViewTypeofmedia;
    //private TextView mMediaTextViewTypeofmediaVal;

    private OfflineStorageWrapper offlineStorageWrapper;
    private String userId = "";
    private String ticketId = "";
    private String ticketName = "";
    private HotoTransactionData hotoTransactionData;
    private HotoDigitalSignatureData hotoDigitalSignatureData;
    private SessionManager sessionManager;

    private String base64SignatureHandOver = "";
    private String base64SignatureTakeOver = "";

    private boolean HandOver = false, TakeOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(HotoSignatureActivity.this);
        setContentView(R.layout.activity_hoto_signature);
        this.setTitle("Digital Signature");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(HotoSignatureActivity.this);
        ticketId = sessionManager.getSessionUserTicketId();
        ticketName = GlobalMethods.replaceAllSpecialCharAtUnderscore(sessionManager.getSessionUserTicketName());
        userId = sessionManager.getSessionUserId();
        offlineStorageWrapper = OfflineStorageWrapper.getInstance(HotoSignatureActivity.this, userId, ticketName);
        hotoTransactionData = new HotoTransactionData();

        mSignaturePadOfHandOver = (SignaturePad) findViewById(R.id.signature_padOfHandOver);
        mSignaturePadOfTakeOver = (SignaturePad) findViewById(R.id.signature_padOfTakeOver);
        mSignaturePadOfHandOver.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(HotoSignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButtonOfHandOver.setVisibility(View.VISIBLE);
                HandOver = true;
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButtonOfHandOver.setVisibility(View.GONE);
            }
        });

        mSignaturePadOfTakeOver.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(HotoSignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButtonOfTakeOver.setVisibility(View.VISIBLE);
                TakeOver = true;
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButtonOfTakeOver.setVisibility(View.GONE);
            }
        });


        mClearButtonOfHandOver = (Button) findViewById(R.id.clear_buttonOfHandOver);
        mClearButtonOfTakeOver = (Button) findViewById(R.id.clear_buttonOfTakeOver);
        mSaveButton = (Button) findViewById(R.id.btn_saveSignature);


        mClearButtonOfHandOver.setAllCaps(false);
        mClearButtonOfTakeOver.setAllCaps(false);
        mSaveButton.setAllCaps(false);

        mClearButtonOfHandOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePadOfHandOver.clear();
            }
        });

        mClearButtonOfTakeOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePadOfTakeOver.clear();
            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Bitmap signatureBitmap = mSignaturePadOfHandOver.getSignatureBitmap();
                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(HotoSignatureActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HotoSignatureActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                if (addSvgSignatureToGallery(mSignaturePadOfHandOver.getSignatureSvg())) {
                    Toast.makeText(HotoSignatureActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HotoSignatureActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        setInputDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dropdown_details_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //startActivity(new Intent(this, HotoSectionsListActivity.class));
                return true;
            case R.id.menuDone:
                if (checkValiadtion()) {
                    submitDetails();
                    setResult(RESULT_OK);
                    finish();
                    //startActivity(new Intent(this, Battery_Set.class));
                }

                //setResult(RESULT_OK);
                //finish();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkValiadtion() {
        if (HandOver == true) {
            Bitmap signatureHandOverBitmap = mSignaturePadOfHandOver.getSignatureBitmap();
            ByteArrayOutputStream streamSignatureHandOver = new ByteArrayOutputStream();
            signatureHandOverBitmap.compress(Bitmap.CompressFormat.JPEG, 70, streamSignatureHandOver);
            byte[] bitmapSignatureHandOverDataArray = streamSignatureHandOver.toByteArray();
            base64SignatureHandOver = Base64.encodeToString(bitmapSignatureHandOverDataArray, Base64.DEFAULT);
        }

        if (TakeOver == true) {
            Bitmap signatureTakeOverBitmap = mSignaturePadOfHandOver.getSignatureBitmap();
            ByteArrayOutputStream streamSignatureTakeOver = new ByteArrayOutputStream();
            signatureTakeOverBitmap.compress(Bitmap.CompressFormat.JPEG, 70, streamSignatureTakeOver);
            byte[] bitmapSignatureTakeOverDataArray = streamSignatureTakeOver.toByteArray();
            base64SignatureTakeOver = Base64.encodeToString(bitmapSignatureTakeOverDataArray, Base64.DEFAULT);
        }
        if (base64SignatureHandOver.isEmpty() || base64SignatureHandOver == null) {
            showToast("Signature of Person who handing Over is mandatory field. ");
            return false;
        } else if (base64SignatureHandOver.isEmpty() || base64SignatureHandOver == null) {
            showToast("Signature of Person who taking Over is mandatory field. ");
            return false;
        }
        return true;
    }

    private void setInputDetails() {
        try {
            if (offlineStorageWrapper.checkOfflineFileIsAvailable(ticketName + ".txt")) {
                String jsonInString = (String) offlineStorageWrapper.getObjectFromFile(ticketName + ".txt");

                Gson gson = new Gson();

                hotoTransactionData = gson.fromJson(jsonInString, HotoTransactionData.class);
                hotoDigitalSignatureData = hotoTransactionData.getHotoDigitalSignatureData();

                base64SignatureHandOver = hotoDigitalSignatureData.getBase64StringHandingOverPersonSignature();
                if (!base64SignatureHandOver.isEmpty() && base64SignatureHandOver != null) {
                    mClearButtonOfHandOver.setVisibility(View.VISIBLE);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    Bitmap inImage = decodeFromBase64ToBitmap(base64SignatureHandOver);
                    mSignaturePadOfHandOver.setSignatureBitmap(inImage);
                    HandOver = true;
                    //inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
                    //String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
                    //imageFileUriFilterCleanedBeforePhoto = Uri.parse(path);
                }

                base64SignatureTakeOver = hotoDigitalSignatureData.getBase64StringTakingOverPersonSignature();
                if (!base64SignatureTakeOver.isEmpty() && base64SignatureTakeOver != null) {
                    mClearButtonOfTakeOver.setVisibility(View.VISIBLE);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    Bitmap inImage = decodeFromBase64ToBitmap(base64SignatureTakeOver);
                    mSignaturePadOfTakeOver.setSignatureBitmap(inImage);
                    TakeOver = true;
                    //inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
                    //String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
                    //imageFileUriFilterCleanedBeforePhoto = Uri.parse(path);
                }


            } else {
                showToast("No previous saved data available");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitDetails() {
        try {


            hotoDigitalSignatureData = new HotoDigitalSignatureData(base64SignatureHandOver, base64SignatureTakeOver);

            hotoTransactionData.setHotoDigitalSignatureData(hotoDigitalSignatureData);

            Gson gson2 = new GsonBuilder().create();
            String jsonString = gson2.toJson(hotoTransactionData);

            offlineStorageWrapper.saveObjectToFile(ticketName + ".txt", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ///////////////////
    //////////////////
    /////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(HotoSignatureActivity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        HotoSignatureActivity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
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

}
