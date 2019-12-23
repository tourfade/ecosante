package com.kamitsoft.ecosante.client.patient.oracles;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.database.UserDAO;
import com.kamitsoft.ecosante.model.PhysicianInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;
import com.kamitsoft.ecosante.services.OracleProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhysistOracleAdapter extends ArrayAdapter {

    private String token;
    //private final UserDAO localDatabaseRepo;
    private List<PhysicianInfo> dataList;
    private Context mContext;


    private PhysistOracleAdapter.ListFilter listFilter = new PhysistOracleAdapter.ListFilter();

    public PhysistOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        mContext = context;
        KsoftDatabase.getInstance(context)
                .userDAO()
                .getConnectedAccount()
                .observe((ComponentActivity)context, accountInfo -> {
                    if(accountInfo !=null)
                        token = accountInfo.getJwtToken();
        });
        initProxy();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public PhysicianInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_oracle_item, parent, false);
        }
        TextView speciality = view.findViewById(R.id.speciality);
        TextView fullName = view.findViewById(R.id.fullName);
        ImageView avatar = view.findViewById(R.id.avatar);
        PhysicianInfo user = getItem(position);
        speciality.setText(Utils.niceFormat(user.speciality));
        fullName.setText(Utils.formatUser(getContext(), user));
        Utils.load(getContext(),user.avatar,avatar,R.drawable.user_avatar,R.drawable.physist);

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();


        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                //Call to database to get matching records using room
                try {
                    Response<List<PhysicianInfo>> response = oracleProxy.search(searchStrLowerCase).execute();
                    results.values = response.body();
                    results.count = response.body().size();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<PhysicianInfo>)results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
    private OracleProxy oracleProxy;
    private void initProxy(){


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    if (token != null) {
                        Request request = chain
                                .request()
                                .newBuilder()
                                .addHeader("Authorization", token)
                                .build();
                        return chain.proceed(request);
                    }
                    return chain.proceed(chain.request());
                }).build();

        oracleProxy =  new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(Utils.getGsonBuilder()))
                .baseUrl(BuildConfig.SERVER_URL)
                .build().create(OracleProxy.class);


    }


}