package com.tigo.cs.android.activity.dynamicform;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntity;

public class DynamicFormSelectionActivity extends AbstractActivity {

    private List<DynamicFormElementEntity> dynamicFormElementList;

    public List<DynamicFormElementEntity> getDynamicFormElementList() {
        if (dynamicFormElementList == null) {
            dynamicFormElementList = CsTigoApplication.getDynamicFormElementHelper().findAllEnabled();
        }
        return dynamicFormElementList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamicform_selection_main);
        onSetContentViewFinish();

        if (getDynamicFormElementList() != null
            && getDynamicFormElementList().size() <= 0) {
            // mostrar mensaje
        }

        addButtoms();
    }

    private void addButtoms() {
        LinearLayout buttomsLayout = (LinearLayout) findViewById(R.id.dynamicformSelectionLayout);
        buttomsLayout.removeAllViews();

        for (int i = 0; i < getDynamicFormElementList().size(); i++) {
            final DynamicFormElementEntity dynamicFormElementEntity = getDynamicFormElementList().get(
                    i);

            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout buttom = (LinearLayout) li.inflate(
                    R.layout.dynamicform_entrytype_buttom, null);

            Button selectionButtom = (Button) buttom.findViewById(R.id.dynamicformEntryTypeButtom);

            selectionButtom.setText(dynamicFormElementEntity.getDescription());
            selectionButtom.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final HashMap<String, String> extras = new HashMap<String, String>();

                    extras.put(
                            "dynamicForm",
                            dynamicFormElementEntity.getDynamicFormElementCod().toString());
                    startEventActivity(DynamicFormRegisterActivity.class,
                            extras);
                }
            });

            buttomsLayout.addView(buttom);

        }
    }

}
