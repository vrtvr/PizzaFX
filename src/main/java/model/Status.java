package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor             // konstruktor Status(String statusName) { }
public enum Status {
    NEW("nowe zamówienie"),
    IN_PROGRESS("w realizacji"),
    DONE("dostarczone");

    private String statusName;

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
