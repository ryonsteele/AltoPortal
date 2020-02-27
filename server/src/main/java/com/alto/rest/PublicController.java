package com.alto.rest;

import com.alto.model.AppUser;
import com.alto.model.AppUserRequest;
import com.alto.model.Shift;
import com.alto.model.ShiftRequest;
import com.alto.model.*;
import com.alto.service.AppUserService;
import com.alto.service.ShiftBoardService;
import com.alto.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
@CrossOrigin
@RequestMapping( value = "/api/mobile", produces = MediaType.APPLICATION_JSON_VALUE )
public class PublicController {

    @Autowired
    ShiftService shiftService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    ShiftBoardService shiftBoardService;

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

    @RequestMapping( method = POST, value= "/openshift")
    public ShiftBoardRecord postInterest(@RequestBody InterestRequest request) {

        return shiftBoardService.saveRecord(request);
    }

    @RequestMapping( method = GET, value= "/openshift/{orderid}/{tempid}")
    public ShiftBoardRecord getOpenShift(@PathVariable String orderid, @PathVariable String tempid) {

        return shiftBoardService.getRecord(orderid, tempid);
    }

    @RequestMapping( method = GET, value= "/shift/{orderid}")
    public Shift getShift(@PathVariable String orderid) {

        return shiftService.getShift(orderid);
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
