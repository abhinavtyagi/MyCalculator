package com.chronos.mycalculator;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.AndroidException;
import android.util.AndroidRuntimeException;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements GridView.OnItemClickListener {

    protected int columnIndex;
    protected GridView mGridView;
    protected TextAdapter mAdapter;
    protected String mExpression;
    private boolean isCurrentNumContainsPoint = false;       // check for num is with multiple points 1.2.3
    private boolean isExpresssionEvaluated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ToDo: http://codetheory.in/understanding-android-gridview/ here I am
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.calculatorCellsView);
        mAdapter = new TextAdapter(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        TextView expressionsView = (TextView) findViewById(R.id.expressionsView);
        if(mExpression==null || mExpression.isEmpty()) {
            mExpression="";
            expressionsView.setText(getString(R.string.default_expression));
        }
        else {
            expressionsView.setText(mExpression);
        }
    }

    private boolean isOperator(char ch)
    {
        return (ch=='+' || ch=='-' || ch=='*' || ch=='/');
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView expressionsView = (TextView) findViewById(R.id.expressionsView);
        TextView inputCell = (TextView)view.findViewById(R.id.id_gridCell);
        String input = inputCell.getText().toString();
        char ch = input.charAt(0);  // input always have 1 char
        if(input.equalsIgnoreCase("="))
        {
            String evaluatedExpression = evaluateStringExpression(mExpression);
            expressionsView.setText(evaluatedExpression);
            mExpression = evaluatedExpression;
            isCurrentNumContainsPoint = true;   // At the end, the expression will contain result
            isExpresssionEvaluated = true;
            return;
        }
        else
        {
            // Not '=' pressed. So, update expression string with following restrictions
            // 1. Cant have operators at beginning
            // 2. cant have 2 operators / decimal together. For this, check at time of adding key-pressed
            // 3. Cant have 2 decimals in a number
            // 4. ToDo: 3+.5 needs 3+0.5

            if(isExpresssionEvaluated)
            {
                if(!isOperator(ch))
                {
                    mExpression = "";
                    isCurrentNumContainsPoint=false;
                }
                isExpresssionEvaluated = false;
            }

            if(isCurrentNumContainsPoint && ch=='.')
            {
                return;
            }
            if(mExpression.length()==0)
            {
                if(ch=='+' || ch=='-' || ch=='*' || ch=='/')
                {
                    // do nothing. ToDo: show notification if possible
                    return;
                }
            }
            else
            {
                // the mExpression string is not zero length; check it didnt ended with operator or decimal
                char expressionEnd = mExpression.charAt(mExpression.length()-1);
                if((ch=='+' || ch=='-' || ch=='*' || ch=='/') &&
                        (expressionEnd=='+' || expressionEnd=='-' || expressionEnd=='*' || expressionEnd=='/' || expressionEnd=='.'))
                {
                    // do nothing. ToDo: show notification if possible
                    return;
                }
            }
        }
        if(ch=='.')
        {
            isCurrentNumContainsPoint = true;
        }
        else if (ch=='+' || ch=='-' || ch=='*' || ch=='/')
        {
            isCurrentNumContainsPoint = false;
        }
        mExpression += input;
        int start, end;
        int allowedCharacters = 30;
        end = mExpression.length();
        start = end-allowedCharacters >=0 ? end-allowedCharacters : 0;
        String textViewString = mExpression.substring(start, end);
        expressionsView.setText(textViewString);
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

    String evaluateStringExpression(String expression)
    {
        String[] numbers = mExpression.split("[^0123456789.]");
        Double expressionValue = 0.0;
        if (numbers.length >= 1)
        {
            int nextOperandIndex = -1;
            char operand = ' ';
            for (String element : numbers)
            {
                Double op1 = expressionValue;
                Double op2 = new Double(element);
                switch (operand)
                {
                    case '+':
                        expressionValue = op1 + op2;
                        break;
                    case '-':
                        expressionValue = op1 - op2;
                        break;
                    case '*':
                        expressionValue = op1 * op2;
                        break;
                    case '/':
                        expressionValue = op1 / op2;
                        break;
                    case ' ':
                        assert (expressionValue == 0);
                        expressionValue = op2;
                        break;
                    default:
                        assert (false) ;
                        break;
                }
                nextOperandIndex += element.length()+1;
                if(nextOperandIndex < mExpression.length())
                {
                    operand = mExpression.charAt(nextOperandIndex);
                }
            }

        }
        return expressionValue.toString();
    }

    // ############################################################################################
    class TextAdapter extends BaseAdapter   {
        private Context mContext;
        public TextAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            return 4*4;
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

            if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_cell_layout, null);
            }

            String cellText = getCellText(position);
            textView = (TextView) convertView.findViewById(R.id.id_gridCell);
            textView.setText(cellText);

            return convertView;
        }
        private String getCellText(int cellId)   {
            String cellText ;
            switch (cellId)   {
                case 0:
                    cellText = getString(R.string.button_seven_text);
                    break;
                case 1:
                    cellText = getString(R.string.button_eight_text);
                    break;
                case 2:
                    cellText = getString(R.string.button_nine_text);
                    break;
                case 3:
                    cellText = getString(R.string.button_plus_sign);
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
                    cellText = getString(R.string.button_minus_sign);
                    break;
                case 8:
                    cellText = getString(R.string.button_one_text);
                    break;
                case 9:
                    cellText = getString(R.string.button_two_text);
                    break;
                case 10:
                    cellText = getString(R.string.button_three_text);
                    break;
                case 11:
                    cellText = getString(R.string.button_multiply_sign);
                    break;
                case 12:
                    cellText = getString(R.string.button_decimal_point);
                    break;
                case 13:
                    cellText = getString(R.string.button_zero_text);
                    break;
                case 14:
                    cellText = getString(R.string.button_equal_to);
                    break;
                case 15:
                    cellText = getString(R.string.button_divide);
                    break;
                default:
                    cellText = getString(R.string.button_undefined);
                    break;
            }
            return cellText;
        }
    }
}
