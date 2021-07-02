package com.pbt.raadrivers.Utils;

import com.pbt.raadrivers.BuildConfig;

import okhttp3.MediaType;

public interface AppConstant {

    public static final String SMS_ORIGIN = "ANHIVE";

    public static String lenguage = "gu";

    public static final int COUNT_DOWN_TIME = 2 * 60 * 1000;

    //    public static String local = "https://raadarbar.com/api/";
    public static String local = "https://raadarbar.com/api/";
//    public static String local = "https://pbt.raadarbar.com/api/";/**/
//    public static String image = "https://raadrivers.com/uploads/";
    public static String image = "https://raadarbar.com/uploads/";

    //local url
    /*public static String local = "https://hopeinfotechsolutions.com/raadarbar/public/api/";
    public static String image="https://hopeinfotechsolutions.com/raadarbar/public/uploads/";*/

    public static String daily_balance = local + "driver/day/balance";
    public static String login = local + "login";
    public static String details = local + "details";
    public static String register = local + "register";
    public static String getcarsnearest = local + "getcarsnearest";
    public static String enteremail = local + "enteremail";
    public static String checkphone = local + "checkphone";
    public static String logout = local + "logout";
    public static String getcardetail = local + "getcars";
    public static String getpackages = local + "getpackages";
    public static String getridenowpackagesapi = local + "getridenowpackagesapi";
    public static String uploadproof = local + "uploadproof";
    public static String ridehistory_past = local + "ridehistory_past";
    public static String ridehistory_detail = local + "ridehistory";
    public static String ridehistory_upcoming = local + "ridehistory_upcoming";
    public static String confirmride = local + "confirmride";
    //    public static String confirmride = local + "confirmride_test";
    public static String updatecarlatlong = local + "updatecarlatlong";
    public static String adddrivertotalminute = local + "adddrivertotalminnew";
    public static String adddrivertotalmin_driver = local + "adddrivertotalmin_driver";
    public static String newtrip = local + "newtrip";
    public static String adddriverlognew = local + "adddriverlognew";
    public static String acceptride = local + "acceptride";
    public static String driveronduty = local + "driveronduty";
    public static String starttrip = local + "starttrip";
    public static String getpackageslist = local + "getpackageslist";
    public static String finishtripotp = local + "finishtripotp";
    public static String finishtrip = local + "finishtrip";
    public static String confirmendtrip = local + "confirmendtrip";
    //    public static String confirmendtrip = local + "confirmendtrip_new";
    public static String advancebooking = local + "advancebooking";
    public static String balance = local + "driver/balance/";
    public static String cancelride = local + "cancelride";
    public static String forget = local + "password/forget";
    public static String profile = local + "user/profile";
    public static String pdf_download = local + "driver/download";
    public static String help = local + "help/";
    public static String offers = local + "offers";
    public static String whyraadarbar = local + "whyraadarbar";
    public static String features = local + "features";
    public static String updatesignature = local + "updatesignature";
    public static String advancebookingdelete = local + "advancebookingdelete";
    public static String gettripstatus = local + "gettripstatus";
    public static String updatefcm = local + "updatefcm";
    public static String checkcurrenttime = local + "checkcurrenttime";
    public static String tripcount = local + "tripcount";
    public static String resonForCancelride = local + "resonForCancelride";
    public static String changecartype = local + "changecartype";
    public static String lastlogin = local + "lastlogin";
    public static String getwalletdetail = local + "getwalletdetail";
    public static String missedtrip = local + "missedtrip";
    public static String carlatlong = local + "carlatlong";
    public static String monthlyDutyReport = local + "monthlyDutyReport";
    public static String updateride = local + "updateride";
    public static String firsttrip = local + "firsttrip";
    public static String checkblock = local + "checkblock";
    public static String enterphone = local + "enterphone";

    public static String phoneverify = local + "phoneverify";
    public static String secondarydriverdetails = local + "secondarydriverdetails";
    public static String secondarydrivercheck = local + "secondarydrivercheck";
    public static String secondarydriverupdate = local + "secondarydriverupdate";
    public static String tripstatus = local + "tripstatus";
    public static String getcars_duration = local + "getcars_duration";
    public static String errorUpload = local + "errorUpload";

    public static String SPLASCREEN = "101";
    public static String LOGIN = "102";
    public static String FORGROUND = "103";
    public static String VEFICATION = "104";
    public static String JOBSERVICE = "105";
    public static String NORMAL_SERVICE = "106";
    public static String MYFIREBASE = "107";
    public static String REGISERTER = "108";


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static String phone = "9727379793";

    // For DATE AND TIME
    public static final int DATE_PICKER_ID = 999;
    public static final int TIME_PICKER_ID = 444;
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm a";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_FORMAT_AM = "HH:mm:ss a";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static int PRE_BOOKING_DELAY_TIME = 60 * 15 * 1000;

    // Foir Email Validation
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
    //Shared Prefrances
    public static final String KEY_PREF_CANCEL_TIME_STAMP = "KEY_PREF_CANCEL_TIME_STAMP";
    public static final String KEY_PREF_TRIP_ID = "KEY_PREF_TRIP_ID";

    public static String rating = local + "rating";

    public static String FOLDERNAME = "RaadarbarCrash";
    public static String PACKAGE = "package";
}
