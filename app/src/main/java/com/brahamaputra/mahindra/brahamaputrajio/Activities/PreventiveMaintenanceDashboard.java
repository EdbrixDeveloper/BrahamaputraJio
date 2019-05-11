package com.brahamaputra.mahindra.brahamaputrajio.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.Bundle;
import android.provider.Settings;
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
import com.brahamaputra.mahindra.brahamaputrajio.Adapters.PmSiteExpListAdapter;

import com.brahamaputra.mahindra.brahamaputrajio.Application;
import com.brahamaputra.mahindra.brahamaputrajio.Data.BatteryType;
import com.brahamaputra.mahindra.brahamaputrajio.Data.SitePMTicketsList;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PreventiveMaintenanceDashboard extends BaseActivity {

    private String TAG = PreventiveMaintenanceDashboard.class.getName();
    private ProgressWheel wheelprogress;
    private TextView acPreventiveMaintenanceSection_textView_openTickets;
    private TextView acPreventiveMaintenanceSection_textView_allTickets;
    private PmSiteExpListAdapter pmSiteExpListAdapter;
    public ExpandableListView pmSiteList_listView_siteList;
    private AlertDialogManager alertDialogManager;
    private SessionManager sessionManager;
    private SitePMTicketsList sitePMTicketsList;
    public GPSTracker gpsTracker;
    ArrayList<BatteryType> batteryType;
    /////
    public static final int RESULT_PM_SITE_SUBMIT = 257;
    private TextView txtNoTicketFound;
    private double siteLatitude;
    private double siteLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preventive_maintenance_dashboard);
        this.setTitle("Site PM");

        sitePMTicketsList = new SitePMTicketsList();
        batteryType = new ArrayList<BatteryType>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        wheelprogress = (ProgressWheel) findViewById(R.id.wheelprogress);
        acPreventiveMaintenanceSection_textView_openTickets = (TextView) findViewById(R.id.acPreventiveMaintenanceSection_textView_openTickets);
        acPreventiveMaintenanceSection_textView_allTickets = (TextView) findViewById(R.id.acPreventiveMaintenanceSection_textView_allTickets);
        pmSiteList_listView_siteList = (ExpandableListView) findViewById(R.id.pmSiteList_listView_siteList);
        txtNoTicketFound = (TextView) findViewById(R.id.txtNoTicketFound);
        txtNoTicketFound.setVisibility(View.GONE);

        alertDialogManager = new AlertDialogManager(PreventiveMaintenanceDashboard.this);
        sessionManager = new SessionManager(PreventiveMaintenanceDashboard.this);
        gpsTracker = new GPSTracker(PreventiveMaintenanceDashboard.this);
        if (gpsTracker.canGetLocation()) {
            Log.e(PreventiveMaintenanceDashboard.class.getName(), "Lat : " + gpsTracker.getLatitude() + "\n Long : " + gpsTracker.getLongitude());
        }

        prepareListData();


        pmSiteList_listView_siteList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                pmSiteList_listView_siteList.expandGroup(groupPosition);
                return true;
            }
        });
        pmSiteList_listView_siteList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, final int childPosition, long id) {
                // notify user

                LocationManager lm = (LocationManager) PreventiveMaintenanceDashboard.this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;
                siteLatitude = Double.valueOf(sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLatitude().toString() == null || sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLatitude().toString().isEmpty() ? "0.0" : sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLatitude().toString());
                siteLongitude = Double.valueOf(sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLongitude().toString() == null || sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLongitude().toString().isEmpty() ? "0.0" : sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLongitude().toString());


                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                if (!gps_enabled && !network_enabled) {
                    // notify user
                    alertDialogManager.Dialog("Conformation", "Location is not enabled. Do you want to enable?", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                        @Override
                        public void onPositiveClick() {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            PreventiveMaintenanceDashboard.this.startActivity(myIntent);
                        }
                    }).show();
                } else {
                    if (gpsTracker.getLongitude() > 0 && gpsTracker.getLongitude() > 0) {
                        //calculate area distance
                        /*if (gpsTracker.distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(), siteLatitude, siteLongitude) < 0.0310686) {///// ( 0.0310686 MILE == 50 Meter )
                            Log.i(TAG, "" + "in Area \n" + gpsTracker.distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(), siteLatitude, siteLongitude));*/


                            if (sitePMTicketsList != null) {
                                String myFormat = "dd/MMM/yyyy"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                String currentDateTimeString = sdf.format(new Date());

                                //String currentDateTimeString = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date());

                                final String sitePMTicketId = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getId().toString();
                                final String sitePMTicketNo = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSitePMTicketNo().toString();

                                final String sitePMTicketDate = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSitePMTicketDate().toString();
                                final String siteId = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteId().toString();
                                final String siteName = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteName().toString();
                                final String siteAddress = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteAddress().toString();
                                final String status = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getStatus().toString();
                                final String siteType = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteType().toString();
                                final String stateName = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getStateName().toString();
                                final String customerName = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getCustomerName().toString();
                                final String circleName = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getCircleName().toString();
                                final String ssaName = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSSAName().toString();
                                final String sourceOfPower = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSourceOfPower().toString();
                                final String sitePmScheduledDate = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSitePMScheduledDate().toString();
                                final String SiteBoundaryStatus = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteBoundaryStatus().toString();
                                final String NoOfACprovided = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getNoOfACprovided().toString();
                                final String ServoStabilizerStatus = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getServoStabilizerWorkingStatus().toString();

                                final String SiteLat = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLatitude().toString() == null || sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLatitude().toString().isEmpty() ? "0.0" : sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLatitude().toString();
                                final String SiteLng = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLongitude().toString() == null || sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLongitude().toString().isEmpty() ? "0.0" : sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getSiteLongitude().toString();


                                if (getDaysRemainingForSheduledDate(currentDateTimeString, sitePmScheduledDate)) {

                                    if (sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getBatteryTypes() != null) {
                                        batteryType = new ArrayList<BatteryType>();
                                        batteryType.addAll(sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getBatteryTypes());
                                    }

                                    Constants.hototicket_Selected_SiteType = siteType;
                                    Constants.hototicket_sourceOfPower = sourceOfPower;
                                    Constants.sitePm_siteBoundaryStatus = SiteBoundaryStatus;
                                    Constants.sitePmNoOfAcAvailableAtSite = NoOfACprovided;
                                    Constants.sitePmServoStabilizerWorkingStatus = ServoStabilizerStatus;

                                    Constants.sitePmCustomerName = customerName;
                                    Constants.sitePmCircleName = circleName;
                                    Constants.sitePmStateName = stateName;
                                    Constants.sitePmSiteName = siteName;
                                    Constants.sitePmSiteId = siteId;
                                    Constants.sitePmSsaName = ssaName;

                                    String sitePMTickStatus = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getStatus().toString();
                                    //hototicket_nameOfSupplyCompany = sitePMTicketsList.getSitePMTicketsDates().get(groupPosition).getSitePMTickets().get(childPosition).getNameOfSupplyCompany().toString();

                                    if (sitePMTickStatus.equals("Open") || sitePMTickStatus.equals("WIP") || sitePMTickStatus.equals("Reassigned")) {
                                        if (sitePMTickStatus.equals("Open")) {

                                            alertDialogManager.Dialog("Conformation", "Do you want to proceed doing Site PM?", "Yes", "No", new AlertDialogManager.onTwoButtonClickListner() {
                                                @Override
                                                public void onPositiveClick() {
                                                    checkSystemLocation(sitePMTicketNo, sitePMTicketId, sitePMTicketDate, siteId, siteName, siteAddress, status, siteType,
                                                            stateName, customerName, circleName, ssaName, sitePmScheduledDate, batteryType, SiteLat, SiteLng);
                                                }

                                                @Override
                                                public void onNegativeClick() {

                                                }
                                            }).show();

                                        } else {
                                            checkSystemLocation(sitePMTicketNo, sitePMTicketId, sitePMTicketDate, siteId, siteName, siteAddress, status, siteType,
                                                    stateName, customerName, circleName, ssaName, sitePmScheduledDate, batteryType, SiteLat, SiteLng);
                                        }

                                    }
                                }
                            }

                        /*} else {
                            Log.i(TAG, "" + "not in Area\n" + gpsTracker.distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(), siteLatitude, siteLongitude));
                            alertDialogManager.Dialog("Conformation", "User not in area of site", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                                @Override
                                public void onPositiveClick() {
                                    //nothing to do
                                }
                            }).show();
                        }*/


                    } else {
                        alertDialogManager.Dialog("Conformation", "Could not get your location. Please try again.", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                            @Override
                            public void onPositiveClick() {
                                if (gpsTracker.canGetLocation()) {
                                    //showToast("Lat : "+gpsTracker.getLatitude()+"\n Long : "+gpsTracker.getLongitude()); comment By 008 on 10-11-2018
                                    Log.e(PriventiveMaintenanceSiteTransactionActivity.class.getName(), "Lat : " + gpsTracker.getLatitude() + "\n Long : " + gpsTracker.getLongitude());
                                }
                            }
                        }).show();
                    }
                }


                return false;
            }
        });

    }

    // added by tiger on 17092019 for date validation
    public boolean getDaysRemainingForSheduledDate(String currentDateTimeString, String sitePmScheduledDate) {

        long requiredDaysForStartWork = 3;
        long lastDayForStartWork = 0;
        String newCurrentDate, newSheduledDate;
        Date newFormatedCurrentDate;
        Date newFormatedSheduledDate;

        SimpleDateFormat simpleDateFormatForCurrentDate = new SimpleDateFormat("dd/MMM/yyyy");
        SimpleDateFormat simpleDateFormatForSheduleDate = new SimpleDateFormat("dd/MMM/yyyy");

        SimpleDateFormat newSimpleDateFormatForDaysCalculate = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        try {

            Date currentDate = simpleDateFormatForCurrentDate.parse(currentDateTimeString);
            Date sheduledDate = simpleDateFormatForSheduleDate.parse(sitePmScheduledDate);

            newCurrentDate = newSimpleDateFormatForDaysCalculate.format(currentDate);
            newSheduledDate = newSimpleDateFormatForDaysCalculate.format(sheduledDate);

            newFormatedCurrentDate = newSimpleDateFormatForDaysCalculate.parse(newCurrentDate);
            newFormatedSheduledDate = newSimpleDateFormatForDaysCalculate.parse(newSheduledDate);

            long different = newFormatedSheduledDate.getTime() - newFormatedCurrentDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            if (elapsedDays <= requiredDaysForStartWork) {//&& elapsedDays >= lastDayForStartWork
                return true;
            } else if (elapsedDays > requiredDaysForStartWork) {
                showToast("You can open this ticket only 3 days before Scheduled Date:" + sitePmScheduledDate);
            } /*else if (elapsedDays < lastDayForStartWork) {
                showToast("You don't have access to this ticket after Scheduled Date:" + sitePmScheduledDate);
            }*/

        } catch (ParseException e) {
            Log.d("ParseException", e.getMessage());
        } catch (java.text.ParseException e) {
            Log.d("ParseException", e.getMessage());
        }

        return false;
    }

    private void prepareListData() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();


            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("AccessToken", sessionManager.getSessionDeviceToken());

            Log.i(PreventiveMaintenanceDashboard.class.getName(), Constants.sitePmTicketList + "\n\n" + jo.toString());

            GsonRequest<SitePMTicketsList> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.sitePmTicketList, jo.toString(), SitePMTicketsList.class,
                    new Response.Listener<SitePMTicketsList>() {
                        @Override
                        public void onResponse(@NonNull SitePMTicketsList response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    sitePMTicketsList = response;
                                    if (sitePMTicketsList.getSitePMTicketSummary() != null) {
                                        acPreventiveMaintenanceSection_textView_openTickets.setText(sitePMTicketsList.getSitePMTicketSummary().getOpenTickets() == null || sitePMTicketsList.getSitePMTicketSummary().getOpenTickets().isEmpty() ? "0" : sitePMTicketsList.getSitePMTicketSummary().getOpenTickets().toString());
                                        acPreventiveMaintenanceSection_textView_allTickets.setText(sitePMTicketsList.getSitePMTicketSummary().getTotalTickets() == null || sitePMTicketsList.getSitePMTicketSummary().getTotalTickets().isEmpty() ? "0" : sitePMTicketsList.getSitePMTicketSummary().getTotalTickets().toString());

                                        double per = 0.0;
                                        double circlePer = 0.0;
                                        int roundPer = 0;
                                        per = sitePMTicketsList.getSitePMTicketSummary().getPercentage();
                                        circlePer = (3.6) * Double.valueOf(per);
                                        roundPer = (int) Math.round(circlePer);

                                        DecimalFormat df = new DecimalFormat("###.##");
                                        df.setRoundingMode(RoundingMode.FLOOR);
                                        per = new Double(df.format(per));


                                        wheelprogress.setPercentage(roundPer);
                                        wheelprogress.setStepCountText(String.valueOf(per));


                                    }
                                    if (sitePMTicketsList.getSitePMTicketsDates() != null && sitePMTicketsList.getSitePMTicketsDates().size() > 0) {
                                        txtNoTicketFound.setVisibility(View.GONE);
                                        pmSiteList_listView_siteList.setVisibility(View.VISIBLE);
                                        pmSiteExpListAdapter = new PmSiteExpListAdapter(PreventiveMaintenanceDashboard.this, sitePMTicketsList);
                                        pmSiteList_listView_siteList.setAdapter(pmSiteExpListAdapter);
                                        for (int i = 0; i < sitePMTicketsList.getSitePMTicketsDates().size(); i++) {
                                            pmSiteList_listView_siteList.expandGroup(i);
                                        }
                                    } else {
                                        pmSiteList_listView_siteList.setVisibility(View.GONE);
                                        txtNoTicketFound.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();

                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().

                    addToRequestQueue(getAssignAvailabilityLearnersListRequest, "assignavailabilitylearnerslist");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.refresh_icon_menu_site_pm, menu);
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
            case R.id.menuReport:
                //showToast("Hi...");
                //prepareListData();
                startActivity(new Intent(PreventiveMaintenanceDashboard.this, PreventiveMaintenanceSiteReportDashboard.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_PM_SITE_SUBMIT && resultCode == RESULT_OK) {
            prepareListData();
        }
    }

    public void checkSystemLocation(final String sitePMTicketNo,
                                    final String sitePMTicketId, String sitePMTicketDate, String siteId,
                                    String siteName, String siteAddress, String status, String siteType,
                                    String stateName, String customerName, String circleName, String ssaName,
                                    String sitePmScheduledDate, ArrayList<BatteryType> batteryType, final String SiteLat, final String SiteLng) {

        LocationManager lm = (LocationManager) PreventiveMaintenanceDashboard.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            alertDialogManager.Dialog("Conformation", "Location is not enabled. Do you want to enable?", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                @Override
                public void onPositiveClick() {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    PreventiveMaintenanceDashboard.this.startActivity(myIntent);
                }
            }).show();
        } else {
            if (Conditions.isNetworkConnected(PreventiveMaintenanceDashboard.this)) {
                //if (gpsTracker.getLongitude()>0 && gpsTracker.getLongitude()>0){

                Intent intent = new Intent(PreventiveMaintenanceDashboard.this, PriventiveMaintenanceSiteTransactionActivity.class);
                intent.putExtra("isNetworkConnected", Conditions.isNetworkConnected(PreventiveMaintenanceDashboard.this));
                intent.putExtra("Id", sitePMTicketId);

                intent.putExtra("ticketNO", sitePMTicketNo);

                intent.putExtra("sitePMTicketDate", sitePMTicketDate);
                intent.putExtra("sitePmScheduledDate", sitePmScheduledDate);
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

                //String[] array = new String[]{"Item1", "Item2", "item3", "Item4", "item5"};
                //Bundle bundle = new Bundle();
                intent.putExtra("batteryType", batteryType);

                sessionManager.updateSessionUserTicketId(sitePMTicketId);
                sessionManager.updateSessionUserTicketName(sitePMTicketNo);
                sessionManager.updateSessionUserLat(SiteLat);
                sessionManager.updateSessionUserLng(SiteLng);
                startActivityForResult(intent, RESULT_PM_SITE_SUBMIT);

                //}else{
                //    showToast("Sorry could not detect location");
                //    finish();
                //}

            } else {
                alertDialogManager.Dialog("Conformation", "Device has no internet connection. Do you want to use offline mode?", "ok", "cancel", new AlertDialogManager.onSingleButtonClickListner() {
                    @Override
                    public void onPositiveClick() {
                        Intent intent = new Intent(PreventiveMaintenanceDashboard.this, PriventiveMaintenanceSiteTransactionActivity.class);
                        intent.putExtra("isNetworkConnected", Conditions.isNetworkConnected(PreventiveMaintenanceDashboard.this));
                        intent.putExtra("ticketNO", sitePMTicketNo);
                        sessionManager.updateSessionUserTicketId(sitePMTicketId);
                        sessionManager.updateSessionUserTicketName(sitePMTicketNo);
                        sessionManager.updateSessionUserLat(SiteLat);
                        sessionManager.updateSessionUserLng(SiteLng);

                        startActivityForResult(intent, RESULT_PM_SITE_SUBMIT);
                    }
                }).show();
            }
        }
    }


}
