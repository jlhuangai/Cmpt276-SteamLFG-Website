package com.example;

public class ObjGroup {
    private String groupname;
    private String membername;
    private int maxmembers;
    private int currentmembers;
    private String game;
    private int gid;


    public String getMembername() {
        return this.membername;
    }

    public String getGame() {
        return this.game;
    }

    public int getMaxmembers() {
        return this.maxmembers;
    }

    public String getGroupname() {
        return this.groupname;
    }

    public int getGid(){
        return this.gid;
    }

    public int getCurrentmembers(){
        return this.currentmembers;
    }

    public void setMembername(String n) {
        this.membername = n;
    }

    public void setGame(String n){
        this.game = n;
    }

    public void setMaxmembers(int n) {
        this.maxmembers = n;
    }

    public void setGroupname(String p) {
        this.groupname = p;
    }

    public void setGid(int id){
        this.gid = id;
    }

    public int setCurrentmembers(int id){
        return this.currentmembers = id;
    }
}
