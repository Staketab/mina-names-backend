package com.staketab.minanames.repository.projection;

public interface ActivityProjection {

    String getId();

    String getDetails();

    String getDomainName();

    String getOwnerAddress();

    Long getTimestamp();

    String getTxHash();

    String getStatus();

    String getDomainId();
}
