package com.example.denis.p7.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.p7.R;
import com.example.denis.p7.TCPClient;
import com.example.denis.p7.algorithms.helpers.ByteHelper;

import java.io.IOException;

public class second extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ActionBar ab;
    FloatingActionButton fabAttach, fabSend;
    EditText editText;
    LinearLayout scrollLL, msgTextLL, msgImageLL;
    LinearLayout.LayoutParams msgTextLParamsLL, msgImageLParamsLL, msgLParamsTV, msgLParamsIV;
    TextView msgTV;
    ImageView msgIV;
    InputMethodManager imm;
    Intent intent;
    SendMsg sendMsg;
    GetMsgs getMsgs;
    final int REQUEST_CODE_IMAGE = 1, REQUEST_CODE_AUDIO = 2;
    String uri, ip = "138.197.176.233";
    TCPClient client;
    byte[] bytes;
    String s;
    int k = 0, port=3129;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(first.TAG, "second.class onCreate");

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        // ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bar));
        intent = getIntent();
        ab.setTitle(R.string.chatting);

        client = new TCPClient(ip, port);

        scrollLL = (LinearLayout) findViewById(R.id.scrollLL);
        msgTextLL = (LinearLayout) findViewById(R.id.msgTextLL);
        msgTV = (TextView) findViewById(R.id.msgTV);
        msgImageLL = (LinearLayout) findViewById(R.id.msgImageLL);
        msgIV = (ImageView) findViewById(R.id.msgIV);
        msgTextLParamsLL = (LinearLayout.LayoutParams) msgTextLL.getLayoutParams();
        msgLParamsTV = (LinearLayout.LayoutParams) msgTV.getLayoutParams();
        msgImageLParamsLL = (LinearLayout.LayoutParams) msgImageLL.getLayoutParams();
        msgLParamsIV = (LinearLayout.LayoutParams) msgIV.getLayoutParams();

        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fabAttach.setVisibility(View.INVISIBLE);
                fabSend.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // прячем клавиатуру. butCalculate - это кнопка
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        fabAttach = (FloatingActionButton) findViewById(R.id.fabAttach);
        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);
        fabSend.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, first.class);
                startActivity(intent);
                return true;
            case R.id.fullInfo:
                return true;
            case R.id.clear:
                getMsgs=new GetMsgs();
                getMsgs.execute(k);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabSend:
                sendMsg = new SendMsg();
                bytes=ByteHelper.getBytesFromString(editText.getText().toString());
                sendMsg.execute(bytes);
                break;
        }
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_attach, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.image:
                intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");

                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_IMAGE);
                }
                break;

            case R.id.audio:
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");

                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_AUDIO);
                }
                break;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    uri = data.getDataString();
                    msgImageLL = new LinearLayout(this);
                    msgImageLL.setBackgroundResource(R.drawable.msg_photo);
                    msgIV = new ImageView(this);
                    msgImageLL.setLayoutParams(msgImageLParamsLL);
                    msgIV.setLayoutParams(msgLParamsIV);
                    // iV.setImageURI(Uri.parse(uri));
                    msgIV.setContentDescription(uri);
                    msgIV.setOnClickListener(onClickListenerIV);

//                    // linearLayout.text

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //bitmap.setConfig(Bitmap.Config.ARGB_4444);
//                    bitmap.setHeight(getWindowManager().getDefaultDisplay().getHeight()/2);
//                    bitmap.setWidth(getWindowManager().getDefaultDisplay().getWidth()/2);
                    msgIV.setImageBitmap(bitmap);

                    msgImageLL.addView(msgIV);
                    scrollLL.addView(msgImageLL);
                    break;

                case REQUEST_CODE_AUDIO:
                    uri = data.getDataString();
                    msgTV = new TextView(this);
                    msgTV.setText(data.toString());
                    msgTV.setContentDescription(uri);
                    msgTV.setOnClickListener(onClickListenerAV);
                    msgTV.setLayoutParams(msgTextLParamsLL);
                    scrollLL.addView(msgTV);
                    break;
            }
        }
    }

    View.OnClickListener onClickListenerIV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(v.getContentDescription().toString()));
            // Verify that the intent will resolve to an activity
            if (intent.resolveActivity(getPackageManager()) != null) {

                startActivity(intent);
            }
        }
    };

    View.OnClickListener onClickListenerAV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(v.getContentDescription().toString()));
            // Verify that the intent will resolve to an activity
            if (intent.resolveActivity(getPackageManager()) != null) {

                startActivity(intent);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(first.TAG, "second.class onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(first.TAG, "second.class onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(first.TAG, "second.class onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(first.TAG, "second.class onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(first.TAG, "second.class onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(first.TAG, "second.class onDestroy");
    }

    class SendMsg extends AsyncTask<byte[], Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(byte[]... bytes) {
            int response = 4;

            // Send bytes to server
            try {
                response = client.sendMessage(bytes[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            switch (response) {
                case 0:
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    editText.setText("");
                    fabAttach.setVisibility(View.VISIBLE);
                    fabSend.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    Toast.makeText(second.this, "Internal database error, probably size limit exceeded which is ~800MB", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(second.this, "Received damaged message", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(second.this, "Connection with server failed", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(second.this, "Some exception", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    class GetMsgs extends AsyncTask<Integer, Void, byte[][]> {

        @Override
        protected byte[][] doInBackground(Integer... alreadyHaveMessages) {
            byte[][] result=new byte[0][];
            // Get messages
            try {
                result = client.getMessages(alreadyHaveMessages[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(byte[][] bytes) {
            super.onPostExecute(bytes);
            if(bytes[0]==null)return;
            for(int i=0;i<bytes.length;i++){
                msgTextLL = new LinearLayout(second.this);
                msgTextLL.setBackgroundResource(R.drawable.msg_in);
                msgTV = new TextView(second.this);

                msgTextLL.setLayoutParams(msgTextLParamsLL);
                msgTV.setLayoutParams(msgLParamsTV);

                s=ByteHelper.getStringFromBytes(bytes[i]);
                msgTV.setText(s);

                msgTextLL.addView(msgTV);
                scrollLL.addView(msgTextLL);
                k++;
            }
        }
    }

}
