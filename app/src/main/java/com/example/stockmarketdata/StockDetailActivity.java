package com.example.stockmarketdata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.stockmarketdata.MainActivity.EXTRA_ADDRESS;
import static com.example.stockmarketdata.MainActivity.EXTRA_ATHPRICE;
import static com.example.stockmarketdata.MainActivity.EXTRA_ID;
import static com.example.stockmarketdata.MainActivity.EXTRA_NAME;
import static com.example.stockmarketdata.MainActivity.EXTRA_PRICE;
import static com.example.stockmarketdata.MainActivity.EXTRA_TYPE;
import static com.example.stockmarketdata.MainActivity.EXTRA_URL;

public class StockDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        Intent intent = getIntent();

        // Setting the image on the Detail activity
        String imageUrl = intent.getStringExtra(EXTRA_URL);
        ImageView imageView = findViewById(R.id.stock_detail_image);
        Picasso.with(this)
                .load(imageUrl)
                .fit()
                .into(imageView);

        // Setting the details on the detail activity for each company
        String name = intent.getStringExtra(EXTRA_NAME);
        TextView nameTextView = findViewById(R.id.stock_detail_name);
        nameTextView.setText(name);

        String address = intent.getStringExtra(EXTRA_ADDRESS);
        TextView addressTextView = findViewById(R.id.stock_detail_address);
        addressTextView.setText("Address: " + address);

        String price = intent.getStringExtra(EXTRA_PRICE);
        TextView priceTextView = findViewById(R.id.stock_detail_price);
        priceTextView.setText("Price: $" + price);

        String athPrice = intent.getStringExtra(EXTRA_ATHPRICE);
        TextView athPriceTextView = findViewById(R.id.stock_detail_all_time_high);
        athPriceTextView.setText("All Time high Price: $" + athPrice);

        String id = intent.getStringExtra(EXTRA_ID);
        TextView idTextView = findViewById(R.id.stock_detail_id);
        idTextView.setText("ID: " + id);

        String companyType = intent.getStringExtra(EXTRA_TYPE);
        TextView typeTextView = findViewById(R.id.stock_detail_company_type);
        typeTextView.setText("Company Type: " + companyType);

        Picasso.with(this)
                .load(imageUrl)
                .resize(400, 400)
                .centerInside()
                .into(imageView);
    }
}