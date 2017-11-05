package me.niran.alc.cryptocompare;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Egbanubi on 05/11/2017.
 */

class CryptoAsyncLoader extends AsyncTaskLoader<List<Currency>> {
    private final String mUrl;

    public CryptoAsyncLoader(Context context, String vUrl) {
        super(context);
        mUrl = vUrl;

    }

    @Override
    public List<Currency> loadInBackground() {

        return QueryUtils.fetchDevData(mUrl);

    }

    @Override
    protected void onStartLoading() {
        forceLoad();

    }
}

