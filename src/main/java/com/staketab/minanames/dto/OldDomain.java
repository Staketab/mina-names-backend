package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OldDomain {
    private String name;
    private String address;
    private int expiry;
    private String metadata;
}
