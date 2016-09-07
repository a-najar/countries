package com.geniusforapp.countries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geniusforapp.countries.adapters.AdapterCountries;
import com.geniusforapp.countries.adapters.FastScroller;
import com.geniusforapp.countries.adapters.ScrollingLinearLayoutManager;
import com.geniusforapp.countries.entites.EntityCountry;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CountriesActivity extends AppCompatActivity implements TextWatcher, TextView.OnEditorActionListener, AdapterCountries.OnCountryClickListener {
    private EditText editSearch;
    private ImageButton btnClose;
    private RecyclerView listCountries;
    private ProgressBar progressBar;
    private FastScroller fastScroller;
    public static final String TAG_COUNTRY = "country";

    private ArrayList<EntityCountry> countries = new ArrayList<>();
    private AdapterCountries adapterCountries;
    public static final String TAG = CountriesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);
        setupButtons();
        setupProgress();
        setupEditSearch();
        setupCountriesList();


    }

    private void setupProgress() {
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    private void setupCountriesList() {
        fastScroller = (FastScroller) findViewById(R.id.fastscroll);
        listCountries = (RecyclerView) findViewById(R.id.list_countries);
        adapterCountries = new AdapterCountries(this);
        adapterCountries.setOnCountryClickListener(this);
        listCountries.setItemAnimator(new DefaultItemAnimator());

        listCountries.setLayoutManager(new ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, 1000));
        listCountries.setAdapter(adapterCountries);
        fastScroller.setRecyclerView(listCountries);
        setupCountriesData();

    }

    private void setupCountriesData() {
        try {
            JSONArray response = new JSONArray(readJsonCountries());
            for (int i = 0; i < response.length(); i++) {
                try {
                    countries.add(new Gson().fromJson(response.getJSONObject(i).toString(), EntityCountry.class));
                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: " + e.toString());
                }
            }
            adapterCountries.addAll(countries);
            progressBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setupButtons() {
        btnClose = (ImageButton) findViewById(R.id.btn_close);

    }

    private void setupEditSearch() {
        editSearch = (EditText) findViewById(R.id.edt_search);
        editSearch.addTextChangedListener(this);
        editSearch.setOnEditorActionListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        btnClose.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
        adapterCountries.filter(charSequence.toString());

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    public void clear(View view) {
        editSearch.setText("");
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyBoard();
            return true;
        }
        return false;
    }


    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void OnCountryClick(EntityCountry country) {
        Log.d(TAG, "OnCountryClick: " + country.toString());
        Intent intent = new Intent();
        intent.putExtra(TAG_COUNTRY, country);
        setResult(RESULT_OK, intent);
        finish();
    }


    public String readJsonCountries() {
        //Get Data From Text Resource File Contains Json Data.
        InputStream inputStream = getResources().openRawResource(R.raw.countries_v1);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
