package com.san.app.merchant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SalesReportModel {

    @SerializedName("payload")
    @Expose
    private List<Payload> payload = null;
    @SerializedName("page")
    @Expose
    private Integer page;

    @SerializedName("total_earn")
    @Expose
    private String total_earn;

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    public String getTotal_earn() {
        return total_earn;
    }

    public void setTotal_earn(String total_earn) {
        this.total_earn = total_earn;
    }

    public List<Payload> getPayload() {
        return payload;
    }

    public void setPayload(List<Payload> payload) {
        this.payload = payload;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
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


    public class Payload{
        @SerializedName("order_id")
        @Expose
        private Integer orderId;
        @SerializedName("order_number")
        @Expose
        private String orderNumber;
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;
        @SerializedName("customer_name")
        @Expose
        private String customerName;
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
        @SerializedName("category_id")
        @Expose
        private Integer categoryId;
        @SerializedName("category_name")
        @Expose
        private String categoryName;
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

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
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

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
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

    public class Packagequantity{
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
