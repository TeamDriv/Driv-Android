package accenture.driv;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by DJ on 6/28/2015.
 */
public class PhonePrefActivity extends PreferenceActivity {

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
                if(pAutoRep.isChecked())
                    pAutoRep.setChecked(true);
                else
                    pAutoRep.setChecked(false);
                return true;
            }
        });
        pBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if(pBlock.isChecked())
                    pBlock.setChecked(true);
                else
                    pBlock.setChecked(false);
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
}
