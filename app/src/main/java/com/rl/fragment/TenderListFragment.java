package com.rl.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rl.adapter.ConsumerTenderListAdapter;
import com.rl.fieldworker.LoginActivity;
import com.rl.fieldworker.R;
import com.rl.pojo.TenderList;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TenderListFragment extends Fragment {

    RecyclerView recyclerCustomerTenderList;
    RelativeLayout rlNotFound,rlLoader;
    SwipeRefreshLayout swipeRefresh;
    String uuid="";
    ArrayList<TenderList> tenderLists;
    ConsumerTenderListAdapter customerTenderListAdapter;
    EditText editText_search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tender_list, container, false);
        AppController.initialize(getActivity());
        SharedPreference.initialize(getActivity());
        FirebaseAnalytics.getInstance(getActivity());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        recyclerCustomerTenderList=v.findViewById(R.id.recyclerCustomerTenderList);
        swipeRefresh=v.findViewById(R.id.swipeRefresh);
        rlLoader=v.findViewById(R.id.rlLoader);
        editText_search=v.findViewById(R.id.edittextAPsearch);
        rlNotFound=v.findViewById(R.id.rlNotFound);
//        FirebaseAnalytics.getInstance(getActivity());
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        recyclerCustomerTenderList.setLayoutManager(new LinearLayoutManager(getActivity()));
        tenderLists=new ArrayList<TenderList>();
        recyclerCustomerTenderList.setHasFixedSize(true);

        customerTenderListAdapter= new ConsumerTenderListAdapter(
                getActivity(),tenderLists);
        recyclerCustomerTenderList.setAdapter(customerTenderListAdapter);
        uuid= SharedPreference.get("uuid");

        getConsumerTender(uuid);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.black));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tenderLists.clear();
                getConsumerTender(uuid);
            }
        });

        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                customerTenderListAdapter.getFilter().filter(editText_search.getText().toString());
            }
        });

        return v;
    }

    private void getConsumerTender(String uuid) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.get_tender, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipeRefresh.setRefreshing(false);
                rlLoader.setVisibility(View.GONE);
                Log.i("pri","customer request list"+response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")){
                        rlLoader.setVisibility(View.GONE);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        // Inside the loop where you populate tenderLists
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            JSONArray tenderPdfArray = jsonObject1.getJSONArray("tender_pdf");
                            ArrayList<String> tenderPdfList = new ArrayList<>();
                            for (int j = 0; j < tenderPdfArray.length(); j++) {
                                tenderPdfList.add(tenderPdfArray.getString(j));
                            }
                            tenderLists.add(new TenderList(
                                    jsonObject1.getString("serial"),
                                    jsonObject1.getString("bdm_manager_id"),
                                    jsonObject1.getString("name"),
                                    jsonObject1.getString("mobile"),
                                    jsonObject1.getString("email_id"),
                                    jsonObject1.getString("tender_no"),
                                    jsonObject1.getString("occupation"),
                                    jsonObject1.getString("workplace_address"),
                                    jsonObject1.getString("pincode"),
                                    jsonObject1.getString("date"),
                                    jsonObject1.getString("id"),
                                    tenderPdfList,
                                    jsonObject1.getString("expiry_date"),
                                    jsonObject1.getString("tender_accepted_consumer_id"),
                                    jsonObject1.getString("consumer_mob_no"),
                                    jsonObject1.getString("consumer_name")
                            ));
                        }


                        customerTenderListAdapter= new ConsumerTenderListAdapter(
                                getActivity(),tenderLists);
                        recyclerCustomerTenderList.setAdapter(customerTenderListAdapter);
                        customerTenderListAdapter.notifyDataSetChanged();
                    }else {
                        rlLoader.setVisibility(View.GONE);
                        rlNotFound.setVisibility(View.VISIBLE);
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid mismatch logout")) {
                            if (SharedPreference.contains("consumer_uuid")) {
                                SharedPreference.removeKey("consumer_uuid");
                                SharedPreference.removeKey("userid");
                                SharedPreference.removeKey("adhar_no");
                                SharedPreference.removeKey("pan");
                            }
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    rlLoader.setVisibility(View.GONE);
                }
                swipeRefresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                rlLoader.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Technical problem arises", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("uuid",uuid);
                Log.i("pri","dashboard request parameter: "+uuid.toString());
                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

}