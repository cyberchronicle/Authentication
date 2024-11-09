package org.cyberchronicle.auth;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid password")
public class InvalidPasswordException extends RuntimeException {
}
