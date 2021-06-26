package com.example.serbigoworker;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;





public class service_request {
    private long status;
    private Timestamp sdate;
    private String client_id;
    private String provider_id;
    private String remarks;
    private long total_fee;



    public service_request() {
    }

    public service_request(String client_id, String provider_id, String remarks,  Timestamp sdate, long status, long total_fee) {
        this.client_id = client_id;
        this.sdate = sdate;
        this.status = status;
        this.provider_id = provider_id;
        this.remarks = remarks;
        this.total_fee = total_fee;



    }


    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }


    public Timestamp getSdate() {
        return sdate;
    }

    public void setSdate(Timestamp sdate) {
        this.sdate = sdate;
    }


    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(long total_fee) {
        this.total_fee = total_fee;
    }
}
