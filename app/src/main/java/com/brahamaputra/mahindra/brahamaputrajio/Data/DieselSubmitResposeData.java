package com.brahamaputra.mahindra.brahamaputrajio.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DieselSubmitResposeData {

    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
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
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
