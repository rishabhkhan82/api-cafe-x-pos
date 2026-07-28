package com.cafex.pos.event;

import org.springframework.context.ApplicationEvent;

public class DashboardRefreshEvent extends ApplicationEvent {

    public DashboardRefreshEvent(Object source) {
        super(source);
    }
}
