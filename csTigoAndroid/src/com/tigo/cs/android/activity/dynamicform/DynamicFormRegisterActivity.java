package com.tigo.cs.android.activity.dynamicform;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntity;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntryEntity;
import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.entities.DynamicFormValueDataService;
import com.tigo.cs.api.entities.DynamicFormValueService;

public class DynamicFormRegisterActivity extends AbstractActivity {

    private static final Integer SERVICECOD = 20;
    private static final String SERVICEEVENTCOD = "REGISTER";

    private static final int BIFURCATION = 1;
    private static final int INPUT = 2;
    private static final int OUTPUT = 3;
    // private static final int ITEM_LIST = 4;
    private static final int OPTION = 5;

    private DynamicFormElementEntity dynamicFormElement;
    // private ProgressBar progressBar;
    private Button registerButtom;

    private static Integer[] radioButtomOptionArray = new Integer[] {
            R.id.dynamicformEntryTypeOptionItem1,
            R.id.dynamicformEntryTypeOptionItem2,
            R.id.dynamicformEntryTypeOptionItem3,
            R.id.dynamicformEntryTypeOptionItem4,
            R.id.dynamicformEntryTypeOptionItem5 };
    private static Integer[] radioButtomOptionCheckArray = new Integer[] {
            R.id.dynamicformEntryTypeOptionCheck1,
            R.id.dynamicformEntryTypeOptionCheck2,
            R.id.dynamicformEntryTypeOptionCheck3,
            R.id.dynamicformEntryTypeOptionCheck4,
            R.id.dynamicformEntryTypeOptionCheck5 };
    private static Integer[] radioButtomBifurcationArray = new Integer[] {
            R.id.dynamicformEntryTypeBifurcationItem1,
            R.id.dynamicformEntryTypeBifurcationItem2,
            R.id.dynamicformEntryTypeBifurcationItem3,
            R.id.dynamicformEntryTypeBifurcationItem4,
            R.id.dynamicformEntryTypeBifurcationItem5 };

    private final List<ComponentValidation> validations = new ArrayList<DynamicFormRegisterActivity.ComponentValidation>();

    class ComponentValidation {

        private final DynamicFormValueDataService data;
        private final DynamicFormElementEntryEntity entry;
        private final int entryType;

        public ComponentValidation(DynamicFormValueDataService data,
                DynamicFormElementEntryEntity entry, int entryType) {
            this.data = data;
            this.entry = entry;
            this.entryType = entryType;
        }

        public boolean validate() {

            switch (entryType) {
            case INPUT:

                if (TextUtils.isEmpty(data.getData())) {
                    Toast.makeText(
                            DynamicFormRegisterActivity.this,
                            MessageFormat.format(
                                    CsTigoApplication.getContext().getString(
                                            R.string.dynamic_form_not_empty),
                                    entry.getTitle()), Toast.LENGTH_LONG).show();
                    return false;
                }
                break;

            case OPTION:
                if (TextUtils.isEmpty(data.getData())) {
                    Toast.makeText(
                            DynamicFormRegisterActivity.this,
                            MessageFormat.format(
                                    CsTigoApplication.getContext().getString(
                                            R.string.dynamic_form_must_select),
                                    entry.getTitle()), Toast.LENGTH_LONG).show();
                    return false;
                }

                break;
            }

            return true;
        }

    }

    public DynamicFormElementEntity getDynamicFormElement() {
        if (dynamicFormElement == null) {
            String id = getIntent().getExtras().getString("dynamicForm");

            dynamicFormElement = CsTigoApplication.getDynamicFormElementHelper().findByCod(
                    Long.parseLong(id));
        }
        return dynamicFormElement;
    }

    public BaseServiceBean getEntity() {
        if (entity == null) {
            entity = new DynamicFormService();
            ((DynamicFormService) entity).setDynamicFormCod(getDynamicFormElement().getDynamicFormElementCod());
            ((DynamicFormService) entity).setValueBean(new DynamicFormValueService());
            ((DynamicFormService) entity).getValueBean().setEntryList(
                    new ArrayList<DynamicFormValueDataService>());
        }
        return entity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamicform_register_main);
        onSetContentViewFinish();
        // progressBar = (ProgressBar)
        // findViewById(R.id.dynamicformRegisterProgress);
        registerButtom = (Button) findViewById(R.id.dynamicFormRegisterButton);

        /*
         * validar parametros
         */
        LinearLayout registerlayout = (LinearLayout) findViewById(R.id.dynamicformRegisterLayout);

        DynamicFormElementEntryEntity root = CsTigoApplication.getDynamicFormElementEntryHelper().findRootByDynamicFormElement(
                getDynamicFormElement());

        addComponents(registerlayout, root);

        registerButtom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!startUserMark()) {
                    return;
                }

                getEntity().setEvent(serviceEvent.getServiceEventCod());

                endUserMark("");
            }
        });

    }

    private void addComponents(LinearLayout registerlayout, DynamicFormElementEntryEntity root) {

        if (root == null) {
            return;
        }

        registerlayout.removeAllViews();

        /*
         * si la entrada no es parte de una bifurcacion,validar
         */

        List<DynamicFormElementEntryEntity> childs = CsTigoApplication.getDynamicFormElementEntryHelper().findChilds(
                root.getDynamicFormElementEntryCod());

        for (DynamicFormElementEntryEntity child : childs) {
            switch (child.getEntryType().getDynamicFormEntryTypeCod().intValue()) {
            case OUTPUT:
                registerlayout.addView(addOutput(child));
                break;
            case INPUT:
                registerlayout.addView(addInput(child));
                break;
            case OPTION:
                if (child.getMultiSelectOption()) {
                    registerlayout.addView(addOptionMulti(child));
                } else {
                    registerlayout.addView(addOption(child));
                }
                break;
            case BIFURCATION:
                registerlayout.addView(addBifurcation(child));
                break;
            }
        }

    }

    private RelativeLayout addOutput(DynamicFormElementEntryEntity entry) {
        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout outputLayout = (RelativeLayout) li.inflate(
                R.layout.dynamicform_entrytype_output, null);

        TextView title = (TextView) outputLayout.findViewById(R.id.dynamicformEntryTypeOutputTitle);

        title.setText(entry.getTitle());
        return outputLayout;

    }

    private RelativeLayout addInput(DynamicFormElementEntryEntity entry) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout inputLayout = (RelativeLayout) li.inflate(
                R.layout.dynamicform_entrytype_input, null);

        TextView title = (TextView) inputLayout.findViewById(R.id.dynamicformEntryTypeInputTitle);
        EditText edit = (EditText) inputLayout.findViewById(R.id.dynamicformEntryTypeInputEdit);
        final DynamicFormValueDataService dataService = new DynamicFormValueDataService();
        dataService.setCodFeatureElementEntryBean(entry.getDynamicFormElementEntryCod());
        ((DynamicFormService) getEntity()).getValueBean().getEntryList().add(
                dataService);

        validations.add(new ComponentValidation(dataService, entry, INPUT));

        edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                dataService.setData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        title.setText(entry.getTitle());
        return inputLayout;

    }

    private RelativeLayout addOption(DynamicFormElementEntryEntity entry) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout optionLayout = (RelativeLayout) li.inflate(
                R.layout.dynamicform_entrytype_option, null);

        TextView title = (TextView) optionLayout.findViewById(R.id.dynamicformEntryTypeOptionTitle);
        RadioGroup radioGroup = (RadioGroup) optionLayout.findViewById(R.id.dynamicformEntryTypeOptionRadioGroup);
        radioGroup.setClickable(true);

        List<DynamicFormElementEntryEntity> childs = CsTigoApplication.getDynamicFormElementEntryHelper().findChilds(
                entry.getDynamicFormElementEntryCod());

        title.setText(entry.getTitle());

        final DynamicFormValueDataService dataService = new DynamicFormValueDataService();
        dataService.setCodFeatureElementEntryBean(entry.getDynamicFormElementEntryCod());

        ((DynamicFormService) getEntity()).getValueBean().getEntryList().add(
                dataService);

        validations.add(new ComponentValidation(dataService, entry, OPTION));

        for (int i = 0; i < 5; i++) {

            RadioButton radioButtom = (RadioButton) optionLayout.findViewById(radioButtomOptionArray[i]);
            if (i < childs.size()) {
                final DynamicFormElementEntryEntity child = childs.get(i);
                radioButtom.setText(child.getTitle());
                radioButtom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dataService.setData(child.getTitle());
                        /*
                         * dibujar bifurcacion
                         */

                    }
                });
                radioButtom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        /*
                         * borrar bifurcacion
                         */
                    }
                });
            } else {
                radioGroup.removeView(radioButtom);
            }

        }

        return optionLayout;

    }

    private RelativeLayout addOptionMulti(final DynamicFormElementEntryEntity entry) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout optionLayout = (RelativeLayout) li.inflate(
                R.layout.dynamicform_entrytype_option_check, null);

        TextView title = (TextView) optionLayout.findViewById(R.id.dynamicformEntryTypeOptionCheckTitle);

        List<DynamicFormElementEntryEntity> childs = CsTigoApplication.getDynamicFormElementEntryHelper().findChilds(
                entry.getDynamicFormElementEntryCod());

        title.setText(entry.getTitle());

        for (int i = 0; i < 5; i++) {

            CheckBox checkBox = (CheckBox) optionLayout.findViewById(radioButtomOptionCheckArray[i]);
            if (i < childs.size()) {
                final DynamicFormElementEntryEntity child = childs.get(i);
                checkBox.setText(child.getTitle());
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    private DynamicFormValueDataService dataService;

                    public DynamicFormValueDataService getDataService() {
                        if (dataService == null) {

                            dataService = new DynamicFormValueDataService();

                            dataService.setCodFeatureElementEntryBean(entry.getDynamicFormElementEntryCod());

                            ((DynamicFormService) getEntity()).getValueBean().getEntryList().add(
                                    dataService);
                            // validations.add(new
                            // ComponentValidation(dataService, entry, OPTION));
                        }

                        return dataService;
                    }

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            getDataService().setData(child.getTitle());
                        } else {
                            ((DynamicFormService) getEntity()).getValueBean().getEntryList().remove(
                                    dataService);
                            dataService = null;
                        }
                    }
                });

            } else {
                optionLayout.removeView(checkBox);
            }

        }

        return optionLayout;

    }

    private RelativeLayout addBifurcation(DynamicFormElementEntryEntity entry) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout bifurcationLayout = (RelativeLayout) li.inflate(
                R.layout.dynamicform_entrytype_bifurcation, null);

        TextView title = (TextView) bifurcationLayout.findViewById(R.id.dynamicformEntryTypeBifurcationTitle);
        final RadioGroup radioGroup = (RadioGroup) bifurcationLayout.findViewById(R.id.dynamicformEntryTypeBifurcationRadioGroup);
        radioGroup.setClickable(true);

        List<DynamicFormElementEntryEntity> childs = CsTigoApplication.getDynamicFormElementEntryHelper().findChilds(
                entry.getDynamicFormElementEntryCod());

        title.setText(entry.getTitle());

        final DynamicFormValueDataService dataService = new DynamicFormValueDataService();
        dataService.setCodFeatureElementEntryBean(entry.getDynamicFormElementEntryCod());

        ((DynamicFormService) getEntity()).getValueBean().getEntryList().add(
                dataService);

        validations.add(new ComponentValidation(dataService, entry, OPTION));

        for (int i = 0; i < 5; i++) {

            RadioButton radioButtom = (RadioButton) bifurcationLayout.findViewById(radioButtomBifurcationArray[i]);
            if (i < childs.size()) {
                final DynamicFormElementEntryEntity child = childs.get(i);
                radioButtom.setText(child.getTitle());
                radioButtom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(false);
                        }
                        dataService.setData(child.getTitle());

                        LinearLayout bifurcationLinearLayout = (LinearLayout) bifurcationLayout.findViewById(R.id.dynamicformEntryTypeBifurcationLayout);
                        addComponents(bifurcationLinearLayout, child);

                    }
                });
            } else {
                radioGroup.removeView(radioButtom);
            }

        }

        return bifurcationLayout;
    }

    @Override
    protected boolean validateUserInput() {
        for (ComponentValidation validation : validations) {
            if (!validation.validate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Integer getServicecod() {
        return SERVICECOD;
    }

    @Override
    public String getServiceEventCod() {
        return SERVICEEVENTCOD;
    }
}
