package com.staketab.minanames.dto.request;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

@Data
public class SearchParams {
    private String searchStr;

    public SearchParams() {
    }

    public SearchParams(String searchStr) {
        this.searchStr = Strings.isNotBlank(searchStr) ? searchStr : null;
    }
}
