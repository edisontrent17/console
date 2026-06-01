package com.acme.data360agent.data360;

public class ConnectApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public ConnectApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int statusCode() {
        return statusCode;
    }

    public String responseBody() {
        return responseBody;
    }
}
