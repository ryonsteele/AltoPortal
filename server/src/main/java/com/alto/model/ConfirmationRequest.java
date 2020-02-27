package com.alto.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "records"
})
public class ConfirmationRequest {

    @JsonProperty("records")
    private List<ShiftBoardRecord> records = null;

    @JsonProperty("records")
    public List<ShiftBoardRecord> getRecords() {
        return records;
    }

    @JsonProperty("records")
    public void setRecords(List<ShiftBoardRecord> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("records", records).toString();
    }

}
