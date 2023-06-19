package com.akash.emergency.service.intf;

import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.dto.location.Location;

public interface EmergencyService {
    ThreeWordAddress coordTo3wa(Location location);

    Location _3waToCoord(ThreeWordAddress threeWordAddress);

    ThreeWordAddress _3waLanguageConvert(ThreeWordAddress word, String language);
}
