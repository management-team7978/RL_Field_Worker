package com.rl.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.rl.adapter.RequestListAdapter;
import com.rl.fieldworker.LoginActivity;
import com.rl.fieldworker.R;
import com.rl.pojo.RequestList;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GetRquestListFragment extends Fragment {
    RecyclerView recyclerRequestList;
    RelativeLayout rlNotFound,rlLoader;
    SwipeRefreshLayout swipeRefresh;
    String uuid="";
    ArrayList<RequestList> requestLists;
    RequestListAdapter requestListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_get_rquest_list, container, false);
        AppController.initialize(getActivity());
        SharedPreference.initialize(getActivity());
        FirebaseAnalytics.getInstance(getActivity());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        recyclerRequestList=v.findViewById(R.id.recyclerRequestList);
        swipeRefresh=v.findViewById(R.id.swipeRefresh);
        rlLoader=v.findViewById(R.id.rlLoader);
        rlNotFound=v.findViewById(R.id.rlNotFound);
//        FirebaseAnalytics.getInstance(getActivity());
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        recyclerRequestList.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestLists=new ArrayList<RequestList>();
        recyclerRequestList.setHasFixedSize(true);

        uuid= SharedPreference.get("uuid");

        getRequestList(uuid);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.black));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLists.clear();
                getRequestList(uuid);
            }
        });
        return v;
    }

    private void getRequestList(String uuid) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.show_quotation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipeRefresh.setRefreshing(false);
                rlLoader.setVisibility(View.GONE);
                Log.i("pri","consumer dashboard"+response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")){
                        rlLoader.setVisibility(View.GONE);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            requestLists.add(new RequestList(
                                    jsonObject1.getString("serial"),
                                    jsonObject1.getString("service_request_id"),
                                    jsonObject1.getString("consumer_user_id"),
                                    jsonObject1.getString("name"),
                                    jsonObject1.getString("phone"),
                                    jsonObject1.getString("quotation"),
                                    jsonObject1.getString("quotations"),
                                    jsonObject1.getString("occupation"),
                                    jsonObject1.getString("id"),
                                    jsonObject1.getString("work"),
                                    jsonObject1.getString("address"),
                                    jsonObject1.getString("status_code"),
                                    jsonObject1.getString("status_message")));
                        }
                        requestListAdapter= new RequestListAdapter(
                                getActivity(),requestLists);
                        recyclerRequestList.setAdapter(requestListAdapter);
                    }else {
                        rlLoader.setVisibility(View.GONE);
                        rlNotFound.setVisibility(View.VISIBLE);
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("name");
                                SharedPreference.removeKey("referral_code");
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
                swipeRefresh.setRefreshing(false);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Technical problem arises", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error occurred!", Toast.LENGTH_SHORT).show();
                }
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