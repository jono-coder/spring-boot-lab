package com.jono.restapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Value("${customInfo.appName}")
    private String appName;

    @Value("${customInfo.maintainer}")
    private String maintainer;

    @Value("${customInfo.description}")
    private String description;

    protected CustomInfoContributor() {
    }

    @Override
    public void contribute(final Info.Builder builder) {
        builder.withDetail("app", Map.of(
                "name", appName,
                "maintainer", maintainer,
                "description", description
        ));
    }

}
