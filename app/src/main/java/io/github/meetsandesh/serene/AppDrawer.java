package io.github.meetsandesh.serene;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.github.meetsandesh.serene.adapters.ListViewAnimationAdapter;
import io.github.meetsandesh.serene.listeners.OnSwipeTouchListener;
import io.github.meetsandesh.serene.vo.BroadcastActions;
import io.github.meetsandesh.serene.vo.HomeScreenConstants;
import io.github.meetsandesh.serene.vo.IntentMetadata;
import io.github.meetsandesh.serene.vo.PackageMetadata;

public class AppDrawer extends AppCompatActivity {

    private static final String TAG = "AppDrawer";
    private ListView appListContainer;
    private Map<String, PackageMetadata> appMetadata;
    private Map<String, PackageMetadata> filteredAppMetadata;
    private ArrayList<String> filteredAppNames;
    private OnSwipeTouchListener onSwipeTouchListener;
    private DisplayMetrics metrics;
    private int mode = 3;
    private ImageView backToHomeAction;
    private SearchView searchApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_drawer);
        //setup global variables
        setupGlobalVars();
    }

    private void setupGlobalVars() {
        setupSwipeListener();
        //back to home action
        backToHomeAction = this.findViewById(R.id.img2);
        backToHomeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //search box
        searchApps = this.findViewById(R.id.search_apps);
        searchApps.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                paintFilteredList(newText);
                return false;
            }
        });
        //list all apps
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        appListContainer = this.findViewById(R.id.appList1);
        setupAppListReceiver();
        Intent intent = new Intent();
        intent.setAction(BroadcastActions.FETCH_APP_LIST);
        sendBroadcast(intent);
    }

    private void paintFilteredList(String newText) {
        if(newText.isEmpty()){
            paintAppList(this.appMetadata);
        } else {
            Map<String, PackageMetadata> filteredList = new TreeMap<>();
            for(Map.Entry<String, PackageMetadata> entry : this.appMetadata.entrySet()) {
                if (entry.getKey().toUpperCase().contains(newText.toUpperCase())) {
                    filteredList.put(entry.getKey(), entry.getValue());
                }
            }
            paintAppList(filteredList);
        }
    }

    private void setupAppListReceiver() {
        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                Log.d(TAG, "Broadcast received with apps");
                Map<String, PackageMetadata> appNameList = (Map<String, PackageMetadata>) intent.getSerializableExtra(HomeScreenConstants.APP_NAMES);
                initAppList(appNameList);
            }
        };
        this.registerReceiver(mBatInfoReceiver, new IntentFilter(BroadcastActions.BEAR_APP_LIST));
    }

    private void initAppList(Map<String, PackageMetadata> appNameList) {
        this.appMetadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.appMetadata.putAll(appNameList);
        paintAppList(this.appMetadata);
    }

    private void paintAppList(Map<String, PackageMetadata> appNameList){
        this.filteredAppMetadata = appNameList;
        this.filteredAppNames = new ArrayList<>(this.filteredAppMetadata.keySet());
        ListViewAnimationAdapter mAdapter = new ListViewAnimationAdapter(this, this.filteredAppNames, metrics, mode);
        appListContainer.setAdapter(mAdapter);
        appListContainer.setFastScrollEnabled(true);
        appListContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchApp(position);
            }
        });
    }

    private void launchApp(int position) {
        Intent launchApp = getPackageManager().getLaunchIntentForPackage(this.appMetadata.get(this.filteredAppNames.get(position)).getPackageName());
        startActivity(launchApp);
        this.finish();
    }

    private void setupSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(AppDrawer.this, this.findViewById(R.id.appList1));
        onSwipeTouchListener.overrideSwipeAction(new OnSwipeTouchListener.OnSwipeListener() {
            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeTop() {

            }

            @Override
            public void swipeBottom() {
//                Toast.makeText(AppDrawer.this, "Swipe Down : "+canScrollUp(appListContainer), Toast.LENGTH_SHORT).show();
                if(!canScrollUp(appListContainer)){
                    finish();
                }
            }

            @Override
            public void swipeLeft() {

            }
        });
    }

    public boolean canScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView
                        .getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }

}