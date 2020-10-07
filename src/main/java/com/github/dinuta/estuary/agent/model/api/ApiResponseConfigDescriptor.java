package com.github.dinuta.estuary.agent.model.api;

import com.github.dinuta.estuary.agent.model.ConfigDescriptor;

public class ApiResponseConfigDescriptor extends ApiResponse {

    @Override
    public ApiResponse description(Object description) {
        return super.description(description);
    }

    @Override
    public ConfigDescriptor getDescription() {
        return (ConfigDescriptor) super.getDescription();
    }

    @Override
    public void setDescription(Object description) {
        super.setDescription(description);
    }
}

