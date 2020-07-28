package in.orange.noticeboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoticeDatabase{
    private Firebase root;

    private static final String DB_NAME="noticeboard.db";
    private static final int DB_VERSION=1;

    private static final String TABLE_NAME="sahildb";
    private static final String TABLE_PIN="pindb";
    private int id;
    private static Date d;

    private static final String COLUMN_ID="notice_id";
    private static final String COLUMN_SUBJECT="notice_subject";
    private static final String COLUMN_SUMMARY="notice_summary";
    private static final String COLUMN_REL_DATE="notice_rel_date";
    private static final String COLUMN_END_DATE="notice_end_date";
    private static final String COLUMN_FLAG="notice_flag";
    private static final String COLUMN_URL="notice_url";
    private static final String COLUMN_STATUS="notice_status";
    private static final String COLUMN_NAME="depart_name";
    private static final String COLUMN_B="notice_b";
    private static final String COLUMN_PIN = "notice_p";

    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private static NoticeDatabase noticeDatabaseInstance;
    private Cursor cursor;

    public NoticeDatabase(Context context)
    {
        this.context=context;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sqliteDatabase=new NoticeDBHelper(this.context,DB_NAME,null,DB_VERSION).getWritableDatabase();
        root=new Firebase("https://apponfire-4b488.firebaseio.com/Notice");
        d=NoticeBoard.getDate();
    }

    public static NoticeDatabase getNoticeDatabaseInstance(Context context)
    {
        if(noticeDatabaseInstance==null){
            noticeDatabaseInstance=new NoticeDatabase(context);
        }
        d=NoticeBoard.getDate();
        return noticeDatabaseInstance;
    }

    public boolean insert(String Table,int id,Notice n){
        Cursor cursor=sqliteDatabase.query(TABLE_NAME,new String[]{},COLUMN_NAME+"=? ",new String[]{Table},null,null,COLUMN_ID+" DESC",null);
        int max=-1;
        try{
            cursor.moveToNext();
            max=Integer.parseInt(cursor.getString(0));
        }catch(Exception e){}
        if(max==-1 || max<id){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, id);
            contentValues.put(COLUMN_NAME,Table);
            contentValues.put(COLUMN_SUBJECT, n.subject);
            contentValues.put(COLUMN_SUMMARY, n.summary);
            contentValues.put(COLUMN_REL_DATE, n.release_date);
            contentValues.put(COLUMN_END_DATE, n.end_date);
            contentValues.put(COLUMN_FLAG, n.file);
            contentValues.put(COLUMN_URL, n.data);
            contentValues.put(COLUMN_STATUS, 0);
            contentValues.put(COLUMN_B,0);
            contentValues.put(COLUMN_PIN,0);
            return (0<sqliteDatabase.insert(TABLE_NAME,null,contentValues));
        }
        return false;
    }

    public boolean update(int id,String category)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_STATUS,1);
        return sqliteDatabase.update(TABLE_NAME,contentValues,COLUMN_ID+"=? AND "+COLUMN_NAME+"=?",new String[]{id+"",category})>0;
    }

    public boolean updatepin(String category,String id,String pin)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_PIN,pin);
        return sqliteDatabase.update(TABLE_NAME,contentValues,COLUMN_ID+"=? AND "+COLUMN_NAME+"=?",new String[]{id+"",category})>0;
    }

    public boolean update(String category,String id,Notice n)
    {
        sqliteDatabase.delete(TABLE_NAME,COLUMN_ID+"="+id+" AND "+COLUMN_NAME+"="+category,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_NAME, category);
        contentValues.put(COLUMN_SUBJECT, n.subject);
        contentValues.put(COLUMN_SUMMARY, n.summary);
        contentValues.put(COLUMN_REL_DATE, n.release_date);
        contentValues.put(COLUMN_END_DATE, n.end_date);
        contentValues.put(COLUMN_FLAG, n.file);
        contentValues.put(COLUMN_URL, n.data);
        contentValues.put(COLUMN_STATUS, 0);
        contentValues.put(COLUMN_B,0);
        long x=0;
        try{
            x=sqliteDatabase.insert(TABLE_NAME,null,contentValues);
        }catch(SQLiteConstraintException e){}
        return x>0;
    }

    public boolean updateb(String id,String category)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_B,1);
        return sqliteDatabase.update(TABLE_NAME,contentValues,COLUMN_ID+"=? AND "+COLUMN_NAME+"=?",new String[]{id,category})>0;
    }

    public boolean updatex()
    {
        sqliteDatabase.delete(TABLE_NAME,COLUMN_B+"=? ",new String[]{"0"});
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_B,0);
        return sqliteDatabase.update(TABLE_NAME,contentValues,COLUMN_B+"=? ",new String[]{"1"})>0;
    }

    public void putNotices(final String category)
    {
        root.child(category).addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                String id=dataSnapshot.getKey();
                insert(category, Integer.parseInt(id),dataSnapshot.getValue(Notice.class));
                //updateb(id,category);
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s){
                //update(category,dataSnapshot.getKey(),dataSnapshot.getValue(Notice.class));
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot){
                //sqliteDatabase.delete(TABLE_NAME,COLUMN_ID+"="+dataSnapshot.getKey()+" AND "+COLUMN_NAME+"="+category,null);
            }
            @Override public void onChildMoved(DataSnapshot dataSnapshot,String s){}
            @Override public void onCancelled(FirebaseError firebaseError){}
        });
    }

    public List<DisplayNotice> getPinnedNotices(String category)
    {
        List<DisplayNotice> noticeList=new ArrayList<DisplayNotice>();
        Cursor cursor;
        if(!category.equals(("All"))){
            cursor=sqliteDatabase.query(TABLE_NAME,new String[]{},COLUMN_PIN+" =? AND "+COLUMN_NAME+" =? ",
                new String[]{"1",category},null,null,"date("+COLUMN_REL_DATE+")",null);
        }
        else{
            cursor=sqliteDatabase.query(TABLE_NAME,new String[]{},COLUMN_PIN+" =? ",
                new String[]{"1"},null,null,COLUMN_REL_DATE,null);
        }
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                DisplayNotice notice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),
                        cursor.getString(10));
                noticeList.add(0,notice);
            }
        }
        return noticeList;
    }

    public List<DisplayNotice> getAllNotices(String[] category,int id,String status){
        List<DisplayNotice> noticeList=new ArrayList<DisplayNotice>();
        Cursor cursor=sqliteDatabase.query(false,TABLE_NAME,new String[]{},COLUMN_STATUS+" =? AND "
                        +COLUMN_ID+" >? AND ("+COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "
                        +COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "+COLUMN_NAME+"=? OR "
                        +COLUMN_NAME+"=?) ",new String[]{status,id+"",category[10],category[1],
                category[2],category[3],category[4],category[5],category[6],category[7],category[8],category[9]},
                null,null,COLUMN_REL_DATE,null);
        if(cursor!=null & cursor.getCount()>0)
        {
            while(cursor.moveToNext()){
                Log.d("noticex",cursor.getString(0)+" "+cursor.getString(1));
                if(d.before(new Date(Long.parseLong(cursor.getString(5)))) && status.equals("0")){
                    DisplayNotice notice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                            cursor.getString(2),cursor.getString(3),cursor.getString(4),
                            cursor.getString(5),cursor.getString(6),cursor.getString(7),
                            cursor.getString(10));
                    Log.d("noticep",cursor.getString(0)+" "+cursor.getString(1));
                    noticeList.add(0, notice);
                }
                else if(d.after(new Date(Long.parseLong(cursor.getString(5)))) && status.equals("1")){
                    DisplayNotice notice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                            cursor.getString(2),cursor.getString(3),cursor.getString(4),
                            cursor.getString(5),cursor.getString(6),cursor.getString(7),
                            cursor.getString(10));
                    Log.d("noticep",cursor.getString(0)+" "+cursor.getString(1));
                    noticeList.add(0, notice);
                }
                else{
                    update(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
                }
            }
            for(int i=0;i<noticeList.size();i++)
            {
                Log.d("noticez",noticeList.get(i).id+" "+noticeList.get(i).name);
            }
        }
        return noticeList;
    }

    public List<DisplayNotice> getAllNotices(String category,int id,String status){
        List<DisplayNotice> noticeList=new ArrayList<DisplayNotice>();
        Cursor cursor=sqliteDatabase.query(TABLE_NAME,new String[]{},COLUMN_STATUS+" =? AND "
                +COLUMN_ID+" >? AND "+COLUMN_NAME+"=? ",new String[]{status,id+"",category},null,null,COLUMN_ID,null);
        if(cursor!=null & cursor.getCount()>0)
        {
            while(cursor.moveToNext()){
                if(d.before(new Date(Long.parseLong(cursor.getString(5)))) && status.equals("0")){
                    DisplayNotice notice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                            cursor.getString(2),cursor.getString(3), cursor.getString(4),
                            cursor.getString(5),cursor.getString(6),cursor.getString(7),
                            cursor.getString(10));
                    Log.d("noticey",cursor.getString(0)+" "+cursor.getString(1));
                    noticeList.add(0, notice);
                }
                else if(d.after(new Date(Long.parseLong(cursor.getString(5)))) && status.equals("1")){
                    DisplayNotice notice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                            cursor.getString(2),cursor.getString(3),cursor.getString(4),
                            cursor.getString(5),cursor.getString(6),cursor.getString(7),
                            cursor.getString(10));
                    Log.d("noticep",cursor.getString(0)+" "+cursor.getString(1));
                    noticeList.add(0, notice);
                }
                else{
                    update(Integer.parseInt(cursor.getString(0)),category);
                }
            }
            for(int i=0;i<noticeList.size();i++)
            {
                Log.d("noticec",noticeList.get(i).id+" "+noticeList.get(i).name);
            }
        }
        return noticeList;
    }

    public List<DisplayNotice> getCreateNotice(String category)
    {
        List<DisplayNotice> noticeList=new ArrayList<>();
        Cursor cursor=sqliteDatabase.query(TABLE_NAME,new String[]{},COLUMN_NAME+" =? ",new String[]{category},null,null,COLUMN_ID);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                DisplayNotice notice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),
                        cursor.getString(10));
                noticeList.add(0, notice);
            }
        }
        return noticeList;
    }

    public DisplayNotice getNotice(String id,String category)
    {
        DisplayNotice displayNotice=new DisplayNotice();
        Cursor cursor=sqliteDatabase.query(TABLE_NAME,new String[]{},COLUMN_NAME+" =? AND "+COLUMN_ID+" =? ",new String[]{category,id},null,null,null);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                displayNotice=new DisplayNotice(cursor.getString(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),
                        cursor.getString(10));
            }
        }
        return displayNotice;
    }

    private static class NoticeDBHelper extends SQLiteOpenHelper{



        public NoticeDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int dbVersion)
        {
            super(context,databaseName,factory,dbVersion);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+TABLE_NAME+"("+COLUMN_ID+" INTEGER NOT NULL, "+COLUMN_NAME+" TEXT NOT NULL, "+COLUMN_SUBJECT
                    +" TEXT NOT NULL, "+COLUMN_SUMMARY+" TEXT NOT NULL, "+COLUMN_REL_DATE+" TEXT NOT NULL, "+COLUMN_END_DATE+" TEXT NOT NULL, "
                    +COLUMN_FLAG+" TEXT NOT NULL, "+COLUMN_URL+" TEXT NOT NULL, "+COLUMN_STATUS+" INTEGER NOT NULL, "+COLUMN_B+" INTEGER NOT NULL, "
                    +COLUMN_PIN+ " INTEGER NOT NULL, PRIMARY KEY ("+COLUMN_ID+","+COLUMN_NAME+"))");
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
    }
}
