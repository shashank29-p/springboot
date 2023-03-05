package com.example.demo.response;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class Response {
    String message;
    HttpStatus reponseCode;

    public String getContentType() {
        return contentType;
    }

    public Response(String message, HttpStatus reponseCode, String contentType) {
        this.message = message;
        this.reponseCode = reponseCode;
        this.contentType = contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    String contentType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getReponseCode() {
        return reponseCode;
    }

    public void setReponseCode(HttpStatus reponseCode) {
        this.reponseCode = reponseCode;
    }

    @Override
    public String toString() {
        return "response{" +
                "message='" + message + '\'' +
                ", reponseCode=" + reponseCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return reponseCode == response.reponseCode && Objects.equals(message, response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, reponseCode);
    }
}
