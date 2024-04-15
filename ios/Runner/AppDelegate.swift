import UIKit
import Flutter
import FaceTecSDK

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate, FaceTecFaceScanProcessorDelegate {
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        
        let controller: FlutterViewController = window?.rootViewController as! FlutterViewController;
        
        let faceTecSDKChannel = FlutterMethodChannel(name: "com.facetec.sdk", binaryMessenger: controller.binaryMessenger)
        
        faceTecSDKChannel.setMethodCallHandler(onFaceTecSDKMethodCall(call:result:))
        GeneratedPluginRegistrant.register(with: self)
        
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    private func onFaceTecSDKMethodCall(call: FlutterMethodCall, result: @escaping FlutterResult) -> Void {
        switch(call.method) {
        case "initialize":
            guard let args = call.arguments as? Dictionary<String, Any>,
                  let deviceKeyIdentifier = args["deviceKeyIdentifier"] as? String,
                  let faceScanEncryptionKey = args["publicFaceScanEncryptionKey"] as? String
            else {
                return result(FlutterError())
            }
            return initialize(deviceKeyIdentifier: deviceKeyIdentifier, publicFaceScanEncryptionKey: faceScanEncryptionKey, result: result);
        case "startLiveness":
            return startLiveness(result: result);
        default:
            result(FlutterMethodNotImplemented);
            return;
        }
    }
    
    private func initialize(deviceKeyIdentifier: String, publicFaceScanEncryptionKey: String, result: @escaping FlutterResult) {
        
        var ftCustomization = FaceTecCustomization()
        ftCustomization.overlayCustomization.brandingImage = UIImage(named: "flutter_logo")
        FaceTec.sdk.setCustomization(ftCustomization)
        
        FaceTec.sdk.initializeInDevelopmentMode(deviceKeyIdentifier: deviceKeyIdentifier, faceScanEncryptionKey: publicFaceScanEncryptionKey, completion: { initializationSuccessful in
            if (initializationSuccessful) {
                result(true)
            }
            else {
                let statusStr = FaceTec.sdk.description(for: FaceTec.sdk.getStatus())
                result(FlutterError(code: "InitError", message: statusStr, details: nil))
            }
        })
    }
    
    private func startLiveness(result: @escaping FlutterResult) {
        let livenessCheckViewController = FaceTec.sdk.createSessionVC(faceScanProcessorDelegate: self)

        let controller: FlutterViewController = window?.rootViewController as! FlutterViewController;
        controller.present(livenessCheckViewController, animated: true, completion: nil)
    }
    
    // FaceTecFaceScanProcessorDelegate method
    func processSessionWhileFaceTecSDKWaits(sessionResult: FaceTecSessionResult, faceScanResultCallback: FaceTecFaceScanResultCallback) {
        fatalError("FaceTecSDK: This needs to be implemented")
    }
    
    // FaceTecFaceScanProcessorDelegate method
    func onFaceTecSDKCompletelyDone() {
        fatalError("FaceTecSDK: This needs to be implemented")
    }
    
}
