package com.ristana.newspro.ui;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.ristana.newspro.R;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.config.Config;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.manager.PrefManager;
import com.ristana.newspro.ui.fragement.CityFragment;
import com.ristana.newspro.ui.fragement.HomeFragment;
import com.ristana.newspro.ui.fragement.PopularFragment;
import com.ristana.newspro.ui.fragement.SavedFragment;
import com.ristana.newspro.ui.fragement.VideosFragment;
import com.ristana.newspro.ui.views.BottomNavigationViewBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private NavigationView navigationView;


    private TextView text_view_name_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;
    private ImageView image_view_google_nav_header;
    private ImageView image_view_email_nav_header;
    private ImageView image_view_facebook_nav_header;
    private Button button_login_nav_header;
    private Button button_logout_nav_header;
    private Button button_edit_nav_header;

    private Boolean FromLogin = false;
    private Boolean DialogOpened = false;
    private TextView text_view_go_pro;
    private Dialog dialog;
    private FloatingSearchView main_floating_search_view;
    private PrefManager prefManager;
    private FloatingActionButton fab;


    IInAppBillingService mService;



    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            // Toast.makeText(MainActivity.this, "set null", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            //Toast.makeText(MainActivity.this, "set Stub", Toast.LENGTH_SHORT).show();

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager= new PrefManager(getApplicationContext());

        if (prefManager.getString("LANGUAGE_DEFAULT").equals("0")){
            Intent intent = new Intent(MainActivity.this,LanguageActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }

        setContentView(R.layout.activity_main);
        getCategories();
        this.main_floating_search_view =    (FloatingSearchView) findViewById(R.id.main_floating_search_view);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.main_floating_search_view.attachNavigationDrawerToMenuButton(drawer);


        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initView();
        initAction();
        initBuy();
        FirebaseMessaging.getInstance().subscribeToTopic("AllTopic");

    }
    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Config.LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  showToast("onProductPurchased: " + productId);
                Intent intent= new Intent(MainActivity.this,IntroActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    private void updateTextViews() {
        PrefManager prf= new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();

    }
    public Bundle getPurchases(){
        if (!bp.isInitialized()) {


            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
        try{
            // Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();

            return  mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        }catch (Exception e) {
            //  Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }
    public Boolean isSubscribe(String SUBSCRIPTION_ID_CHECK){

        if (!bp.isSubscribed(Config.SUBSCRIPTION_ID))
            return false;
        Bundle b =  getPurchases();
        if (b==null)
            return  false;
        if( b.getInt("RESPONSE_CODE") == 0){
            // Toast.makeText(this, "RESPONSE_CODE", Toast.LENGTH_SHORT).show();
            ArrayList<String> ownedSkus =
                    b.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String>  purchaseDataList =
                    b.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String>  signatureList =
                    b.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            String continuationToken =
                    b.getString("INAPP_CONTINUATION_TOKEN");

            if(purchaseDataList == null){
                // Toast.makeText(this, "purchaseDataList null", Toast.LENGTH_SHORT).show();
                return  false;

            }
            if(purchaseDataList.size()==0){
                //  Toast.makeText(this, "purchaseDataList empty", Toast.LENGTH_SHORT).show();
                return  false;
            }
            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku_1 = ownedSkus.get(i);
                //Long tsLong = System.currentTimeMillis()/1000;

                try {
                    JSONObject rowOne = new JSONObject(purchaseData);
                    String  productId =  rowOne.getString("productId") ;
                    // Toast.makeText(this,productId, Toast.LENGTH_SHORT).show();

                    if (productId.equals(SUBSCRIPTION_ID_CHECK)){

                        Boolean  autoRenewing =  rowOne.getBoolean("autoRenewing");
                        if (autoRenewing){
                            // Toast.makeText(this, "is autoRenewing ", Toast.LENGTH_SHORT).show();
                            return  true;
                        }else{
                            //    Toast.makeText(this, "is not autoRenewing ", Toast.LENGTH_SHORT).show();
                            Long tsLong = System.currentTimeMillis()/1000;
                            Long  purchaseTime =  rowOne.getLong("purchaseTime")/1000;
                            if (tsLong > (purchaseTime + (Config.SUBSCRIPTION_DURATION*86400)) ){
                                //   Toast.makeText(this, "is Expired ", Toast.LENGTH_SHORT).show();
                                return  false;
                            }else{
                                return  true;
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }else{
            return false;
        }

        return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    public void initAction(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent  =  new Intent(getApplicationContext(), SelectCityActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }catch (IndexOutOfBoundsException e){

                }

            }
        });
        button_logout_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        button_login_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FromLogin=true;
                startActivity(new Intent(MainActivity.this,AuthActivity.class));
            }
        });
        button_edit_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initView() {

        fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new VideosFragment());
        adapter.addFragment(new PopularFragment());
        adapter.addFragment(new SavedFragment());
        adapter.addFragment(new CityFragment());


        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header=(TextView) headerview.findViewById(R.id.text_view_name_nave_header);
        this.circle_image_view_profile_nav_header=(CircleImageView) headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.image_view_google_nav_header=(ImageView) headerview.findViewById(R.id.image_view_google_nav_header);
        this.image_view_facebook_nav_header=(ImageView) headerview.findViewById(R.id.image_view_facebook_nav_header);
        this.image_view_email_nav_header=(ImageView) headerview.findViewById(R.id.image_view_email_nav_header);
        this.button_login_nav_header=(Button) headerview.findViewById(R.id.button_login_nav_header);
        this.button_logout_nav_header=(Button) headerview.findViewById(R.id.button_logout_nav_header);
        this.button_edit_nav_header=(Button) headerview.findViewById(R.id.button_edit_nav_header);



        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        BottomNavigationViewEx bnve=(BottomNavigationViewEx) findViewById(R.id.bnve);

        bnve.setupWithViewPager(viewPager);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bnve.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
        bnve.enableShiftingMode(false);
        bnve.setTextVisibility(true);
        bnve.enableItemShiftingMode(false);
        bnve.setTextSize(10);
        bnve.setSmallTextSize(10);
        main_floating_search_view.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.select_language:
                        Intent intent  = new Intent(MainActivity.this,LanguageActivity.class);
                        startActivity(intent);
                        finish();
                }
            }
        });
        main_floating_search_view.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Intent intent  = new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("query",currentQuery);
                startActivity(intent);
            }
        });
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() ==  R.id.i_visibility){
                    fab.show();
                }else{
                    fab.hide();
                }
                return true;
            }
        });
        bnve.getMenu().findItem(R.id.i_visibility).setTitle(prefManager.getString("CITY_DEFAULT_NAME"));
        /*final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_camera),
                        Color.parseColor("#FFFFFF"))
                        .title("Latest")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_send),
                        Color.parseColor(colors[1]))
                        .title("Popular")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_manage),
                        Color.parseColor(colors[1]))

                        .title("Categories")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_share),
                        Color.parseColor(colors[1]))
                        .title("Favorites")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_gallery),
                        Color.parseColor(colors[1]))
                        .title("Me")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {

                switch (position){
                    case 0:
                        getSupportActionBar().setTitle("Latest");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Popular");

                        break;
                    case 2:
                        getSupportActionBar().setTitle("Categories");

                        break;
                    case 3:
                        getSupportActionBar().setTitle("Favorites");
                        break;
                    case 4:
                        getSupportActionBar().setTitle("Me");
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });



        */




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_article clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item_article clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        if (id == R.id.nav_surveys) {
            Intent intent = new Intent(getApplicationContext(), QuestionsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id == R.id.nav_help  ){
            Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        else if(id ==  R.id.nav_go_pro){
            showDialog();
        }
        else if (id == R.id.nav_policy  ){
            Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id == R.id.nav_share  ){
            final String appPackageName=getApplication().getPackageName();
            String shareBody = "Download "+getString(R.string.app_name)+" From :  "+"http://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));

        }else if(id == R.id.nav_rate){
            final String appPackageName=getApplication().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        else if (id == R.id.nav_exit){
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void logout(){
        getCategories();
        PrefManager prf= new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.setString("LOGGED","FALSE");
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")){
                image_view_google_nav_header.setVisibility(View.VISIBLE);
                image_view_facebook_nav_header.setVisibility(View.GONE);
                image_view_email_nav_header.setVisibility(View.GONE);

            }
            else if(prf.getString("TYPE_USER").toString().equals("email")){
                image_view_google_nav_header.setVisibility(View.GONE);
                image_view_facebook_nav_header.setVisibility(View.GONE);
                image_view_email_nav_header.setVisibility(View.VISIBLE);
            }
            else {
                image_view_google_nav_header.setVisibility(View.GONE);
                image_view_facebook_nav_header.setVisibility(View.VISIBLE);
                image_view_email_nav_header.setVisibility(View.GONE);
            }
            button_logout_nav_header.setVisibility(View.VISIBLE);
            button_edit_nav_header.setVisibility(View.VISIBLE);
            button_login_nav_header.setVisibility(View.GONE);
        }else{
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            button_logout_nav_header.setVisibility(View.GONE);
            button_login_nav_header.setVisibility(View.VISIBLE);
            button_edit_nav_header.setVisibility(View.GONE);

            image_view_google_nav_header.setVisibility(View.VISIBLE);
            image_view_facebook_nav_header.setVisibility(View.VISIBLE);
            image_view_email_nav_header.setVisibility(View.GONE);

        }


        Toasty.warning(getApplicationContext(),getString(R.string.message_logout),Toast.LENGTH_LONG).show();
    }
    public void showDialog(){
        this.dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_subscribe);
        this.text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(MainActivity.this, Config.SUBSCRIPTION_ID);
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
        DialogOpened=true;

    }
    public  void getCategories(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> request = service.categoriesAll(0);
        request.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });

    }
    public void setFromLogin(){
        this.FromLogin = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCategories();

        PrefManager prf= new PrefManager(getApplicationContext());

        if (prf.getString("LOGGED").toString().equals("TRUE")){
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")){
                image_view_google_nav_header.setVisibility(View.VISIBLE);
                image_view_facebook_nav_header.setVisibility(View.GONE);
                image_view_email_nav_header.setVisibility(View.GONE);

            }
            else if(prf.getString("TYPE_USER").toString().equals("email")){
                image_view_google_nav_header.setVisibility(View.GONE);
                image_view_facebook_nav_header.setVisibility(View.GONE);
                image_view_email_nav_header.setVisibility(View.VISIBLE);
            }
            else {
                image_view_google_nav_header.setVisibility(View.GONE);
                image_view_facebook_nav_header.setVisibility(View.VISIBLE);
                image_view_email_nav_header.setVisibility(View.GONE);
            }
            button_logout_nav_header.setVisibility(View.VISIBLE);
            button_login_nav_header.setVisibility(View.GONE);
            button_edit_nav_header.setVisibility(View.VISIBLE);

        }else{
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            button_logout_nav_header.setVisibility(View.GONE);
            button_login_nav_header.setVisibility(View.VISIBLE);
            button_edit_nav_header.setVisibility(View.GONE);
            image_view_google_nav_header.setVisibility(View.VISIBLE);
            image_view_facebook_nav_header.setVisibility(View.VISIBLE);
            image_view_email_nav_header.setVisibility(View.VISIBLE);

        }
        if (FromLogin){
            FromLogin = false;
        }
    }
}
