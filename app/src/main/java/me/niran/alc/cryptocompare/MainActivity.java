package me.niran.alc.cryptocompare;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egbanubi on 05/11/2017.
 */

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<Currency>> {

    private static final String CRYPTOSTR = "https://min-api.cryptocompare.com/data/pricemultifull";

    private int KEY = 1;
    private CryptoAdapter cryptoAdapter;
    private ImageView emptyView;
    private android.app.LoaderManager loaderManager;
    private ProgressBar progressBar2;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);

        cryptoAdapter = new CryptoAdapter(this, new ArrayList<Currency>());
        //finds the listView reference
        listView = (ListView) findViewById(R.id.rootView);
        listView.setAdapter(cryptoAdapter);


        emptyView = (ImageView) findViewById(R.id.progress);
        progressBar2 = (ProgressBar) findViewById(R.id.progress2);


        if (cryptoAdapter.isEmpty()) {

            listView.setEmptyView(emptyView);

            emptyView.setOnClickListener(new View.OnClickListener() {


                                             @Override
                                             public void onClick(View view) {
                                                 check();
                                             }
                                         }
            );
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Currency current = cryptoAdapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), ConvertCurrency.class);
                assert current != null;
                intent.putExtra("price", current.getmPrice());
                intent.putExtra("percentage", current.getmChangepct24hr());
                intent.putExtra("update", current.getmLastUpdate());
                intent.putExtra("volume24hr", current.getmVolume24hr());
                intent.putExtra("mktcap", current.getMktcap());
                intent.putExtra("tosymbol", current.getmToSYmbol());


                startActivity(intent);
            }
        });

        if (hasNetwork()) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(KEY, null, this);
        } else {

            progressBar2.setVisibility(View.GONE);

            emptyView.setImageResource(R.drawable.progress_internet);
        }

    }


    private void check() {
        KEY = KEY + 1;

        emptyView.setImageResource(0);

        progressBar2.setVisibility(View.VISIBLE);

        if (cryptoAdapter.isEmpty()) {

            listView.setEmptyView(emptyView);
        }

        if (hasNetwork()) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(KEY, null, MainActivity.this);

        } else {
            progressBar2.setVisibility(View.GONE);

            emptyView.setImageResource(R.drawable.progress_internet);
        }


        Toast.makeText(getApplicationContext(), "reloading", Toast.LENGTH_LONG).show();

    }


    @Override
    public Loader<List<Currency>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_key), getString(R.string.settings_order_default));


        Uri baseUri = Uri.parse(CRYPTOSTR);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("fsyms", orderBy);

        builder.appendQueryParameter("tsyms", "EUR,USD,IDR,KRW,CNY,USDT,JPY,AUD,CAD,MXN,VND,HKD,SGD,BRL,PLN,MYR,RUB,GBP,NGN,ZAR");
        String dam = builder.toString();
        String damm = dam.replace("%2C", ",");

        return new CryptoAsyncLoader(this, damm);
    }

    @Override
    public void onLoadFinished(Loader<List<Currency>> loader, List<Currency> cryptoWordses) {


        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress2);
        cryptoAdapter.clear();
        emptyView.setImageResource(R.drawable.progress_list);
        if (!cryptoWordses.isEmpty() || cryptoWordses != null) {

            progressBar2.setVisibility(View.GONE);
            cryptoAdapter.addAll(cryptoWordses);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        cryptoAdapter.clear();
    }

    private boolean hasNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }


}
