package com.example.sportin.model;

public class UserDetail {
    private String userName;
    private String professionSport;
    private String fun_sport;

    public UserDetail() {
    }

    public UserDetail( String userName, String professionSport, String fun_sport){
        this.userName=userName;
        this.professionSport=professionSport;
        this.fun_sport=fun_sport;
    }

    public UserDetail( String userName, String professionSport){
        this.userName=userName;
        this.professionSport=professionSport;
    }

    public UserDetail( String userName){
        this.userName=userName;
    }

    public String getFun_sport() {
        return fun_sport;
    }

    public String getProfessionSport() {
        return professionSport;
    }

    public String getUserName() {
        return userName;
    }
}
