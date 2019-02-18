package com.romanport.arkwebmap.NetEntities.UsersMe;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.romanport.arkwebmap.NetEntities.Servers.PingReply;
import com.romanport.arkwebmap.WebUser;

import java.io.Serializable;
import java.util.List;

public class UsersMeServer implements Serializable {
    public String display_name;
    public String image_url;
    public String owner_uid;
    public String id;
    public Boolean has_ever_gone_online;
    public boolean is_hidden;
    public String endpoint_ping;
    public String endpoint_leave;
    public String endpoint_createsession;
    public String[] enabled_notifications;
    public PingReply ping_status;

    public void CheckIfOnline(Activity c, final Response.Listener<Boolean> callback) {
        if(ping_status == null) {
            //We must request it from the internet.
            WebUser.SendAuthenticatedGetRequest(c, endpoint_ping, new Response.Listener<Object>() {
                @Override
                public void onResponse(Object objPingReply) {
                    PingReply pingReply = (PingReply)objPingReply;
                    ping_status = pingReply;
                    callback.onResponse(pingReply.online);
                }
            }, PingReply.class);
        } else {
            //Use sent ping status
            callback.onResponse(ping_status.online);
        }
    }
}
