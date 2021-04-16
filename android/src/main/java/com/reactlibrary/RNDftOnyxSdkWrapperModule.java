
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

public class RNDftOnyxSdkWrapperModule extends ReactContextBaseJavaModule
{

  private final ReactApplicationContext reactContext;
    private static final int ONYX_REQUEST_CODE = 1337;
    private Activity activity;
    private AlertDialog alertDialog;

    MainApplication application = new MainApplication();

    private OnyxConfiguration.SuccessCallback successCallback;
    private OnyxConfiguration.ErrorCallback errorCallback;
    private OnyxConfiguration.OnyxCallback onyxCallback;

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
//            Log.d(" imgarray", String.valueOf(imgarray.size()));
            OnyxResult res = MainApplication.getOnyxResult();

            WritableNativeArray map = new WritableNativeArray();
            for (int i = 0; i < imgarray.size(); i++) {
                String listItem = (String) imgarray.get(i);
                map.pushString(listItem);
            }
            callbackResult.invoke(map);
        }
    }

    @ReactMethod
    void navigateToOnyx(Callback callBack) {
      callbackResult = callBack;
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, OnyxStartActivity.class);
            activity.startActivity(intent);
        }
    }

}