package com.example;

public class User {
    private String username;
    private String password;
    private int age;
    private String gender;
    private String region;
    private String bio;
    private String pfp;
    private String groups;
    private int id;
    private String type;
    private String adminkey;
    private int level;
    private int experience;
    private String steamid;

    public String getUsername() {
        return this.username;
    }


    public String getPassword() {
        return this.password;
    }

    public int getId() {
        return this.id;
    }

    public int getAge() {
        return this.age;
    }

    public String getGender() {
        return this.gender;
    }

    public String getRegion() {
        return this.region;
    }

    public String getBio() {
        return this.bio;
    }

    public String getPfp(){
        return this.pfp;
    }

    public String getGroups(){
        return this.groups;
    }

    public String getType(){
        return this.type;
    }

    public String getAdminkey(){
        return this.adminkey;
    }

    public int getLevel(){
        return this.level;
    }

    public int getExperience(){
        return this.experience;
    }

    public String getSteamid(){
        return this.steamid;
    }

    public void setUsername(String n) {
        this.username = n;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public void setId(int i){
        this.id = i;
    }

    public void setAge(int n) {
        this.age = n;
    }

    public void setGender(String p) {
        this.gender = p;
    }

    public void setRegion(String i){
        this.region = i;
    }

    public void setBio(String n) {
        this.bio = n;
    }

    public void setPfp(String p) {
        this.pfp = p;
    }

    public void setGroups(String i){
        this.groups = i;
    }

    public void setType(String i){
        this.type = i;
    }

    public void setAdminkey(String i){
        this.adminkey = i;
    }

    public int setLevel(int n){
        return this.level = n;
    }

    public void setExperience(int n){
        this.experience=n;
    }

    public void setSteamid(String n){
        this.steamid = n;
    }

}