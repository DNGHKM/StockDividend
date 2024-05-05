package zerobase.stockDividends.exception.impl;

import org.springframework.http.HttpStatus;
import zerobase.stockDividends.exception.AbstractException;

public class AlreadyExAlreadyExistUserException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return ("이미 존재하는 사용자명입니다.");
    }
}
