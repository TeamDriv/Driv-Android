package accenture.driv;

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by DJ on 6/28/2015.
 */
public class WhitelistDialogFragment extends DialogFragment implements OnItemClickListener{

    List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    WhiteAdapter adapter ;
    Button btnSelect;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String FILENAME = "whitelistPref";
    Set<String> setName,setId;
    int size=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_whitelist, container, false);

        sharedPreferences = getActivity().getSharedPreferences(FILENAME, getActivity().MODE_PRIVATE);
        setName = sharedPreferences.getStringSet("contactName", null);
        setId = sharedPreferences.getStringSet("contactName", null);
        ArrayList<String> restoredName=new ArrayList<String>();
        ArrayList<String> restoredId=new ArrayList<String>();

        if(setName!=null) {
            restoredName = new ArrayList<String>(setName);
            restoredId = new ArrayList<String>(setId);
        }

        getAllContacts(getActivity().getContentResolver());
        ListView listWhite= (ListView) v.findViewById(R.id.listWhite);
        adapter = new WhiteAdapter(restoredId);
        listWhite.setAdapter(adapter);
        listWhite.setOnItemClickListener(this);
        btnSelect = (Button) v.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener()
        {@Override
         public void onClick(View v) {
                dismiss();
            }});
        return v;
    }

    private void save() {
        StringBuilder checkedcontacts= new StringBuilder();

        int a=0;
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> id = new ArrayList<String>();
        for (int i = 0; i < name1.size(); i++)

        {
            if(adapter.mCheckStates.get(i)==true)
            {
                checkedcontacts.append(name1.get(i).toString());
                checkedcontacts.append("\n");
                names.add(a, name1.get(i).toString());
                id.add(a++,i+"");
            }
            else
            {

            }
            sharedPreferences = getActivity().getSharedPreferences(FILENAME,
                    getActivity().MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Set<String> setName = new HashSet<String>(names);
            Set<String> setId = new HashSet<String>(id);
            size = setId.size();
            editor.putStringSet("contactName", setName);
            editor.putStringSet("contactName", setId);
            editor.commit();
        }
    }

    public  void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name1.add(name);
            phno1.add(phoneNumber);
        }

        phones.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        adapter.toggle(i);
    }

    class WhiteAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
    {  private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView txtPhone,txtName;
        CheckBox checkbox;
        int[] restoredId;

        public WhiteAdapter(ArrayList<String> restoredId) {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.restoredId = new int[restoredId.size()];
            for(int x=0;x<restoredId.size();x++){
                this.restoredId[x]= Integer.parseInt(restoredId.get(x));
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name1.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView==null)
                convertView = mInflater.inflate(R.layout.list_item_whitelist, null);
            txtName= (TextView) convertView.findViewById(R.id.txtName);
            txtPhone= (TextView) convertView.findViewById(R.id.txtPhone);
            checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
            txtName.setText(name1.get(position));
            txtPhone.setText(phno1.get(position));
            checkbox.setTag(position);
                checkbox.setChecked(mCheckStates.get(position, false));
                checkbox.setOnCheckedChangeListener(this);


            for(int x=0;x<restoredId.length;x++){
                if(restoredId[x]==position){
                    checkbox.setChecked(true);
                }
            }

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(name1.get(position).charAt(0)+"", ColorGenerator.MATERIAL.getRandomColor());

            ImageView image = (ImageView) convertView.findViewById(R.id.imgLetter);
            image.setImageDrawable(drawable);

            return convertView;
        }
        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if(size<=5) {
                mCheckStates.put((Integer) buttonView.getTag(), isChecked);
                save();
            }else{
                checkbox.setChecked(false);
                Toast.makeText(getActivity(), "Maximum of 5 Contacts is allowed", Toast.LENGTH_LONG).show();
            }
        }
    }
}