package com.example.androidlabs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) { isTablet = tablet; }


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        dataFromActivity=getArguments();
        Long id=dataFromActivity.getLong(ChatRoomActivity.ID);
        Boolean isSend=(dataFromActivity.getBoolean(ChatRoomActivity.IS_SEND));
        View result =  inflater.inflate(R.layout.detail, container, false);

        TextView message_textView=result.findViewById(R.id.message);
        message_textView.setText(dataFromActivity.getString(ChatRoomActivity.MESSAGE));

        TextView id_textView=result.findViewById(R.id.id);
        id_textView.setText(id.toString());

        TextView isSend_textView = result.findViewById(R.id.isChecker);
        isSend_textView.setText(isSend.toString());

        Button delete =result.findViewById(R.id.deleteButton);

        delete.setOnClickListener(clk -> {
            if (isTablet) {
                ChatRoomActivity parent = (ChatRoomActivity) getActivity();
                parent.delete_msg(id);

                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();

            }
            else{
                EmptyActivity parent = (EmptyActivity) getActivity();
                Intent backToFragmentExample = new Intent();
                backToFragmentExample.putExtra(ChatRoomActivity.ID, dataFromActivity.getLong(ChatRoomActivity.ID ));

                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }

        });





        return result;
    }
}
