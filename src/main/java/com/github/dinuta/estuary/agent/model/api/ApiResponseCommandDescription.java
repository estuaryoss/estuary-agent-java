package com.github.dinuta.estuary.agent.model.api;

public class ApiResponseCommandDescription extends ApiResponse {

    @Override
    public ApiResponse description(Object description) {
        return super.description(description);
    }

    @Override
    public CommandDescription getDescription() {
        return (CommandDescription) super.getDescription();
    }

    @Override
    public void setDescription(Object description) {
        super.setDescription(description);
    }
}

