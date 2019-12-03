package me.buck.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;

/**
 * Created by gwf on 2019/12/3
 */
public class AutoReceiver {

    public static void regist(Activity activity) {
        try {
            Class<?> clazz = Class.forName(activity.getClass().getCanonicalName() + "_LocalReceiver");
            BroadcastReceiver o = (BroadcastReceiver) clazz.newInstance();
            //activity.registerReceiver(o);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void unregist() {

    }
}
