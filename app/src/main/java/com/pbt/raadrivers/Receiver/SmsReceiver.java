package com.pbt.raadrivers.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.pbt.raadrivers.Utils.AppConstant;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ravi on 09/07/15.
 */
public class SmsReceiver{

}

//
// extends BroadcastReceiver {
//    private static final String TAG = SmsReceiver.class.getSimpleName();
//    public static String SMSRECEIVE = "SMSRECEIVE";
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        final Bundle bundle = intent.getExtras();
//        try {
//
//            Log.e(TAG, " : SmsReciever " );
//
//            if (bundle != null) {
//                Object[] pdusObj = (Object[]) bundle.get("pdus");
//                for (Object aPdusObj : pdusObj) {
//                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
//                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
//                    String message = currentMessage.getDisplayMessageBody();
//
//                    if(senderAddress.equals("VK-RaaCab")) {
//                        Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
//
//                        if (message != null && !message.isEmpty()) {
//                            String[] strSMS = message.split(" ");
//                            Log.e(TAG, "Receive SMS :" + strSMS[0]);
//
//                            Intent intent1 = new Intent();
//                            intent1.setAction(SMSRECEIVE);
//                            intent1.putExtra(SMSRECEIVE, strSMS[0]);
//                            getApplicationContext().sendBroadcast(intent1);
//                        }
//
//
//                        // if the SMS is not from our gateway, ignore the message
//                        if (!senderAddress.toLowerCase().contains(AppConstant.SMS_ORIGIN.toLowerCase())) {
//                            return;
//                        }
//                    }
//
////                    Intent intent1 = new Intent();
////                    intent1.setAction(SMSRECEIVE);
////                    intent1.putExtra(SMSRECEIVE, "");
////                    getApplicationContext().sendBroadcast(intent1);
//                    // verification code from sms
////                    String verificationCode = getVerificationCode(message);
//
////                    Log.e(TAG, "OTP received: " + verificationCode);
////
////                    Intent hhtpIntent = new Intent(context, HttpService.class);
////                    hhtpIntent.putExtra("otp", verificationCode);
////                    context.startService(hhtpIntent);
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Getting the OTP from sms message body
//     * ':' is the separator of OTP from the message
//     *
//     * @param message
//     * @return
//     */
////    private String getVerificationCode(String message) {
////        String code = null;
////        int index = message.indexOf(Config.OTP_DELIMITER);
////
////        if (index != -1) {
////            int start = index + 2;
////            int length = 6;
////            code = message.substring(start, start + length);
////            return code;
////        }
////
////        return code;
////    }
//}