package tik.itera.covid.adapter.presensi;


import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import tik.itera.covid.R;
import tik.itera.covid.activity.FragmentPresensi;
import tik.itera.covid.model.presensi.Presensi;
import tik.itera.covid.utils.MyUtils;

public class PresensiAdapter extends RecyclerView.Adapter<PresensiAdapter.PresensiViewHolder> {

    private ArrayList<Presensi> dataList;
    private RecyclerPresensiAdapter listener;
    private View view;

    private MyUtils myUtils;

    class PresensiViewHolder extends RecyclerView.ViewHolder {

        TextView txtTanggal, txtjamMasuk, txtJamPulang,txtJamSiang, statusBoking, statusTerlambat;

        PresensiViewHolder(View itemView) {
            super(itemView);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtjamMasuk = itemView.findViewById(R.id.txtJamMasuk);
            txtJamPulang = itemView.findViewById(R.id.txtJamPulang);
            txtJamSiang = itemView.findViewById(R.id.txtJamSiang);

            statusBoking = itemView.findViewById(R.id.statusBoking);
            statusTerlambat = itemView.findViewById(R.id.statusTerlambat);
            view = itemView;

            myUtils = new MyUtils(view.getContext());
        }
    }

    public PresensiAdapter(ArrayList<Presensi> dataList, RecyclerPresensiAdapter listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public PresensiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.presensi_list_row, parent, false);
        return new PresensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PresensiViewHolder holder, final int position) {
//        holder.txtTanggal.setText(myUtils.changeTanggal(dataList.get(position).getTgl_bln_thn()));
//        holder.txtjamMasuk.setText(dataList.get(position).getJam_masuk());
//        holder.txtJamPulang.setText(dataList.get(position).getJam_pulang());
//        holder.txtJamSiang.setText(dataList.get(position).getJam_siang());
//
        String xStatus = dataList.get(position).getStatus();
        String xLibur = dataList.get(position).getLibur();
        String xCovid = dataList.get(position).getCoStatus();
        String xTerlambat = dataList.get(position).getTerlambat();

        holder.txtTanggal.setText(myUtils.changeTanggal(dataList.get(position).getTgl_bln_thn()));
        holder.txtjamMasuk.setText(dataList.get(position).getJam_masuk());
        holder.txtJamPulang.setText(dataList.get(position).getJam_pulang());
        holder.txtJamSiang.setText(dataList.get(position).getJam_siang());
        //holder.statusBoking.setText(dataList.get(position).getStatus());

        if (xLibur.equalsIgnoreCase("Libur")){
            holder.statusBoking.setText(xLibur);
            holder.statusBoking.setTextColor(Color.WHITE);
            holder.statusTerlambat.setVisibility(View.GONE);
            holder.statusBoking.setBackgroundResource(R.drawable.presen_red);
        }else{
            if (xCovid.equals("Tidak")){
                if (xStatus.equals("Hadir")){
                    if (xTerlambat.equals("0")){
                        holder.statusBoking.setText(xStatus);
                        holder.statusBoking.setTextColor(Color.WHITE);
                        holder.statusTerlambat.setVisibility(View.GONE);
                        holder.statusBoking.setBackgroundResource(R.drawable.presen_blue);
                    } else {
                        holder.statusBoking.setText(xStatus);
                        holder.statusBoking.setTextColor(Color.WHITE);
                        holder.statusTerlambat.setVisibility(View.VISIBLE);
                        holder.statusBoking.setBackgroundResource(R.drawable.presen_blue);
                    }
                }else {
                    holder.statusBoking.setText(xStatus);
                    holder.statusBoking.setTextColor(Color.WHITE);
                    holder.statusTerlambat.setVisibility(View.GONE);
                    holder.statusBoking.setBackgroundResource(R.drawable.presen_red);
                }
            }else{
                if (xTerlambat.equals("0")){
                    holder.statusBoking.setText(xCovid);
                    holder.statusBoking.setTextColor(Color.WHITE);
                    holder.statusTerlambat.setVisibility(View.GONE);
                    holder.statusBoking.setBackgroundResource(R.drawable.presen_yellow);
                }else {
                    holder.statusBoking.setText(xCovid);
                    holder.statusBoking.setTextColor(Color.WHITE);
                    holder.statusTerlambat.setVisibility(View.VISIBLE);
                    holder.statusBoking.setBackgroundResource(R.drawable.presen_yellow);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    public interface RecyclerPresensiAdapter {
        void onRecyclerpresensiSelected(Presensi presensi);
    }
}
