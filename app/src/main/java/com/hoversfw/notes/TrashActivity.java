package com.hoversfw.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private RecyclerviewAdapter adapter;
    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayList<RecyclerviewItem> list=new ArrayList<>();
        manager=new LinearLayoutManager(this);
        adapter=new RecyclerviewAdapter(list);
        recycler=findViewById(R.id.trashRecycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                File dir=new File(getFilesDir()+"/trash");
                final File file=new File(dir, list.get(position).getTitle());
                final File destination=new File(getFilesDir()+"/notes/"+list.get(position).getTitle());
                if(destination.exists()) {
                    AlertDialog.Builder replace = new AlertDialog.Builder(TrashActivity.this);
                    replace.setTitle("Replace file?")
                            .setMessage("There's already a file with the same title. When you restore, it will replaced that note with this one. Replace?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    file.renameTo(destination);
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result", 1);
                                    setResult(1, returnIntent);
                                    Toast.makeText(TrashActivity.this, "Restored and replaced", Toast.LENGTH_SHORT).show();
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
                    adapter.removeByTitle(list.get(position).getTitle());
                    Toast.makeText(TrashActivity.this, "Restored", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //get all files, put them in a list and then setList()
        String path=getFilesDir()+"/trash";
        File directory=new File(path);
        File[] files=directory.listFiles();
        FileInputStream fis = null;
        if(files!=null) {
            for (File file : files) {
                try {
                    fis = new FileInputStream(new File(path+"/"+file.getName()));
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String text;
                    String product;
                    while ((text = br.readLine()) != null) {
                        sb.append(text).append("\n");
                    }
                    if(sb.toString().length()>100){
                        product=sb.toString().substring(0,100)+"...";
                    }
                    else{
                        product=sb.toString();
                    }
                    RecyclerviewItem item = new RecyclerviewItem(file.getName(), product);
                    list.add(item);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        adapter.setList(list);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}