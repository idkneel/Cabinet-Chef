package com.example.cabinetchef.Enums;

public class UserSettings {

    public String userID;
    public String uEmail;
    public String uPassword;
    // Set the user's household size to 1 by default
    public int uHousehold = 1;
    // Set the user's "app tint" to light mode (true) by default
    public boolean uTint = true;
    // Set user's cooking difficulty to 0 (beginner) by default / 1 is intermediate and 2 is expert/advanced
    public int uDifficulty = 0;

}
