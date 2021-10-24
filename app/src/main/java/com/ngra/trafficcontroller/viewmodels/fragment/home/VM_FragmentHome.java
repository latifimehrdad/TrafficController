package com.ngra.trafficcontroller.viewmodels.fragment.home;

import static com.ngra.trafficcontroller.utility.StaticFunctions.CheckResponse;
import static com.ngra.trafficcontroller.utility.StaticFunctions.GetAuthorization;
import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

import android.content.Context;
import android.location.Location;

import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.models.ModelChartMeasureDistance;
import com.ngra.trafficcontroller.models.ModelLocation;
import com.ngra.trafficcontroller.models.ModelLocations;
import com.ngra.trafficcontroller.models.ModelResponcePrimery;
import com.ngra.trafficcontroller.utility.ApplicationUtility;
import com.ngra.trafficcontroller.utility.DeviceTools;
import com.ngra.trafficcontroller.utility.StaticFunctions;
import com.ngra.trafficcontroller.views.application.TrafficController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VM_FragmentHome {

    private Context context;
    private String MessageResponcse;

    public VM_FragmentHome(Context context) {//_____________________________________________________ Start VM_FragmentHome
        this.context = context;
    }//_____________________________________________________________________________________________ End VM_FragmentHome


    public ArrayList<ModelChartMeasureDistance> getModelChartMeasureDistances() {//_________________ Start getModelChartMeasureDistances

        ApplicationUtility utility = new ApplicationUtility();

        ArrayList<ModelChartMeasureDistance> result = new ArrayList<>();

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DATE, -9);
        calendarStart.set(Calendar.HOUR_OF_DAY, 00);
        calendarStart.set(Calendar.MINUTE, 00);
        calendarStart.set(Calendar.SECOND, 00);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.DATE, -8);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 00);
        calendarEnd.set(Calendar.MINUTE, 00);
        calendarEnd.set(Calendar.SECOND, 00);

        return result;

    }//_____________________________________________________________________________________________ End getModelChartMeasureDistances


    public void sendLocationToServer(Location location) {//_________________________________________________________ Start SendLocatoinToServer

        ArrayList<ModelLocation> list = new ArrayList<>();
        list.add(new ModelLocation(
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                location.getSpeed(),
                getDate(getStringCurrentDate()),
                location.getAccuracy(),
                true));

        ModelLocations lo = new ModelLocations(list);

        RetrofitModule.isCancel = false;
        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();

        DeviceTools deviceTools = new DeviceTools(context);
        String imei = deviceTools.getIMEI();
        String Authorization = GetAuthorization(context);

        retrofitComponent
                .getRetrofitApiInterface()
                .DeviceLogs(
                        imei,
                        Authorization,
                        StaticFunctions.Get_aToken(context),
                        lo
                )
                .enqueue(new Callback<ModelResponcePrimery>() {
                    @Override
                    public void onResponse(Call<ModelResponcePrimery> call, Response<ModelResponcePrimery> response) {
                        MessageResponcse = CheckResponse(response, true);
                        if (MessageResponcse == null) {
                            if (response.body().getMessages().size() > 0)
                                MessageResponcse = response.body().getMessages().get(0).getMessage();
                            else
                                MessageResponcse = "موقعیت شما ارسال شد، میتوانید از برنامه خارج شوید";
                            ObservablesGpsAndNetworkChange.onNext("send");
                        } else
                            ObservablesGpsAndNetworkChange.onNext("error");
                    }

                    @Override
                    public void onFailure(Call<ModelResponcePrimery> call, Throwable t) {
                        ObservablesGpsAndNetworkChange.onNext("Failure");
                    }
                });

    }//_____________________________________________________________________________________________ End SendLocatoinToServer

    public String getStringCurrentDate() {//_______________________________________________________ Start getStringCurrentDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date());
    }//_____________________________________________________________________________________________ End getStringCurrentDate


    public Date getDate(String sdate) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        try {
            date = format.parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String getMessageResponcse() {
        return MessageResponcse;
    }

}
