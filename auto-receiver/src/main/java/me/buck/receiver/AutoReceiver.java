package me.buck.receiver;

import android.app.Activity;
import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gwf on 2019/12/3
 */
public class AutoReceiver {

    private static Map<Object, SimpleReceiver> sLocalRegistMap = new ConcurrentHashMap<>();
    private static Map<Object, SimpleReceiver> sGlobalRegistMap = new ConcurrentHashMap<>();

    private static String END_FIX_LOCAL = "_LocalReceiver";
    private static String END_FIX_GLOBAL = "_GlobalReceiver";

    public static void bindLocal(Activity activity) {
        bind(activity, activity, true);
    }

    public static void bindGlobal(Activity activity) {
        bind(activity, activity, false);
    }

    public static void unbindLocal(Activity activity) {
        unbind(activity,true);
    }

    public static void unbindGlobal(Activity activity) {
        unbind(activity, false);
    }

    public static void bind(Context context, Object target, boolean isLocal) {
        if (isLocal && sLocalRegistMap.get(target) != null) return;
        if (!isLocal && sGlobalRegistMap.get(target) != null) return;
        String endFix = isLocal ? END_FIX_LOCAL : END_FIX_GLOBAL;

        try {
            Class<?> clazz = Class.forName(target.getClass().getCanonicalName() + endFix);
            Constructor<?> constructor = clazz.getConstructor(Context.class, target.getClass());
            SimpleReceiver receiver = (SimpleReceiver) constructor.newInstance(context, target);
            receiver.register();
            if (isLocal) {
                sLocalRegistMap.put(target, receiver);
            } else {
                sGlobalRegistMap.put(target, receiver);
            }
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


    public static void unbind(Object target, boolean isLocal) {
        if (isLocal) {
            SimpleReceiver receiver = sLocalRegistMap.get(target);
            if (receiver != null) {
                receiver.unregister();
                sLocalRegistMap.remove(target);
            }
        } else {
            SimpleReceiver receiver = sGlobalRegistMap.get(target);
            if (receiver != null) {
                receiver.unregister();
                sGlobalRegistMap.remove(target);
            }
        }
    }
}
