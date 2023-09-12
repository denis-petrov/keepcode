package com.keepcode.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AppStateHolder {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AppStateHolder(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    private AppState appState = AppState.LOADING;

    public synchronized void updateAppState(AppState updatedState) {
        appState = updatedState;
        eventPublisher.publishEvent(new AppStateChangeEvent(this, updatedState));
    }

    public synchronized AppState getCurrentState() {
        return appState;
    }
}
