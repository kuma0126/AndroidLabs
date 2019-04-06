package com.example.androidlabs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    ListView chatV;
    Button btn, receive;//button send or receive
    EditText edt;
    List<Message> messages;
    DatabaseClass databaseHelp;
    public static final String MESSAGE = "message";
    public static final String IS_SEND = "isSend";
    public static final String ID = "id";
    public static final String POSITION = "position";
    public static final int EMPTY_ACTIVITY = 345;

    public ChatAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        chatV = findViewById(R.id.listChat);
        btn = findViewById(R.id.buttonSend);

        edt = findViewById(R.id.messageInput);
        messages = new ArrayList<>();
        databaseHelp = new DatabaseClass(this);

        messageAdapter = new ChatAdapter(messages, this);
        chatV.setAdapter(messageAdapter);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        chatV.setOnItemClickListener((list, item, position, id) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(MESSAGE, messageAdapter.getItem(position).getMessage());
                    bundle.putInt(POSITION, position);
                    bundle.putLong(ID, messageAdapter.getItem(position).getId());
                    bundle.putBoolean(IS_SEND, messageAdapter.getItem(position).isChecker());


                    if (isTablet) {
                        DetailFragment dFragment = new DetailFragment();
                        dFragment.setArguments(bundle);
                        dFragment.setTablet(true);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentLocation, dFragment)

                                .commit();
                    } else {

                        Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                        nextActivity.putExtras(bundle); //send data to next activity
                        startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition

                    }

                }


        );
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt.getText().toString().equals("")) {
                    databaseHelp.insertData(edt.getText().toString(), true);
                    edt.setText("");
                    messages.clear();
                    viewData();
                }
            }
        });

        viewData();
        Log.e("ChatRoomActivity", "onCreate");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EMPTY_ACTIVITY) {
            if (resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(ID, 0);
                int position = data.getIntExtra(POSITION, 0);
                delete_msg(id, position);
            }
        }
    }

    public void delete_msg(Long id, int position) {
        Log.i("Delete this message:", " id=" + id);
        databaseHelp.delete(id);
        messages.remove(position);


        messageAdapter.notifyDataSetChanged();


    }

    private void viewData() {

        Cursor cursor = databaseHelp.viewData();

        if (cursor.getCount() != 0) {

            while (cursor.moveToNext()) {

                Message msg = new Message(cursor.getString(1), cursor.getLong(0), cursor.getInt(2) == 0);

                messages.add(msg);
                messageAdapter.notifyDataSetChanged();
            }

        }

    }


    private class ChatAdapter extends BaseAdapter {
        List<Message> msg;
        Context ctx;
        LayoutInflater inflater;


        public ChatAdapter(List<Message> msg, Context ctx) {
            this.ctx = ctx;
            this.msg = msg;
            this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return msg.size();
        }


        @Override
        public Message getItem(int position) {
            return msg.get(position);
        }


        @Override
        public long getItemId(int position) {
            return (long) position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View result = convertView;

            if (msg.get(position).isChecker()) {

                result = inflater.inflate(R.layout.activity_send, null);

            } else {
                result = inflater.inflate(R.layout.activity_receive, null);

            }

            TextView message = result.findViewById(R.id.messageText);
            message.setText(msg.get(position).getMessage()); // get the string at position


            return result;

        }
    }


}
