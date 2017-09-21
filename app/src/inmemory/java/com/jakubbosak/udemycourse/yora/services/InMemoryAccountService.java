package com.jakubbosak.udemycourse.yora.services;


import com.jakubbosak.udemycourse.yora.infrastructure.Auth;
import com.jakubbosak.udemycourse.yora.infrastructure.User;
import com.jakubbosak.udemycourse.yora.infrastructure.YoraApplication;
import com.squareup.otto.Subscribe;

import java.util.Objects;

public class InMemoryAccountService extends BaseInMemoryService{

    public InMemoryAccountService(YoraApplication application){
        super(application);
    }

    @Subscribe
    public void updateProfile(final Account.UpdateProfileRequest request) {
        final Account.UpdateProfileResponse response = new Account.UpdateProfileResponse();

        if(request.DisplayName.equals("nelson")){
            response.setPropertyErrors("displayName", "You may not be named Nelson!");
        }
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                User user = application.getAuth().getUser();
                user.setDisplayName(request.DisplayName);
                user.setEmail(request.Email);

                bus.post(response);
                bus.post(new Account.UserDetailsUpdatedEvent(user));
            }
        }, 2000,3000);



    }
    @Subscribe
    public void updateAvatar(final Account.ChangeAvatarRequest request){

        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                User user = application.getAuth().getUser();
                user.setAvatarUrl(request.NewAvatarUri.toString());

                bus.post(new Account.ChangeAvatarResponse());
                bus.post(new Account.UserDetailsUpdatedEvent(user));
            }
        },4000,5000);
    }
    @Subscribe
    public void changePassword(Account.ChangePasswordRequest request){
        Account.ChangeAvatarResponse response = new Account.ChangeAvatarResponse();

        if(!request.NewPassword.equals(request.ConfirmNewPassword))
            response.setPropertyErrors("confirmNewPassword", "Password must match!");
        if(request.NewPassword.length() < 3) {
            response.setPropertyErrors("newPassword", "Password must be larger than 3 characters");
        }
        postDelayed(response);
    }
    @Subscribe
    public void loginWithUsername( Account.LoginWithUsernameRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithUsernameResponse response = new Account.LoginWithUsernameResponse();

                loginUser(new Account.UserResponse());
                bus.post(response);
            }
        },1000,2000);
    }
    @Subscribe
    public void loginWithExternalToken(Account.LoginWithUsernameRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithExternalTokenResponse response = new Account.LoginWithExternalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        },1000,2000);
    }

    @Subscribe
    public void register (Account.RegisterRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.RegisterResponse response = new Account.RegisterResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }
    @Subscribe
    public void externalRegister(Account.RegisterWithExternalTokenRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.RegisterResponse response = new Account.RegisterResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void loginWithLocalToken(Account.LoginWithLocalTokenRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithLocalTokenResponse response = new Account.LoginWithLocalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        },1000,2000);
    }

    @Subscribe
    public void updateGcmRegistration(Account.UpdateGcmRegistrationRequest request){
        postDelayed(new Account.UpdateGcmRegistrationResponse());
    }

    private void loginUser(Account.UserResponse response){
        Auth auth = application.getAuth();
        User user = auth.getUser();

        user.setDisplayName("Karol");
        user.setUserName("Karol Bosak");
        user.setEmail("karolbosak@gmail.com");
        user.setAvatarUrl("http://www.gravatar.com/avatar/1?d=identicon");
        user.setId(123);
        user.setLoggedIn(true);

        bus.post(new Account.UserDetailsUpdatedEvent(user));

        auth.setAuthToken("fakeauthtoken");

        response.DisplayName = user.getDisplayName();
        response.Username = user.getUserName();
        response.Email = user.getEmail();
        response.AvatarUrl = user.getAvatarUrl();
        response.Id = user.getId();
        response.AuthToken = auth.getAuthToken();
    }
}
