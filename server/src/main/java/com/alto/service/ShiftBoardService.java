package com.alto.service;


import com.alto.model.ConfirmationRequest;
import com.alto.model.InterestRequest;
import com.alto.model.ShiftBoardRecord;

import java.util.List;

public interface ShiftBoardService {
    ShiftBoardRecord saveRecord(InterestRequest request);
    ShiftBoardRecord getRecord(String orderid, String tempid);
    List<ShiftBoardRecord> findAll();
    boolean processConfirmation(ConfirmationRequest request);
}
