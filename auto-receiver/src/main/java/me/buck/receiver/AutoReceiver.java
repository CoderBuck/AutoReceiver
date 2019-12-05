package me.buck.receiver;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gwf on 2019/12/3
 */
public class AutoReceiver {
    private static final String TAG = "AutoReceiver";

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

    public static void bindLocal(View view) {
        bind(view.getContext(), view, true);
    }

    public static void bindGlobal(View view) {
        bind(view.getContext(), view, false);
    }

    public static void unbindLocal(View view) {
        unbind(view,true);
    }

    public static void unbindGlobal(View view) {
        unbind(view, false);
    }

    public static void bindLocal(Fragment fragment) {
        bind(fragment.getContext(), fragment, true);
    }

    public static void bindGlobal(Fragment fragment) { bind(fragment.getContext(), fragment, false); }

    public static void unbindLocal(Fragment fragment) {
        unbind(fragment,true);
    }

    public static void unbindGlobal(Fragment fragment) {
        unbind(fragment, false);
    }

    public static void bind(Context context, Object target, boolean isLocal) {
        if (isLocal && sLocalRegistMap.get(target) != null) return;
        if (!isLocal && sGlobalRegistMap.get(target) != null) return;
        String endFix = isLocal ? END_FIX_LOCAL : END_FIX_GLOBAL;
        String receiverClassName = target.getClass().getCanonicalName() + endFix;
        Constructor<?> constructor = null;
        try {
            Class<?> clazz = Class.forName(receiverClassName);
            constructor = clazz.getConstructor(Context.class, target.getClass());
            SimpleReceiver receiver = (SimpleReceiver) constructor.newInstance(context, target);
            receiver.register();
            if (isLocal) {
                sLocalRegistMap.put(target, receiver);
            } else {
                sGlobalRegistMap.put(target, receiver);
            }
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Not found binding class for " + target.getClass().getName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + target.getClass().getName(), e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
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

    //private static
}
