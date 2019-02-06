package ru.sberbank.homework9.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.sberbank.homework9.model.Note;

@Database(entities = Note.class, version = 1)
public abstract class NoteDB extends RoomDatabase {
    public abstract NoteDAO getNoteDAO();
}
