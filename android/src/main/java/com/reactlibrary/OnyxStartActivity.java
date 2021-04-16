package com.reactlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxConfigurationBuilder;
import com.dft.onyxcamera.config.OnyxError;
import com.dft.onyxcamera.config.OnyxResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

import com.facebook.react.bridge.Callback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class OnyxStartActivity extends Activity implements ProviderInstaller.ProviderInstallListener {
//    private static final String TAG = OnyxSetupActivity.class.getName();
    private static final int ONYX_REQUEST_CODE = 1337;
    MainApplication application = new MainApplication();
    private Activity activity;
    private ImageView fingerprintView;
    private Button startOnyxButton;
    private AlertDialog alertDialog;
    private TextView livenessResultTextView;
    private TextView nfiqScoreTextView;
    private ProgressBar pbLoader;

    ValuesUtil valuesUtil = new ValuesUtil();

    private OnyxConfiguration.SuccessCallback successCallback;
    private OnyxConfiguration.ErrorCallback errorCallback;
    private OnyxConfiguration.OnyxCallback onyxCallback;
    FileUtil fileUtil;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProviderInstaller.installIfNeededAsync(this, this); // This is needed in order for SSL to work on Android 5.1 devices and lower
        fileUtil = new FileUtil();
        // This is for file writing permission on SDK >= 23
        // setupUI();
        activity = this;
        setContentView(R.layout.activity_start);
        fingerprintView = new ImageView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(fingerprintView, layoutParams);
        livenessResultTextView = findViewById(R.id.livenessResult);
        nfiqScoreTextView = findViewById(R.id.nfiqScore);
        startOnyxButton = findViewById(R.id.start_onyx);
        pbLoader = findViewById(R.id.progressBar2);

        setupCallbacks();

        setupOnyx();
        if(activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
//            MainApplication.setOnyxResult(null);
//            startActivityForResult(new Intent(activity, OnyxActivity.class), ONYX_REQUEST_CODE);
        }
        else{
            fileUtil.getWriteExternalStoragePermission(this);
        }
    }

    private void setupCallbacks() {
        successCallback = new OnyxConfiguration.SuccessCallback() {
            @Override
            public void onSuccess(OnyxResult onyxResult) {
                Log.d("onSuccess", onyxResult.toString());
                application.setOnyxResult(onyxResult);
                finishActivityForRunningOnyx();
                ArrayList<String> base64Image=new ArrayList<>();
//                for (int image=0;image<onyxResult.getProcessedFingerprintImages().size();image++){
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    Bitmap bitmap=onyxResult.getProcessedFingerprintImages().get(image);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                    byte[] byteArray = byteArrayOutputStream .toByteArray();
//                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                    base64Image.add(encoded);
//                }

                for (int image=0;image<onyxResult.getWsqData().size();image++){
                    String encoded = Base64.encodeToString(onyxResult.getWsqData().get(image), Base64.DEFAULT);
                    base64Image.add(encoded);
                }

                RNDftOnyxSdkWrapperModule.getResData(base64Image);
                finish();
            }
        };

        errorCallback = new OnyxConfiguration.ErrorCallback() {
            @Override
            public void onError(OnyxError onyxError) {
                Log.e("OnyxError", onyxError.getErrorMessage());
//                Log.e("onyxError.errorMessage", onyxError.errorMessage);
                application.setOnyxError(onyxError);
                Toast.makeText(activity, onyxError.errorMessage, Toast.LENGTH_SHORT).show();
//                showAlertDialog(onyxError);
                finishActivityForRunningOnyx();
            }
        };

        onyxCallback = new OnyxConfiguration.OnyxCallback() {
            @Override
            public void onConfigured(Onyx configuredOnyx) {
                Log.d("onConfigured", configuredOnyx.toString()  );
                application.setConfiguredOnyx(configuredOnyx);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startOnyxButton.setEnabled(true);
                        pbLoader.setVisibility(View.GONE);
                        MainApplication.setOnyxResult(null);
                        startActivityForResult(new Intent(activity, OnyxActivity.class), ONYX_REQUEST_CODE);
                        finish();
                    }
                });
            }
        };
    }

    private void finishActivityForRunningOnyx() {
        if (MainApplication.getActivityForRunningOnyx() != null) {
            MainApplication.getActivityForRunningOnyx().finish();
            Log.d("RunningOnyx", "rRunningOnyx"  );

        }
    }

    private void setupOnyx() {
        // Create an OnyxConfigurationBuilder and configure it with desired options
        OnyxConfigurationBuilder onyxConfigurationBuilder = new OnyxConfigurationBuilder()
                .setActivity(activity)
                .setLicenseKey(getResources().getString(R.string.onyx_license))
                .setReturnRawImage(valuesUtil.getReturnRawImage(this))
                .setReturnProcessedImage(valuesUtil.getReturnProcessedImage(this))
                .setReturnEnhancedImage(valuesUtil.getReturnEnhancedImage(this))
                .setReturnWSQ(valuesUtil.getReturnWSQ(this))
                .setReturnFingerprintTemplate(
                        valuesUtil.getReturnFingerprintTemplate(this)
                )
                // .setThresholdProcessedImage(valuesUtil.getThresholdImage(this))
                .setShowLoadingSpinner(valuesUtil.getShowLoadingSpinner(this))
                .setUseOnyxLive(valuesUtil.getUseOnyxLive(this))
                // .setComputeNfiqMetrics(valuesUtil.getComputeNfiqMetrics(this))
                .setUseFlash(valuesUtil.getUseFlash(this))
                .setImageRotation(valuesUtil.getImageRotation(this))
                .setReticleOrientation(valuesUtil.getReticleOrientation(this))
                .setCropSize(valuesUtil.getCropSizeWidth(this), valuesUtil.getCropSizeHeight(this))
                .setCropFactor(valuesUtil.getCropFactor(this))
                // .setTargetPixelsPerInch(valuesUtil.getTargetPixelsPerInch(this))
                // .setUseFourFingerReticle(true, false)
                // .setUseRightHandLayout(valuesUtil.getUseRightHandLayout(this))
                // .setLayoutPreference(OnyxConfiguration.LayoutPreference.FULL)
                .setSuccessCallback(successCallback)
                .setErrorCallback(errorCallback)
                .setOnyxCallback(onyxCallback);
        // Reticle Angle overrides Reticle Orientation so have to set this separately
        // if (valuesUtil.getReticleAngle(this) != null) {
        //     onyxConfigurationBuilder.setReticleAngle(valuesUtil.getReticleAngle(this));
        // }
        // if (valuesUtil.getUseManualCapture(this)) {
        //     onyxConfigurationBuilder.setUseManualCapture(true);
        // }
        // Finally, build the OnyxConfiguration
        onyxConfigurationBuilder.buildOnyxConfiguration();
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
//        if (MainApplication.getOnyxResult() != null) {
//            Log.d("onsuccess", MainApplication.getOnyxResult().toString()  );
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//            Log.v(TAG,"Permission: "+permissions[0]+ " was " + grantResults[0]);
            pbLoader.setVisibility(View.VISIBLE);
            pbLoader.bringToFront();
        }
    }



    /**
     * This displays an AlertDialog upon receiving an OnyxError, please handle appropriately for
     * your application
     * @param onyxError
     */
    private void showAlertDialog(OnyxError onyxError) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle("Onyx Error");
        alertDialogBuilder.setMessage(onyxError.getErrorMessage());
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                finish();
            }
        });
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alertDialog.dismiss();
                finish();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    /**
     * The below is for updating the device's security provider to protect against SSL exploits
     * See https://developer.android.com/training/articles/security-gms-provider#java
     */
    private static final int ERROR_DIALOG_REQUEST_CODE = 11111;
    private boolean mRetryProviderInstall;

    /**
     * This method is only called if the provider is successfully updated
     * (or is already up-to-date).
     */
    @Override
    public void onProviderInstalled() {
        Log.i("OnyxSetupActivity","Provider is up-to-date, app can make secure network calls.");
    }

    /**
     * This method is called if updating fails; the error code indicates
     * whether the error is recoverable.
     */
    @Override
    public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    this,
                    errorCode,
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
        Log.i("OnyxSetupActivity","ProviderInstaller not available, device cannot make secure network calls.");
    }
}
