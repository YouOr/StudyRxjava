package com.liufeng.testproject;

/**
 * Created by Administrator on 2017/11/29.
 */
public class UserBean {
    private int rowno;
    private String StopID;
    private String StopName;
    private String Address;
    private String Product_Type;
    private String UDFT3;


    public UserBean(int rowno, String stopID, String stopName, String address, String product_Type, String UDFT3) {
        this.rowno = rowno;
        StopID = stopID;
        StopName = stopName;
        Address = address;
        Product_Type = product_Type;
        this.UDFT3 = UDFT3;
    }

    public int getRowno() {
        return rowno;
    }

    public void setRowno(int rowno) {
        this.rowno = rowno;
    }

    public String getStopID() {
        return StopID;
    }

    public void setStopID(String stopID) {
        StopID = stopID;
    }

    public String getStopName() {
        return StopName;
    }

    public void setStopName(String stopName) {
        StopName = stopName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getProduct_Type() {
        return Product_Type;
    }

    public void setProduct_Type(String product_Type) {
        Product_Type = product_Type;
    }

    public String getUDFT3() {
        return UDFT3;
    }

    public void setUDFT3(String UDFT3) {
        this.UDFT3 = UDFT3;
    }
}
