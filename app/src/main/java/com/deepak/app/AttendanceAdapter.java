package com.deepak.app;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private final List<AttendanceModel> attendanceList;
    public AttendanceAdapter(List<AttendanceModel> attendanceList) {
        this.attendanceList = attendanceList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        AttendanceModel model = attendanceList.get(position);
        holder.date.setText(model.getDate());
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(model.getDate());
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            assert date != null;
            holder.day.setText(dayFormat.format(date));
        } catch (ParseException e) {
            holder.day.setText("");
        }
        String status = model.getStatus();
        holder.status.setText(status);
        if (status.equalsIgnoreCase("P")) {
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundColor(Color.rgb(46, 125, 50));
        } else if (status.equalsIgnoreCase("A")) {
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundColor(Color.rgb(198, 40, 40));
        } else {
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundColor(Color.GRAY);
        }
    }
    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView day;
        TextView status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.attendance_date);
            day = itemView.findViewById(R.id.attendance_day);
            status = itemView.findViewById(R.id.attendance_status);
        }
    }

}
