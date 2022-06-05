package kr.hnu.mainproject_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase writableDB, readableDB;

    public DBHelper(Context context) {
        super(context, "Messenger.db", null, 3);
        writableDB = getWritableDatabase();
        readableDB = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE person (id TEXT PRIMARY KEY, password TEXT, name TEXT, department TEXT, photo TEXT);");
        db.execSQL("CREATE TABLE msg(sender TEXT, receiver TEXT, title TEXT, time TEXT, contents TEXT, isReply TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS person");
        db.execSQL("DROP TABLE IF EXISTS msg");
        onCreate(db);
    }

    public void deleteAllPerson() {
        writableDB.delete("person", null, null);
    }

    public void deleteAllMsg() {
        writableDB.delete("msg", null, null);
    }

    public void addPerson(Person person) {
        ContentValues values = new ContentValues();
        values.put("id", person.getID());
        values.put("password", person.getPassword());
        values.put("name", person.getName());
        values.put("department", person.getDepartment());
        values.put("photo", person.getPhoto());
        writableDB.insert("person", null, values);
    }

    public void addMsg(Msg msg) {
        ContentValues values = new ContentValues();
        values.put("sender", msg.getSender());
        values.put("receiver", msg.getReceiver());
        values.put("title", msg.getTitle());
        values.put("time", msg.getTime());
        values.put("contents", msg.getContents());
        values.put("isReply", msg.getIsReply());
        writableDB.insert("msg", null, values);

    }

    public void updatePerson(String id, String newPassword, String newName, String newDepartment, String newPhoto) {
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        values.put("name", newName);
        values.put("department", newDepartment);
        values.put("photo", newPhoto);
        writableDB.update("person", values, "id = ?", new String[] {id});
    }

    public void updateMsg(String msgTime, String isReply) {
        ContentValues values = new ContentValues();
        values.put("isReply", isReply);
        writableDB.update("msg", values,"time = ?", new String[] {msgTime});
    }

    public Cursor getCursorUserFromPerson(String id, String password) {
        String selectQuery = "SELECT id, password, name, department, photo FROM person WHERE id = ? AND password = ?;";
        return readableDB.rawQuery(selectQuery, new String[] {id, password});
    }

    public Cursor getCursorUserFromPerson(String id) {
        String selectQuery = "SELECT id, password, name, department, photo FROM person WHERE id = ?;";
        return readableDB.rawQuery(selectQuery, new String[] {id});
    }

    public Cursor getCursorReceiveMessageFromMsg(Person person) {
        String receiver = person.getID();
        String selectQuery = "SELECT sender, receiver, title, time, contents, isReply FROM msg WHERE receiver = ?;";
        return readableDB.rawQuery(selectQuery, new String[] {receiver});
    }

    public Cursor getCursorSendMessageFromMsg(Person person) {
        String sender = person.getID();
        String selectQuery = "SELECT sender, receiver, title, time, contents, isReply FROM msg WHERE sender = ?;";
        return readableDB.rawQuery(selectQuery, new String[] {sender});
    }

    public Cursor getCursorMessageFromMsg(String time) {
        String selectQuery = "SELECT sender, receiver, title, time, contents, isReply FROM msg WHERE time = ?;";
        return readableDB.rawQuery(selectQuery, new String[] {time});
    }

}
