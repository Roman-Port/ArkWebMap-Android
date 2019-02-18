package com.romanport.arkwebmap.NetEntities.Servers;

import java.io.Serializable;

public class PingReply implements Serializable {
    public String display_name;
    public String image_url;
    public String owner_uid;
    public String id;
    public Boolean online;
    public Double ping;
}
