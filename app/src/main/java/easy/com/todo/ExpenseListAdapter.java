package easy.com.todo;

/**
 * Created by Hp on 7/5/2017.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;




public class ExpenseListAdapter extends ArrayAdapter<Expense> {

    ArrayList<Expense> expenseArrayList;
    Context context;
//    MainActivity mainActivity;
//    OnListButtonClickedListener listener;
//
//
//    void setOnListButtonClickedListener(OnListButtonClickedListener listener){
//        this.listener = listener;
//    }

    public ExpenseListAdapter(@NonNull Context context, ArrayList<Expense> expenseArrayList) {
        super(context, 0, expenseArrayList);
        this.context = context;
//        mainActivity = (MainActivity) context;
        this.expenseArrayList = expenseArrayList;
    }


//    @Override
//    public int getCount() {
//        return expenseArrayList.size();
//    }



    static class ExpenseViewHolder{

        TextView nameTextView;
        TextView dateTextView;
        TextView timeTextView;

        ExpenseViewHolder(TextView nameTextView,TextView dateTextView,TextView timeTextView){
            this.nameTextView = nameTextView;
            this.dateTextView=dateTextView;
            this.timeTextView=timeTextView;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item,null);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.expenseNameTextView);
            //  Button priceTextView = (Button) convertView.findViewById(R.id.expenselistItemButton);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.expenseDateTextView);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.expenseTimeTextView);
            ExpenseViewHolder expenseViewHolder = new ExpenseViewHolder(nameTextView,dateTextView,timeTextView);
            convertView.setTag(expenseViewHolder);

        }

        //     final int pos = position;
        Expense e = expenseArrayList.get(position);
        ExpenseViewHolder expenseViewHolder = (ExpenseViewHolder)convertView.getTag();
        expenseViewHolder.nameTextView.setText(e.title);
//        expenseViewHolder.button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(listener != null)
//                listener.listButtonClicked(v, pos);
//
//            }
//        });
        expenseViewHolder.dateTextView.setText(e.date);
        expenseViewHolder.timeTextView.setText(e.time);
      //  expenseViewHolder.timeTextView.setText(e.time);
//        expenseViewHolder.priceTextView.setText(e.price+"");

        return  convertView;
    }
}
//
// interface OnListButtonClickedListener{
//    void listButtonClicked(View v, int pos);
//}


