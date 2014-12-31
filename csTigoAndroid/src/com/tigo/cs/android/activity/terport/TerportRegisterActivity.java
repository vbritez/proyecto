package com.tigo.cs.android.activity.terport;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.service.LocationService.ResultManager;
import com.tigo.cs.android.util.Cypher;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.MetaDataServiceBean;
import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.entities.TerportService;

public class TerportRegisterActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 24;
    }

    @Override
    public String getServiceEventCod() {
        return "REGISTER";
    }

    private Spinner terportRegisterOperator;
    private Spinner terportRegisterMachine;
    private CheckBox terportRegisterIsNewUbication;

    private EditText terportRegisterContainer1;
    private EditText terportRegisterContainer2;
    private EditText terportRegisterContainer3;

    private EditText terportRegisterUbication1;
    private EditText terportRegisterUbication2;
    private EditText terportRegisterUbication3;
    private EditText terportRegisterUbication4;
    private EditText terportRegisterUbication5;

    private EditText terportRegisterRegistrationNumber;

    private Button terportRegisterButton;

    private LinearLayout ubicationLayout;

    private static List<MetaDataServiceBean> operatorList = null;
    private static List<MetaDataServiceBean> machineList = null;

    class MetaListWrapper {
        private List<MetaDataServiceBean> data;

        public MetaListWrapper() {

        }

        public List<MetaDataServiceBean> getData() {
            return data;
        }

        public void setData(List<MetaDataServiceBean> data) {
            this.data = data;
        }

    }

    public class WSAsyncTask extends AsyncTask<String, Void, String> {

        protected String serviceName;
        protected String json;
        protected ResultManager resultManager;

        public WSAsyncTask(String serviceName, String json,
                ResultManager resultManager) {
            this.serviceName = serviceName;
            this.json = json;
            this.resultManager = resultManager;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                // WebService ws = new WebService(serviceName, null);
                // return ws.webInvoke("", json);
                return null;

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // resultManager.gotResponse(result);
        }

    }

    private void loadList() throws Exception {

        /*
         * aca consultamos al WS de meta
         */

        MetadataCrudService data = null;
        Boolean internet = true;

        ServiceEventEntity event = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                99, "ALL");
        String wsHost = CsTigoApplication.getGlobalParameterHelper().getInternetWsHost();
        String servicePath = event.getWsPath();
        String uri = wsHost.concat(servicePath);

        if (operatorList == null) {
            operatorList = new ArrayList<MetaDataServiceBean>();

            data = new MetadataCrudService();
            data.setMetaCode(20);
            data.setEvent("ALL");

            data.setTimestamp(new Date().getTime());
            data.setSendSMS(false);

            data.setMsisdn(CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNum());

            try {
                data.setHash(Cypher.encrypt(data.getMsisdn(), data.getMsisdn()
                    + data.getTimestamp()));
            } catch (Exception e) {
            }

            internet = event.getForceInternet()
                || CsTigoApplication.getGlobalParameterHelper().getInternetEnabled();
            if (internet && event.getInternet()) {

                Notifier.info(getClass(), "Internet is enabled.");

                WSAsyncTask task = new WSAsyncTask(uri, data.toStringWithHash(), null);
                task.execute(new String[] {});

                try {
                    String result = null;
                    result = task.get();
                    if (result == null
                        || (result != null && result.trim().length() <= 0)) {
                        throw new Exception();
                    }
                    internet = true;

                    JSONArray jsonArray = new JSONArray(result);
                    operatorList = new ArrayList<MetaDataServiceBean>();
                    List<String> array = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject menuObject = jsonArray.getJSONObject(i);

                        MetaDataServiceBean bean = new MetaDataServiceBean();
                        bean.setMemberCod(menuObject.getLong("memberCod"));
                        bean.setCode(menuObject.getString("code"));
                        bean.setValue(menuObject.getString("value"));
                        array.add(bean.getValue());
                        operatorList.add(bean);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    terportRegisterOperator.setAdapter(adapter);

                } catch (InterruptedException e1) {
                    internet = false;
                } catch (ExecutionException e1) {
                    internet = false;
                } catch (Exception e1) {
                    internet = false;
                }
            }

        } else {
            List<String> array = new ArrayList<String>();

            for (MetaDataServiceBean bean : operatorList) {
                array.add(bean.getValue());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            terportRegisterOperator.setAdapter(adapter);
        }
        if (machineList == null) {

            machineList = new ArrayList<MetaDataServiceBean>();

            data = new MetadataCrudService();
            data.setMetaCode(19);
            data.setEvent("ALL");

            data.setTimestamp(new Date().getTime());
            data.setSendSMS(false);

            data.setMsisdn(CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNum());

            try {
                data.setHash(Cypher.encrypt(data.getMsisdn(), data.getMsisdn()
                    + data.getTimestamp()));
            } catch (Exception e) {
            }
            internet = event.getForceInternet()
                || CsTigoApplication.getGlobalParameterHelper().getInternetEnabled();
            if (internet && event.getInternet()) {

                Notifier.info(getClass(), "Internet is enabled.");

                WSAsyncTask task = new WSAsyncTask(uri, data.toStringWithHash(), null);
                task.execute(new String[] {});

                try {
                    String result = null;
                    result = task.get();
                    if (result == null
                        || (result != null && result.trim().length() <= 0)) {
                        throw new Exception();
                    }
                    internet = true;

                    JSONArray jsonArray = new JSONArray(result);
                    machineList = new ArrayList<MetaDataServiceBean>();
                    List<String> array = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject menuObject = jsonArray.getJSONObject(i);

                        MetaDataServiceBean bean = new MetaDataServiceBean();
                        bean.setMemberCod(menuObject.getLong("memberCod"));
                        bean.setCode(menuObject.getString("code"));
                        bean.setValue(menuObject.getString("value"));
                        array.add(bean.getValue());
                        machineList.add(bean);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    terportRegisterMachine.setAdapter(adapter);

                } catch (InterruptedException e1) {
                    internet = false;
                } catch (ExecutionException e1) {
                    internet = false;
                } catch (Exception e1) {
                    internet = false;
                }
            }
        } else {

            List<String> array = new ArrayList<String>();

            for (MetaDataServiceBean bean : machineList) {
                array.add(bean.getValue());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            terportRegisterMachine.setAdapter(adapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadList();
        } catch (Exception e) {
            Toast toast = Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.invalid_operator), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            loadList();
        } catch (Exception e) {
            Toast toast = Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.invalid_operator), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terport_register_main);
        onSetContentViewFinish();

        terportRegisterOperator = (Spinner) findViewById(R.id.terportRegisterOperatorSpinner);
        terportRegisterMachine = (Spinner) findViewById(R.id.terportRegisterMachineSpinner);

        try {
            loadList();
        } catch (Exception e) {
            Toast toast = Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.invalid_operator), Toast.LENGTH_LONG);
            toast.show();
        }

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */

        terportRegisterIsNewUbication = (CheckBox) findViewById(R.id.terportRegisterIsNewUbication);

        terportRegisterContainer1 = (EditText) findViewById(R.id.terportRegisterContainer1Edit);
        terportRegisterContainer2 = (EditText) findViewById(R.id.terportRegisterContainer2Edit);
        terportRegisterContainer3 = (EditText) findViewById(R.id.terportRegisterContainer3Edit);

        ubicationLayout = (LinearLayout) findViewById(R.id.terportRegisterUbicationlayout);

        terportRegisterIsNewUbication.setChecked(true);

        terportRegisterIsNewUbication.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;

                if (checkBox.isChecked()) {
                    addNewUbication();
                } else {
                    addRegistrationNumber();
                }

            }
        });

        terportRegisterContainer1.setInputType(InputType.TYPE_CLASS_TEXT
            | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        terportRegisterContainer1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3) {
                    terportRegisterContainer2.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterContainer2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 5) {
                    terportRegisterContainer3.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterContainer3.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0) {
                    if (terportRegisterIsNewUbication.isChecked()) {
                        terportRegisterUbication1.requestFocus();
                    } else {
                        terportRegisterRegistrationNumber.requestFocus();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterButton = (Button) findViewById(R.id.terportRegisterButton);
        terportRegisterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new TerportService();

                if (!startUserMark()) {
                    return;
                }

                String manager = CsTigoApplication.getGlobalParameterHelper().getTerportManagerEntity().getParameterValue();

                String containerPattern = "{0}-{1}-{2}";
                String ubicationPattern = "{0}/{1}/{2}-{3}/{4}";

                String container = MessageFormat.format(containerPattern,
                        terportRegisterContainer1.getText().toString().trim(),
                        terportRegisterContainer2.getText().toString().trim(),
                        terportRegisterContainer3.getText().toString().trim());

                container = container.toUpperCase();

                String ubication = null;
                String registrationNumber = null;
                if (terportRegisterIsNewUbication.isChecked()) {
                    ubication = MessageFormat.format(
                            ubicationPattern,
                            terportRegisterUbication1.getText().toString().trim(),
                            terportRegisterUbication2.getText().toString().trim(),
                            terportRegisterUbication3.getText().toString().trim(),
                            terportRegisterUbication4.getText().toString().trim(),
                            terportRegisterUbication5.getText().toString().trim());

                    ubication = ubication.toUpperCase();
                } else {
                    registrationNumber = terportRegisterRegistrationNumber.getText().toString().trim();
                }

                ((TerportService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((TerportService) entity).setManager(manager);
                MetaDataServiceBean operator = operatorList.get((int) terportRegisterOperator.getSelectedItemId());
                MetaDataServiceBean machine = machineList.get((int) terportRegisterMachine.getSelectedItemId());
                ((TerportService) entity).setOperator(operator.getCode());
                ((TerportService) entity).setMachine(machine.getCode());
                ((TerportService) entity).setContainer(container);
                ((TerportService) entity).setUbication(ubication);
                ((TerportService) entity).setRegistrationNumber(registrationNumber);

                endUserMark(null);
            }
        });

        addNewUbication();
    }

    protected void addNewUbication() {
        ubicationLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(
                R.layout.terport_register_subcontent_ubication, null);

        terportRegisterUbication1 = (EditText) layout.findViewById(R.id.terportRegisterUbication1Edit);
        terportRegisterUbication2 = (EditText) layout.findViewById(R.id.terportRegisterUbication2Edit);
        terportRegisterUbication3 = (EditText) layout.findViewById(R.id.terportRegisterUbication3Edit);
        terportRegisterUbication4 = (EditText) layout.findViewById(R.id.terportRegisterUbication4Edit);
        terportRegisterUbication5 = (EditText) layout.findViewById(R.id.terportRegisterUbication5Edit);
        terportRegisterRegistrationNumber = null;

        ubicationLayout.addView(layout);

        terportRegisterUbication1.setInputType(InputType.TYPE_CLASS_TEXT
            | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        terportRegisterUbication1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0) {
                    terportRegisterUbication2.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterUbication2.setInputType(InputType.TYPE_CLASS_TEXT
            | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        terportRegisterUbication2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 1) {
                    terportRegisterUbication3.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterUbication3.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 1) {
                    terportRegisterUbication4.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterUbication4.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 1) {
                    terportRegisterUbication5.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        terportRegisterUbication5.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0) {
                    terportRegisterButton.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    protected void addRegistrationNumber() {
        ubicationLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(
                R.layout.terport_register_subcontent_registration_number, null);

        terportRegisterRegistrationNumber = (EditText) layout.findViewById(R.id.terportRegisterRegistrationNum);

        terportRegisterUbication1 = null;
        terportRegisterUbication2 = null;
        terportRegisterUbication3 = null;
        terportRegisterUbication4 = null;
        terportRegisterUbication5 = null;

        ubicationLayout.addView(layout);
    }

    @Override
    protected boolean validateUserInput() {

        if (terportRegisterOperator.getSelectedItem() == null) {
            Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_register_operator_not_selected),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (terportRegisterMachine.getSelectedItem() == null) {
            Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_register_machine_not_selected),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(terportRegisterContainer1.getText())) {
            Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_register_container_1_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(terportRegisterContainer2.getText())) {
            Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_register_container_2_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(terportRegisterContainer3.getText())) {
            Toast.makeText(
                    TerportRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_register_container_3_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (terportRegisterIsNewUbication.isChecked()) {

            if (TextUtils.isEmpty(terportRegisterUbication1.getText())) {
                Toast.makeText(
                        TerportRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.terport_register_ubication_1_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(terportRegisterUbication2.getText())) {
                Toast.makeText(
                        TerportRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.terport_register_ubication_2_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(terportRegisterUbication3.getText())) {
                Toast.makeText(
                        TerportRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.terport_register_ubication_3_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(terportRegisterUbication4.getText())) {
                Toast.makeText(
                        TerportRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.terport_register_ubication_4_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(terportRegisterUbication5.getText())) {
                Toast.makeText(
                        TerportRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.terport_register_ubication_5_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            if (TextUtils.isEmpty(terportRegisterRegistrationNumber.getText())) {
                Toast.makeText(
                        TerportRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.terport_register_registration_number_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void goToMain() {

        terportRegisterContainer1.setText(null);
        terportRegisterContainer2.setText(null);
        terportRegisterContainer3.setText(null);

        if (terportRegisterIsNewUbication.isChecked()) {
            terportRegisterUbication1.setText(null);
            terportRegisterUbication2.setText(null);
            terportRegisterUbication3.setText(null);
            terportRegisterUbication4.setText(null);
            terportRegisterUbication5.setText(null);
        } else {
            terportRegisterRegistrationNumber.setText(null);
        }
        terportRegisterContainer1.requestFocus();

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        operatorList = null;
        machineList = null;

    }
}
