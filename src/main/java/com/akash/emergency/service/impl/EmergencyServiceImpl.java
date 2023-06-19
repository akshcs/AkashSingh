package com.akash.emergency.service.impl;

import com.akash.emergency.dto.autoSuggest.EmergencySuggestion;
import com.akash.emergency.dto.location.Location;
import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.exception.exceptionClass.InvalidLanguageCodeException;
import com.akash.emergency.exception.exceptionClass.LocationAutoSuggestException;
import com.akash.emergency.exception.exceptionClass.LocationUnserviceableException;
import com.akash.emergency.exception.exceptionClass.UnableToFind3waException;
import com.akash.emergency.service.intf.EmergencyService;
import com.what3words.javawrapper.What3WordsV3;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.ConvertTo3WA;
import com.what3words.javawrapper.response.ConvertToCoordinates;
import com.what3words.javawrapper.response.Suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmergencyServiceImpl implements EmergencyService {
    private final What3WordsV3 what3WordsV3;
    private final String defaultLanguage;
    private final List<String> allowedCountries;
    private final String serviceableCountryResponse;
    private final int numberOfSuggestionsPerCountry;

    public EmergencyServiceImpl(String defaultLanguage, List<String> allowedCountries, int numberOfSuggestionsPerCountry,
                                What3WordsV3 what3WordsV3){
        this.what3WordsV3 = what3WordsV3;
        this.defaultLanguage = defaultLanguage;
        this.allowedCountries = allowedCountries;
        this.numberOfSuggestionsPerCountry = numberOfSuggestionsPerCountry;
        this.serviceableCountryResponse = getServiceableCountryResponse();
    }
    @Override
    public ThreeWordAddress coordTo3wa(Location location) {
        try{
            return getThreeWordsFromLocation(location);
        } catch (LocationUnserviceableException ex){
            throw ex;
        } catch (Exception ex) {
            throw new UnableToFind3waException(ex.getMessage(), ex);
        }
    }

    @Override
    public Location _3waToCoord(ThreeWordAddress threeWordAddress) {
        return getLocationFromThreeWords(threeWordAddress);
    }

    @Override
    public ThreeWordAddress _3waLanguageConvert(ThreeWordAddress threeWordAddress, String language) {
        Location location = getLocationFromThreeWords(threeWordAddress);
        return getThreeWordsFromLocationAndLanguage(location, language);
    }

    private ThreeWordAddress getThreeWordsFromLocation(Location location){
        ConvertTo3WA convertTo3WA = what3WordsV3.convertTo3wa(new Coordinates(location.getLat(), location.getLng())).language(defaultLanguage).execute();
        if(isPresentInAllowedCountries(convertTo3WA.getCountry())) {
            String threeWords = convertTo3WA.getWords();
            return new ThreeWordAddress(threeWords);
        } else {
            throw new LocationUnserviceableException(serviceableCountryResponse);
        }
    }

    private ThreeWordAddress getThreeWordsFromLocationAndLanguage(Location location, String targetLanguage){
        String threeWordAddress = what3WordsV3.convertTo3wa(new Coordinates(location.getLat(), location.getLng())).
                language(targetLanguage).execute().getWords();
        if(Objects.isNull(threeWordAddress) || threeWordAddress.isBlank() || threeWordAddress.isEmpty()){
            throw new InvalidLanguageCodeException(targetLanguage);
        } else {
            return new ThreeWordAddress(threeWordAddress);
        }
    }

    private Location getLocationFromThreeWords(ThreeWordAddress threeWordAddress){
        ConvertToCoordinates convertToCoordinates = what3WordsV3.convertToCoordinates(threeWordAddress.getThreeWordAddress()).
                execute();
        if(isPresentInAllowedCountries(convertToCoordinates.getCountry())) {
            return new Location(convertToCoordinates.getCoordinates().getLat(),
                    convertToCoordinates.getCoordinates().getLng());
        } else {
            throw new LocationAutoSuggestException(threeWordAddress.getThreeWordAddress(),
                    getSuggestions(threeWordAddress));
        }
    }

    private boolean isPresentInAllowedCountries(String country){
        return allowedCountries.contains(country);
    }

    private String getServiceableCountryResponse(){
        StringBuilder countryResponse = new StringBuilder("Serviceable countries with " +
                "ISO 3166-1 alpha-2 codes are : ");
        int count = 1;
        for(String country: allowedCountries){
            countryResponse.append(country);
            if(count < allowedCountries.size()){
                count++;
                countryResponse.append(" , ");
            }
        }
        return countryResponse.toString();
    }

    private List<EmergencySuggestion> getSuggestions(ThreeWordAddress threeWordAddress){
        List<EmergencySuggestion> suggestions = new ArrayList<>();
        for(String country: allowedCountries){
            List<Suggestion> suggestionListByCountry = what3WordsV3.autosuggest(threeWordAddress.getThreeWordAddress()).
                    clipToCountry(country).execute().getSuggestions();
            updateSuggestions(suggestions, suggestionListByCountry);
        }
        return suggestions;
    }

    private void updateSuggestions(List<EmergencySuggestion> suggestions, List<Suggestion> suggestionListByCountry){
        int count = 0;
        for(Suggestion suggestion: suggestionListByCountry){
            suggestions.add(new EmergencySuggestion(suggestion.getCountry(),
                    suggestion.getNearestPlace(), suggestion.getWords()));
            count++;
            if(count>=numberOfSuggestionsPerCountry){
                break;
            }
        }
    }
}
