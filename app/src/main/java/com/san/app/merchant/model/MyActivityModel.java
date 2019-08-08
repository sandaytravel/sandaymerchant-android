package com.san.app.merchant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MyActivityModel {

    @SerializedName("payload")
    @Expose
    private List<Payload> payload = null;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

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

    public class Payload {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("merchant_id")
        @Expose
        private Integer merchantId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("subtitle")
        @Expose
        private String subtitle;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("actual_price")
        @Expose
        private String actualPrice;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("city_id")
        @Expose
        private Integer cityId;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("category_id")
        @Expose
        private Integer categoryId;
        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("admin_approve")
        @Expose
        private Integer adminApprove;
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("total_review")
        @Expose
        private Integer totalReview;
        @SerializedName("average_review")
        @Expose
        private Double averageReview;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(Integer merchantId) {
            this.merchantId = merchantId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getActualPrice() {
            return actualPrice;
        }

        public void setActualPrice(String actualPrice) {
            this.actualPrice = actualPrice;
        }

        public String getDisplayPrice() {
            return displayPrice;
        }

        public void setDisplayPrice(String displayPrice) {
            this.displayPrice = displayPrice;
        }

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getAdminApprove() {
            return adminApprove;
        }

        public void setAdminApprove(Integer adminApprove) {
            this.adminApprove = adminApprove;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public Integer getTotalReview() {
            return totalReview;
        }

        public void setTotalReview(Integer totalReview) {
            this.totalReview = totalReview;
        }

        public Double getAverageReview() {
            return averageReview;
        }

        public void setAverageReview(Double averageReview) {
            this.averageReview = averageReview;
        }

    }
}
