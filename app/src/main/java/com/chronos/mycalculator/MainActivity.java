package com.chronos.mycalculator;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    protected int columnIndex;
    protected GridView mGridView;
    protected TextAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ToDo: http://codetheory.in/understanding-android-gridview/ here I am
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.calculatorCellsView);
        mAdapter = new TextAdapter(this);
        mGridView.setAdapter(mAdapter);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ############################################################################################
    class TextAdapter extends BaseAdapter   {
        private Context mContext;
        public TextAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            return 3*4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public int dpToPx(int dps)  {
            final float scale = getResources().getDisplayMetrics().density;
            int pix = (int) (dps * scale + 0.5);
            return pix;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            int index = 0;

            int wPix = dpToPx(120);
            int hPix = dpToPx(60);


            index = position / 4;

            if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_cell_layout, null);
            }

            String cellText = getCellText(position);
            textView = (TextView) convertView.findViewById(R.id.id_gridCell);
            textView.setLayoutParams(new LinearLayout.LayoutParams(wPix, hPix));
            textView.setText(cellText);
            return convertView;
        }
        private String getCellText(int cellId)   {
            String cellText = null;
            switch (cellId)   {
                case 0:
                    cellText = getString(R.string.button_zero_text);
                    break;
                case 1:
                    cellText = getString(R.string.button_one_text);
                    break;
                case 2:
                    cellText = getString(R.string.button_two_text);
                    break;
                case 3:
                    cellText = getString(R.string.button_three_text);
                    break;
                case 4:
                    cellText = getString(R.string.button_four_text);
                    break;
                case 5:
                    cellText = getString(R.string.button_five_text);
                    break;
                case 6:
                    cellText = getString(R.string.button_six_text);
                    break;
                case 7:
                    cellText = getString(R.string.button_five_text);
                    break;
                case 8:
                    cellText = getString(R.string.button_eight_text);
                    break;
                case 9:
                    cellText = getString(R.string.button_nine_text);
                    break;
                default:
                    cellText = getString(R.string.button_zero_text);
                    break;
            }
            return cellText;
        }
    }
}


