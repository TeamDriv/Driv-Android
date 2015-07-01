package accenture.driv;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;


/**
 * Created by DJ on 6/28/2015.
 */
public class PhonePrefActivity extends PreferenceActivity {
    PendingIntent deliveredPI;
    PendingIntent sentPI;

    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    SmsManager sms;
    String message = "Im not capable to answer your call. I'm driving";

    BroadcastReceiver sendBroadcastReceiver;

    int sendSmsCounter = 0;
    int deliverCounter = 0;

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private Button btnDisplay;

    RadioButton RB1,RB2;
    private Button btnActivate, btnDeactivate ;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private boolean isReceiverRegistered;


    CheckBoxPreference pBlock;
    CheckBoxPreference pAutoRep;
    Preference pWhitelist;
    Toolbar mToolBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        View content = root.getChildAt(0);
        LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.activity_prefs, null);

        root.removeAllViews();
        toolbarContainer.addView(content);
        root.addView(toolbarContainer);

        mToolBar = (Toolbar) toolbarContainer.findViewById(R.id.toolbar);
        mToolBar.setTitle(getTitle());
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // TODO: Add preferences from XML
        addPreferencesFromResource(R.xml.pref_phone);

        pBlock = (CheckBoxPreference) findPreference("block");
        pAutoRep = (CheckBoxPreference) findPreference("autorep");
        pWhitelist = findPreference("whitelist");
        pAutoRep.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if(pAutoRep.isChecked()) {
                    BlockAndAutoText();
                    registerReceiver(receiver, filter);
                    pAutoRep.setChecked(true);
                }
                else {
                    unregisterReceiver(receiver);
                    pAutoRep.setChecked(false);
                }
                return true;
            }
        });
        pBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if(pBlock.isChecked()) {
                    Toast.makeText(getApplication(),"Activate",Toast.LENGTH_SHORT).show();
                    BlockAll();
                    registerReceiver(receiver, filter);
                    pBlock.setChecked(true);
                }
                else {
                    unregisterReceiver(receiver);
                    Toast.makeText(getApplication(),"Deactivate",Toast.LENGTH_SHORT).show();
                    pBlock.setChecked(false);
                }
                return true;
            }
        });
        pWhitelist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                Toast.makeText(getApplicationContext(),"Show whitelist",Toast.LENGTH_LONG).show();
                WhitelistDialogFragment white = new WhitelistDialogFragment();
                white.show(getFragmentManager(),"WHITELIST_DIALOG");
                return true;
            }
        });
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public void BlockAll(){
        filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Intent Action", action);
                if(intent!=null){
                    if(action.equals("android.intent.action.PHONE_STATE")){
                        try {
                            TelephonyManager tm = (TelephonyManager) context
                                    .getSystemService(Context.TELEPHONY_SERVICE);
                            Class<?> c = Class.forName(tm.getClass().getName());
                            Method m = c.getDeclaredMethod("getITelephony");
                            m.setAccessible(true);
                            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m
                                    .invoke(tm);
                            telephonyService.endCall();
                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
                        }
                    }else if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                        Bundle extras = intent.getExtras();
                        if (extras != null) {
                            abortBroadcast();
                        }
                    }
                }
            }
        };
    }

    public void BlockAndAutoText(){
        filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Intent Action", action);
                if(intent!=null){
                    if(action.equals("android.intent.action.PHONE_STATE")){
                        try {
                            TelephonyManager tm = (TelephonyManager) context
                                    .getSystemService(Context.TELEPHONY_SERVICE);
                            Class<?> c = Class.forName(tm.getClass().getName());
                            Method m = c.getDeclaredMethod("getITelephony");
                            m.setAccessible(true);
                            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m
                                    .invoke(tm);
                            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            telephonyService.endCall();
                            Toast.makeText(getApplicationContext(),"caller: "+number,Toast.LENGTH_SHORT).show();
                            sendSMS(number, message);
                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
                        }
                    }else if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                        Bundle extras = intent.getExtras();
                        if (extras != null) {
                            abortBroadcast();
                        }
                    }
                }
            }
        };
    }

    public void sendSMS(String phoneNumber, String message) {

        sentPI = PendingIntent.getBroadcast(this, sendSmsCounter++, new Intent(
                SENT), PendingIntent.FLAG_CANCEL_CURRENT);
        deliveredPI = PendingIntent.getBroadcast(this, deliverCounter++,
                new Intent(DELIVERED), PendingIntent.FLAG_CANCEL_CURRENT);

        sendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // sms sent if bol true cancel the Asynctask :)
                        Toast.makeText(getApplicationContext(), "Message Sent",
                                Toast.LENGTH_LONG).show();

                        //new checkList().cancel(true);

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "Generic Failure",
                                Toast.LENGTH_LONG).show();

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "No Service",
                                Toast.LENGTH_LONG).show();

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "Null PDU",
                                Toast.LENGTH_LONG).show();

                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "Radio OFF",
                                Toast.LENGTH_LONG).show();

                        break;
                }
                // used for end receiving
                // CollectionTerminalActivity.this.unregisterReceiver(this);

            }
        };
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        sms = SmsManager.getDefault();
        if (!phoneNumber.equals("")) {
            sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
            Toast.makeText(getApplicationContext(), "sending",
                    Toast.LENGTH_LONG).show();
        }
    }
}
