package com.example.ninh.facenewsprt3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by ninh on 22/06/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDatabase.db";
    private static final String TABLE_LIST = "list";
    private static final String TABLE_IMAGE = "image";
    private static final String TABLE_PROFILECOVERPIC = "profilecoverpic";
    private static final String COLUMN_IDN = "idn";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_PATH = "imagePath";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_ID_PERSON = "id_person";
    private static final String COLUMN_PROFILE_PIC = "profilePath";
    private static final String COLUMN_COVER_PIC = "coverPath";
    private static final String DROP_LIST = "DROP TABLE IF EXISTS list";
    private static final String DROP_IMAGE = "DROP TABLE IF EXISTS image";
    private static final String DROP_PROFILECOVERPIC = "DROP TABLE IF EXISTS profilecoverpic";

    Context context;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table list " +"(id integer primary key, idn text, title text, time text, link text, id_person text)"
        );

        db.execSQL(
                "create table image " +"(id integer primary key, id_person text, imagePath text)"
        );

        db.execSQL(
                "create table profilecoverpic " +"(id integer primary key, profilePath text, coverPath text)"
        );
    }

    public boolean insertList  (String idn, String title, String time, String link, String id_person)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_IDN, idn);
            contentValues.put(COLUMN_TITLE, title);
            contentValues.put(COLUMN_TIME,time);
            contentValues.put(COLUMN_LINK,link);
            contentValues.put(COLUMN_ID_PERSON,id_person);

            db.insert(TABLE_LIST, null, contentValues);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public void insertImage(String id_person, String imagePath)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_ID_PERSON, id_person);
            contentValues.put(COLUMN_IMAGE_PATH, imagePath);

            db.insert(TABLE_IMAGE, null, contentValues);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void insertProfileCoverPic(String profilePath, String coverPath)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_PROFILE_PIC, profilePath);
            contentValues.put(COLUMN_COVER_PIC, coverPath);

            db.insert(TABLE_PROFILECOVERPIC, null, contentValues);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList getProfileCoverPic() {
        String profile;
        String cover;
        ArrayList array_list = new ArrayList();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.query(TABLE_PROFILECOVERPIC, new String[]{COLUMN_PROFILE_PIC, COLUMN_COVER_PIC}, null, null, null, null, null);
            res.moveToFirst();
            profile = res.getString(res.getColumnIndex(COLUMN_PROFILE_PIC));
            cover = res.getString(res.getColumnIndex(COLUMN_COVER_PIC));

            array_list.add(profile);
            array_list.add(cover);

            res.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return array_list;
    }

    public ArrayList getPersonId() {
        ArrayList array_list = new ArrayList();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.query(TABLE_IMAGE, new String[]{COLUMN_ID_PERSON}, null, null, null, null, null);

            res.moveToFirst();
            while (!res.isAfterLast()) {

                array_list.add(
                                res.getString(res.getColumnIndex(COLUMN_ID_PERSON))

                );
                res.moveToNext();
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array_list;
    }

    public String getImagePathFromIDPerson(String id_person) {
        String imagePath = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.query(TABLE_IMAGE, new String[]{COLUMN_IMAGE_PATH}, "id_person = " + id_person, null, null, null, null);
            res.moveToFirst();
            imagePath = res.getString(res.getColumnIndex(COLUMN_IMAGE_PATH));

            res.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return imagePath;
    }

    public ArrayList getList() {
        ArrayList array_list = new ArrayList();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.query(TABLE_LIST, null, null, null, null, null, null);

            res.moveToFirst();
            while (!res.isAfterLast()) {

                array_list.add(
                        new Item(
                                null,
                                res.getString(res.getColumnIndex(COLUMN_IDN)),
                                res.getString(res.getColumnIndex(COLUMN_TITLE)),
                                res.getString(res.getColumnIndex(COLUMN_TIME)),
                                res.getString(res.getColumnIndex(COLUMN_LINK)),
                                res.getString(res.getColumnIndex(COLUMN_ID_PERSON)))

                );
                res.moveToNext();
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array_list;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIST, null, null);
        db.delete(TABLE_IMAGE, null, null);
        db.delete(TABLE_PROFILECOVERPIC, null, null);
    }

        @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL(DROP_LIST);
            db.execSQL(DROP_IMAGE);
            db.execSQL(DROP_PROFILECOVERPIC);
            onCreate(db);
    }
}
