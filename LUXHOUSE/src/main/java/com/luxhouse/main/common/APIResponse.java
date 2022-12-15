package com.luxhouse.main.common;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class APIResponse {
    private Integer status;
    private Object data;
    private Object error;

    public APIResponse() {
        this.status = HttpStatus.OK.value();
        this.data = data;
        this.error = error;
    }
}
