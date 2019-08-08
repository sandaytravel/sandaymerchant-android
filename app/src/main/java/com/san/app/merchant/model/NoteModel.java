package com.san.app.merchant.model;


public class NoteModel {
    String Note = "";
    String date = "";

    public NoteModel(String note, String date) {
        Note = note;
        this.date = date;
    }

    public String getNote() {
        return Note;
    }

    public String getDate() {
        return date;
    }
}
