package com.se491.app.two2er.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.se491.app.two2er.R;
import com.se491.app.two2er.SearchView.GetSubjects;

import java.util.HashMap;

public class AdditionalProfileActivity extends AppCompatActivity
        {
    private static String TAG = "AdditionalProfileActivity";
    private static HashMap<String, String> curSelections = new HashMap<String, String>();


            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_profile);

        Button createTutorBttn = (Button) findViewById(R.id.registerAsTutorBtn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addltoolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.subjects_select);

        GetSubjects getSubjects = new GetSubjects();
        getSubjects.start();

        try {
            getSubjects.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Defined Array values to show in ListView
        String[] stockArr = new String[getSubjects.getSubjectList().size()];
        stockArr = getSubjects.getSubjectStringList().toArray(stockArr);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.subjects_list, R.id.checkedSubjectTV, stockArr);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        createTutorBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new PostCreateTutor();
            }
        });

    }

    public void toggle(View v)
    {
        CheckedTextView cView = (CheckedTextView) v.findViewById(R.id.checkedSubjectTV);
        String fieldValue = cView.getText().toString();
        if (cView.isSelected())
        {
            cView.setSelected(false);
            cView.setCheckMarkDrawable (android.R.drawable.checkbox_off_background);
            curSelections.remove(fieldValue);
        }
        else
        {
            cView.setSelected(true);
            cView.setCheckMarkDrawable (android.R.drawable.checkbox_on_background);
            curSelections.put(fieldValue, fieldValue);
//            Toast.makeText(getApplicationContext(),
//                    "fieldValue2 :"+fieldValue+"  ListSize : " +curSelections.size() , Toast.LENGTH_LONG)
//                    .show();
        }
    }

}
