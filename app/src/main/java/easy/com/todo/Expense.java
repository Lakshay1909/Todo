package easy.com.todo;

import java.io.Serializable;

/**
 * Created by Hp on 7/5/2017.
 */

public class Expense implements Serializable {
    int id;
    String title;
    String Description;
    String date;
    String time;


    public Expense(int id, String title, String Description,String date,String time) {
        this.id = id;
        this.title = title;
        this.Description=Description;
        this.date=date;
        this.time=time;
    }

    public String getTitle() {
        return title;
    }
}
