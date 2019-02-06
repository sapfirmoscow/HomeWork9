package ru.sberbank.homework9;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ru.sberbank.homework9.db.NoteDB;
import ru.sberbank.homework9.model.Note;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "notes";
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteDB mNoteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListeners();
        initRecyclerView();
        initRoom();

        downloadNotes();
    }

    private void initRoom() {
        mNoteDB = Room.databaseBuilder(getApplicationContext(), NoteDB.class, DATABASE_NAME).allowMainThreadQueries().build();

    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        mMyAdapter = new MyAdapter(MainActivity.this, this::openNote, this::deleteNote);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMyAdapter);
    }



    //метод используется когда мы хотим отредачить записку
    private void openNote(Note note) {
        Intent intent = CreateNoteActivity.newIntent(MainActivity.this);
        intent.putExtra("note", note);
        startActivityForResult(intent, 2);
    }

    private void initListeners() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CreateNoteActivity.newIntent(MainActivity.this);
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.floatingActionButton), "", Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));


        switch (resultCode) {
            case 1:
                adddNote((Note) data.getExtras().get("note"));
                downloadNotes();
                snackbar.setText("Note added");
                snackbar.show();
                break;
            case 2:
                updateNote((Note) data.getExtras().get("note"));
                downloadNotes();
                snackbar.setText("Note updated");
                snackbar.show();
                break;
            case 3:
                snackbar.setText("Text size updated");
                snackbar.show();
                mMyAdapter.invalidateItems();
                break;
        }
    }


    private void initView() {
        mFloatingActionButton = findViewById(R.id.floatingActionButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }


    private void adddNote(Note note) {
        new Thread(() -> mNoteDB.getNoteDAO().insert(note)).start();
    }

    private void updateNote(Note note) {

        new Thread(() -> mNoteDB.getNoteDAO().update(note)).start();

    }

    private void downloadNotes() {
        new Thread(() -> mMyAdapter.setNotes(mNoteDB.getNoteDAO().getNotes())).start();
    }

    private void deleteNote(Note note) {
        new Thread(() -> {
            mNoteDB.getNoteDAO().delete(note);
            downloadNotes();
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting_menu) {
            Intent intent = SettingActivity.newIntent(MainActivity.this);
            startActivityForResult(intent, 3);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
