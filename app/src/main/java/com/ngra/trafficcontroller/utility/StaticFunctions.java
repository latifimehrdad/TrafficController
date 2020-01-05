package com.ngra.trafficcontroller.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.ngra.trafficcontroller.R;


public class StaticFunctions {

    public static boolean isCancel = false;

    public static String GetAuthorization(Context context) {//______________________________________ Start GetAuthorization
        String Authorization = "Bearer ";
        SharedPreferences prefs = context.getSharedPreferences("wmstoken", 0);
        if (prefs != null) {
            String access_token = prefs.getString("accesstoken", null);
            if (access_token != null)
                Authorization = Authorization + access_token;
        }
        return Authorization;
    }//_____________________________________________________________________________________________ End GetAuthorization


    public static TextWatcher TextChangeForChangeBack(EditText editText) {//______________________________ Satart TextChangeForChangeBack

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setBackgroundResource(R.drawable.edit_back);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

    }//_____________________________________________________________________________________________ End TextChangeForChangeBack


//
//    public static View.OnKeyListener SetBackClickAndGoHome(Boolean execute) {//_____________________ Start SetBackClickAndGoHome
//        return new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() != KeyEvent.ACTION_DOWN)
//                    return true;
//
//                if (keyCode != 4) {
//                    return false;
//                }
//                if (execute)
//                    MainActivity1.FragmentMessage.onNext("Main");
//                return true;
//            }
//        };
//    }//_____________________________________________________________________________________________ End SetBackClickAndGoHome


//    public static String CheckResponse(Response response, Boolean Authorization) {//________________ Start CheckResponse
//        if (response.body() != null)
//            return null;
//        else {
//            if (Authorization) {
//                try {
//                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                    return jObjError.getString("error_description");
//                } catch (Exception ex) {
//                    return "Failure";
//                }
//            } else {
//                return GetErrorٍMessage(response);
//            }
//        }
//
//    }//_____________________________________________________________________________________________ End CheckResponse


//    public static String GetErrorٍMessage(Response response) {//_____________________________________ Start GetErrorٍMessage
//        try {
//            JSONObject jObjError = new JSONObject(response.errorBody().string());
//            JSONArray jsonArray = jObjError.getJSONArray("messages");
//            String message = "";
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject temp = new JSONObject(jsonArray.get(i).toString());
//                message = message + temp.getString("message");
//                message = message + "\n";
//            }
//            return message;
//        } catch (Exception ex) {
//            return "Failure";
//        }
//    }//_____________________________________________________________________________________________ End GetErrorٍMessage


//    public static String GetMessage(Response<ModelResponcePrimery> response) {//____________________ Start GetMessage
//        try {
//            ArrayList<ModelMessage> modelMessages = response.body().getMessages();
//            String message = "";
//            for (int i = 0; i < modelMessages.size(); i++) {
//                message = message + modelMessages.get(i).getMessage();
//                message = message + "\n";
//            }
//            return message;
//        } catch (Exception ex) {
//            return "Failure";
//        }
//    }//_____________________________________________________________________________________________ End GetMessage


}
