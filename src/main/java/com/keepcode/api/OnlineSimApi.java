package com.keepcode.api;

import com.keepcode.entity.AvailableNumber;
import com.keepcode.entity.Country;

import java.util.List;
import java.util.Map;

public interface OnlineSimApi {
    List<Country> fetchCountries();
    Map<Country, List<AvailableNumber>> fetchAvailableNumbers(List<Country> countries);
    List<AvailableNumber> fetchAvailableNumbers(int countryId);
}
