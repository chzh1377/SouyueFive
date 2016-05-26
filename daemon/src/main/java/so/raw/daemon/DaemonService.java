package so.raw.daemon;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DaemonService extends JobService {

    static final String TAG = "DaemonService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, intent.toString());
        return super.onStartCommand(intent, flags, startId);
    }


    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            DaemonManager.getManager().processAction(DaemonService.this);
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });


    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(1);
        return false;
    }

}
