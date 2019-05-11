package com.brahamaputra.mahindra.brahamaputrajio.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DgLastReadingsForDieselRequest {

    @SerializedName("Success")
    private Integer Success;

    @SerializedName("Code")
    private String Code;

    @SerializedName("Message")
    private String Message;

    @SerializedName("SiteDieselTransactions")
    private SiteDieselTransactions siteDieselTransactions = null;

    public Integer getSuccess() {
        return Success;
    }

    public void setSuccess(Integer success) {
        Success = success;
    }

    public com.brahamaputra.mahindra.brahamaputrajio.Data.Error getError() {
        return error;
    }

    public void setError(com.brahamaputra.mahindra.brahamaputrajio.Data.Error error) {
        this.error = error;
    }

    @SerializedName("Error")
    @Expose
    private com.brahamaputra.mahindra.brahamaputrajio.Data.Error error;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public SiteDieselTransactions getSiteDieselTransactions() {
        return siteDieselTransactions;
    }

    public void setSiteDieselTransactions(SiteDieselTransactions siteDieselTransactions) {
        this.siteDieselTransactions = siteDieselTransactions;
    }

}