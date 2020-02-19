package com.bfwg.rest;

import com.bfwg.model.*;
import com.bfwg.service.AppUserService;
import com.bfwg.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by fan.jin on 2017-05-08.
 */

@RestController
@CrossOrigin
@RequestMapping( value = "/api/mobile", produces = MediaType.APPLICATION_JSON_VALUE )
public class PublicController {

    @Autowired
    ShiftService shiftService;
    @Autowired
    AppUserService appUserService;

    @RequestMapping( method = GET, value= "/foo")
    public Map<String, String> getFoo() {
        Map<String, String> fooObj = new HashMap<>();
        fooObj.put("foo", "bar");
        return fooObj;
    }

    @RequestMapping( method = POST, value= "/shift")
    public Shift postShift(@RequestBody ShiftRequest request) {

        return shiftService.addShift(request);
    }

    @RequestMapping( method = PATCH, value= "/shift")
    public Shift patchShift(@RequestBody ShiftRequest request) {

        return shiftService.updateShift(request);
    }

    @RequestMapping( method = POST, value= "/login")
    public AppUser postLogin(@RequestBody AppUserRequest request) {

        return appUserService.save(request);
    }

}
