package com.github.magicalmuggle.blog.entity;

public abstract class Result<T> {
    public enum ResultStatus {
        OK("ok"), FAIL("fail");

        private final String status;

        ResultStatus(String status) {
            this.status = status;
        }
    }

    final ResultStatus status;
    final String msg;
    final T data;


    protected Result(ResultStatus status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public String getStatus() {
        return status.status;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
