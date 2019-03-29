package com.example.androidlabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) { isTablet = tablet; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        id = dataFromActivity.getLong("id");
        View result =  inflater.inflate(R.layout.detail, container, false);

        TextView message1 = (TextView)result.findViewById(R.id.message);
        TextView id1 = (TextView)result.findViewById(R.id.id);
        TextView isCkhecker1 = (TextView)result.findViewById(R.id.isChecker);
        Button deleteButton1 = (Button)result.findViewById(R.id.deleteButton);


        message1.setText(dataFromActivity.getString("message"));
        id1.setText(Long.toString(dataFromActivity.getLong("id")));

        isCkhecker1.setText(Boolean.toString(dataFromActivity.getBoolean("isCkhecker")));

        deleteButton1.setOnClickListener( clk -> {

            if(isTablet) { //both the list and details are on the screen:
                com.example.androidlabs.Fragment parent = (com.example.androidlabs.Fragment) getActivity();
                parent.deleteMessageId((int)id); //this deletes the item and updates the list


                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                EmptyActivity parent = (EmptyActivity) getActivity();
             //  Intent backToFragmentExample = new Intent();

                backToFragmentExample.putExtra("id", dataFromActivity.getLong("id" ));
                backToFragmentExample.putExtra("message", dataFromActivity.getString("message" ));
                backToFragmentExample.putExtra("isChecker", dataFromActivity.getBoolean("isChecker" ));

                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });


        return result;

    }


}
