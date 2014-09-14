package com.t3coode.togg.services.dtos;

public class DataDTO<T extends BaseDTO> extends BaseDTO {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
