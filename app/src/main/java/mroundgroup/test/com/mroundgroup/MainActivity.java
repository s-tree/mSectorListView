package mroundgroup.test.com.mroundgroup;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bjj.librarys.sectorlistview.SectorListView;

public class MainActivity extends AppCompatActivity {
    private SectorListView myRoundListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int s = size.x > size.y ? size.y : size.x;
        myRoundListView = (SectorListView) findViewById(R.id.myRoundListView);
        myRoundListView.getLayoutParams().width = s;
        myRoundListView.getLayoutParams().height = s;
        myRoundListView.setText("哈哈");
        myRoundListView.setAdapter(new MyAdapter());

        SectorListView myRoundListView2 = (SectorListView) findViewById(R.id.myRoundListView2);
        myRoundListView2.setText("哈哈");
        myRoundListView2.setAdapter(new MyAdapter());
    }

    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if(convertView == null){
                textView = new TextView(parent.getContext());
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                convertView = textView;
            }else{
                textView = (TextView) convertView;
            }
            textView.setText(""+position);
            return convertView;
        }
    }
}
