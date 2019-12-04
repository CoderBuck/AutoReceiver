package me.buck.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gwf on 2019/12/3
 */
public class AutoReceiver {

    private static Map<Activity, SimpleReceiver> sRegistMap = new ConcurrentHashMap<>();

    public static void regist(Activity activity) {
        Context context = activity;
        Activity target = activity;
        if (sRegistMap.get(target) != null) return;
        try {
            Class<?> clazz = Class.forName(activity.getClass().getCanonicalName() + "_LocalReceiver");
            Constructor<?> constructor = clazz.getConstructor(Context.class, target.getClass());
            SimpleReceiver receiver = (SimpleReceiver) constructor.newInstance(context, target);
            receiver.register();
            sRegistMap.put(activity, receiver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void unregist(Activity activity) {
        SimpleReceiver receiver = sRegistMap.get(activity);
        if (receiver != null) {
            receiver.unregister();
            sRegistMap.remove(activity);
        }
    }
}
