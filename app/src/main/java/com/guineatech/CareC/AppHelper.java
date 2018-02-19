package com.guineatech.CareC;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.iot.AWSIotClient;

import java.io.UnsupportedEncodingException;

/**
 * Created by CAHans on 2017/12/28.
 */

public class AppHelper {

    private static AppHelper appHelper;
    private static CognitoUserPool userPool;
    private static String user;
    private static final String userPoolId = "us-west-2_Aj3frUrZo";
    private static final String clientId = "6nn164bo79srih9c48t0pkj6ql";
    private static final String clientSecret=null;
    private static final Regions cognitoRegion = Regions.US_WEST_2;
    private static final String identityPoolld ="us-west-2:a6eb6a56-b494-448c-83a7-26b3dfe51c7a";
    public static final String CUSTOMER_SPECIFIC_ENDPOINT = "a2hd4hpd193y9c.iot.us-west-2.amazonaws.com";
    private static CognitoUserSession currSession;
    public static CognitoUserPool getPool() {
        return userPool;
    }
    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }
    public static CognitoCachingCredentialsProvider credentialsProvider ;
    public static String userid;
    public static AWSIotClient iotClient;
    public static AWSIotMqttManager mqttManager;
    public static void mqttcreate()//MQTT
    {
        mqttManager = new AWSIotMqttManager(userid, AppHelper.CUSTOMER_SPECIFIC_ENDPOINT);
        mqttManager.setKeepAlive(10);
    }

    public static  void iotcreate()
    {
        iotClient = new AWSIotClient(credentialsProvider);
        iotClient.setRegion(Region.getRegion(Regions.US_WEST_2));
    }

    //建置環境
    public static void checkpool(Context c)
    {
        if(getPool()==null)
        {
            ClientConfiguration clientConfiguration= new ClientConfiguration();
            AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
            cipClient.setRegion(Region.getRegion(cognitoRegion));
            userPool = new CognitoUserPool(c,userPoolId, clientId, clientSecret, cipClient);
            credentialsProvider=new CognitoCachingCredentialsProvider(c, identityPoolld, Regions.US_WEST_2);
        }
    }




}
