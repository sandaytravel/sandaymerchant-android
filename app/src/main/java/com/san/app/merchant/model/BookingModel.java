package com.san.app.merchant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BookingModel {

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class Payload {
        @SerializedName("order_id")
        @Expose
        private Integer orderId;

        @SerializedName("status")
        @Expose
        private Integer status;

        @SerializedName("category")
        @Expose
        private String category;

        @SerializedName("voucher_number")
        @Expose
        private String voucher_number;

        @SerializedName("order_number")
        @Expose
        private String orderNumber;
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;

        @SerializedName("country_name")
        @Expose
        private String countryName;

        @SerializedName("birth_date")
        @Expose
        private String birth_date;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("customer_name")
        @Expose
        private String customerName;

        @SerializedName("customer_contact_number")
        @Expose
        private String customerContactNumber;

        @SerializedName("customer_email")
        @Expose
        private String customerEmail;

        public String getCustomerContactNumber() {
            return customerContactNumber;
        }

        public void setCustomerContactNumber(String customerContactNumber) {
            this.customerContactNumber = customerContactNumber;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        @SerializedName("profile_pic")
        @Expose
        private String profilePic;
        @SerializedName("booking_date")
        @Expose
        private String bookingDate;
        @SerializedName("participation_date")
        @Expose
        private String participationDate;

        @SerializedName("is_redeem")
        @Expose
        private Integer is_redeem;

        @SerializedName("total_price")
        @Expose
        private String totalPrice;
        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("activity_name")
        @Expose
        private String activityName;
        @SerializedName("activity_image")
        @Expose
        private String activityImage;
        @SerializedName("package_title")
        @Expose
        private String packageTitle;
        @SerializedName("package_id")
        @Expose
        private Integer packageId;
        @SerializedName("packagequantity")
        @Expose
        private List<Packagequantity> packagequantity = null;

        public String getVoucher_number() {
            return voucher_number;
        }

        public void setVoucher_number(String voucher_number) {
            this.voucher_number = voucher_number;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getBirth_date() {
            return birth_date;
        }

        public void setBirth_date(String birth_date) {
            this.birth_date = birth_date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getBookingDate() {
            return bookingDate;
        }

        public void setBookingDate(String bookingDate) {
            this.bookingDate = bookingDate;
        }

        public String getParticipationDate() {
            return participationDate;
        }

        public void setParticipationDate(String participationDate) {
            this.participationDate = participationDate;
        }

        public Integer getIs_redeem() {
            return is_redeem;
        }

        public void setIs_redeem(Integer is_redeem) {
            this.is_redeem = is_redeem;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getActivityImage() {
            return activityImage;
        }

        public void setActivityImage(String activityImage) {
            this.activityImage = activityImage;
        }

        public String getPackageTitle() {
            return packageTitle;
        }

        public void setPackageTitle(String packageTitle) {
            this.packageTitle = packageTitle;
        }

        public Integer getPackageId() {
            return packageId;
        }

        public void setPackageId(Integer packageId) {
            this.packageId = packageId;
        }

        public List<Packagequantity> getPackagequantity() {
            return packagequantity;
        }

        public void setPackagequantity(List<Packagequantity> packagequantity) {
            this.packagequantity = packagequantity;
        }
    }

    public class Packagequantity {

        @SerializedName("quantity")
        @Expose
        private Integer quantity;
        @SerializedName("quantity_id")
        @Expose
        private Integer quantityId;
        @SerializedName("quantity_name")
        @Expose
        private String quantityName;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getQuantityId() {
            return quantityId;
        }

        public void setQuantityId(Integer quantityId) {
            this.quantityId = quantityId;
        }

        public String getQuantityName() {
            return quantityName;
        }

        public void setQuantityName(String quantityName) {
            this.quantityName = quantityName;
        }
    }
}
