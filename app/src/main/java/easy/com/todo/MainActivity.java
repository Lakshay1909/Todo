package easy.com.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    //    ArrayList<String> expenseList;
    ArrayList<Expense> expenseList;
    ExpenseListAdapter expenseListAdapter;
    final static  int NEW_EXPENSE = 1;
    final static int update=1,add=2;
    int pos;
    EditText edit;
    boolean Added_item=false;
    boolean updated_item=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("TODO");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, ExpenseDetailActivity.class);
                i.putExtra(IntentConstants.ADD_ITEM,Added_item);
                startActivityForResult(i,NEW_EXPENSE);
            }
        });
        listView = (ListView) findViewById(R.id.expenseListView);
        expenseList = new ArrayList<>();
        expenseListAdapter = new ExpenseListAdapter(this, expenseList);
        //  expenseListAdapter.setOnListButtonClickedListener(this);
        listView.setAdapter(expenseListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //parent.getAdapter().getItem(position);

                Intent i = new Intent(MainActivity.this, ExpenseDetailActivity.class);
                i.putExtra(IntentConstants.EXPENSE_TITLE,expenseList.get(position).title);
//                i.putExtra("expense", expenseList.get(position));
                //startActivity(i);
                pos=position;
                i.putExtra(IntentConstants.EXPENSE_ID,expenseList.get(position).id);
                i.putExtra(IntentConstants.EXPENSE_DATE,expenseList.get(position).date);
                i.putExtra(IntentConstants.EXPENSE_DESCRIPTION,expenseList.get(position).Description);
                i.putExtra(IntentConstants.EXPENSE_TIME,expenseList.get(position).time);
                i.putExtra(IntentConstants.ADD_ITEM,updated_item);

                startActivityForResult(i, 1);
//              ExpenseDetailActivity.title = "abcd";

//                Toast.makeText(MainActivity.this, expenseList.get(position)
//                        + " Clicked ", Toast.LENGTH_SHORT).show();

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                   AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete");
                builder.setCancelable(false);
                final View v1=view;
            View  v =  getLayoutInflater().inflate(R.layout.dialog_view,null);
                final TextView textView=(TextView) v.findViewById(R.id.dialogTextView);
                textView.setText("Are you sure to delete this ?");
                builder.setView(v);
              final int longclickposition=position;
              final  String snackbartitle=expenseList.get(position).title;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(expenseList.size()==0)
                        {return ;
                        }
                        int id=expenseList.get(longclickposition).id;
                        final int ID=id;
                        ExpenseOpenHelper expenseOpenHelper=new ExpenseOpenHelper(MainActivity.this);
                        final SQLiteDatabase database=expenseOpenHelper.getWritableDatabase();
                        expenseList.remove(longclickposition);
                        expenseListAdapter.notifyDataSetChanged();
                       Snackbar s= Snackbar.make(v1, snackbartitle+" is "+"deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       // Toast.makeText(MainActivity.this,"Snacky ...",Toast.LENGTH_SHORT).show();
                                        Cursor cursor=database.query(ExpenseOpenHelper.EXPENSE_TABLE_NAME,null,ExpenseOpenHelper.EXPENSE_ID+"="+ID,null,null,null,null);
                                        cursor.moveToFirst();
                                        String title = cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_TITLE));
                                        int id = cursor.getInt(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_ID));
                                        String description=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_DESCRIPTION));
                                        String date=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_DATE));
                                        String time=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_TIME));
                                        Expense e = new Expense(id, title,description,date,time);
                                        expenseList.add(longclickposition,e);
                                        expenseListAdapter.notifyDataSetChanged();
                                    }
                                }).addCallback(new Snackbar.Callback()
                        {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if(event==Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
                                {

                                    database.delete(ExpenseOpenHelper.EXPENSE_TABLE_NAME,ExpenseOpenHelper.EXPENSE_ID+"="+ID,null);
                                    AlarmManager am=(AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                                    Intent intent=new Intent(MainActivity.this,MyReceiver.class);
                                    PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,ID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                    am.cancel(pendingIntent);

                                }
                            }
                        });
                        View snackbar_view=s.getView();
                       TextView snackbar_text=(TextView) snackbar_view.findViewById(android.support.design.R.id.snackbar_text);
                       // snackbar_text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.images1,0,0,0);
                        snackbar_text.setGravity(Gravity.CENTER);
                        snackbar_text.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        snackbar_text.setTextColor(getResources().getColor(R.color.white));
                        s.show();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();



                return true;
            }
        });

        updateExpenseList();
    }
    private void updateExpenseList() {

        ExpenseOpenHelper expenseOpenHelper = new ExpenseOpenHelper(this);
        expenseList.clear();
        SQLiteDatabase database = expenseOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(ExpenseOpenHelper.EXPENSE_TABLE_NAME,null,null,null,null, null, null);
        while(cursor.moveToNext()){

            String title = cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_TITLE));
            int id = cursor.getInt(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_ID));
            String description=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_DESCRIPTION));
            String date=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_DATE));
            String time=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_TIME));
            Expense e = new Expense(id, title,description,date,time);
            expenseList.add(e);
        }

        expenseListAdapter.notifyDataSetChanged();

    }
    protected void Alarmsetter(long timing,int id)
    { AlarmManager am=(AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(MainActivity.this,MyReceiver.class);
        intent.putExtra("ID1",id);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,id,intent,0);
        am.set(AlarmManager.RTC_WAKEUP,timing,pendingIntent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if (resultCode == add) {
//               String newTitle = data.getStringExtra(IntentConstants.EXPENSE_TITLE);
//               Log.i("MainActivityTag", "New Title " + newTitle);

                updateExpenseList();

               int id= data.getIntExtra("id",-1);
                Log.i("ADD",id+"");
                long epoch=data.getLongExtra("Epoch",-2);
                Log.i("ADD",epoch+"");
                Alarmsetter(epoch,id);

            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"No Changes Done",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==update)
            {
                ExpenseOpenHelper expenseOpenHelper=new ExpenseOpenHelper(this);
                SQLiteDatabase database=expenseOpenHelper.getReadableDatabase();
                Cursor cursor = database.query(ExpenseOpenHelper.EXPENSE_TABLE_NAME,null,null,null,null,null,null);
                cursor.moveToPosition(pos);

                int id = cursor.getInt(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_ID));
                String title = cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_TITLE));
                String description= cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_DESCRIPTION));
                String date=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_DATE));
                String time=cursor.getString(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_TIME));
              //  int id1=data.getIntExtra("id",-1);
               // Log.i("Tag",id1+"");
                long epoch=data.getLongExtra("Epoch",-2);
                Log.i("Update",epoch+"");
                Alarmsetter(epoch,id);

                Expense e = new Expense(id,title,description,date,time);

                expenseList.remove(pos);
                expenseList.add(pos,e);

                expenseListAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     if(R.id.remove == id){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("Delete");
        builder.setCancelable(false);
//            builder.setMessage("Are you sure you want to delete ??");

        View v = getLayoutInflater().inflate(R.layout.dialog_view2,null);

        final TextView tv = (TextView) v.findViewById(R.id.dialogTextView);
        tv.setText("Enter the title to be deleted ?");
        edit=(EditText) v.findViewById(R.id.editview);
        builder.setView(v);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title=edit.getText().toString();
                if(title.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Enter title first",Toast.LENGTH_SHORT).show();
                    return;
                }
                ExpenseOpenHelper expenseOpenHelper=new ExpenseOpenHelper(MainActivity.this);
                SQLiteDatabase database=expenseOpenHelper.getReadableDatabase();
                //   String[] abcd ={"'title'"};
                database.delete(ExpenseOpenHelper.EXPENSE_TABLE_NAME,ExpenseOpenHelper.EXPENSE_TITLE+"="+"'"+title+"'",null);

                int x=0;
                ArrayList<Expense> list1=new ArrayList<>();
                for(Expense i:expenseList)
                {
                    if(i.getTitle().equals(title))
                    {
                        list1.add(i);
                        x++;
                    }
                }
                expenseList.removeAll(list1);
                if(x==0)
                {
                    Toast.makeText(MainActivity.this,"No such title",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,title+" removed successfully",Toast.LENGTH_SHORT).show();
                }



                expenseListAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }else if(id == R.id.aboutUs){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("https://www.google.co.in");
        i.setData(uri);

        startActivity(i);
    }else if(id == R.id.contactUs){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:123345");
        i.setData(uri);
        startActivity(i);
    }else if(id == R.id.feedback){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SENDTO);
        Uri uri = Uri.parse("mailto:manisha@codingninjas.in");
        i.putExtra(Intent.EXTRA_SUBJECT,"Feedback");
        i.setData(uri);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
    }
return true;
}
}
