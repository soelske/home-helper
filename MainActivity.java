package com.example.bartsuelze.homehelper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // UI variables
    private EditText editText;
    private ListView listView;
    private String macAddress = "user";
    private String buttonPressed = "add";

    // File variables
    private final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Homehelper";
    private final File file = new File(path + File.separator + "savedfile.txt");

    public ArrayAdapter<String> adapter;
    public ArrayList<String> showList;
    public ArrayList<String> homeList;
    public String[] actions = {"added", "cleared all items"};

    // variables for permissions
    private boolean permissionCheck = false;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_add:
                    buttonPressed = "add";
                    buttonSave();
                    buttonLoad();
                    return true;
                case R.id.navigation_delete:
                    buttonPressed = "delete";
                    buttonReset();
                    buttonLoad();
                    return true;
                case R.id.navigation_notifications:
                    buttonPressed = "notification";
                    buttonLoad();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkWriteExternalStoragePermission();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        listView = (ListView) findViewById(R.id.listView);
        showList = new ArrayList<>();
        homeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.lvItem,showList);

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setText("");
                return false;
            }
        });

        if(permissionCheck) {
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        macAddress = IDgenerator.getID();

        buttonLoad();
        listView.setAdapter(adapter);



    }

    public void buttonSave() {
        if (editText.getText().toString().equals("Type something here") || editText.getText().length() == 0) {
            editText.setText("Type something here");
            return;
        }

        if(permissionCheck) {
            String[] saveText = {""};
            saveText[0] = String.valueOf(editText.getText())/*.split(System.getProperty("line.separator"))*/;
            Save(file, saveText, true);
            saveText[0] = String.valueOf(macAddress + " added item " + editText.getText().toString())/*.split(System.getProperty("line.separator"))*/;
            Save(file, saveText, true);
            Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_LONG).show();
        }else{
            addToListview(editText.getText().toString());
        }

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute("save", macAddress, editText.getText().toString(), "item");
        new BackgroundWorker(this).execute("save", macAddress, macAddress + " added item " + editText.getText().toString(), "notification");

        editText.setText("");
    }

    public void buttonLoad() {
        clearListview();

        if(permissionCheck) {
            String[] loadText = Load(file);
            for (int i = 0; i < loadText.length; i++) {
                addToListview(loadText[i]);
            }
        }

        new BackgroundWorker(new BackgroundWorker.AsyncResponse() {

            @Override
            public void processFinish(ArrayList databaseList) {
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.
                syncLists(homeList,databaseList);
            }
        }).execute("load", macAddress, editText.getText().toString());
    }

    public void buttonReset() {
        if(permissionCheck){
            String[] saveText = {""};
            Save(file, saveText, false);
            saveText[0] = String.valueOf(macAddress + " cleared all items")/*.split(System.getProperty("line.separator"))*/;
            Save(file, saveText, false);
        }

        editText.setText("");

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute("clear", macAddress, editText.getText().toString(), "item");
        new BackgroundWorker(this).execute("save", macAddress, macAddress + " cleared all items", "notification");
    }

    public void Save(File file, String[] data, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {

                for (int i = 0; i < data.length; i++) {
                    fos.write(data[i].getBytes());
                    if (i == data.length - 1 && !data[i].equals("")) {
                        fos.write("\n".getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String[] Load(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl = 0;
        try {
            while ((test = br.readLine()) != null) {
                anzahl++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis.getChannel().position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try {
            while ((line = br.readLine()) != null) {
                array[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    private void addToListview(String item) {
        homeList.add(item);
        if(buttonPressed.equals("notification") && (item.contains(actions[0])||item.contains(actions[1]))){
            showList.add(item);
        }
        else if (buttonPressed.equals("add") && !(item.contains(actions[0])||item.contains(actions[1]))){
            showList.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    private void clearListview() {
        homeList.clear();
        showList.clear();
        adapter.notifyDataSetChanged();
    }

    private void syncLists(ArrayList homeList, ArrayList databaseList) {
        boolean found = false;
        for (Object item1 : homeList) {
            for (Object item2 : databaseList) {
                if (item1.equals(item2)) {

                    found = true;
                }
            }
            if (!found && !(item1.toString().contains(actions[0])||item1.toString().contains(actions[1]))) {
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("save", macAddress, item1.toString(), "item");
                new BackgroundWorker(this).execute("save", macAddress, macAddress + " added item " + item1.toString() , "notification");
            }
            found = false;
        }

        for (Object item1 : databaseList) {
            for (Object item2 : homeList) {
                if (item1.equals(item2)) {
                    found = true;
                }
            }
            if (!found) {
                if(permissionCheck) {

                    String[] saveText = {""};
                    saveText[0]= String.valueOf(item1.toString())/*.split(System.getProperty("line.separator"))*/;
                    Save(file, saveText, true);

                }
                addToListview(item1.toString());
            }
            found = false;
        }
    }

    /**
     * Method to check if there is permission to write data to the external memory.
     * Without this permission, the app will still work but with Internet Connection.
     */
    public void checkWriteExternalStoragePermission() {
        ContextCompat.checkSelfPermission(this,      Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }else{
            permissionCheck = true;
        }
    }

    /**
     * Part of the checkWriteExternalStoragePermission method.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    permissionCheck = true;
                } else {
                    showAlert();
                    permissionCheck = false;
                }

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Part of the checkWriteExternalStoragePermission method.
     */
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App only works with internet connection without permission.");
        /*alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /*ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);*/

                    }
                });
        alertDialog.show();
    }
}
