package com.brahamaputra.mahindra.brahamaputrajio.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotoDigitalSignatureData {

    @SerializedName("base64StringHandingOverPersonSignature")
    @Expose
    private String base64StringHandingOverPersonSignature;
    @SerializedName("base64StringTakingOverPersonSignature")
    @Expose
    private String base64StringTakingOverPersonSignature;

    @SerializedName("isSubmited")
    private int isSubmited;

    public HotoDigitalSignatureData() {
        this.base64StringHandingOverPersonSignature = "";
        this.base64StringTakingOverPersonSignature = "";
        this.isSubmited = 0;
    }

    public HotoDigitalSignatureData(String base64StringHandingOverPersonSignature, String base64StringTakingOverPersonSignature) {
        this.base64StringHandingOverPersonSignature = base64StringHandingOverPersonSignature;
        this.base64StringTakingOverPersonSignature = base64StringTakingOverPersonSignature;
        this.isSubmited = 1;
        if (!this.base64StringHandingOverPersonSignature.isEmpty() && !this.base64StringTakingOverPersonSignature.isEmpty()) {
            this.isSubmited = 2;
        }
    }

    public String getBase64StringHandingOverPersonSignature() {
        return base64StringHandingOverPersonSignature;
    }

    public void setBase64StringHandingOverPersonSignature(String base64StringHandingOverPersonSignature) {
        this.base64StringHandingOverPersonSignature = base64StringHandingOverPersonSignature;
    }

    public String getBase64StringTakingOverPersonSignature() {
        return base64StringTakingOverPersonSignature;
    }

    public void setBase64StringTakingOverPersonSignature(String base64StringTakingOverPersonSignature) {
        this.base64StringTakingOverPersonSignature = base64StringTakingOverPersonSignature;
    }

    public int getSubmited() {
        return isSubmited;
    }

    public void setSubmited(int submited) {
        isSubmited = submited;
    }


}
