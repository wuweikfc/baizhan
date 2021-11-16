package com.bjsxt.commons.exception;

/**
 * 数据库异常
 */
public class DaoException extends RuntimeException {

    public DaoException(String message) {

        super(message);
    }

}
