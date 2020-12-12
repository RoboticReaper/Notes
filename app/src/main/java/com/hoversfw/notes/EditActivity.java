package com.hoversfw.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditActivity extends AppCompatActivity {
    Boolean saved=false;
    Boolean titled=false;
    String title="New Note";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText editText=findViewById(R.id.edit_text);
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saved=false;
            }
        };
        editText.addTextChangedListener(textWatcher);

        String action=getIntent().getStringExtra("ACTION");
        if(action.equals("new")){
            saved=true;
            titled=false;
        }
        if(action.equals("load")){
            title=getIntent().getStringExtra("FILENAME");
            try {
                FileInputStream fis = new FileInputStream(new File(getFilesDir() + "/notes/" + title));
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;
                while ((text = br.readLine()) != null) {
                    sb.append(text).append("\n");
                }
                editText.setText(sb.toString());
            } catch (IOException ignore){}
            saved=true;
            titled=true;
        }

        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(!saved){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Exit without saving?")
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create().show();
                    return true;
                }
                else{
                    finish();
                    return true;
                }
            case R.id.save_note:
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view == null) {
                    view = new View(EditActivity.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                save();
                return true;
            case R.id.open_edit_settings:
                Intent intent=new Intent(this,EditSetting.class);
                intent.putExtra("FILENAME", title);
                startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==1){
            finish();
        }
        if(requestCode==1&&resultCode==2){
            title=data.getStringExtra("resultTitle");
            getSupportActionBar().setTitle(title);
            titled=true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.edit_menu,menu);
        return true;
    }

    public void save(){
        if(Build.VERSION.SDK_INT>=23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        final Runnable run=new Runnable() {
            @Override
            public void run() {
                EditText mEditText=findViewById(R.id.edit_text);
                String content=mEditText.getText().toString();

                FileOutputStream fos;
                try{
                    File folder=new File(getFilesDir()+"/notes");
                    if(!folder.exists()){
                        folder.mkdir();
                    }
                    File newFile=new File(getFilesDir()+File.separator+"/notes/"+title);
                    newFile.createNewFile();
                    fos=new FileOutputStream(newFile);
                    fos.write(content.getBytes());fos.close();
                    saved=true;
                    Snackbar snackbar=Snackbar.make(findViewById(R.id.llayout),"Saved",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (IOException e) {
                    saved=false;
                    Toast.makeText(EditActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if(!titled){
            ViewGroup viewGroup=findViewById(android.R.id.content);
            final View dialogView= LayoutInflater.from(this).inflate(R.layout.title,viewGroup,false);
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setView(dialogView)
                    .setTitle("Give your note a title").setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText t=dialogView.findViewById(R.id.title);
                            title=t.getText().toString();
                            getSupportActionBar().setTitle(title);
                            if(title.equals("")){
                                AlertDialog.Builder builder1=new AlertDialog.Builder(EditActivity.this);
                                builder1.setTitle("Notice")
                                        .setMessage("Keep in mind that next unnamed note will replace this one because their names are the same (unnamed)")
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create().show();
                                title="unnamed";
                                getSupportActionBar().setTitle(title);
                            }
                            titled=true;
                            run.run();
                        }
                    }).create().show();
        }
        else if(saved){
            Snackbar snackbar=Snackbar.make(findViewById(R.id.llayout),"File is already saved",Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        else run.run();
    }

    @Override
    public void onBackPressed() {
        if(!saved){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Exit without saving?")
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setCancelable(false).create().show();
        }
        else{
            finish();
            super.onBackPressed();
        }
    }
}
