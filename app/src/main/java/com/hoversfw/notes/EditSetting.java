package com.hoversfw.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class EditSetting extends AppCompatActivity {
    String title;
    EditText setTitle;
    SwitchCompat switchCompat;
    Boolean auto_del=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_setting);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title=getIntent().getStringExtra("FILENAME");
        setTitle=findViewById(R.id.setTitle);
        setTitle.setText(title);

        switchCompat=findViewById(R.id.del);
        SharedPreferences sharedPreferences=getSharedPreferences("auto_del",MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("auto_del",false));
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchCompat.isChecked()){
                    SharedPreferences.Editor editor=getSharedPreferences("auto_del", MODE_PRIVATE).edit();
                    editor.putBoolean("auto_del",true);
                    editor.apply();
                    switchCompat.setChecked(true);
                    auto_del=true;
                }
                else{
                    SharedPreferences.Editor editor=getSharedPreferences("auto_del", MODE_PRIVATE).edit();
                    editor.putBoolean("auto_del",false);
                    editor.apply();
                    switchCompat.setChecked(false);
                    auto_del=false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            save();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void Delete(View v){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this note?")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(auto_del) {
                            File dir = new File(getFilesDir() + "/notes");
                            if (title == null) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result", 1);
                                setResult(1, returnIntent);
                                finish();
                            } else {
                                File file = new File(dir, title);
                                file.delete();
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result", 1);
                                setResult(1, returnIntent);
                                finish();
                            }
                            Toast.makeText(EditSetting.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            File dir=new File(getFilesDir()+"/notes");
                            if (title == null) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result", 1);
                                setResult(1, returnIntent);
                                finish();
                            } else {
                                File d=new File(getFilesDir()+"/trash");
                                if(!d.exists()){
                                    d.mkdir();
                                }
                                final File file = new File(dir, title);
                                final File destination=new File(getFilesDir()+"/trash/"+title);
                                if(destination.exists()){
                                    AlertDialog.Builder replace=new AlertDialog.Builder(EditSetting.this);
                                    replace.setTitle("Replace file?")
                                            .setMessage("There's already a file with the same title in trash as this one. Replace that one?")
                                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    file.renameTo(destination);
                                                    Intent returnIntent = new Intent();
                                                    returnIntent.putExtra("result", 1);
                                                    setResult(1, returnIntent);
                                                    Toast.makeText(EditSetting.this, "Moved to trash and replaced", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create().show();
                                }
                                else {
                                    file.renameTo(destination);
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result", 1);
                                    setResult(1, returnIntent);
                                    Toast.makeText(EditSetting.this, "Moved to trash", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                    }
                }).create().show();
    }

    private void save() {
        if(!setTitle.getText().toString().equals(title)) {
            File dir = new File(getFilesDir() + "/notes");
            File file = new File(dir, title);
            File newFile = new File(dir, setTitle.getText().toString());
            file.renameTo(newFile);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("resultTitle", setTitle.getText().toString());
            setResult(2, returnIntent);
        }
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    public void DeleteInfo(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(EditSetting.this);
        builder.setTitle("Delete Permanently")
                .setMessage("If you turn this on, when you press delete it will directly delete instead of moving to trash.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }
}