package com.staketab.minanames.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class Constants {
    //common
    public static final String API_KEY_HEADER = "x-api-key";
    public static final String MINA_DOMAIN = ".mina";

    public static final Long MINA_DENOMINATION = 1_000_000_000L;
    public static final BigDecimal DEFAULT_DENOMINATION = BigDecimal.valueOf(1_000_000_000L);
}
