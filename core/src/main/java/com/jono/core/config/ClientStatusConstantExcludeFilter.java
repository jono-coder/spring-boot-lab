package com.jono.core.config;

import com.jono.core.service.constant.ClientStatusConstant;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.support.RegisteredBean;

public class ClientStatusConstantExcludeFilter implements BeanRegistrationExcludeFilter {

    @Override
    public boolean isExcludedFromAotProcessing(final RegisteredBean bean) {
        return bean.getBeanClass().equals(ClientStatusConstant.class);
    }

}
