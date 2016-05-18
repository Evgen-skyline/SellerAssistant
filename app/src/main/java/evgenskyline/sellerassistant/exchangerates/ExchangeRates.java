package evgenskyline.sellerassistant.exchangerates;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import evgenskyline.sellerassistant.R;

public class ExchangeRates extends AppCompatActivity {
    //UI
    private Spinner spinnerCurrency;
    private TextView mTV_main;
    private TextView mTV_date;
    private TextView mTV_state;

    private Calendar calendar;

    private String lastSelected = "";
    private static final String mURL = "http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=";
    //20160516&json


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        //Initialization
        spinnerCurrency = (Spinner)findViewById(R.id.ExchangeRatesSpinnerCurrency);
        mTV_date = (TextView)findViewById(R.id.ExchangeRatesTextViewDate);
        mTV_main = (TextView)findViewById(R.id.ExchangeRatesTextViewMain);
        mTV_state = (TextView)findViewById(R.id.ExchangeRatesTextViewState);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 8);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        mTV_date.setText(DateUtils.formatDateTime(ExchangeRates.this, calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        //Toast.makeText(this, strBuilder.toString(), Toast.LENGTH_LONG).show();
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(makeURLstring(calendar));

        mTV_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ExchangeRates.this, dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR, 8);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            calendar.set(Calendar.MINUTE, 1);
            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 1);
            mTV_date.setText(DateUtils.formatDateTime(ExchangeRates.this, calendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(makeURLstring(calendar));
        }
    };

    private String makeURLstring(Calendar cal){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(mURL);
        urlBuilder.append(String.valueOf(cal.get(Calendar.YEAR)));
        int monthTMP = cal.get(Calendar.MONTH) + 1;
        if (monthTMP<10){
            urlBuilder.append("0" + String.valueOf(monthTMP));
        }else {
            urlBuilder.append(String.valueOf(monthTMP));
        }
        int dayTMP = cal.get(Calendar.DAY_OF_MONTH);
        if (dayTMP<10){
            urlBuilder.append("0" + String.valueOf(dayTMP));
        }else {
            urlBuilder.append(String.valueOf(dayTMP));
        }
        urlBuilder.append("&json");
        return urlBuilder.toString();
    }

//==================================================================================================

    private class DownloadTask extends AsyncTask<String, Integer, ArrayList<String>>{
        private HashMap<String, CurrencyRate> hMap;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTV_state.setText("Downloading data...");
            hMap = new HashMap<String, CurrencyRate>();
            pDialog = ProgressDialog.show(ExchangeRates.this, "", "Downloading...", true);
            pDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder resultJSONstrBuilder = new StringBuilder();
            ArrayList currencyList = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                //для сохранения строки
                /*reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    resultJSONstrBuilder.append(line);
                }*/

                //парсинг
                currencyList = new ArrayList<String>();
                JsonReader jReader = new JsonReader(new InputStreamReader(inputStream));
                jReader.beginArray();
                while (jReader.hasNext()){
                    CurrencyRate cr = new CurrencyRate();
                    cr = readMessage(jReader);
                    hMap.put(cr.toString(), cr);
                    currencyList.add(cr.toString());
                }
                jReader.endArray();
            }catch (Exception e){
                CurrencyRate cr = new CurrencyRate();
                cr.setCurrencyCode(e.toString());
                hMap.put("exeception", cr);
            }

            return currencyList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            pDialog.dismiss();
            if (hMap.containsKey("exeception")){
                Toast.makeText(ExchangeRates.this, hMap.get("exeception").getCurrencyCode(), Toast.LENGTH_LONG).show();
                return;
            }

            mTV_state.setText("Download complete!");
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ExchangeRates.this,
                    R.layout.spinner_layout_left, list);
            spinnerCurrency.setAdapter(arrayAdapter);
            if(!(lastSelected.equals(""))){
                spinnerCurrency.setSelection(arrayAdapter.getPosition(lastSelected));
            }else{
                if (list.contains("USD  Долар США")) {
                    spinnerCurrency.setSelection(arrayAdapter.getPosition("USD  Долар США"));
                }
            }

            if (spinnerCurrency.getSelectedItem() == null){
                mTV_main.setText("Неверная дата или какая-то фигня на сайте НБУ");
            }

            spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StringBuilder strBuilder = new StringBuilder();
                    CurrencyRate cr = new CurrencyRate();
                    cr = hMap.get(spinnerCurrency.getSelectedItem().toString());
                    strBuilder.append("Данные на:  " + cr.getDate() + "\n");
                    strBuilder.append(cr.toString() + "\n");
                    strBuilder.append("Курс: " + cr.getRate() + "\n");
                    mTV_main.setText(strBuilder.toString());
                    lastSelected = spinnerCurrency.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private CurrencyRate readMessage(JsonReader reader) throws IOException {
            CurrencyRate currencyRate = new CurrencyRate();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("r030")) {
                    currencyRate.setCurrencyCode(reader.nextString());
                } else if (name.equals("txt")) {
                    currencyRate.setName(reader.nextString());
                } else if (name.equals("rate")) {
                    currencyRate.setRate(reader.nextDouble());
                } else if (name.equals("cc")) {
                    currencyRate.setShortName(reader.nextString());
                } else if (name.equals("exchangedate")) {
                    currencyRate.setDate(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return currencyRate;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

   /* //FOR DEBUG ONLY!!!(write data to SD card)
    private void writeToSD(String sourceStr){
        final String DIR_SD ="1MyJsonFile";

        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        sdPath.mkdirs();
        File sdFile = new File(sdPath, "downloaded_json.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            bw.write(sourceStr);
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
    /*private void writeToSdArr(ArrayList<CurrencyRate> arr){
        StringBuilder strBuilder = new StringBuilder();
        for(int i =0; i < arr.size(); i++){
            strBuilder.append(arr.get(i).currencyCode + "\n");
            strBuilder.append(arr.get(i).name + "\n");
            strBuilder.append(arr.get(i).shortName + "\n");
            strBuilder.append(String.valueOf(arr.get(i).rate) + "\n");
            strBuilder.append(arr.get(i).date + "\n\n");
        }
        writeToSD(strBuilder.toString());
    }*/
}
