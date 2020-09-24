package com.alto;

import com.alto.model.response.ShiftResponse;

import java.util.ArrayList;
import java.util.List;

public class OpenShiftCSSRepository {

    private static OpenShiftCSSRepository mInstance;
    private ArrayList<ShiftResponse> list = null;

    public static OpenShiftCSSRepository getInstance() {
        if(mInstance == null)
            mInstance = new OpenShiftCSSRepository();

        return mInstance;
    }

    private OpenShiftCSSRepository() {
        list = new ArrayList<ShiftResponse>();
    }
    // retrieve array from anywhere
    public ArrayList<ShiftResponse> getArray() {
        return (ArrayList<ShiftResponse>) this.list.clone();
    }
    //Add element to array
    public void addToArray(ShiftResponse value) {
        list.add(value);
    }

    //Add All elements to array
    public void addAllToArray(List<ShiftResponse> value) {
        list.clear();
        list.addAll(value);
    }

    //Add All elements to array
    public void clear() {
        list.clear();
    }
}
