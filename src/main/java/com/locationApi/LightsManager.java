package com.locationApi;

import com.philips.lighting.hue.sdk.*;
import com.philips.lighting.model.*;

import java.util.List;
import java.util.logging.Logger;


public class LightsManager {

    private static PHHueSDK phHueSDK;
    private static String ipAddress = "192.168.0.26";
    private static String username = "pc7Q7BW-TiPyBCvfexkOxBj1SFiEg-T-k-TRjuQk";

    private final static Logger LOGGER = Logger.getLogger(LightsManager.class.getName());

    public static void initMe() {
        LightsManager lightsManager = new LightsManager();

        lightsManager.phHueSDK = PHHueSDK.getInstance();
        lightsManager.phHueSDK.getNotificationManager().registerSDKListener(lightsManager.listener);

        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(ipAddress);
        accessPoint.setUsername(username);

        lightsManager.phHueSDK.connect(accessPoint);
    }

    public void listLights(PHBridge bridge) {
        List<PHLight> myLights = bridge.getResourceCache().getAllLights();
        System.out.print(myLights);
    }

    // Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onError(int code, String message)            {
            System.out.println("On Error.. " + code + " " +  message);  }

        @Override
        public void onCacheUpdated(List<Integer> cacheNotificationsList, PHBridge bridge)     {
            System.out.println("Hue Cache Updated..");
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            System.out.println("On Access Point Found.");
        }

        @Override
        public void onBridgeConnected(PHBridge b, String bb)  {
            System.out.println("********** BRIDGE IS CONNECTED *************");
            phHueSDK.setSelectedBridge(b);
            listLights(b);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            System.out.println("On Authentication Required.. Pushlink the bridge");
            phHueSDK.startPushlinkAuthentication(accessPoint);

        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {  System.out.println("On Connection Resumed");  }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {   System.out.println("On Connection Lost"); }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrors) { }

    };

    public static void changeLights(List<String> lightsList, Boolean switchDir) {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> myLights = bridge.getResourceCache().getAllLights();
        PHLightState lightState = new PHLightState();
        lightState.setOn(switchDir);

        for (int i=0; i < lightsList.size(); i++) {
            for (int j = 0; j < myLights.size(); j++) {
                if (lightsList.get(i).equals(myLights.get(j).getName())) {
                    System.out.println("Turning off " + myLights.get(j).getName());
                    bridge.updateLightState(myLights.get(j), lightState);
                } else if (lightsList.get(i).equals("All")) {
                    System.out.println("Turning off " + myLights.get(j).getName());
                    bridge.updateLightState(myLights.get(j), lightState);
                }
            }
        }

    }


}
