package com.keepcode.schedule;

import com.keepcode.api.OnlineSimApi;
import com.keepcode.entity.AvailableNumber;
import com.keepcode.entity.Country;
import com.keepcode.state.AppState;
import com.keepcode.state.AppStateHolder;
import com.keepcode.storage.InMemoryMapListStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PhoneNumberSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(PhoneNumberSyncScheduler.class);

    private final OnlineSimApi onlineSimApi;
    private final InMemoryMapListStorage<Country, AvailableNumber> inMemoryStorage;
    private final AppStateHolder stateHolder;

    @Autowired
    public PhoneNumberSyncScheduler(
            OnlineSimApi onlineSimApi,
            InMemoryMapListStorage<Country, AvailableNumber> inMemoryStorage,
            AppStateHolder stateHolder
    ) {
        this.onlineSimApi = onlineSimApi;
        this.inMemoryStorage = inMemoryStorage;
        this.stateHolder = stateHolder;
    }

    @Scheduled(fixedRateString = "${phone.number.sync.scheduled.task.interval}")
    public void updateStoredPhoneNumbers() {
        log.info("Start scheduled storage update");
        stateHolder.updateAppState(AppState.LOADING);

        List<Country> countries = onlineSimApi.fetchCountries();
        log.info("Found {} countries", countries.size());

        Map<Country, List<AvailableNumber>> availableNumbersByCountry = onlineSimApi.fetchAvailableNumbers(countries);
        inMemoryStorage.clearAndReplaceStoredData(availableNumbersByCountry);

        stateHolder.updateAppState(AppState.DATA_AVAILABLE);
        log.info("Finish scheduled storage update");
    }
}
