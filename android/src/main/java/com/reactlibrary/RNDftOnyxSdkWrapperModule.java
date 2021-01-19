
package com.reactlibrary;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
//*** */
import com.facebook.react.uimanager.IllegalViewOperationException;
//*** */

public class RNDftOnyxSdkWrapperModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNDftOnyxSdkWrapperModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDftOnyxSdkWrapper";
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
    void navigateToOnyx() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, OnyxSetupActivity.class);
            activity.startActivity(intent);
        }
    }
  //*** */
    
}