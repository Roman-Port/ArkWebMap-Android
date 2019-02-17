package com.romanport.arkwebmap.NetEntities.UsersMe;

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
}
