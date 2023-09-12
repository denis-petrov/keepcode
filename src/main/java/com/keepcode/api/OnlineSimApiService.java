package com.keepcode.api;

import com.keepcode.entity.AvailableNumber;
import com.keepcode.entity.Country;
import com.keepcode.mapper.ResponseToEntityMapper;
import com.keepcode.state.AppState;
import com.keepcode.state.AppStateHolder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class OnlineSimApiService implements OnlineSimApi {

    private static final Integer EXECUTOR_THREADS_COUNT = 10;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);

    private final WebClientErrorHandlingService webClientErrorHandlingService;
    private final AppStateHolder appStateHolder;
    private final ResponseToEntityMapper toEntityMapper;
    private WebClient webClient;

    @Value("${onlinesim.api.url}")
    private String onlineSimApiUrl;

    @Value("${onlinesim.api.endpoints.countries-path}")
    private String getCountriesPath;

    @Value("${onlinesim.api.endpoints.available-phones-path}")
    private String getAvailablePhoneNumbersPath;

    @Autowired
    public OnlineSimApiService(
            WebClientErrorHandlingService webClientErrorHandlingService,
            AppStateHolder appStateHolder,
            ResponseToEntityMapper toEntityMapper
    ) {
        this.webClientErrorHandlingService = webClientErrorHandlingService;
        this.appStateHolder = appStateHolder;
        this.toEntityMapper = toEntityMapper;
    }

    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(onlineSimApiUrl)
                .build();
    }

    @Override
    public List<Country> fetchCountries() {
        return webClient.get()
                .uri(getCountriesPath)
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        webClientErrorHandlingService::handleErrorResponse
                )
                .bodyToMono(String.class)
                .map(toEntityMapper::parseCountries)
                .onErrorResume(this::handleAnyError)
                .block();
    }

    @Override
    public Map<Country, List<AvailableNumber>> fetchAvailableNumbers(List<Country> countries) {
        List<CompletableFuture<Map.Entry<Country, List<AvailableNumber>>>> futures = countries.stream()
                .map(country -> CompletableFuture.supplyAsync(() -> {
                    List<AvailableNumber> numbers = fetchAvailableNumbers(country.countryId());
                    return Map.entry(country, numbers);
                }, EXECUTOR))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(ignored ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
        ).join();
    }

    @Override
    public List<AvailableNumber> fetchAvailableNumbers(int countryId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(getAvailablePhoneNumbersPath)
                        .queryParam("country", countryId)
                        .build()
                )
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        webClientErrorHandlingService::handleErrorResponse
                )
                .bodyToMono(String.class)
                .map(toEntityMapper::parsePhoneNumbers)
                .onErrorResume(this::handleAnyError)
                .block();
    }

    private <T> Mono<List<T>> handleAnyError(Throwable ex) {
        return webClientErrorHandlingService.handleAnyErrors(
                ex,
                () -> appStateHolder.updateAppState(AppState.ERROR)
        );
    }
}
