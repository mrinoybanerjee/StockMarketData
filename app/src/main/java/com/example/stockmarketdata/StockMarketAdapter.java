package com.example.stockmarketdata;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockMarketAdapter extends RecyclerView.Adapter<StockMarketAdapter.MyViewHolder> implements Filterable {
    private JSONArray mDataset;
    private JSONArray filteredDataset;
    private LayoutInflater mInflater;
    private Context mcontext;

    private OnItemClickListener mlistener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(int position) throws JSONException;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView textView_name;
        public TextView textView_price;
        public TextView textView_pricedelta;
        public TextView textView_id;
        public ImageView imageView;
        public JSONObject data;

        public MyViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.card_view);
            textView_name = v.findViewById(R.id.text_name);
            textView_price = v.findViewById(R.id.text_price);
            textView_pricedelta = v.findViewById(R.id.text_price2);
            textView_id = v.findViewById(R.id.text_id);
            imageView = v.findViewById(R.id.card_image);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            try {
                                mlistener.onItemClick(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StockMarketAdapter(Context context, JSONArray myDataset) throws JSONException {
        this.mDataset = myDataset;
        this.mInflater = LayoutInflater.from(context);
        this.mcontext = context;
        this.filteredDataset = new JSONArray();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StockMarketAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.adapter_stocks, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StockMarketAdapter.MyViewHolder holder, final int position) {
        try {
            holder.textView_name.setText((String) filteredDataset.getJSONObject(position).get("name"));

            double price = (double)filteredDataset.getJSONObject(position).get("price");
            double previousPrice = (double)filteredDataset.getJSONObject(position).get("previousValue");
            double deltaPrice = price-previousPrice;
            String priceString = String.format("$%.2f", price);
            String deltaPriceString = String.format("$%.2f", deltaPrice);


            if (deltaPrice == 0) {
                holder.textView_pricedelta.setTextColor(Color.GRAY);
                deltaPriceString = deltaPriceString;
            } else if (deltaPrice > 0) {
                holder.textView_pricedelta.setTextColor(Color.GREEN);
                deltaPriceString = "+" + deltaPriceString;
            } else {
                holder.textView_pricedelta.setTextColor(Color.RED);
                deltaPriceString = "-" + deltaPriceString;
            }

            holder.textView_price.setText(priceString);
            holder.textView_id.setText((String) filteredDataset.getJSONObject(position).get("id"));
            holder.textView_pricedelta.setText(deltaPriceString);
            holder.data = filteredDataset.getJSONObject(position);
            holder.itemView.setSelected(selectedPos == position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Picasso.with(mcontext)
                    .load((String) filteredDataset.getJSONObject(position).get("imageUrl"))
                    .resize(400, 150)
                    .centerInside()
                    .into(holder.imageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return filteredDataset.length();
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void clearFilteredData() {
        for (int i = filteredDataset.length()-1; i >= 0; i--) {
            filteredDataset.remove(i);
        }
    }

    public void resetFilteredData() throws JSONException {
        for (int i = 0; i < mDataset.length(); i++) {
            filteredDataset.put(mDataset.getJSONObject(i));
        }
    }

    Filter filter = new Filter() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            clearFilteredData();
            String filterPattern = constraint.toString().toLowerCase();
            for (int i = 0; i < mDataset.length(); i++) {
                try {
                    String name = (String) mDataset.getJSONObject(i).get("name");
                    String id = (String) mDataset.getJSONObject(i).get("id");

                    // Filters by company type when the company type is typed on the search bar.
                    JSONArray companyTypes = (JSONArray) mDataset.getJSONObject(i).get("companyType");

                    if (name.toLowerCase().contains(filterPattern) || id.toLowerCase().contains(filterPattern)) {
                        filteredDataset.put(mDataset.getJSONObject(i));
                    } else {
                        for (int q = 0; q < companyTypes.length(); q++) {
                            if (((String)companyTypes.get(i)).toLowerCase().equals(filterPattern)) {
                                filteredDataset.put(mDataset.getJSONObject(i));
                                break;
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };




}

