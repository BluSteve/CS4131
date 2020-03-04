package com.stevecao.assignment2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stevecao.assignment2.model.ClinicHandler;

public class SettingsFragment extends Fragment {
    Switch darkModeSwitch;
    Button loginBtn, signUpBtn, updateClinicsBtn;
    Spinner newsLangSpinner;
    ImageView settingsLoadingIV;
    SharedPreferences prefs;
    Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        prefs = getView().getContext().getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);
        darkModeSwitch = getView().findViewById(R.id.darkModeSwitch);
        loginBtn = getView().findViewById(R.id.loginBtn);
        signUpBtn = getView().findViewById(R.id.signUpBtn);
        updateClinicsBtn = getView().findViewById(R.id.updateClinicsBtn);
        newsLangSpinner = getView().findViewById(R.id.newsLangSpinner);
        settingsLoadingIV = getView().findViewById(R.id.settingsLoadingIV);
        mContext = getContext();

        Glide.with(mContext).load(R.drawable.loading2).into(settingsLoadingIV);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.newsLanguages, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newsLangSpinner.setAdapter(spinnerAdapter);
        newsLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String shortLang = "en";

                switch (parent.getItemAtPosition(position).toString()) {
                    case "English":
                        shortLang = "en";
                        break;
                    case "Spanish":
                        shortLang = "es";
                        break;
                    case "French":
                        shortLang = "fr";
                        break;
                    case "German":
                        shortLang = "de";
                        break;
                }
                Log.d("shortlang", shortLang);
                prefs.edit().putString("com.stevecao.assignment2.globalnewsurl", "https://newsapi.org/v2/top-headlines?q=c" +
                        "oronavirus&language=" + shortLang + "&apiKey=98d25766996b4d85a81df8c048cffe35").apply();
                prefs.edit().putInt("com.stevecao.assignment2.spinnerpos", position).apply();
                Log.d("globalurl2", prefs.getString("com.stevecao.assignment2.globalnewsurl",
                        "asdf"));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateClinicsBtn.setOnClickListener((s) -> {
            (new UpdateClinics()).execute();
        });

        darkModeSwitch.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));
        darkModeSwitch.setChecked(prefs.getBoolean("com.stevecao.assignment2.darkmode", true));
        darkModeSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                prefs.edit().putBoolean("com.stevecao.assignment2.darkmode", true).apply();
            } else {
                prefs.edit().putBoolean("com.stevecao.assignment2.darkmode", false).apply();
            }
            Toast.makeText(getContext(), "Restart app to see changes.", Toast.LENGTH_SHORT).show();
        });

        updateBtns();
    }

    private final class UpdateClinics extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            ClinicHandler.updateClinics(mContext);
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            settingsLoadingIV.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            settingsLoadingIV.setVisibility(View.GONE);
            Toast.makeText(mContext, "Clinics database update successful!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        newsLangSpinner.setSelection(prefs.getInt("com.stevecao.assignment2.spinnerpos",
                0));
    }

    private void updateBtns() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("login", FirebaseAuth.getInstance().getCurrentUser() + "");
            loginBtn.setText(getResources().getString(R.string.logoutBtn));
            signUpBtn.setVisibility(View.GONE);

            loginBtn.setOnClickListener((s) -> {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Sign-out successful!", Toast.LENGTH_SHORT).show();
                updateBtns();
            });
        } else {
            loginBtn.setText(getResources().getString(R.string.loginBtn));
            signUpBtn.setVisibility(View.VISIBLE);

            loginBtn.setOnClickListener((s) -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });

            signUpBtn.setOnClickListener((s) -> {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
            });
        }
    }
}
