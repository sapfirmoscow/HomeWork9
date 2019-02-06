package ru.sberbank.homework9.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.sberbank.homework9.model.Note;

@Dao
public interface NoteDAO {

    @Query("select * from note")
    List<Note> getNotes();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(Note note);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Note note);

    @Delete
    void delete(Note note);


}
