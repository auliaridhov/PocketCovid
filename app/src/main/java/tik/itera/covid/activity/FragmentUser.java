package tik.itera.covid.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;

import tik.itera.covid.R;
import tik.itera.covid.network.BaseApiService;
import tik.itera.covid.session.SessionManager;

public class FragmentUser extends Fragment {


    //Request Options
    private RequestOptions options;

    //Services
    private SessionManager session;
    private BaseApiService baseApiService;

    //String
    private String sEmail, sIDPegawai, sNamaUnit, sNamaPegawai, sFoto, sToken;

    public FragmentUser() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        nampilUser(v);
        return v;
    }

    private void nampilUser(View v) {
        options = new RequestOptions()
                .centerCrop();

        session = new SessionManager(getContext());
        session.checkLogin();

        if(session.isLoggedIn()) {
            HashMap<String, String> sUser = session.getUserDetails();
            sEmail = sUser.get(SessionManager.KEY_EMAIL);
            sIDPegawai = sUser.get(SessionManager.KEY_ID_PEGAWAI);
            sNamaUnit = sUser.get(SessionManager.KEY_NAMA_UNIT);
            sNamaPegawai = sUser.get(SessionManager.KEY_NAMA_PEGAWAI);
            sFoto = sUser.get(SessionManager.KEY_FOTO);
            sToken = sUser.get(SessionManager.KEY_TOKEN);
        }

        TextView txtNamaPegawai = v.findViewById(R.id.txt_user);
        TextView txtEmail = v.findViewById(R.id.txtEmail);
        ImageView imageUser = v.findViewById(R.id.img_user);

        txtNamaPegawai.setText(sNamaPegawai);
        txtEmail.setText(sEmail);

        Glide.with(getContext())
                .load(sFoto)
                .apply(options)
                .into(imageUser);


    }

}
