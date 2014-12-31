package com.tigo.cs.android.activity.tigomoney;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.SessionEntity;

public class TigoMoneyRegisterActivity extends AbstractActivity {

    static final int DATE_PICKER_ID = 1111;

    @Override
    public Integer getServicecod() {
        return -3;
    }

    @Override
    public String getServiceEventCod() {
        return "REGISTER";
    }

    private TextView nameText;
    private EditText birthDate;
    private EditText address;
    private Button nextButton;
    private AutoCompleteTextView citiAutocomplete;
    private TigoMoneyTmpEntity currentEntity;

    private int birthDay;
    private int birthMonth;
    private int birthYear;

    private final String[] cities = { "25 DE DICIEMBRE", "3 DE FEBRERO", "3 DE MAYO", "ABAI", "ACAHAY", "ACEPAR", "ALBERDI", "ALTO VERA", "ALTOS", "APE AIME ALBORADA", "ARA PYAHU", "ARAZAPE", "AREGUA", "ARROYITO", "ARROYO PORA", "ARROYOS Y ESTEROS", "ASUNCIÓN", "ATYRA", "AYOLAS", "AZOTEY", "BAHIA NEGRA", "BARRIO SAN PEDRO", "BELEN", "BELLA VISTA NORTE", "BELLA VISTA ITAPUA", "BENJAMIN ACEVAL", "BONANZA - PTO. TRIUNFO", "BOQUERON", "BRASIL CUE", "BRITEZ CUE", "BUENA VISTA", "CAACUPE", "CAAGUAZU", "CAAPUCU", "CAAZAPA", "CABALLERO", "CALLE 6000 DEFENSORES", "CAMBYRETA", "CAMPICHUELO", "CAMPO ACEVAL", "CAPITAN BADO", "CAPITAN MEZA", "CAPITAN MIRANDA", "CAPITAN TROCHE", "CAPIATA", "CAPIIBARY", "CAPITAN SOSA", "CARAGUATAY", "CARAPEGUA", "CARAYAO", "CARLOS ANTONIO LOPEZ", "CARMELO PERALTA", "CARMEN DEL PARANA", "CASILLA 2", "CERRITO", "CERRO MEMBY", "CHINO KUE", "CHORE", "CIUDAD DEL ESTE", "CLETO ROMERO", "CORONEL BOGADO", "CORONEL MARTINEZ", "CORONEL OVIEDO", "COLONIA 8 DE DICIEMBRE", "COLONIA AURORA", "COLONIA BERNARDINO CABALLERO", "COLONIA GRAL. DIAZ", "COLONIA INDEPENDENCIA", "COLONIA LOMAS VALENTINAS", "COLONIA MANDUARA", "COLONIA MBARETE", "COLONIA NARANJITO", "COLONIA NEUFELD", "COLONIA PIRAPYTA", "COLONIA SAN ALFREDO - ALTO PARANÁ", "COLONIA SAN ALFREDO - CONCEPCIÓN", "COLONIA SANTA CLARA", "COLONIA TIROL", "COLONIA BARBERO", "COLONIA GUARANI", "COLONIA NAVIDAD", "COMPAÑIA CERRO LEON", "COMPAÑIA MARIA ANTONIA", "CONCEPCION", "CORA GUAZU", "CORATEI", "CORPUS CHRISTI", "CORREA RUGUA", "CRUCE CAROLINA", "CRUCE GUARANI", "CRUCE LIBERACION", "CRUCE MBUTUY", "CRUCE PIONEROS", "CURUGUATY", "CURUPAYTY", "DR. CECILIO BAEZ", "DR. J. E. ESTIGARRIBIA", "DR. JUAN LEON MALLORQUIN", "DR. JUAN MANUEL FRUTOS", "EDELIRA", "EL TRIUNFO", "EMBOSCADA", "EMILIANORE", "ENCARNACION", "ENRAMADITA", "ESTANCIA STA. TERESA", "ESTERO KAMBA", "EUSEBIO AYALA", "FELIX PEREZ CARDOZO", "FERNANDO DE LA MORA", "FILADELFIA", "FORTUNA GUAZU", "FRAM", "FUERTE OLIMPO", "FULGENCIO YEGROS", "GENERAL RESQUIN", "GENERAL ARTIGAS", "GRGENERAL DELGADO", "GENERAL ELIZARDO AQUINO", "GENERAL EUGENIO A. GARAY", "GENERAL HIGINIO MORINIGO", "GENERAL JOSE EDUVIGIS DIAZ", "GUARAMBARE", "GUASU IGUA", "GUAYAIBI", "GUYRAUGUA", "HERNANDARIAS", "HOHENAU", "HORQUETA", "HUGUA POTI", "HUGUA REY", "HUMAITA", "IRUÑA", "ISLA PUCU", "ISLA UMBU", "ITA", "ITA CORA", "ITA PLANCHON", "ITACURUBI DE LA CORDILLERA", "ITACURUBI DEL ROSARIO", "ITAKYRY", "ITAPE", "ITAPUA POTY", "ITAUGUA", "ITURBE", "J. AUGUSTO SALDIVAR", "JAGUARETE FOREST", "JASY KAÑY", "JEJUI", "JERUSALEN II", "JESUS", "JOSE D. OCAMPOS", "JOSE FALCON", "JOSE FASSARDI", "JUAN DE MENA", "JUAN E. O'LEARY", "KARAPAI", "KATUETE", "KRESSBURGO", "KURUSU DE HIERRO", "LA COLMENA", "LA PALOMA", "LA PASTORA", "LA PAZ", "LAGUNA 7", "LAMBARE", "LAPACHAL", "LAS MERCEDES", "LAURELES", "LEANDRO OVIEDO", "LIMA", "LIMOY", "LIMPIO", "LOLITA", "LOMA GRANDE", "LOMA GUAZU", "LOMA PLATA", "LORETO", "LOS CEDRALES", "LUQUE", "LUZ BELLA", "MACIEL", "MANGRULLO", "MARACANA", "MARIA AUXILIADORA", "MARIANO ROQUE ALONSO", "MAYOR JOSE D. MARTINEZ", "MAYOR OTAÑO", "MBOCAYATY", "MBOCAYATY DEL YHAGUY", "MBUYAPEY", "MCAL. FRANCISCO SOLANO LOPEZ", "MCAL. JOSE F. ESTIGARRIBIA", "MINGA GUAZU", "MINGA PORA", "MOISES BERTONI", "MONTELINDO", "NANAWA", "NARANJAL", "NARANJITO ITAPUA", "NARANJITO SAN PEDRO", "NATALICIO TALAVERA", "NATALIO", "NEULAND", "NUCLEAR 3", "NUEVA ALBORADA", "NUEVA COLOMBIA", "NUEVA ESPERANZA", "NUEVA FORTUNA", "NUEVA GERMANIA", "NUEVA ITALIA", "NUEVA LONDRES", "NUMI", "ÑATIURY GUASU", "ÑEMBY", "OBLIGADO", "PARAGUARI", "PARATODO", "PASO BARRETO", "PASO DE PATRIA", "PASO HORQUETA", "PASO YOBAI", "PDTE. FRANCO", "PEDRO JUAN CABALLERO", "PEJUPA", "PERLITA MBATOVI", "PILAR", "PINDOI", "PINDOTY PORA", "PINDOYU", "PIRAPEY", "PIRAPO", "PIRAYU", "PIRI PUCU", "PIRIBEBUY", "PIRIZAL", "POZO COLORADO", "PRIMERO DE MARZO", "PTO. YVAPOVO", "PUEBLO DE DIOS", "PUENTE KYJA", "PUENTESIÑO", "PUERTO ANTEQUERA", "PUERTO CASADO", "PUERTO IRALA", "PUERTO ITACUA", "PUERTO ITAVERA", "PUERTO PALOMA", "PUERTO PARANAMBU", "PUERTO PINAZCO", "PUERTO SAN JOSE", "QUIINDY", "QUYQUYHO", "R.I. 3 CORRALES", "RAUL ARSENIO OVIEDO", "RAUL PEÑA", "REPATRIACION", "RIO NEGRO", "RIO VERDE", "SALTO DEL GUAIRA", "SAN AGUSTIN", "SAN ALBERTO", "SAN ANTONIO", "SAN BERNARDINO", "SAN BUENAVENTURA", "SAN CARLOS CAAZAPA", "SAN CARLOS CONCEPCIÓN", "SAN COSME Y DAMIAN", "SAN CRISTOBAL", "SAN ESTANISLAO", "SAN FRANCISCO", "SAN IGNACIO", "SAN JOAQUIN", "SAN JOSE DE LOS ARROYOS", "SAN JOSE OBRERO", "SAN JUAN BAUTISTA", "SAN JUAN BAUTISTA DE NEEMBUCU", "SAN JUAN DEL PARANA", "SAN JUAN NEPOMUCENO", "SAN LAZARO", "SAN LORENZO", "SAN LORENZO ESTE", "SAN LUIS DEL PARANA", "SAN MIGUEL", "SAN PABLO KOKUERE", "SAN PATRICIO", "SAN PEDRO DEL PARANA", "SAN PEDRO DEL YCUAMANDIYU", "SAN PEDRO POTY", "SAN R. GONZALEZ DE STA. CRUZ", "SAN RAFAEL DEL PARANA", "SAN RAMON", "SAN SALVADOR", "SAN VICENTE PANCHOLO", "SANTA BARBARA", "SANTA CATALINA", "SANTA CECILIA", "SANTA FE DEL PARANA", "SANTA ROSA DEL AGUARAY", "SANTIAGO", "SAPUCAI", "SIMON BOLIVAR", "SANTA ELENA", "SANTA MARIA", "SANTA RITA", "SANTA ROSA", "SANTA ROSA DEL MBUTUY", "SANTA ROSA DEL MONDAY", "SANTA TERESA", "TACUATI", "TAVAI", "TAVAPY", "TEBICUARY", "TEBICUARYMI", "TEMBIAPORA", "TITO FIRPO", "TOBATI", "TOLEDO", "TRES CORAZONES", "TRINIDAD", "TTE. MONTANIA", "TUPA RENDA", "UNION", "VALENZUELA", "VALLEMI", "VALLEPE", "VAQUERIA", "VILLA BOQUERON", "VILLA CHOFERES", "VILLA DEL ROSARIO", "VILLA ELISA", "VILLA FLORIDA", "VILLA HAYES", "VILLA IGATIMI", "VILLA OLIVA", "VILLALBIN", "VILLARRICA", "VILLETA", "VIRGEN DEL ROSARIO", "YABEBYRY", "YACUTY", "YAGUARON", "YAKAREI", "YATAITY", "YATAITY DEL NORTE", "YATYTAY", "YBY YAU", "YBYCUI", "YBYPYTA", "YBYTIMI", "YGUAZU", "YHOVY CENTRAL", "YHU", "YPACARAI", "YPANE", "YPEHU", "YROYSA", "YRYVU KUA", "YTANARA", "YUTY", "ZANJA PYTA" };
    List<String> ciudades = Arrays.asList(cities);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tigo_money_register_main);
        onSetContentViewFinish();

        SessionEntity session = CsTigoApplication.getSessionHelper().findByImsi(
                telephonyManager.getSubscriberId());
        if (session == null || new Date().after(session.getExpirationDate())) {
            Toast.makeText(
                    TigoMoneyRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_must_init_session),
                    Toast.LENGTH_LONG).show();
            goToMain();
        } else {
            /* obtenemos el current TigoMoneyTmpEntity */
            currentEntity = CsTigoApplication.getCurrentTigoMoneyEntity();

            /*
             * obtenemos el los componentes de la pantalla para validar sus
             * valores
             */
            nameText = (EditText) findViewById(R.id.tigoMoneyNameText);
            birthDate = (EditText) findViewById(R.id.birthDateEdit);
            address = (EditText) findViewById(R.id.addressEdit);
            // city = (EditText) findViewById(R.id.cityEdit);
            nextButton = (Button) findViewById(R.id.tigoMoneyNextButton);

            birthDate.setFocusable(false);
            birthDate.setClickable(true);

            nameText.setText(currentEntity.getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cities);
            citiAutocomplete = (AutoCompleteTextView) findViewById(R.id.cityAutocompleteEdit);
            citiAutocomplete.setAdapter(adapter);

            if (currentEntity != null) {
                if (currentEntity.getBirthDate() != null) {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.setTime(currentEntity.getBirthDate());
                    birthYear = cal.get(Calendar.YEAR);
                    birthMonth = cal.get(Calendar.MONTH);
                    birthDay = cal.get(Calendar.DAY_OF_MONTH);
                    // birthDate.updateDate(cal.get(Calendar.YEAR),
                    // cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    birthDate.setText(new StringBuilder().append(birthDay).append(
                            "/").append(birthMonth + 1).append("/").append(
                            birthYear).append(" "));
                } else {
                    Calendar c = Calendar.getInstance();
                    birthYear = c.get(Calendar.YEAR);
                    birthMonth = c.get(Calendar.MONTH);
                    birthDay = c.get(Calendar.DAY_OF_MONTH);
                    birthDate.setText(new StringBuilder().append(birthDay).append(
                            "/").append(birthMonth + 1).append("/").append(
                            birthYear).append(" "));
                }
                address.setText(currentEntity.getAddress());
                citiAutocomplete.setText(currentEntity.getCity());
            }

            nextButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (validateUserInput()) {
                        serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                                getServicecod(), getServiceEventCod());

                        currentEntity = CsTigoApplication.getCurrentTigoMoneyEntity();

                        if (currentEntity == null) {
                            currentEntity = new TigoMoneyTmpEntity();
                        }

                        GregorianCalendar cal = new GregorianCalendar();
                        cal.set(Calendar.DAY_OF_MONTH, birthDay);
                        cal.set(Calendar.MONTH, birthMonth);
                        cal.set(Calendar.YEAR, birthYear);

                        // ((TigoMoneyTmpEntity)
                        // currentEntity).setEvent(serviceEvent.getServiceEventCod());
                        currentEntity.setBirthDate(cal.getTime());
                        currentEntity.setAddress(address.getText().toString());
                        currentEntity.setCity(citiAutocomplete.getText().toString());

                        CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);

                        startEventActivity(TigoMoneyTakePictureAndRegisterActivity.class);
                    }
                }
            });
        }

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(address.getText())) {
            Toast.makeText(
                    TigoMoneyRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_address_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(citiAutocomplete.getText())) {
            Toast.makeText(
                    TigoMoneyRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_city_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (!ciudades.contains(citiAutocomplete.getText().toString())) {
            Toast.makeText(
                    TigoMoneyRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_city_in_list), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_PICKER_ID:
            return new DatePickerDialog(this, pickerListener, birthYear, birthMonth, birthDay);
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            birthYear = selectedYear;
            birthMonth = selectedMonth;
            birthDay = selectedDay;

            // Show selected date
            birthDate.setText(new StringBuilder().append(birthDay).append("/").append(
                    birthMonth + 1).append("/").append(birthYear).append(" "));

        }
    };

}
