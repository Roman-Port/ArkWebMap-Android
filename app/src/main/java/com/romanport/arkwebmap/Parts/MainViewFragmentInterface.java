package com.romanport.arkwebmap.Parts;

import com.android.volley.Response;
import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.romanport.arkwebmap.NetEntities.Servers.ArkServerCreateSession;
import com.romanport.arkwebmap.NetEntities.Servers.Tribes.ArkTribe;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;

public interface MainViewFragmentInterface {
    void GetDino(String url, final Response.Listener<ArkDinosReply> callback);
    ArkTribe GetTribe();
    ArkServerCreateSession GetSession();
    UsersMeServer GetServer();
    double GetServerTimeNow();
    double GetServerTimeSinceUpdate();
}
