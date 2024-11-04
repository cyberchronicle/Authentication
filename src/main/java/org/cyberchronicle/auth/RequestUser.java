package org.cyberchronicle.auth;

import java.util.List;

public record RequestUser(String login, List<String> authorities) {
}
