/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;




import javax.swing.JOptionPane;
import javax.swing.text.Document;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;

import static javax.swing.JOptionPane.showMessageDialog;

import javax.script.ScriptEngineManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
@SessionAttributes("loggeduser") //CREATING THE VARIABLE FOR THE SESSION
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;


   @ModelAttribute("loggeduser") //loggeduser IS THE User OBJECT THAT IS PER SESSION SO THIS CAN CARRY THE LGGED IN USERDATA MAYBE ????
   public User setUpUserForm() {
      return new User();
   }



  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }


  @RequestMapping("/")
  String index(@ModelAttribute("loggeduser") User loggeduser) {
    return "redirect:/mainpage";
  }

  @GetMapping(
    path = "/mainpage"
  )
  public String getUserForm(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS grouptable (id serial, groupname varchar(20), maxmembers integer, game varchar(20))");
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id serial, username varchar(20), password varchar(20), type varchar(20), age integer, gender varchar(20), region varchar(20), bio varchar(150), pfp varchar(150), groups varchar(20), level integer, experience integer, steamid varchar(200))");
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
      ArrayList<User> output = new ArrayList<User>();
      output.add(loggeduser);
      if (loggeduser.getId() != 0){
        model.put("welcome", "Welcome " + loggeduser.getUsername());
      }
      model.put("records", loggeduser);
      return "mainpage";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/signup"
  )
  public String getSignUpForm(Map<String, Object> model){
    User user = new User();
    model.put("user", user);
    return "signup";
  }

  @PostMapping(
    path = "/signup",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )//ADD CONFIRM PASSWORD AND MAYBE EMAIL? SEQURITY QUESTIONS? ENCRYPT PASSWORD? REGION? SEX? AGE? ETC>...>>>
  public String handleBrowserSignupSubmit(Map<String, Object> model, User user) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id serial, username varchar(20), password varchar(20), type varchar(20), age integer, gender varchar(20), region varchar(20), bio varchar(150), pfp varchar(150), groups varchar(20), level integer, experience integer, steamid varchar(200))");
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
      while(rs.next()){
        String tname = rs.getString("username");
        if(user.getUsername().equals(tname)){
        model.put("message", "Username taken. Have an account? Log in.");
        return "signup";
        }
      }
      String sql = "INSERT INTO accounts (username,password,type,age,gender,region,bio) VALUES ('" + user.getUsername() + "','" + user.getPassword() + "','" + "user" + "','" + user.getAge() + "','" + user.getGender() + "','" + user.getRegion() + "','" + "User Bio is Empty" + "')";
      stmt.executeUpdate(sql);
      return "login";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/login"
  )
  public String getLoginForm(Map<String, Object> model) {
    User user = new User();
    model.put("user", user);
    return "login";
  }

   @PostMapping("/login")
   public String doLogin(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) { //THE (@ModelAttribute("loggeduser") ... PARAM CARRIES SESSION DATA INTO THE PAGE SO WE CAN FIND THE USER STUFF IT WORKS!!!
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
      ArrayList<User> output = new ArrayList<User>();
      while (rs.next()) {
        User tuser = new User();
        String tname = rs.getString("username");
        String tpassword = rs.getString("password");
        int id = rs.getInt("id");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String region = rs.getString("region");
        String bio = rs.getString("bio");
        String pfp = rs.getString("pfp");
        String groups = rs.getString("groups");
        String type = rs.getString("type");
        if(user.getUsername().equals(tname)){
            if(user.getPassword().equals(tpassword)) {
            loggeduser.setId(id);
            loggeduser.setUsername(tname);
            loggeduser.setPassword(tpassword);
            loggeduser.setId(id);
            loggeduser.setAge(age);
            loggeduser.setGender(gender);
            loggeduser.setRegion(region);
            loggeduser.setBio(bio);
            loggeduser.setPfp(pfp);
            loggeduser.setGroups(groups);
            loggeduser.setType(type);
            return "redirect:/profile";
            }else{
                model.put("message", "Username/Password is incorrect. Create an account.");
                return "login";
            }
        }
      }
      model.put("message", "Username/Password is incorrect. Create an account.");
      return "login";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
}

  @GetMapping(
    path = "/logout"
  )
   public String doLogout(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
        loggeduser.setId(0);
        loggeduser.setUsername("");
        loggeduser.setPassword("");
        loggeduser.setAge(0);
        loggeduser.setGender("");
        loggeduser.setRegion("");
        loggeduser.setBio("");
        loggeduser.setPfp("");
        loggeduser.setGroups("");
        loggeduser.setType("");
        loggeduser.setSteamid("");
        return "mainpage";
        }

@GetMapping(
    path = "/groupdb"
  )
  public String getGroupDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groupconnections (id serial, gid integer, uid integer, request integer, type varchar(20))");
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS grouptable (id serial, groupname varchar(20), maxmembers integer, game varchar(20))");
      ResultSet rs = stmt.executeQuery("SELECT * FROM grouptable");
      ArrayList<ObjGroup> output = new ArrayList<ObjGroup>();
      while (rs.next()) {
        int currentmembers = 0;
        ObjGroup tgroup = new ObjGroup();
        String gname = rs.getString("groupname");
        int mcount = rs.getInt("maxmembers");
        String game = rs.getString("game");
        int id = rs.getInt("id");
        tgroup.setGroupname(gname);
        tgroup.setMaxmembers(mcount);
        tgroup.setGame(game);
        tgroup.setGid(id);
        ResultSet rs2 = stmt2.executeQuery("SELECT * FROM groupconnections WHERE gid="+id);
        while(rs2.next()){
            currentmembers = currentmembers + 1;
        }
        tgroup.setCurrentmembers(currentmembers);
        output.add(tgroup);
      }
      model.put("records", output);
      return "groupdb";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


@GetMapping(
    path = "/group/create"
  )
  public String getGroupCreate(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model) {
   User user = new User();
   ObjGroup objgroup = new ObjGroup();
   model.put("objgroup", objgroup);
   model.put("user", user);
   if(loggeduser.getId() == 0){
    model.put("message", "You must be logged in");
    return "login";
    }
  return "creategroup";
  }
  @PostMapping(
    path = "/group/create",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleGroupCreate(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, ObjGroup objgroup) {
  if(loggeduser.getId() == 0){
    return "login";
  }else{
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS grouptable (id serial, groupname varchar(20), maxmembers integer, game varchar(20))");
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groupconnections (id serial, gid integer, uid integer, request integer, type varchar(20))");
      ResultSet rs = stmt.executeQuery("INSERT INTO grouptable (groupname,maxmembers,game) VALUES ('" + objgroup.getGroupname() + "','" + objgroup.getMaxmembers() + "','" + objgroup.getGame() + "') RETURNING id");
      int ngroupid = 0;
      while(rs.next()){
            ngroupid = rs.getInt("id");
      }
      String sql = "INSERT INTO groupconnections (gid,uid,type) VALUES ('" + ngroupid + "','" + loggeduser.getId() + "','Owner')";
      stmt.executeUpdate(sql);
      return "redirect:/profile";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    }
  }

@GetMapping(
    path = "/group/{gid}"
  )
  public String getSpecificGroup(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String gid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM grouptable WHERE id="+gid);
      ArrayList<ObjGroup> output = new ArrayList<ObjGroup>();
      
      while (rs.next()) {
        int currentmembers = 0;
        ObjGroup tempgroup = new ObjGroup();
        int id = rs.getInt("id");
        String gname = rs.getString("groupname");
        int maxmembers = rs.getInt("maxmembers");
        String game = rs.getString("game");
        tempgroup.setGid(id);
        tempgroup.setGroupname(gname);
        tempgroup.setMaxmembers(maxmembers);
        tempgroup.setGame(game);
        ResultSet rs2 = stmt2.executeQuery("SELECT * FROM groupconnections WHERE gid="+id);
        while(rs2.next()){
            currentmembers = currentmembers + 1;
        }
        tempgroup.setCurrentmembers(currentmembers);
        output.add(tempgroup);
      }
      ResultSet rss = stmt.executeQuery("SELECT * FROM groupconnections WHERE gid="+gid);
      ArrayList<String> usersout = new ArrayList<String>();
      while(rss.next()){
      int uid = rss.getInt("uid");
      ResultSet srs = stmt2.executeQuery("SELECT * FROM accounts WHERE id="+uid);
        while(srs.next()){
            String usern = srs.getString("username");
            usersout.add(usern);
        }
      }
      model.put("members", usersout);
      model.put("records", output);
      return "readgroup";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


@GetMapping(
    path = "/group/{gid}/join"
  )
  public String getJoinGroup(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String gid) {
   if(loggeduser.getId() == 0){
    model.put("message", "You must be logged in");
    return "login";
    }
  try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      ResultSet rsm = stmt.executeQuery("SELECT * FROM grouptable");
      ArrayList<ObjGroup> output = new ArrayList<ObjGroup>();
      while (rsm.next()) {
        int currentmembers = 0;
        ObjGroup tgroup = new ObjGroup();
        String gname = rsm.getString("groupname");
        int mcount = rsm.getInt("maxmembers");
        String game = rsm.getString("game");
        int id = rsm.getInt("id");
        tgroup.setGroupname(gname);
        tgroup.setMaxmembers(mcount);
        tgroup.setGame(game);
        tgroup.setGid(id);
        ResultSet rs = stmt2.executeQuery("SELECT * FROM groupconnections WHERE gid="+id);
        while(rs.next()){
            currentmembers = currentmembers + 1;
        }
        tgroup.setCurrentmembers(currentmembers);
        output.add(tgroup);
      }
      model.put("records", output);


      int currentgroupmembers = 0;
      ResultSet rs = stmt.executeQuery("SELECT * FROM groupconnections WHERE gid="+gid);
      while (rs.next()) {
      currentgroupmembers = currentgroupmembers + 1;
      int uid = rs.getInt("uid");
      if(uid == loggeduser.getId()){
        model.put("message", "You are already in this group");
        return "groupdb";
      }
      }
      ResultSet rsk = stmt.executeQuery("SELECT * FROM grouptable WHERE id="+gid);
      while(rsk.next()){
        if(rsk.getInt("maxmembers") <= currentgroupmembers){
        model.put("message", "This group is full");
        return "groupdb";
        }
      }
      String sql = "INSERT INTO groupconnections (gid,uid,type) VALUES ('" + gid + "','" + loggeduser.getId() + "','User')";
      stmt.executeUpdate(sql);
      model.put("message", "You are now in this group");
      return "groupdb";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


@GetMapping(
    path = "/accdb"
  )
  public String getAccountDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
  if(!(loggeduser.getType().equals("admin"))){
  model.put("message", "You need to be an admin to do that");
  model.put("records", loggeduser);
  return "profile";
  }else{
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
      ArrayList<User> output = new ArrayList<User>();
      while (rs.next()) {
        User tempuser = new User();
        int id = rs.getInt("id");
        String tname = rs.getString("username");
        String password = rs.getString("password");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String region = rs.getString("region");
        String bio = rs.getString("bio");
        String pfp = rs.getString("pfp");
        String groups = rs.getString("groups");
        String type = rs.getString("type");
        tempuser.setUsername(tname);
        tempuser.setPassword(password);
        tempuser.setId(id);
        tempuser.setAge(age);
        tempuser.setGender(gender);
        tempuser.setRegion(region);
        tempuser.setBio(bio);
        tempuser.setPfp(pfp);
        tempuser.setGroups(groups);
        tempuser.setType(type);
        output.add(tempuser);
      }
      model.put("records", output);
      return "accdb";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    }
  }
  

  @GetMapping(
    path = "/userslist"
  )
  public String getOtherUsersDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
  if(loggeduser.getId() == 0){
    model.put("message", "You must be logged in");
    return "login";
  }else{
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
      ArrayList<User> output = new ArrayList<User>();
      while (rs.next()) {
        if(loggeduser.getId() != rs.getInt("id")){
          User tempuser = new User();
          int id = rs.getInt("id");
          String tname = rs.getString("username");
          String password = rs.getString("password");
          int age = rs.getInt("age");
          String gender = rs.getString("gender");
          String region = rs.getString("region");
          String bio = rs.getString("bio");
          String pfp = rs.getString("pfp");
          String groups = rs.getString("groups");
          String type = rs.getString("type");
          tempuser.setUsername(tname);
          tempuser.setPassword(password);
          tempuser.setId(id);
          tempuser.setAge(age);
          tempuser.setGender(gender);
          tempuser.setRegion(region);
          tempuser.setBio(bio);
          tempuser.setPfp(pfp);
          tempuser.setGroups(groups);
          tempuser.setType(type);
          output.add(tempuser);
        }
      }
      model.put("records", output);
      return "userslist";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    }
  }


@GetMapping(
    path = "/user/{pid}"
  )
  public String getAccountDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+pid);
      ArrayList<User> output = new ArrayList<User>();
      while (rs.next()) {
        User tempuser = new User();
        int id = rs.getInt("id");
        String tname = rs.getString("username");
        String password = rs.getString("password");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String region = rs.getString("region");
        String bio = rs.getString("bio");
        String pfp = rs.getString("pfp");
        String groups = rs.getString("groups");
        String type = rs.getString("type");
        tempuser.setUsername(tname);
        tempuser.setPassword(password);
        tempuser.setId(id);
        tempuser.setAge(age);
        tempuser.setGender(gender);
        tempuser.setRegion(region);
        tempuser.setBio(bio);
        tempuser.setPfp(pfp);
        tempuser.setGroups(groups);
        tempuser.setType(type);
        output.add(tempuser);
      }
      model.put("records", output);
      return "User";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/otheruser/{pid}"
  )
  public String getOtherUserDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+pid);
      ArrayList<User> output = new ArrayList<User>();
      while (rs.next()) {
        User tempuser = new User();
        int id = rs.getInt("id");
        String tname = rs.getString("username");
        String password = rs.getString("password");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String region = rs.getString("region");
        String bio = rs.getString("bio");
        String pfp = rs.getString("pfp");
        String groups = rs.getString("groups");
        String type = rs.getString("type");
        String tsteamid = rs.getString("steamid");
        tempuser.setUsername(tname);
        tempuser.setPassword(password);
        tempuser.setId(id);
        tempuser.setAge(age);
        tempuser.setGender(gender);
        tempuser.setRegion(region);
        tempuser.setBio(bio);
        tempuser.setPfp(pfp);
        tempuser.setGroups(groups);
        tempuser.setType(type);
        tempuser.setSteamid(tsteamid);
        output.add(tempuser);
      }
      model.put("records", output);
      ResultSet srs = stmt.executeQuery("SELECT * FROM friendslist WHERE username="+loggeduser.getId());
      while (srs.next()) {
        if(srs.getString("friend").equals(pid)){
          if(srs.getInt("request") == 1) {
            model.put("request", "sent");
          }
          else {
            model.put("request", "accepted");
          }
        }
      }
      ResultSet srs2 = stmt.executeQuery("SELECT * FROM friendslist WHERE friend="+loggeduser.getId());
      while (srs2.next()) {
        if(srs2.getString("username").equals(pid)){
          if(srs2.getInt("request") == 1) {
            model.put("request", "received");
          }
          else {
            model.put("request", "accepted");
          }
        }
      }
      ArrayList<ObjGroup> groupout = new ArrayList<ObjGroup>();
      
      ResultSet srs3 = stmt.executeQuery("SELECT * FROM groupconnections WHERE uid="+ pid);
      while(srs3.next()){
        ObjGroup grouplist = new ObjGroup();
        int groupid = srs3.getInt("gid");
        ResultSet srs4 = stmt2.executeQuery("SELECT * FROM grouptable WHERE id="+ groupid);
        while(srs4.next()){
        grouplist.setGroupname(srs4.getString("groupname"));
        grouplist.setGame(srs4.getString("game"));
        grouplist.setGid(groupid);
        }
            
            groupout.add(grouplist);
      }
      model.put("groupout",groupout);
      return "otheruser";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/friends/{pid}"
  )
  public String getFriendsDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
      ResultSet rs = stmt.executeQuery("SELECT * FROM friendslist WHERE username="+pid);
      ArrayList<User> output = new ArrayList<User>();
      Statement stmt2 = connection.createStatement();
      while (rs.next()) {
        int friendID = rs.getInt("friend");
        ResultSet srs = stmt2.executeQuery("SELECT * FROM accounts WHERE id="+friendID);
        if(rs.getInt("request") == 2){
          while (srs.next()) {
            User tempuser = new User();
            String tname = srs.getString("username");
            String password = srs.getString("password");
            int age = srs.getInt("age");
            int id = srs.getInt("id");
            String gender = srs.getString("gender");
            String region = srs.getString("region");
            String bio = srs.getString("bio");
            String pfp = srs.getString("pfp");
            String groups = srs.getString("groups");
            String type = srs.getString("type");
            tempuser.setUsername(tname);
            tempuser.setPassword(password);
            tempuser.setAge(age);
            tempuser.setId(id);
            tempuser.setGender(gender);
            tempuser.setRegion(region);
            tempuser.setBio(bio);
            tempuser.setPfp(pfp);
            tempuser.setGroups(groups);
            tempuser.setType(type);
            output.add(tempuser);
          }
        }
      }
      Statement stmt1 = connection.createStatement();
      ResultSet rs1 = stmt1.executeQuery("SELECT * FROM friendslist WHERE friend="+pid);
      Statement stmt3 = connection.createStatement();
      while (rs1.next()) {
        int friendID = rs1.getInt("username");
        ResultSet srs1 = stmt3.executeQuery("SELECT * FROM accounts WHERE id="+friendID);
        if(rs1.getInt("request") == 2){
          while (srs1.next()) {
            User tempuser = new User();
            String tname = srs1.getString("username");
            String password = srs1.getString("password");
            int age = srs1.getInt("age");
            int id = srs1.getInt("id");
            String gender = srs1.getString("gender");
            String region = srs1.getString("region");
            String bio = srs1.getString("bio");
            String pfp = srs1.getString("pfp");
            String groups = srs1.getString("groups");
            String type = srs1.getString("type");
            tempuser.setUsername(tname);
            tempuser.setPassword(password);
            tempuser.setAge(age);
            tempuser.setId(id);
            tempuser.setGender(gender);
            tempuser.setRegion(region);
            tempuser.setBio(bio);
            tempuser.setPfp(pfp);
            tempuser.setGroups(groups);
            tempuser.setType(type);
            output.add(tempuser);
          }
        }
      }
      model.put("records", output);
      model.put("message", loggeduser.getId());
      Statement stmt5 = connection.createStatement();
      ResultSet srs5 = stmt5.executeQuery("SELECT * FROM friendslist WHERE friend="+loggeduser.getId());
      while(srs5.next()){
        if(srs5.getInt("request") == 1){
          model.put("testing", "true");
        }
      }
      return "friends";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/friendrequests/{pid}"
  )
  public String getFriendRequestsDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
      ResultSet rs = stmt.executeQuery("SELECT * FROM friendslist WHERE friend="+pid);
      ArrayList<User> output = new ArrayList<User>();
      Statement stmt2 = connection.createStatement();
      while (rs.next()) {
        int friendID = rs.getInt("username");
        ResultSet srs = stmt2.executeQuery("SELECT * FROM accounts WHERE id="+friendID);
        if(rs.getInt("request") == 1){
          while (srs.next()) {
            User tempuser = new User();
            String tname = srs.getString("username");
            String password = srs.getString("password");
            int age = srs.getInt("age");
            int id = srs.getInt("id");
            String gender = srs.getString("gender");
            String region = srs.getString("region");
            String bio = srs.getString("bio");
            String pfp = srs.getString("pfp");
            String groups = srs.getString("groups");
            String type = srs.getString("type");
            tempuser.setUsername(tname);
            tempuser.setPassword(password);
            tempuser.setAge(age);
            tempuser.setId(id);
            tempuser.setGender(gender);
            tempuser.setRegion(region);
            tempuser.setBio(bio);
            tempuser.setPfp(pfp);
            tempuser.setGroups(groups);
            tempuser.setType(type);
            output.add(tempuser);
          }
        }
      }
      model.put("records", output);
      model.put("message", loggeduser.getId());
      model.put("title", "Friend Requests");
      return "friendrequests";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/pendingfriendrequests/{pid}"
  )
  public String getPendingFriendRequestsDatabase(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
      ResultSet rs = stmt.executeQuery("SELECT * FROM friendslist WHERE username="+pid);
      ArrayList<User> output = new ArrayList<User>();
      Statement stmt2 = connection.createStatement();
      while (rs.next()) {
        int friendID = rs.getInt("friend");
        ResultSet srs = stmt2.executeQuery("SELECT * FROM accounts WHERE id="+friendID);
        if(rs.getInt("request") == 1){
          while (srs.next()) {
            User tempuser = new User();
            String tname = srs.getString("username");
            String password = srs.getString("password");
            int age = srs.getInt("age");
            int id = srs.getInt("id");
            String gender = srs.getString("gender");
            String region = srs.getString("region");
            String bio = srs.getString("bio");
            String pfp = srs.getString("pfp");
            String groups = srs.getString("groups");
            String type = srs.getString("type");
            tempuser.setUsername(tname);
            tempuser.setPassword(password);
            tempuser.setAge(age);
            tempuser.setId(id);
            tempuser.setGender(gender);
            tempuser.setRegion(region);
            tempuser.setBio(bio);
            tempuser.setPfp(pfp);
            tempuser.setGroups(groups);
            tempuser.setType(type);
            output.add(tempuser);
          }
        }
      }
      model.put("records", output);
      model.put("message", loggeduser.getId());
      model.put("title", "Pending Friend Requests");
      return "friendrequests";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/addfriend/{pid}"
  )
  public String addAFriend(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
      String sql = "INSERT INTO friendslist (username,friend,request) VALUES ('" + loggeduser.getId() + "','" + pid + "','" + 1 + "')";
      stmt.executeUpdate(sql);
      Statement stmt2 = connection.createStatement();
        ArrayList<User> output2 = new ArrayList<User>();
        ResultSet rs2 = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
        while(rs2.next()){
        int level=rs2.getInt("level");
        int experience=rs2.getInt("experience");
        experience=experience+200;
        if(experience>=level*1000){
        level++;
        experience=0;
      }

      System.out.println(level);
      System.out.println(experience);
      stmt2.executeUpdate("UPDATE accounts SET level='"+level+"' WHERE id="+loggeduser.getId());
      stmt2.executeUpdate("UPDATE accounts SET experience='"+experience+"' WHERE id="+loggeduser.getId());
      loggeduser.setLevel(level);
      loggeduser.setExperience(experience);

      output2.add(loggeduser);
      model.put("records", output2);
    }
      return "redirect:/otheruser/"+pid;
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/acceptfriend/{pid}"
  )
  public String acceptAFriend(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      String sql = "UPDATE friendslist SET request=" + 2 + " WHERE username=" + pid + " AND friend="+ loggeduser.getId();
      stmt.executeUpdate(sql);
      String sql1 ="UPDATE friendslist SET request=" + 2 + " WHERE username=" + loggeduser.getId() + " AND friend="+ pid;
      stmt2.executeUpdate(sql1);
      return "redirect:/otheruser/"+pid;
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/deletefriend/{pid}"
  )
  public String deleteAFriend(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user, @PathVariable String pid) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      stmt.executeUpdate("DELETE FROM friendslist WHERE username="+ pid + " AND friend="+ loggeduser.getId());
      stmt2.executeUpdate("DELETE FROM friendslist WHERE username="+ loggeduser.getId() + " AND friend="+ pid);
      return "redirect:/otheruser/"+pid;
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  

@GetMapping(
    path = "/profile"
  )
  public String getLoginCheck(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      ArrayList<User> output = new ArrayList<User>();
      if(loggeduser.getId() == 0){
      model.put("message", "You must be logged in");
      return "login";
      }else{
        ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
        stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
        while(rs.next()){
        String tname = rs.getString("username");
        String tpassword = rs.getString("password");
        int id = rs.getInt("id");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String region = rs.getString("region");
        String bio = rs.getString("bio");
        String pfp = rs.getString("pfp");
        String groups = rs.getString("groups");
        String type = rs.getString("type");
        int level= rs.getInt("level");
        int experience=rs.getInt("experience");  
        String tsteamid = rs.getString("steamid");
        
        loggeduser.setId(id);
        loggeduser.setUsername(tname);
        loggeduser.setPassword(tpassword);
        loggeduser.setId(id);
        loggeduser.setAge(age);
        loggeduser.setGender(gender);
        loggeduser.setRegion(region);
        loggeduser.setBio(bio);
        loggeduser.setPfp(pfp);
        loggeduser.setGroups(groups);
        loggeduser.setType(type);
        loggeduser.setLevel(level);
        loggeduser.setExperience(experience);
        loggeduser.setSteamid(tsteamid);
        }
      output.add(loggeduser);
      model.put("records", output);
      }
      ResultSet srs = stmt.executeQuery("SELECT * FROM friendslist WHERE friend="+loggeduser.getId());
      while(srs.next()){
        if(srs.getInt("request") == 1){
          model.put("popupmessage", "true");
        }
      }
      ArrayList<ObjGroup> groupout = new ArrayList<ObjGroup>();
      ResultSet srs3 = stmt.executeQuery("SELECT * FROM groupconnections WHERE uid="+ loggeduser.getId());
      while(srs3.next()){
        ObjGroup grouplist = new ObjGroup();
        int groupid = srs3.getInt("gid");
        ResultSet srs4 = stmt2.executeQuery("SELECT * FROM grouptable WHERE id="+ groupid);
        while(srs4.next()){
        grouplist.setGroupname(srs4.getString("groupname"));
        grouplist.setGame(srs4.getString("game"));
        grouplist.setGid(groupid);
        }
            
            groupout.add(grouplist);
      }
      model.put("groupout",groupout);
      return "profile";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping
  (
    path ="/addexp"
  )

  public String AddExp(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
    System.out.println(loggeduser.getType());  
    try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    Statement stmt2 = connection.createStatement();
    ArrayList<ObjGroup> groupout = new ArrayList<ObjGroup>();
    Statement stmt3 = connection.createStatement();
    Statement stmt4 = connection.createStatement();
      ResultSet srs3 = stmt3.executeQuery("SELECT * FROM groupconnections WHERE uid="+ loggeduser.getId());
      while(srs3.next()){
        ObjGroup grouplist = new ObjGroup();
        int groupid = srs3.getInt("gid");
        ResultSet srs4 = stmt4.executeQuery("SELECT * FROM grouptable WHERE id="+ groupid);
        while(srs4.next()){
        grouplist.setGroupname(srs4.getString("groupname"));
        grouplist.setGame(srs4.getString("game"));
        grouplist.setGid(groupid);
        System.out.println("hello");
        }
            
            groupout.add(grouplist);
      }
      model.put("groupout",groupout);
    if(loggeduser.getType()!=null){
      if(!(loggeduser.getType().equals("admin"))){
        model.put("message", "You need to be an admin to do that");
        model.put("records", loggeduser);
        return "profile";
        }
    }
    ArrayList<User> output = new ArrayList<User>();
    ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
    while(rs.next()){
    int level=rs.getInt("level");
    int experience=rs.getInt("experience");
    experience=experience+200;
    if(experience>=level*1000){
      level++;
      experience=0;
    }

    System.out.println(level);
    System.out.println(experience);
     stmt2.executeUpdate("UPDATE accounts SET level='"+level+"' WHERE id="+loggeduser.getId());
   stmt2.executeUpdate("UPDATE accounts SET experience='"+experience+"' WHERE id="+loggeduser.getId());
   loggeduser.setLevel(level);
  loggeduser.setExperience(experience);

   output.add(loggeduser);
   model.put("records", output);
    }
    return "profile";
  } catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
    }
  }


@PostMapping(
  path = "/setsteam",
  consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
)
public String setSteamID(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    if(loggeduser.getId() == 0){
      model.put("message", "You must be logged in");
      return "login";
    }
      ResultSet srs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
      while(srs.next()){
        String tsteamid = srs.getString("steamid");
        String tname = srs.getString("username");
        String tpassword = srs.getString("password");
        int id = srs.getInt("id");
        int age = srs.getInt("age");
        String gender = srs.getString("gender");
        String region = srs.getString("region");
        String bio = srs.getString("bio");
        String pfp = srs.getString("pfp");
        String groups = srs.getString("groups");
        String type = srs.getString("type");
        loggeduser.setId(id);
        loggeduser.setUsername(tname);
        loggeduser.setPassword(tpassword);
        loggeduser.setAge(age);
        loggeduser.setGender(gender);
        loggeduser.setRegion(region);
        loggeduser.setBio(bio);
        loggeduser.setPfp(pfp);
        loggeduser.setGroups(groups);
        loggeduser.setType(type);
        loggeduser.setSteamid(tsteamid);
      }
    stmt.executeUpdate("UPDATE accounts SET steamid='"+user.getSteamid()+"' WHERE id="+loggeduser.getId());
    return "redirect:/profile";
  } catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }

  }

  @GetMapping(
    path = "/profile/edit"
  )
  public String getProfileEdit(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ArrayList<User> output = new ArrayList<User>();
      if(loggeduser.getId() == 0){
      return "login";
      }
        ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
        while(rs.next()){
        String tname = rs.getString("username");
        String tpassword = rs.getString("password");
        int id = rs.getInt("id");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String region = rs.getString("region");
        String bio = rs.getString("bio");
        String pfp = rs.getString("pfp");
        String groups = rs.getString("groups");
        String type = rs.getString("type");
        String tsteamid = rs.getString("steamid");
        loggeduser.setId(id);
        loggeduser.setUsername(tname);
        loggeduser.setPassword(tpassword);
        loggeduser.setId(id);
        loggeduser.setAge(age);
        loggeduser.setGender(gender);
        loggeduser.setRegion(region);
        loggeduser.setBio(bio);
        loggeduser.setPfp(pfp);
        loggeduser.setGroups(groups);
        loggeduser.setType(type);
        loggeduser.setSteamid(tsteamid);
        }
      output.add(loggeduser);
      model.put("records", output);
      return "editprofile";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(
    path = "/profile/edit",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleProfileEdit(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, User user) {
        try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ArrayList<User> output = new ArrayList<User>();
      if(loggeduser.getId() == 0){
      return "login";
      }
        ResultSet srs = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
        while(srs.next()){
        String tname = srs.getString("username");
        String tpassword = srs.getString("password");
        int id = srs.getInt("id");
        int age = srs.getInt("age");
        String gender = srs.getString("gender");
        String region = srs.getString("region");
        String bio = srs.getString("bio");
        String pfp = srs.getString("pfp");
        String groups = srs.getString("groups");
        String type = srs.getString("type");
        String tsteamid = srs.getString("steamid");
        loggeduser.setId(id);
        loggeduser.setUsername(tname);
        loggeduser.setPassword(tpassword);
        loggeduser.setId(id);
        loggeduser.setAge(age);
        loggeduser.setGender(gender);
        loggeduser.setRegion(region);
        loggeduser.setBio(bio);
        loggeduser.setPfp(pfp);
        loggeduser.setGroups(groups);
        loggeduser.setType(type);
        loggeduser.setSteamid(tsteamid);
        }
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE username='" + user.getUsername() + "'");
      while(rs.next()){
      if(rs.getString("username").equals(user.getUsername())){
        model.put("message", "Username taken");
        model.put("records", loggeduser);
        return "editprofile";
      }
      }
      if(!(user.getUsername().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET username='"+user.getUsername()+"' WHERE id="+loggeduser.getId());
      }
      if(!(user.getPassword().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET password='"+user.getPassword()+"' WHERE id="+loggeduser.getId());
      }
      if(user.getAge() != 0){
        stmt.executeUpdate("UPDATE accounts SET age='"+user.getAge()+"' WHERE id="+loggeduser.getId());
      }
      if(!(user.getGender().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET gender='"+user.getGender()+"' WHERE id="+loggeduser.getId());
      }
      if(!(user.getRegion().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET region='"+user.getRegion()+"' WHERE id="+loggeduser.getId());
      }
      if(!(user.getBio().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET bio='"+user.getBio()+"' WHERE id="+loggeduser.getId());
      }
      if(!(user.getPfp().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET pfp='"+user.getPfp()+"' WHERE id="+loggeduser.getId());
        Statement stmt2 = connection.createStatement();
        ArrayList<User> output2 = new ArrayList<User>();
        ResultSet rs2 = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
        while(rs2.next()){
        int level=rs2.getInt("level");
        int experience=rs2.getInt("experience");
        experience=experience+200;
        if(experience>=level*1000){
        level++;
        experience=0;
      }

      System.out.println(level);
      System.out.println(experience);
      stmt2.executeUpdate("UPDATE accounts SET level='"+level+"' WHERE id="+loggeduser.getId());
      stmt2.executeUpdate("UPDATE accounts SET experience='"+experience+"' WHERE id="+loggeduser.getId());
      loggeduser.setLevel(level);
      loggeduser.setExperience(experience);

      output2.add(loggeduser);
      model.put("records", output2);
    }
      }
      if(user.getAdminkey().equals("bobby276")){
        stmt.executeUpdate("UPDATE accounts SET type='admin' WHERE id="+loggeduser.getId());
      }
      if(!(user.getSteamid().length() == 0)){
        stmt.executeUpdate("UPDATE accounts SET steamid='"+user.getSteamid()+"' WHERE id="+loggeduser.getId());
        Statement stmt2 = connection.createStatement();
        ArrayList<User> output2 = new ArrayList<User>();
        ResultSet rs2 = stmt.executeQuery("SELECT * FROM accounts WHERE id="+loggeduser.getId());
        while(rs2.next()){
        int level=rs2.getInt("level");
        int experience=rs2.getInt("experience");
        experience=experience+200;
        if(experience>=level*1000){
        level++;
        experience=0;
      }

      System.out.println(level);
      System.out.println(experience);
      stmt2.executeUpdate("UPDATE accounts SET level='"+level+"' WHERE id="+loggeduser.getId());
      stmt2.executeUpdate("UPDATE accounts SET experience='"+experience+"' WHERE id="+loggeduser.getId());
      loggeduser.setLevel(level);
      loggeduser.setExperience(experience);

      output2.add(loggeduser);
      model.put("records", output2);
    }
      }
      output.add(loggeduser);
      model.put("records", output);
      
      return "redirect:/profile";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }


  @GetMapping(
    path = "/about"
  )
  public String getAboutForm(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
      ArrayList<User> output = new ArrayList<User>();
      output.add(loggeduser);
      if (loggeduser.getId() != 0){
        model.put("welcome", "Welcome " + loggeduser.getUsername());
      }
      model.put("records", loggeduser);
      return "about";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


 

@GetMapping("/accdb/delaccdb")
  public String deleteAllAccountsDB(Map<String, Object> model){
   try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("DROP TABLE accounts");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id serial, username varchar(20), password varchar(20), type varchar(20), age integer, gender varchar(20), region varchar(20), bio varchar(150), pfp varchar(150), groups varchar(20), level integer, experience integer, steamid varchar(200))");
    stmt.executeUpdate("DROP TABLE friendslist");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friendslist (id serial, username integer, friend integer, request integer)");
    stmt.executeUpdate("DROP TABLE grouptable");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS grouptable (id serial, groupname varchar(20), maxmembers integer, game varchar(20))");
    stmt.executeUpdate("DROP TABLE groupconnections");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groupconnections (id serial, gid integer, uid integer, request integer, type varchar(20))");
    stmt.executeUpdate("DROP TABLE hourTable");
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS hourTable (id serial, hoursid integer, hoursplayed integer, hoursslept integer, hoursexercised integer)");
    return "redirect:/mainpage";
   
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/hourstats"
  )
  public String getHourStats(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, Hour hour) {
    Hour hourStats = new Hour();
    model.put("hourStats", hourStats);
      try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ArrayList<Hour> output = new ArrayList<Hour>();
      if(loggeduser.getId() == 0){
      return "login";
      }
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS hourTable (id serial, hoursid integer, hoursplayed integer, hoursslept integer, hoursexercised integer)");
        ResultSet rs = stmt.executeQuery("SELECT * FROM hourTable WHERE hoursid="+loggeduser.getId());
        while(rs.next()){
        Hour hour1 = new Hour();
        int id = rs.getInt("id");
        int hPlayed = rs.getInt("hoursplayed");
        int hSlept = rs.getInt("hoursslept");
        int hExercised = rs.getInt("hoursexercised");
        hour1.setHoursid(id);
        hour1.setHoursplayed(hPlayed);
        hour1.setHoursslept(hSlept);
        hour1.setHoursexercised(hExercised);
        output.add(hour1);
        }
      model.put("records", output);
      return "hourstats";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(
    path = "/hourstats",
          consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String HourStatsSubmit(@ModelAttribute("loggeduser") User loggeduser, Map<String, Object> model, Hour hour) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO hourTable (hoursid,hoursPlayed,hoursslept,hoursexercised) VALUES ('" + loggeduser.getId() + "','" + hour.getHoursplayed() + " ',' " + hour.getHoursslept() + " ',' " + hour.getHoursexercised() + "')";
      stmt.executeUpdate(sql);
      System.out.println(loggeduser.getId() + " " + hour.getHoursplayed() + " " + hour.getHoursslept() + " " + hour.getHoursexercised());
      return "redirect:/hourstats";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }



}
