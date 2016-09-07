package com.geniusforapp.countries.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniusforapp.countries.R;
import com.geniusforapp.countries.entites.EntityCountry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by ahmadnajar on 9/7/16.
 */

public class AdapterCountries extends RecyclerView.Adapter {
    private ArrayList<EntityCountry> countries = new ArrayList<>();
    private ArrayList<EntityCountry> copyCountries = new ArrayList<>();
    OnCountryClickListener onCountryClickListener;
    private Activity activity;

    public AdapterCountries(Activity activity) {
        this.activity = activity;
    }

    public void setOnCountryClickListener(OnCountryClickListener onCountryClickListener) {
        this.onCountryClickListener = onCountryClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CountriesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final EntityCountry country = countries.get(position);
        CountriesViewHolder countriesViewHolder = (CountriesViewHolder) holder;
        countriesViewHolder.countryEn.setText(country.getName() + " - " + country.getNativeName());
        countriesViewHolder.region.setText(country.getRegion());
        countriesViewHolder.timezone.setText(country.getTimezones() != null ? (!country.getTimezones().isEmpty() ? country.getTimezones().get(0) : "") : "");
        countriesViewHolder.dialingCode.setText(country.getCallingCodes() != null ? (!country.getCallingCodes().isEmpty() ? "(+" + country.getCallingCodes().get(0) + ")" : "") : "");
        countriesViewHolder.cardCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCountryClickListener != null) {
                    onCountryClickListener.OnCountryClick(country);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    class CountriesViewHolder extends RecyclerView.ViewHolder {
        public CardView cardCountry;
        public TextView dialingCode;
        public TextView timezone;
        public TextView region;
        public TextView countryEn;
        public ImageView countryImage;

        public CountriesViewHolder(View itemView) {
            super(itemView);
            cardCountry = (CardView) itemView.findViewById(R.id.card_country);
            dialingCode = (TextView) itemView.findViewById(R.id.dialing_code);
            timezone = (TextView) itemView.findViewById(R.id.time_zone);
            region = (TextView) itemView.findViewById(R.id.region);
            countryEn = (TextView) itemView.findViewById(R.id.country_en);
            countryImage = (ImageView) itemView.findViewById(R.id.country_image);
        }
    }

    public void addAll(Collection<EntityCountry> newList) {
        countries.clear();
        countries.addAll(newList);
        copyCountries.clear();
        copyCountries.addAll(newList);
        notifyDataSetChanged();
    }

    public void filter(String newText) {
        countries.clear();
        if (!newText.toLowerCase().isEmpty()) {
            for (EntityCountry country : copyCountries) {
                if (Pattern.compile(Pattern.quote(newText.toLowerCase()), Pattern.CASE_INSENSITIVE).matcher(country.getNativeName().toLowerCase()).find()
                        || Pattern.compile(Pattern.quote(newText.toLowerCase()), Pattern.CASE_INSENSITIVE).matcher(country.getName().toLowerCase()).find()
                        || Pattern.compile(Pattern.quote(newText.toLowerCase()), Pattern.CASE_INSENSITIVE).matcher(country.getCallingCodes().get(0).toLowerCase()).find()) {
                    countries.add(country);
                }
            }
        } else {
            countries.addAll(copyCountries);
        }

        notifyDataSetChanged();
    }

    public interface OnCountryClickListener {
        void OnCountryClick(EntityCountry country);
    }


}
