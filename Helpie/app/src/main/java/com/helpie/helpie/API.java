package com.helpie.helpie;

/**
 * Created by DCM on 08/12/2016.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class API {
    private String LOGIN_URL = "http://138.68.146.193:5000/login";
    private String REGISTER_URL = "http://138.68.146.193:5000/register";
    private String CONFIRM_CONTACT_URL = "http://138.68.146.193:5000/confirmcontact";


    private String SAVE_LOCATION_URL = "http://138.68.146.193:5000/savelocation";
    private String MY_LOCATIONS_URL = "http://138.68.146.193:5000/mylocations";
    private String DELETE_LOCATION_URL =  "http://138.68.146.193:5000/deletelocation";

    private String CREATE_REQUEST_URL = "http://138.68.146.193:5000/createrequest";
    private String ACCEPT_REQUEST_URL = "http://138.68.146.193:5000/acceptrequest";

    private String NEARBY_REQUESTS_URL = "http://138.68.146.193:5000/nearbyrequests";

    private String LIST_MY_REQUESTS_URL = "http://138.68.146.193:5000/listmyrequests";
    private String LIST_ACCEPTED_REQUESTS_URL = "http://138.68.146.193:5000/listacceptedrequests";
    private String LIST_ACCEPTED_REQUESTS_VOLUNTARY_URL = "http://138.68.146.193:5000/listacceptedrequestsvoluntary";

    private String REQUEST_INFO_URL = "http://138.68.146.193:5000/requestinfo";

    private String CANCEL_REQUEST_URL = "http://138.68.146.193:5000/cancelrequest";
    private String FINISH_REQUEST_URL = "http://138.68.146.193:5000/finishrequest";

    private String GIVE_FEEDBACK_HELPER_URL = "http://138.68.146.193:5000/givefeedbackhelper";
    private String GIVE_FEEDBACK_OWNER_URL = "http://138.68.146.193:5000/givefeedbackowner";

    public String sendPOST(String Receiver_URL,String POST_Data){
        String response = "";
        try {
            URL url = new URL(Receiver_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //SEND POST
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(POST_Data);
            osw.flush();
            osw.close();
            os.close();

            //RECEIVE JSON
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"iso-8859-1"));
                response=br.readLine();
                //while ((line = br.readLine()) != null)
                br.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String userRegister(String name, String email, String contact, String password){
        String register_data = "{";
        register_data += "\"name\"" + ":" + "\"" + name + "\"" + "," + "\"email\"" + ":" + "\"" + email + "\"" + "," + "\"contact\"" + ":" + "\"" + contact + "\"" + "," + "\"password\"" + ":" + "\"" + password + "\"";
        register_data += "}";
        return sendPOST(REGISTER_URL,register_data);
    }

    public String confirmContact(String contact){
        String contact_data = "{";
        contact_data += "\"contact\"" + ":" + "\"" + contact + "\"";
        contact_data += "}";
        return sendPOST(CONFIRM_CONTACT_URL,contact_data);

    }

    public String userLogin(String email, String password){
        String login_data = "{";
        login_data += "\"email\"" + ":" + "\"" + email + "\"" + "," + "\"password\"" + ":" + "\"" + password + "\"";
        login_data += "}";
        return sendPOST(LOGIN_URL,login_data);

    }

    public String saveLocation(int user_id, String name, double longitude, double latitude){
        String location_data = "{";
        location_data += "\"id\"" + ":" + "\"" + String.valueOf(user_id) + "\"" + "," + "\"name\"" + ":" + "\"" + name + "\"" + "," + "\"latitude\"" + ":" + "\"" + String.valueOf(latitude) + "\"" + "," + "\"longitude\"" + ":" + "\"" + String.valueOf(longitude) + "\"";
        location_data += "}";
        return sendPOST(SAVE_LOCATION_URL,location_data);
    }

    public String myLocations(int user_id){
        String location_data = "{";
        location_data += "\"user_id\"" + ":" + "\"" + String.valueOf(user_id) + "\"";
        location_data += "}";
        return sendPOST(MY_LOCATIONS_URL,location_data);
    }

    public String deleteLocation(int loc_id){
        String location_data = "{";
        location_data += "\"loc_id\"" + ":" + "\"" + String.valueOf(loc_id) + "\"";
        location_data += "}";
        return sendPOST(DELETE_LOCATION_URL,location_data);
    }

    public String createRequest(int user_id, String title, String description, int loc_id, ArrayList<String> items, Date deadline, int max_helpers){
        String request_data = "{";
        String item_list = "[";
        for (int i=0; i < items.size(); i++){
            item_list += "{" + "\"" + "item_" + String.valueOf(i) + "\"" + ":" + "\"" + items.get(i) + "\"" + "}";
            if (i < items.size()-1){
                item_list+=",";
            }
        }
        item_list +="]";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String deadline_date = dateFormat.format(deadline);
        request_data += "\"user_id\"" + ":" + "\"" + String.valueOf(user_id) + "\"" + "," + "\"title\"" + ":" + "\"" + title + "\"" + "," + "\"description\"" + ":" + "\"" + description + "\"" + "," + "\"max_helpers\"" + ":" + "\"" + max_helpers + "\""+ "," + "\"loc_id\"" + ":" + "\"" + String.valueOf(loc_id) + "\"" + "," + "\"list\"" + ":" + item_list  + "," + "\"deadline\"" + ":" + "\"" + deadline_date + "\"";
        request_data += "}";
        return sendPOST(CREATE_REQUEST_URL,request_data);
    }

    public String acceptRequest(int user_id, int req_id){
        String user_data = "{";
        user_data += "\"user_id\"" + ":" + "\"" + String.valueOf(user_id) + "\"" + "," + "\"req_id\"" + ":" + "\"" + String.valueOf(req_id) + "\"";
        user_data += "}";
        return sendPOST(ACCEPT_REQUEST_URL,user_data);
    }

    public String nearbyRequests(int user_id, double latitude, double longitude, int distance){
        String user_data = "{";
        user_data += "\"user_id\"" + ":" + "\""+ String.valueOf(user_id) + "\"" + "," + "\"latitude\"" + ":" + "\""+ String.valueOf(latitude) + "\"" + "," + "\"longitude\"" + ":" + "\""+ String.valueOf(longitude) + "\"" + "," + "\"distance\"" + ":" + "\""+ String.valueOf(distance) + "\"";
        user_data += "}";
        return sendPOST(NEARBY_REQUESTS_URL,user_data);
    }


    public String listMyRequests(int user_id){
        String user_data = "{";
        user_data += "\"user_id\"" + ":" + "\""+ String.valueOf(user_id) + "\"";
        user_data += "}";
        return sendPOST(LIST_MY_REQUESTS_URL,user_data);
    }

    public String listAcceptedRequests(int user_id){
        String user_data = "{";
        user_data += "\"user_id\"" + ":" + "\"" + String.valueOf(user_id) + "\"";
        user_data += "}";
        return sendPOST(LIST_ACCEPTED_REQUESTS_URL,user_data);
    }

    public String listAcceptedRequestsVoluntary(int user_id){
        String user_data = "{";
        user_data += "\"user_id\"" + ":" + "\"" + String.valueOf(user_id) + "\"";
        user_data += "}";
        return sendPOST(LIST_ACCEPTED_REQUESTS_VOLUNTARY_URL,user_data);
    }

    public String requestInfo(int req_id){
        String req_data = "{";
        req_data += "\"req_id\"" + ":" + "\"" + String.valueOf(req_id) + "\"";
        req_data += "}";
        return sendPOST(REQUEST_INFO_URL,req_data);
    }

    public String cancelRequest(int req_id){
        String req_data = "{";
        req_data += "\"req_id\"" + ":" + "\"" + String.valueOf(req_id) + "\"";
        req_data += "}";
        return sendPOST(CANCEL_REQUEST_URL,req_data);
    }

    public String finishRequest(int req_id){
        String req_data = "{";
        req_data += "\"req_id\"" + ":" + "\"" + String.valueOf(req_id) + "\"";
        req_data += "}";
        return sendPOST(FINISH_REQUEST_URL,req_data);
    }

    public String giveFeedbackHelper(int req_id, int value){
        String req_data = "{";
        req_data += "\"req_id\"" + ":" + "\"" + String.valueOf(req_id) + "\"" + "," +  "\"value\"" + ":" + "\"" + String.valueOf(value) + "\"";
        req_data += "}";
        return sendPOST(GIVE_FEEDBACK_HELPER_URL,req_data);
    }

    public String giveFeedbackOwner(int req_id, int value){
        String req_data = "{";
        req_data += "\"req_id\"" + ":" + "\"" + String.valueOf(req_id) + "\"" + "," +  "\"value\"" + ":" + "\"" + String.valueOf(value) + "\"";
        req_data += "}";
        return sendPOST(GIVE_FEEDBACK_OWNER_URL,req_data);
    }
}