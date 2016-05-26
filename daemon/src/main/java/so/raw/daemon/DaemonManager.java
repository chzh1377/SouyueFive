package so.raw.daemon;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

/**
 * Created by zyw on 2016/2/29.
 */
public class DaemonManager {
    public static final String TAG    = DaemonManager.class.getName();
    static final        int    JOB_ID = 729976608;

    public static final int TYPE_SERVICE  = 324;
    public static final int TYPE_ACTIVITY = 687;
    public static final int TYPE_RECEIVER = 672;

    private static final String KEY_TYPE      = "DaemonManager_type";
    private static final String KEY_CLASSNAME = "DaemonManager_classname";

    private DaemonManager() {
    }

    private static DaemonManager manager = new DaemonManager();

    public static DaemonManager getManager() {
        return manager;
    }

    private Action mAction;

    public void setAction(Action mAction) {
        this.mAction = mAction;
    }

    public void processAction(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mAction = new Action(sp.getInt(KEY_TYPE, 0), new ComponentName(ctx, sp.getString(KEY_CLASSNAME, "")));
        Intent intent = new Intent();
        intent.setComponent(mAction.componentName);
        switch (mAction.type) {
            case TYPE_ACTIVITY:
                ctx.startActivity(intent);
                break;
            case TYPE_SERVICE:
                ctx.startService(intent);
                break;
            case TYPE_RECEIVER:
                ctx.sendBroadcast(intent);
                break;
        }
    }

    /**
     * 启动服务
     *
     * @param context
     * @param timeStep
     */
    public boolean startDaemon(Context context, int timeStep, Action action) {
        context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit()
                .putInt(KEY_TYPE, action.type)
                .putString(KEY_CLASSNAME, action.componentName.getClassName()).apply();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startInner(context, timeStep);
            return true;
        }
        return false;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startInner(Context context, int timeStep) {
        /**
         * 跨进程通信，需要存起来这个action
         */
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,
                new ComponentName(context.getPackageName(), DaemonService.class.getName()));
        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        builder.setPeriodic(timeStep);
        builder.setPersisted(true);
        mJobScheduler.cancelAll();
        if (mJobScheduler.schedule(builder.build()) <= 0) {
            Log.e("jobservice", "start error");
        }
    }


    public boolean calcelAll(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelInner(context);
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void cancelInner(Context context) {
        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        mJobScheduler.cancelAll();
    }


    public static class Action {
        int           type;
        ComponentName componentName;

        public Action(int type, ComponentName componentName) {
            this.type = type;
            this.componentName = componentName;
        }

        @Override
        public String toString() {
            return "Action{" +
                    "type=" + type +
                    ", componentName=" + componentName +
                    '}';
        }
    }

}
