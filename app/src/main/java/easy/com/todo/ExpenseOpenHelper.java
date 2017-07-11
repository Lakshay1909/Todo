package easy.com.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hp on 7/5/2017.
 */

public class ExpenseOpenHelper extends SQLiteOpenHelper {
    public final static String EXPENSE_TABLE_NAME  = "Expense";
    public final static String EXPENSE_TITLE  = "title";
    public final static String EXPENSE_ID  = "_id";
    public  final  static String EXPENSE_DESCRIPTION="Description";
    public final static String EXPENSE_DATE="dated";
    public final  static String EXPENSE_TIME="timing";


    public ExpenseOpenHelper(Context context) {
        super(context, "Expenses.db", null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + EXPENSE_TABLE_NAME +" ( " + EXPENSE_ID +
                " integer primary key autoincrement, " + EXPENSE_TITLE +" text, "
                + EXPENSE_DESCRIPTION + " text, " +EXPENSE_DATE +" text, "+EXPENSE_TIME+" text);";

        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
