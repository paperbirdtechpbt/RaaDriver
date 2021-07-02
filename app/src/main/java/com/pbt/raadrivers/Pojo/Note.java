package com.pbt.raadrivers.Pojo;

public class Note {
    public static final String TABLE_NAME = "address";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MAIN_TEXT = "main_text";
    public static final String COLUMN_DESCRIPTION = "description";

    private int id;
    private String note;
    private String timestamp;
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MAIN_TEXT + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT"
                    + ")";

    public Note() {
    }

    public Note(int id, String note, String timestamp) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}