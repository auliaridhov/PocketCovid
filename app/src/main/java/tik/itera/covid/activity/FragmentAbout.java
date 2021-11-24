package tik.itera.covid.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tik.itera.covid.R;

public class FragmentAbout extends Fragment {

    private View v;
    private TextView txtDevelopment, txtVersion;

    public FragmentAbout() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about, container, false);
        initData();
        return v;
    }

    private void initData() {
        txtDevelopment = v.findViewById(R.id.txtDevelopment);
        txtVersion = v.findViewById(R.id.txtVersion);

        txtDevelopment.setText(
                getResources().getString(R.string.copy_apps) +
                        " 2020 "+
                getResources().getString(R.string.dev_apps));

        txtVersion.setText(getResources().getString(R.string.app_name) + "\nVersion "+getResources().getString(R.string.version_apps));
    }

}
