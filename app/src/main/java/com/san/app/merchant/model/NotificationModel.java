package com.san.app.merchant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationModel {

    @SerializedName("payload")
    @Expose
    private List<Payload> payload = null;
    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("page")
    @Expose
    private Integer page;


    @SerializedName("message")
    @Expose
    private String message;

    public List<Payload> getPayload() {
        return payload;
    }

    public void setPayload(List<Payload> payload) {
        this.payload = payload;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public class Payload {
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;
        @SerializedName("customre_name")
        @Expose
        private String customreName;
        @SerializedName("customre_email")
        @Expose
        private String customreEmail;
        @SerializedName("time_ago")
        @Expose
        private String timeAgo;
        @SerializedName("profile_pic")
        @Expose
        private String profilePic;
        @SerializedName("Message")
        @Expose
        private String message;
        @SerializedName("order_id")
        @Expose
        private String order_id;

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getCustomreName() {
            return customreName;
        }

        public void setCustomreName(String customreName) {
            this.customreName = customreName;
        }

        public String getCustomreEmail() {
            return customreEmail;
        }

        public void setCustomreEmail(String customreEmail) {
            this.customreEmail = customreEmail;
        }

        public String getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(String timeAgo) {
            this.timeAgo = timeAgo;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }
    }
}
