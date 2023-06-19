package com.akash.emergency.rest;

import com.akash.emergency.dto.location.Location;
import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.service.intf.EmergencyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class EmergencyResource {
    @Autowired
    private EmergencyService emergencyService;

    public EmergencyResource(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    @PostMapping("coord-to-3wa")
    @ApiOperation(value = "Converts a co-ordinate to three words",
            notes = "Takes a Location Object in Body and returns a three word Object", response = ThreeWordAddress.class)
    public ResponseEntity<ThreeWordAddress> coordTo3wa(@Valid @RequestBody Location location){
        ThreeWordAddress result = emergencyService.coordTo3wa(location);
        return ResponseEntity.ok(result);
    }

    @PostMapping("3wa-to-coord")
    @ApiOperation(value = "Converts a three word address to co-ordinate",
            notes = "Takes a three word address Object in Body and returns a location Object", response = Location.class)
    public ResponseEntity<Location> _3waToCoord(@Valid @RequestBody ThreeWordAddress threeWordAddress){
        Location result = emergencyService._3waToCoord(threeWordAddress);
        return ResponseEntity.ok(result);
    }

    @PostMapping("language-convert")
    @ApiOperation(value = "Converts a three word address from default Language to a specified language",
            notes = "Takes a three word address Object in Body and a language in query parameter and returns a three word address Object", response = ThreeWordAddress.class)
    public ResponseEntity<ThreeWordAddress> _3waLanguageConvert(@RequestParam("target_language") String targetLanguage, @Valid @RequestBody ThreeWordAddress threeWordAddress){
        ThreeWordAddress result = emergencyService._3waLanguageConvert(threeWordAddress, targetLanguage);
        return ResponseEntity.ok(result);
    }
}
