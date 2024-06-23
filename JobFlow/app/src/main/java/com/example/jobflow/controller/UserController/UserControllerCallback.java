package com.example.jobflow.controller.UserController;

import com.example.jobflow.model.User;

import java.util.List;

public interface UserControllerCallback {
    void onUserInfoReceived(User user);

    void onError(String errorMessage);
}
