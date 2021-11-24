package tik.itera.covid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import tik.itera.covid.R;

public class PocketRowAdapter extends BaseAdapter {

    Context context;
    int flags[];
    String[] strRow;
    LayoutInflater inflter;

    public PocketRowAdapter(Context applicationContext, int[] flags, String[] strRow) {
        this.context = applicationContext;
        this.flags = flags;
        this.strRow = strRow;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.pocket_row, null);
        TextView txtRow = view.findViewById(R.id.txtRow);
        txtRow.setText(strRow[i]);
        return view;
    }
}