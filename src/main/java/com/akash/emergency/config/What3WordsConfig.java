package com.akash.emergency.config;

import com.akash.emergency.service.impl.EmergencyServiceImpl;
import com.akash.emergency.service.intf.EmergencyService;
import com.what3words.javawrapper.What3WordsV3;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "what3words")
@PropertySource("classpath:application.yaml")
//@EnableConfigurationProperties
@Profile("!test")
@Data
public class What3WordsConfig {
    private String apiKey;
    private String language;
    private List<String> allowedCountries;
    private int numberOfSuggestionsPerCountry;
    @Bean
    public What3WordsV3 what3WordsV3(){
        return new What3WordsV3(apiKey);
    }

    @Bean
    public EmergencyService emergencyService(){
        return new EmergencyServiceImpl(language, allowedCountries, numberOfSuggestionsPerCountry, what3WordsV3());
    }
}
