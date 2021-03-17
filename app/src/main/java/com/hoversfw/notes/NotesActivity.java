package com.hoversfw.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    Boolean welcomed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_notes);
                loadNotes();
                welcomed=true;
            }
        }, 1500);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(welcomed){
            loadNotes();
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void loadNotes() throws NullPointerException{
        final ArrayList<RecyclerviewItem> list = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerviewAdapter adapter = new RecyclerviewAdapter(list);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(NotesActivity.this, EditActivity.class);
                intent.putExtra("ACTION", "load");
                intent.putExtra("FILENAME", list.get(position).getTitle());
                startActivity(intent);
            }
        });
        //get all files, put them in a list and then setList()
        String path = getFilesDir() + "/notes";
        File directory = new File(path);
        File[] files = directory.listFiles();
        FileInputStream fis = null;
        if (files != null) {
            for (File file : files) {
                try {
                    fis = new FileInputStream(new File(path + "/" + file.getName()));
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String text;
                    String product;
                    while ((text = br.readLine()) != null) {
                        sb.append(text).append("\n");
                    }
                    if (sb.toString().length() > 100) {
                        product = sb.toString().substring(0, 100) + "...";
                    } else {
                        product = sb.toString();
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
        switch (item.getItemId()) {
            case R.id.new_note:
                Intent intent = new Intent(NotesActivity.this, EditActivity.class);
                intent.putExtra("ACTION", "new");
                startActivity(intent);
                return true;

            case R.id.trash:
                startActivity(new Intent(NotesActivity.this, TrashActivity.class));
                return true;

            case R.id.help:
                final AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.loading, null)).setCancelable(false);
                dialog = builder.create();
                dialog.show();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Questions");
                final String[] message = {"Internet connection needed to get FAQs"};
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        message[0] = (String) snapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
                        builder.setTitle("Help").setMessage(message[0])
                                .setNegativeButton("Send email", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "hoversfw@gmail.com", null));
                                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                    }
                                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCancelable(false).create().show();
                    }
                }, 3000);
                return true;

            case R.id.about:
                AlertDialog.Builder builders = new AlertDialog.Builder(NotesActivity.this);
                builders.setTitle("About this app")
                        .setMessage("This is a note app, used for taking notes. Nothing too complex yet very usable. \n\nHover Software is made by civil software developers, in order to release pure and powerful softwares.\n\nCurrent version: 2.1.1\n\nCopyright Hover Software, all rights reserved.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }
}