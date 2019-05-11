package com.brahamaputra.mahindra.brahamaputrajio.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.progresviews.ProgressWheel;
import com.brahamaputra.mahindra.brahamaputrajio.Adapters.UserHotoExpListAdapter;
import com.brahamaputra.mahindra.brahamaputrajio.Application;
import com.brahamaputra.mahindra.brahamaputrajio.Data.HotoTicketList;
import com.brahamaputra.mahindra.brahamaputrajio.R;
import com.brahamaputra.mahindra.brahamaputrajio.Utils.Conditions;
import com.brahamaputra.mahindra.brahamaputrajio.Utils.Constants;
import com.brahamaputra.mahindra.brahamaputrajio.Utils.SessionManager;
import com.brahamaputra.mahindra.brahamaputrajio.Volley.GsonRequest;
import com.brahamaputra.mahindra.brahamaputrajio.baseclass.BaseActivity;
import com.brahamaputra.mahindra.brahamaputrajio.commons.AlertDialogManager;
import com.brahamaputra.mahindra.brahamaputrajio.commons.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UsersHotoListActivity extends BaseActivity {

    private ProgressWheel wheelprogress;
    private TextView userHotoList_textView_openTickets;
    private TextView userHotoList_textView_allTickets;

    private UserHotoExpListAdapter userHotoExpListAdapter;
    public ExpandableListView userHotoList_listView_hotoList;
    private AlertDialogManager alertDialogManager;
    private SessionManager sessionManager;
    private HotoTicketList hotoTicketList;
    public GPSTracker gpsTracker;


    /////////////////////////
    public static final int RESULT_HOTO_SUBMIT = 257;
    private TextView txtNoTicketFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_hoto_list);
        hotoTicketList = new HotoTicketList();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        wheelprogress = (ProgressWheel) findViewById(R.id.wheelprogress);
        userHotoList_textView_openTickets = (TextView) findViewById(R.id.userHotoList_textView_openTickets);
        userHotoList_textView_allTickets = (TextView) findViewById(R.id.userHotoList_textView_allTickets);

        userHotoList_listView_hotoList = (ExpandableListView) findViewById(R.id.userHotoList_listView_hotoList);
        txtNoTicketFound = (TextView) findViewById(R.id.txtNoTicketFound);
        txtNoTicketFound.setVisibility(View.GONE);

        alertDialogManager = new AlertDialogManager(UsersHotoListActivity.this);
        sessionManager = new SessionManager(UsersHotoListActivity.this);
        gpsTracker = new GPSTracker(UsersHotoListActivity.this);
        if (gpsTracker.canGetLocation()) {
            Log.e(UserHotoTransactionActivity.class.getName(), "Lat : " + gpsTracker.getLatitude() + "\n Long : " + gpsTracker.getLongitude());
        }

        prepareListData();


        userHotoList_listView_hotoList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                userHotoList_listView_hotoList.expandGroup(groupPosition);
                return true;
            }
        });
        userHotoList_listView_hotoList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, final int childPosition, long id) {
                // notify user

                LocationManager lm = (LocationManager) UsersHotoListActivity.this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                if (!gps_enabled && !network_enabled) {
                    // notify user
                    alertDialogManager.Dialog("Information", "Location is not enabled. Do you want to enable?", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                        @Override
                        public void onPositiveClick() {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            UsersHotoListActivity.this.startActivity(myIntent);
                        }
                    }).show();
                } else {
                    if (gpsTracker.getLongitude() > 0 && gpsTracker.getLongitude() > 0) {
                        if (hotoTicketList != null) {
                            final String hotoTicketId = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getId().toString();
                            final String hotoTicketNo = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getHotoTicketNo().toString();

                            final String hotoTicketDate = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getHotoTicketDate().toString();
                            final String siteId = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getSiteId().toString();
                            final String siteName = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getSiteName().toString();
                            final String siteAddress = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getSiteAddress().toString();
                            final String status = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getStatus().toString();
                            final String siteType = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getSiteType().toString();
                            final String stateName = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getStateName().toString();
                            final String customerName = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getCustomerName().toString();
                            final String circleName = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getCircleName().toString();
                            final String ssaName = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getSsaName().toString();
                            Constants.hototicket_Selected_SiteType = siteType;

                            String hotoTickStatus = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getStatus().toString();
                            Constants.hototicket_nameOfSupplyCompany = hotoTicketList.getHotoTicketsDates().get(groupPosition).getHotoTickets().get(childPosition).getNameOfSupplyCompany().toString();

                            if (hotoTickStatus.equals("Open") || hotoTickStatus.equals("WIP") || hotoTickStatus.equals("Reassigned")) {
                                if (hotoTickStatus.equals("Open")) {

                                    alertDialogManager.Dialog("Information", "Do you want to proceed.", "Yes", "No", new AlertDialogManager.onTwoButtonClickListner() {
                                        @Override
                                        public void onPositiveClick() {
                                            checkSystemLocation(hotoTicketNo, hotoTicketId, hotoTicketDate, siteId, siteName, siteAddress, status, siteType,
                                                    stateName, customerName, circleName, ssaName);
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    }).show();

                                } else {
                                    checkSystemLocation(hotoTicketNo, hotoTicketId, hotoTicketDate, siteId, siteName, siteAddress, status, siteType,
                                            stateName, customerName, circleName, ssaName);
                                }

                            }
                        }

                    } else {
                        alertDialogManager.Dialog("Information", "Could not get your location. Please try again.", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                            @Override
                            public void onPositiveClick() {
                                if (gpsTracker.canGetLocation()) {
                                    Log.e(UserHotoTransactionActivity.class.getName(), "Lat : " + gpsTracker.getLatitude() + "\n Long : " + gpsTracker.getLongitude());
                                }
                            }
                        }).show();
                    }
                }

                return false;
            }
        });
    }

    /**
     * Load ticket list. Can use for refresh list as same.
     */
    private void prepareListData() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("AccessToken", sessionManager.getSessionDeviceToken());

            Log.i(UsersHotoListActivity.class.getName(), Constants.hototTicketList + "\n\n" + jo.toString());

            GsonRequest<HotoTicketList> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.hototTicketList, jo.toString(), HotoTicketList.class,
                    new Response.Listener<HotoTicketList>() {
                        @Override
                        public void onResponse(@NonNull HotoTicketList response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    hotoTicketList = response;
                                    if (hotoTicketList.getHotoTicketSummary() != null) {
                                        userHotoList_textView_openTickets.setText(hotoTicketList.getHotoTicketSummary().getOpenTickets() == null || hotoTicketList.getHotoTicketSummary().getOpenTickets().isEmpty() ? "0" : hotoTicketList.getHotoTicketSummary().getOpenTickets().toString());
                                        userHotoList_textView_allTickets.setText(hotoTicketList.getHotoTicketSummary().getTotalTickets() == null || hotoTicketList.getHotoTicketSummary().getTotalTickets().isEmpty() ? "0" : hotoTicketList.getHotoTicketSummary().getTotalTickets().toString());

                                        double per = 0.0;
                                        double circlePer = 0.0;
                                        int roundPer = 0;
                                        per = hotoTicketList.getHotoTicketSummary().getPercentage();
                                        circlePer = (3.6) * Double.valueOf(per);
                                        roundPer = (int) Math.round(circlePer);

                                        DecimalFormat df = new DecimalFormat("###.##");
                                        df.setRoundingMode(RoundingMode.FLOOR);
                                        per = new Double(df.format(per));


                                        wheelprogress.setPercentage(roundPer);
                                        wheelprogress.setStepCountText(String.valueOf(per));
                                    }
                                    if (hotoTicketList.getHotoTicketsDates() != null && hotoTicketList.getHotoTicketsDates().size() > 0) {
                                        txtNoTicketFound.setVisibility(View.GONE);
                                        userHotoList_listView_hotoList.setVisibility(View.VISIBLE);
                                        userHotoExpListAdapter = new UserHotoExpListAdapter(UsersHotoListActivity.this, hotoTicketList);
                                        userHotoList_listView_hotoList.setAdapter(userHotoExpListAdapter);
                                        for (int i = 0; i < hotoTicketList.getHotoTicketsDates().size(); i++) {
                                            userHotoList_listView_hotoList.expandGroup(i);
                                        }
                                    } else {
                                        userHotoList_listView_hotoList.setVisibility(View.GONE);
                                        txtNoTicketFound.setVisibility(View.VISIBLE);
                                    }

                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();

                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "assignavailabilitylearnerslist");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.refresh_icon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuRefresh:
                prepareListData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_HOTO_SUBMIT && resultCode == RESULT_OK) {
            prepareListData();
        }
    }

    public void checkSystemLocation(final String hotoTickitNo, final String hotoTicketId, String hotoTicketDate, String siteId,
                                    String siteName, String siteAddress, String status, String siteType, String stateName,
                                    String customerName, String circleName, String ssaName) {

        LocationManager lm = (LocationManager) UsersHotoListActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            alertDialogManager.Dialog("Information", "Location is not enabled. Do you want to enable?", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                @Override
                public void onPositiveClick() {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    UsersHotoListActivity.this.startActivity(myIntent);
                }
            }).show();
        } else {
            if (Conditions.isNetworkConnected(UsersHotoListActivity.this)) {
                Intent intent = new Intent(UsersHotoListActivity.this, UserHotoTransactionActivity.class);
                intent.putExtra("isNetworkConnected", Conditions.isNetworkConnected(UsersHotoListActivity.this));
                intent.putExtra("Id", hotoTicketId);

                intent.putExtra("ticketNO", hotoTickitNo);

                intent.putExtra("hotoTicketDate", hotoTicketDate);
                intent.putExtra("siteId", siteId);
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteAddress", siteAddress);
                intent.putExtra("status", status);
                intent.putExtra("siteType", siteType);
                intent.putExtra("stateName", stateName);
                intent.putExtra("customerName", customerName);
                intent.putExtra("circleName", circleName);
                intent.putExtra("ssaName", ssaName);
                intent.putExtra("latitude", String.valueOf(gpsTracker.getLatitude()));
                intent.putExtra("longitude", String.valueOf(gpsTracker.getLongitude()));

                sessionManager.updateSessionUserTicketId(hotoTicketId);
                sessionManager.updateSessionUserTicketName(hotoTickitNo);
                startActivityForResult(intent, RESULT_HOTO_SUBMIT);

            } else {
                alertDialogManager.Dialog("Information", "Device has no internet connection. Do you want to use offline mode?", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                    @Override
                    public void onPositiveClick() {
                        Intent intent = new Intent(UsersHotoListActivity.this, UserHotoTransactionActivity.class);
                        intent.putExtra("isNetworkConnected", Conditions.isNetworkConnected(UsersHotoListActivity.this));
                        intent.putExtra("ticketNO", hotoTickitNo);
                        sessionManager.updateSessionUserTicketId(hotoTicketId);
                        sessionManager.updateSessionUserTicketName(hotoTickitNo);
                        startActivityForResult(intent, RESULT_HOTO_SUBMIT);
                    }
                }).show();
            }
        }
    }
}
