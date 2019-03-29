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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Fragment extends AppCompatActivity {
    ListView chatV;
    Button btn;//button send
    EditText edt;
    List<Message> messages;
   private ChatAdapter messageAdapter;
    DatabaseClass databaseHelp;
    public static final int EMPTY_ACTIVITY = 345;



    //ArrayAdapter<Message> theAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        chatV = findViewById(R.id.listChat);
        btn = findViewById(R.id.buttonSend);
        edt = findViewById(R.id.messageInput);

        messages = new ArrayList<>();
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        databaseHelp = new DatabaseClass(this);

         messageAdapter = new ChatAdapter(messages, this);
        chatV.setAdapter(messageAdapter);

        chatV.setOnItemClickListener((parent, view, position, id) -> {
            Message message = messageAdapter.getItem(position);

            Bundle dataToPass = new Bundle();
            dataToPass.putString("message", message.getMessage());
            dataToPass.putBoolean("checker", message.isChecker());
            dataToPass.putLong("id", message.getId());

            if(isTablet)
            {
                DetailFragment dFragment = new DetailFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(Fragment.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
            }

        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Message data = new Message(edt.getText().toString(), true);
                //  messages.add(data);

                // edt.setText("");
                // messageAdapter.notifyDataSetChanged();


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
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra("id", 0);
                deleteMessageId((int)id);
            }
        }
    }

    public void deleteMessageId(int id)
    {
        Log.i("Delete this message:" , " id="+id);
        messages.remove(id);
        messageAdapter.notifyDataSetChanged();
    }




    private void viewData() {

        Cursor cursor = databaseHelp.viewData();

        if (cursor.getCount() != 0) {

            while (cursor.moveToNext()) {

                Message msg = new Message(cursor.getString(1), cursor.getInt(2) == 0);

                messages.add(msg);

                ChatAdapter chatAdapter = new ChatAdapter(messages, getApplicationContext());

                chatV.setAdapter(chatAdapter);

            }

        }

    }


    public class ChatAdapter extends BaseAdapter {
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
