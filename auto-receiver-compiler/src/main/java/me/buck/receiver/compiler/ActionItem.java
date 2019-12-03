package me.buck.receiver.compiler;

/**
 * Created by gwf on 2019/12/3
 */
public class ActionItem {

    String actionName;
    String methodName;
    String clazzSimpleName;
    String clazzFullName;
    String pkgName;

    public ActionItem(String actionName, String methodName) {
        this.actionName = actionName;
        this.methodName = methodName;
    }

    public ActionItem(String actionName, String methodName, String clazzName) {
        this.actionName = actionName;
        this.methodName = methodName;
        this.clazzSimpleName = clazzName;
    }

    public ActionItem(String actionName, String methodName, String clazzSimpleName, String clazzFullName, String pkgName) {
        this.actionName = actionName;
        this.methodName = methodName;
        this.clazzSimpleName = clazzSimpleName;
        this.clazzFullName = clazzFullName;
        this.pkgName = pkgName;
    }
}
