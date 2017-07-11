package easy.com.todo;

/**
 * Created by Hp on 7/5/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by Hp on 6/30/2017.
 */

public class Welcome_Screen extends AppCompatActivity {
    private  static int timespan=3000;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(Welcome_Screen.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },timespan);
    }
}