package com.jakubbosak.udemycourse.yora.services;


import com.jakubbosak.udemycourse.yora.infrastructure.ServiceResponse;
import com.jakubbosak.udemycourse.yora.services.entities.ContactRequest;
import com.jakubbosak.udemycourse.yora.services.entities.UserDetails;

import java.util.List;

public final class Contacts {
    private Contacts(){

    }
    public static class GetContactRequestsRequest{
        public boolean FromUs;

        public GetContactRequestsRequest(boolean fromUs) {
            FromUs = fromUs;
        }

    }
    public static class GetContactRequestsResponse extends ServiceResponse{
        public List<ContactRequest> Requests;
    }
    public static class GetContactsRequest{
        public GetContactsRequest(boolean includePendingContacts) {
            IncludePendingContacts = includePendingContacts;
        }

        public boolean IncludePendingContacts;
    }

    public static class GetContactsResponse extends ServiceResponse{
        public List<UserDetails> Contacts;
    }
    public static class SendContactRequestRequest{
        public int UserId;

        public SendContactRequestRequest(int userId) {
            UserId = userId;
        }
    }

    public static class SendContactRequestResponse extends ServiceResponse{
    }
    public static class RespondToContactRequestRequest{
        public int ContactRequestId;
        public boolean Accept;

        public RespondToContactRequestRequest(int contactRequestId, boolean accept) {
            ContactRequestId = contactRequestId;
            Accept = accept;
        }
    }
    public static class RespondToContactRequestResponse extends ServiceResponse{

    }

    public static class RemoveContactRequest{
        public int ContactId;

        public RemoveContactRequest(int contactId){
            ContactId = contactId;
        }
    }

    public static class RemoveContactResponse extends ServiceResponse{
        public int RemovedContactId;
    }

    public static class SearchUsersRequest {
        public String Query;

        public SearchUsersRequest(String query){
            Query = query;
        }
    }

    public static class SearchUsersResponse extends ServiceResponse{
        public List<UserDetails> Users;
        public String Query;
    }


}
