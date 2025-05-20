package com.ptu.resume.core.exception;

/**
 * 业务异常
 *
 * @author PTU开发团队
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(code, message);
    }
} 