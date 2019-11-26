package com.kamitsoft.ecosante.client.user.subscription.order;

public class RedirectResponse {
    public String response_code;
    public String response_text;
    public String description;
    public String token;

    @Override
    public String toString() {
        return "url :"+response_text;
    }
}
