package io.ten1010.coaster.auth.web.login.v1;

import io.ten1010.coaster.auth.web.UriConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping(UriConstants.APIS + UriConstants.API_LOGIN_V1 + LoginUriConstants.RES_LOGIN)
    public String getLogin() {
        return "login";
    }

}
