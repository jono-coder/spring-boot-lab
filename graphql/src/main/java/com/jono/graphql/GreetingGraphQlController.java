package com.jono.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@PreAuthorize("hasAuthority('ROLE_USER')")
public class GreetingGraphQlController {

    @QueryMapping  // Maps to the 'greeting' field in the schema
    public String greeting(@Argument("name") final String name) {
        return "Hello, " + name + "!";
    }

}
