
package com.reactlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
//*** */
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
//*** */


import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxConfigurationBuilder;
import com.dft.onyxcamera.config.OnyxError;
import com.dft.onyxcamera.config.OnyxResult;
import com.dft.onyxcamera.ui.reticles.Reticle;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;


public class RNDftOnyxSdkWrapperModule extends ReactContextBaseJavaModule implements ActivityEventListener,ProviderInstaller.ProviderInstallListener{

  private final ReactApplicationContext reactContext;
    private static final int ONYX_REQUEST_CODE = 1337;

    private Activity activity;
    private AlertDialog alertDialog;

    MainApplication application = new MainApplication();

    private OnyxConfiguration.SuccessCallback successCallback;
    private OnyxConfiguration.ErrorCallback errorCallback;
    private OnyxConfiguration.OnyxCallback onyxCallback;

//     ProviderInstaller.installIfNeededAsync(this, this);


    public RNDftOnyxSdkWrapperModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

  }

  public static Callback callbackResult;

  @Override
  public String getName() {
    return "RNDftOnyxSdkWrapper";
  }

    @ReactMethod
    public static void getResData(ArrayList imgarray) {
        if (MainApplication.getOnyxResult() != null) {
            Log.d(" MainApplicationResult", String.valueOf(MainApplication.getOnyxResult()));
            Log.d(" imgarray", String.valueOf(imgarray.size()));

            OnyxResult res = MainApplication.getOnyxResult();

            WritableNativeArray map = new WritableNativeArray();
            for (int i = 0; i < imgarray.size(); i++) {
                String listItem = (String) imgarray.get(i);
                map.pushString(listItem);
            }
//            map.putArray("array", (ReadableArray) res.getWsqData());
//            map.pushArray((ReadableArray) imgarray);

            callbackResult.invoke(map);
        }
    }


    //*** */
    @ReactMethod
    public void sayHi(Callback errorCallback, Callback successCallback) {
        try {
            System.out.println("Greetings from Java");
            successCallback.invoke("Callback : Greetings from Java");
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }


    @ReactMethod
    void navigateToOnyx(Callback callBack) {
      callbackResult = callBack;
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, OnyxStartActivity.class);
            activity.startActivity(intent);
//            activity.startActivityForResult(intent,123);
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
            }
        });
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alertDialog.dismiss();
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

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Toast.makeText(activity, "testing...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
//
    private static final int ERROR_DIALOG_REQUEST_CODE = 11111;
    private boolean mRetryProviderInstall;


    @Override
    public void onProviderInstalled() {

    }

    @Override
    public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    getCurrentActivity(),
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



    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
        Log.i("OnyxSetupActivity","ProviderInstaller not available, device cannot make secure network calls.");
    }
    //*** */





//    @ReactMethod
//    public void setupOnyx(final Callback callback) {
//      this.callbackResult = callback;
//        successCallback = new OnyxConfiguration.SuccessCallback() {
//            @Override
//            public void onSuccess(OnyxResult onyxResult) {
//                Log.d("onyxSucccessss",onyxResult.toString());
//
//                callback.invoke("Successsss");
//
////                application.setOnyxResult(onyxResult);
////                finishActivityForRunningOnyx();
//            }
//        };
//        errorCallback = new OnyxConfiguration.ErrorCallback() {
//            @Override
//            public void onError(OnyxError onyxError) {
//                Log.e("OnyxError", onyxError.getErrorMessage());
//                callback.invoke("Errorrrrrr");
//
//
////                application.setOnyxError(onyxError);
////                showAlertDialog(onyxError);
////                finishActivityForRunningOnyx();
//            }
//        };
//        onyxCallback = new OnyxConfiguration.OnyxCallback() {
//            @Override
//            public void onConfigured(final Onyx configuredOnyx) {
//                //Log.d("configuredOnyx", configuredOnyx.toString());
//                callback.invoke("OnyxCallbackkkkk");
//                final Activity activity = getCurrentActivity();
//                if (activity != null){
//                    activity.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            MainApplication.setConfiguredOnyx(configuredOnyx);
//
//                            Intent i = new Intent(getCurrentActivity(), OnyxStartActivity.class);
//                            getCurrentActivity().startActivity(i);
//
////                            MainApplication.setOnyxResult(null);
////                            startActivityForResult(new Intent(getCurrentActivity(), OnyxActivity.class), ONYX_REQUEST_CODE);
//                        }
//                    });
//                }
//
//            }
//        };
//        // Create an OnyxConfigurationBuilder and configure it with desired options
//        OnyxConfigurationBuilder onyxConfigurationBuilder = new OnyxConfigurationBuilder()
//                .setActivity(getCurrentActivity())
//                    .setLicenseKey("7061-1932-2032-1-2")
//                    .setReturnRawImage(true)
//                    .setReturnProcessedImage(true)
//                    .setReturnEnhancedImage(true)
//                    .setReturnWSQ(false)
//                    .setReturnFingerprintTemplate(true)
//                    .setThresholdProcessedImage(false)
//                    .setShowLoadingSpinner(false)
//                    .setUseOnyxLive(true)
//                .setComputeNfiqMetrics(false)
//                    .setUseFlash(true)
//                .setImageRotation(0)
//                .setReticleOrientation( Reticle.Orientation.RIGHT)
//                .setCropSize(300.0,512.0)
//                .setCropFactor(1.0f)
//                .setTargetPixelsPerInch(-1.0)
//                    .setUseFourFingerReticle(true, false)
//                    .setUseRightHandLayout(true)
//                .setLayoutPreference(OnyxConfiguration.LayoutPreference.FULL)
//                    .setSuccessCallback(successCallback)
//                    .setErrorCallback(errorCallback)
//                    .setOnyxCallback(onyxCallback);
//
//        // Reticle Angle overrides Reticle Orientation so have to set this separately
////        if (valuesUtil.getReticleAngle(this) != null) {
////            onyxConfigurationBuilder.setReticleAngle(valuesUtil.getReticleAngle(this));
////        }
////        if (valuesUtil.getUseManualCapture(this)) {
////            onyxConfigurationBuilder.setUseManualCapture(true);
////        }
//            // Finally, build the OnyxConfiguration
//            onyxConfigurationBuilder.buildOnyxConfiguration();
//
////        Toast.makeText(reactContext, "Hello worlddd", Toast.LENGTH_SHORT).show();
////       callback.invoke("Alishaaa");
//    }
//
//
//



}