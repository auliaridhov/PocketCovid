package tik.itera.covid.network;

import android.app.Application;
import com.onesignal.OneSignal;

import tik.itera.covid.utils.MyUtils;
import tik.itera.covid.utils.TypfaceUtil;

public class NotificationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Change Font
        TypfaceUtil.overrideFont(this, "SERIF", "Gilroy-Light.ttf");

        //Start One Signal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }
}
