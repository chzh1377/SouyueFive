package so.raw.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V5.2.0
 * @Copyright (c) 2016 zhongsou
 * @Description baidu
 * @date 2016/2/23
 */
public class BdRegReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "------BdRegReceiver------", Toast.LENGTH_SHORT).show();
        Log.d(this.getClass().getSimpleName(), "------BdRegReceiver------");
        DaemonManager.getManager().processAction(context);
    }
}
