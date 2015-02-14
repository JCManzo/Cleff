package com.freneticlabs.cleff.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jcmanzo on 2/12/15.
 */
public class Music {
    private int iD;

    public Music() {
    }

    public int getId() {
        return iD;
    }

    public void setId(int iD) {
        this.iD = iD;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();


        return jsonObject;
    }
}
