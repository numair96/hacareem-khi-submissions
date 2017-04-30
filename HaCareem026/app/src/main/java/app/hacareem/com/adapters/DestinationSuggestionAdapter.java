package app.hacareem.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.hacareem.com.R;
import app.hacareem.com.bean.Places;


/**
 * Created by Numair Qadir on 04/29/2017.
 */

public class DestinationSuggestionAdapter extends RecyclerView.Adapter<DestinationSuggestionAdapter.MyViewHolder> {
    private Context context;
    private List<Places> placesArrayList;

    public DestinationSuggestionAdapter(Context ctx, List<Places> checkInOutList) {
        this.context = ctx;
        this.placesArrayList = checkInOutList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_list_row_child, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Places movie = placesArrayList.get(position);

        String location = "<big><b>" + movie.getDest() + "</b></big>";
        holder.txtName.setText(Html.fromHtml(location));

        holder.txtLatLng.setText(movie.getLatitude() + ", " + movie.getLongitude());
    }

    @Override
    public int getItemCount() {
        return placesArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtLatLng;
        TextView txtName;

        MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtLocationName);
            txtLatLng = (TextView) view.findViewById(R.id.txtLatLng);
        }
    }
}