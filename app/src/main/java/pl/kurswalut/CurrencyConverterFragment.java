package pl.kurswalut;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrencyConverterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrencyConverterFragment extends Fragment {

    Spinner fromCurrency;
    Spinner toCurrency;
    TextView textView;
    TextView textViewAktualnyKurs;
    Map<String, String> listaFrom;
    Map<String, String> listaTo;

    public CurrencyConverterFragment() {
        // Required empty public constructor
    }

    public static CurrencyConverterFragment newInstance() {
        CurrencyConverterFragment fragment = new CurrencyConverterFragment();
        return fragment;
    }

    public void setCurrentValue(String toCurr) {
        textViewAktualnyKurs.setText("1 PLN = " + listaFrom.get(toCurr) + " " + toCurr);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public HashMap loadConvByType(String base) throws IOException {

        final HashMap<String, String> map = new HashMap<>();
        String url = "https://api.exchangeratesapi.io/latest?base=" + base;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            JSONObject b = obj.getJSONObject("rates");

                            Iterator keysToCopyIterator = b.keys();
//                            keysList = new ArrayList<>();


                            while (keysToCopyIterator.hasNext()) {
                                String key = (String) keysToCopyIterator.next();
//                                keysList.add(key);
                                map.put(key, b.getString(key));
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>(map.keySet()));
                            toCurrency.setAdapter(spinnerArrayAdapter);
                            setCurrentValue(toCurrency.getSelectedItem().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return map;
    }

    //pobieranie liste dostepnych walut do konwersji i wyswietlenie w spinnerze
    public void loadConvTypes() throws IOException {

        listaFrom = new HashMap<>();
        listaTo = new HashMap<>();
        String url = "https://api.exchangeratesapi.io/latest?base=PLN";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            JSONObject b = obj.getJSONObject("rates");

                            Iterator keysToCopyIterator = b.keys();
//                            keysList = new ArrayList<>();


                            while (keysToCopyIterator.hasNext()) {
                                String key = (String) keysToCopyIterator.next();
//                                keysList.add(key);
                                listaFrom.put(key, b.getString(key));
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>(listaFrom.keySet()));
                            toCurrency.setAdapter(spinnerArrayAdapter);
                            fromCurrency.setAdapter(spinnerArrayAdapter);
//                            setCurrentValue(toCurrency.getSelectedItem().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toCurrency = (Spinner) view.findViewById(R.id.toSpinner);
        fromCurrency = (Spinner) view.findViewById(R.id.fromCurrencySpinner);
        final EditText fromValue = (EditText) view.findViewById(R.id.fromCurrencyValue);
        final EditText toValue = (EditText) view.findViewById(R.id.toValue);
        final Button btnConvert = (Button) view.findViewById(R.id.button);
        textView = (TextView) view.findViewById(R.id.textView7);
        textViewAktualnyKurs = (TextView) view.findViewById(R.id.aktualnyKurs);
        try {
            loadConvTypes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        toCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCurrentValue(toCurrency.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hideSoftKeyboard(getActivity());
                } catch (Exception e) {
                }

                if (!fromValue.getText().toString().isEmpty()) {
                    String toCurr = toCurrency.getSelectedItem().toString();
                    double plnVlaue = Double.valueOf(fromValue.getText().toString());
                    Toast.makeText(getActivity(), "Please Wait..", Toast.LENGTH_SHORT).show();
                    try {
                        convertCurrency(toCurr, plnVlaue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Enter a Value to Convert..", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //obliczanie wartosci wybranej waluty
    public void convertCurrency(final String toCurr, final double plnValue) throws IOException {

        String url = "https://api.exchangeratesapi.io/latest?base=PLN";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            JSONObject b = obj.getJSONObject("rates");
                            String val = b.getString(toCurr);
                            double output = plnValue * Double.valueOf(val);
                            textView.setText(String.valueOf(output) + " " + toCurr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency_converter, container, false);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
