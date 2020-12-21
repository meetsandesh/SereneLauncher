package io.github.meetsandesh.serene.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.github.meetsandesh.serene.R;

public class BatteryWidget {

    private static final String TAG = "BatteryWidget";
    private ProgressBar progressBar;
    private TextView textView;
    private Context context;

    public BatteryWidget(Context tContext, ProgressBar tProgressBar, TextView tTextView) {
        this.context = tContext;
        this.progressBar = tProgressBar;
        this.textView = tTextView;
    }

    public void registerBroadcastReceiver(){
        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                progressBar.setProgress(level, true);
                progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.battery_normal, null), android.graphics.PorterDuff.Mode.SRC_IN);
                int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                if(status == BatteryManager.BATTERY_PLUGGED_AC ||
                        status == BatteryManager.BATTERY_PLUGGED_USB ||
                        status == BatteryManager.BATTERY_PLUGGED_WIRELESS){
                    progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.battery_charging, null), android.graphics.PorterDuff.Mode.SRC_IN);
                } else if(level<20){
                    progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.battery_low, null), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                textView.setText(level+"%");
            }
        };
        context.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

}
