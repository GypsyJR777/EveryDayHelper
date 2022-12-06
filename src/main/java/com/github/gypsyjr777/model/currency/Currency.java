package com.github.gypsyjr777.model.currency;

public class Currency {
    private Integer error;
    private String error_message;
    private Double amount;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "error=" + error +
                ", errorMessage='" + error_message + '\'' +
                ", amount=" + amount +
                '}';
    }
}
