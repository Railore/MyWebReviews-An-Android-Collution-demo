package androidbuffer.com.simplecontactviewer1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

// Ce code a été inspirer de https://github.com/papusingh/
// ce code a été fait à partir des explication de https://www.youtube.com/watch?v=Bsm-BlXo2SI&ab_channel=CodinginFlow


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<ContactData> contactDataList = new ArrayList<>();

    ContactAdapter contactAdapter;

    private static String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CONTACT = 1;
    private Button button;
    public String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv);
        setDataToAdapter();
        requestContactsPermissions();

        button = (Button)findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                json = new Gson().toJson(contactDataList);
                openDialog();
            }
        });

    }
    public void openDialog(){
        RateAppDialog exampleDialog = new RateAppDialog();
        Bundle argument = new Bundle();
        argument.putString("dataToSend",json);

        exampleDialog.setArguments(argument);
        exampleDialog.show(getSupportFragmentManager(),"example dialog");
    }

    private void setDataToAdapter(){
        contactAdapter = new ContactAdapter(contactDataList);
        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactAdapter);
    }

    //Methode qui permet de recuperer les informations

    private void getContactInfo(){
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,null,null,null,DISPLAY_NAME);

        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String CONTACT_ID = cursor.getString(cursor.getColumnIndex(ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));
                ContactData contactData = new ContactData();
                if (hasPhoneNumber > 0){
                    contactData.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(PHONE_URI, new String[]{NUMBER},PHONE_ID+" = ?",new String[]{CONTACT_ID},null);
                    List<String> contactList = new ArrayList<>();
                    phoneCursor.moveToFirst();
                    while (!phoneCursor.isAfterLast()){
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ","");
                        contactList.add(phoneNumber);
                        phoneCursor.moveToNext();
                    }
                    contactData.setContactNumber(contactList);
                    contactDataList.add(contactData);
                    phoneCursor.close();
                }
            }
            contactAdapter.notifyDataSetChanged();
        }
    }

    // Methode qui permet de demander les permissions de lecture
    public void requestContactsPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){

                Snackbar.make(recyclerView, "permission Contact", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
            }
        } else {
            getContactInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults){
            if (result == PackageManager.PERMISSION_GRANTED){
                getContactInfo();
            }
        }
    }
}
