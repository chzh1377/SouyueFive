package so.raw.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description JPush Daemon
 * @date 2016/5/7
 */
public class JWakeUpService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(), "------JWakeUpService------", Toast.LENGTH_SHORT).show();
        Log.d(this.getClass().getSimpleName(), "------JWakeUpService------");
        DaemonManager.getManager().processAction(this);

        return super.onStartCommand(intent, flags, startId);
    }
}
