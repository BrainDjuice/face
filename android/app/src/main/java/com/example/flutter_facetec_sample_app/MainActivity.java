package com.example.flutter_facetec_sample_app;

import android.content.Context;

import androidx.annotation.NonNull;

import com.facetec.sdk.FaceTecCustomization;
import com.facetec.sdk.FaceTecFaceScanProcessor;
import com.facetec.sdk.FaceTecFaceScanResultCallback;
import com.facetec.sdk.FaceTecOverlayCustomization;
import com.facetec.sdk.FaceTecSDK;
import com.facetec.sdk.FaceTecSDKStatus;
import com.facetec.sdk.FaceTecSessionActivity;
import com.facetec.sdk.FaceTecSessionResult;

import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import kotlin.NotImplementedError;

public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "com.facetec.sdk";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        BinaryMessenger binaryMessenger = flutterEngine.getDartExecutor().getBinaryMessenger();
        new MethodChannel(binaryMessenger, CHANNEL).setMethodCallHandler(this::onMethodCall);
    }

    private void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        // This method is invoked on the main thread.
        if (Objects.equals(call.method, "initialize")) {
            String deviceKeyIdentifier = call.argument("deviceKeyIdentifier");
            String publicFaceScanEncryptionKey = call.argument("publicFaceScanEncryptionKey");
            initializeSDK(deviceKeyIdentifier, publicFaceScanEncryptionKey, result);
        }
        else if (Objects.equals(call.method, "startLiveness")) {
            startLiveness();
        }
        else {
            result.notImplemented();
        }
    }

    private void initializeSDK(String deviceKeyIdentifier, String publicFaceScanEncryptionKey, MethodChannel.Result result) {
        final Context context = this;

        FaceTecCustomization ftCustomization = new FaceTecCustomization();
        ftCustomization.getOverlayCustomization().brandingImage = R.drawable.flutter_logo;
        FaceTecSDK.setCustomization(ftCustomization);

        FaceTecSDK.initializeInDevelopmentMode(context, deviceKeyIdentifier, publicFaceScanEncryptionKey, new FaceTecSDK.InitializeCallback() {
            @Override
            public void onCompletion(boolean success) {
                if (success) {
                    result.success(true);
                }
                else {
                    FaceTecSDKStatus status = FaceTecSDK.getStatus(context);
                    result.error(status.name(), status.toString(), null);
                }
            }
        });
    }

    private void startLiveness() {
        FaceTecSessionActivity.createAndLaunchSession(this, new FaceTecFaceScanProcessor() {
            @Override
            public void processSessionWhileFaceTecSDKWaits(FaceTecSessionResult faceTecSessionResult, FaceTecFaceScanResultCallback faceTecFaceScanResultCallback) {
                // TODO: Add code to handle processing
                throw new NotImplementedError();
            }
        });
    }
}
