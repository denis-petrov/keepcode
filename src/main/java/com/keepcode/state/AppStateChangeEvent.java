package com.keepcode.state;

import org.springframework.context.ApplicationEvent;

public class AppStateChangeEvent extends ApplicationEvent {

    private final AppState newState;

    public AppStateChangeEvent(Object source, AppState newState) {
        super(source);
        this.newState = newState;
    }

    public AppState getNewState() {
        return newState;
    }
}
