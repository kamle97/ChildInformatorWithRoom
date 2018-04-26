package com.example.kamle.childinformatorwithroom;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyDatabase database;
    final String DATABASE_NAME = "children.db";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    EditText editName;
    EditText editSurname;

    private Spinner createSpinner() {
        Integer[] childAge = {3, 4, 5, 6, 7, 8};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, childAge);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private String createTxtFile(String name, String surname, int age, String content) throws IOException {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                Log.e("MyLog, zapytano", "tak, zapytano");
        }

        File root = null, file = null;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            TextView statusBar = (TextView) findViewById(R.id.statusBar);
            statusBar.setText("Nie przyznano pozwolenia - nie można dodać");
            Log.e("MyLog", "Nie przyzznano pozwolenia");
            statusBar.setBackgroundColor(Color.RED);
        } else {
            root = new File(Environment.getExternalStorageDirectory(), "childInfo");
            if (!root.exists()) {
                root.mkdirs();
                root.createNewFile();
            }
            file = new File(root, name + "_" + surname + ".txt");
            Log.e("MyLog, path", String.valueOf(root.getAbsolutePath()));
            Log.e("MyLog, imie_nazw", name + "_" + surname);
            FileWriter writer = new FileWriter(file);
            writer.write("Imie: "+name+"\nNazwisko: "+surname+"\nWiek: "+age+"\nDodatkowe informacje:\n"+content);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
        }
        String path = root.getAbsolutePath() + "/" + file.getName();
        return path;

    }

    private void addNewChildToDatabase(final String name, final String surname, final int age) {


        List<Person> allPeople = database.personDao().getAllPeople();

        final TextView statusBar = (TextView) findViewById(R.id.statusBar);

        for(Person p : allPeople)
        {
            if(p.getName().equals(name) && p.getSurname().equals(surname))
            {
                Log.i("MyLog", "Znaleziono "+name+"_"+surname);
                statusBar.setText("Znaleziono w bazie danych");
                editSurname.setEnabled(false);
                editName.setEnabled(false);
                try {
                    showChildInfo(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        statusBar.setText("Nie znaleziono w bazie, podaj informacje o dziecku!");
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(null);

        editName.setEnabled(false);
        editSurname.setEnabled(false);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.childInfo);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText addChildInfo = new EditText(this);
        addChildInfo.setHint("Tu wpisz informacje o dziecku");
        addChildInfo.setLayoutParams(params);
        linearLayout.addView(addChildInfo);
        Button confirmButton = new Button(this);
        confirmButton.setText("Potwierdź");
        confirmButton.setLayoutParams(params);
        linearLayout.addView(confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String path = createTxtFile(name, surname, age, addChildInfo.getText().toString());
                    database.personDao().addPerson(new Person(name, surname, age, path));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    linearLayout.removeAllViews();
                    statusBar.setText("Pomyślnie zapisano");
                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

            }
        });
    }

    private void showChildInfo(Person child) throws IOException {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            Log.i("MyLog", "Read");
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            TextView statusBar = (TextView) findViewById(R.id.statusBar);
            statusBar.setText("Nie przyznano pozwolenia - nie można odczytać informacji");
            statusBar.setBackgroundColor(Color.RED);
        } else {
            File file = new File(child.getPath());
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            Log.e("MyLog, tresc", sb.toString());

            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.childInfo);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView info = new TextView(this);
            info.setLayoutParams(params);
            info.setText(sb.toString());
            linearLayout.addView(info);
            Button okButton = new Button(this);
            okButton.setLayoutParams(params);
            okButton.setText("OK");
            linearLayout.addView(okButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linearLayout.removeAllViews();
                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        editName = (EditText) findViewById(R.id.name);
        editSurname = (EditText) findViewById(R.id.surname);

        final Spinner spinner = createSpinner();
        final TextView statusBar = (TextView) findViewById(R.id.statusBar);
        Log.i("MyLog", "Inicjalizacja DB");
        database = Room.databaseBuilder(
                getApplicationContext(),
                MyDatabase.class,
                DATABASE_NAME
        ).allowMainThreadQueries().build();
        Log.i("MyLog", "Udało się");

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editName != null && editSurname != null &&
                        !editName.getText().toString().equals("") && !editSurname.getText().toString().equals("")) {
                    String nameString = editName.getText().toString();
                    String surnameString = editSurname.getText().toString();
                    int age = Integer.parseInt(spinner.getSelectedItem().toString());
                    addNewChildToDatabase(nameString, surnameString, age);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
