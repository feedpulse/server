package dev.feder.exceptions;

public class UserNotFoundInDbException extends BaseException{
    public UserNotFoundInDbException(String findBy) {
        super("User not found in db by [" + findBy + "]");
    }
}
