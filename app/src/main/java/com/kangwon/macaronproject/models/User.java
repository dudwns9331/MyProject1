package com.kangwon.macaronproject.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START Schedular_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String id;
    public String phone;
    public String isowner;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public User(String id, String email, String username, String phone, boolean isowner) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        if(isowner)
            this.isowner = "true";
        else
            this.isowner = "false";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("email", email);
        result.put("username", username);
        result.put("phone", phone);
        result.put("isowner", isowner);

        return result;
    }
}
// [END Schedular_user_class]