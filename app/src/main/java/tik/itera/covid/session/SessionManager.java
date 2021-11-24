package tik.itera.covid.session;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import tik.itera.covid.activity.LoginActivity;

public class SessionManager {

	SharedPreferences pref;

	Editor editor;
	Context _context;

	int PRIVATE_MODE = 0;

	private static final String PREF_NAME = "User";
	private static final String IS_LOGIN = "IsLoggedIn";

	public static final String KEY_EMAIL = "email";
	public static final String KEY_ID_PEGAWAI = "id_pegawai";
	public static final String KEY_NAMA_UNIT = "nama_unit";
	public static final String KEY_NAMA_PEGAWAI = "nama_pegawai";
	public static final String KEY_FOTO = "foto";
	public static final String KEY_TOKEN = "token";

	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void createLoginSession(String email, String id_pegawai, String nama_unit,
								   String nama_pegawai, String foto, String token){
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_ID_PEGAWAI, id_pegawai);
		editor.putString(KEY_NAMA_UNIT, nama_unit);
		editor.putString(KEY_NAMA_PEGAWAI, nama_pegawai);
		editor.putString(KEY_FOTO, foto);
		editor.putString(KEY_TOKEN, token);
		editor.commit();
	}

	public void checkLogin(){
		if(!this.isLoggedIn()){
			Intent i = new Intent(_context, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(i);
		}
	}

	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();

		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_ID_PEGAWAI, pref.getString(KEY_ID_PEGAWAI, null));
		user.put(KEY_NAMA_UNIT, pref.getString(KEY_NAMA_UNIT, null));
		user.put(KEY_NAMA_PEGAWAI, pref.getString(KEY_NAMA_PEGAWAI, null));
		user.put(KEY_FOTO, pref.getString(KEY_FOTO, null));
		user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

		return user;
	}

	public void logoutUser(){
		editor.clear();
		editor.commit();

		Intent i = new Intent(_context, LoginActivity.class);

		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		_context.startActivity(i);
	}

	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
