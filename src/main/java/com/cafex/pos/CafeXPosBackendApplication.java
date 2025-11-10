package com.cafex.pos;

import com.cafex.pos.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CafeXPosBackendApplication {

	@Autowired
	private SystemSettingsService systemSettingsService;

	public static void main(String[] args) {
		SpringApplication.run(CafeXPosBackendApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initializeSystemSettings() {
		systemSettingsService.initializeDefaultSettings();
	}

}