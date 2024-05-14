package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IpfsDomainMetadataNftMetadataZkDataDTO {
    private String data;
    private String kind;
    private IpfsDomainMetadataLinkedObjectZkDataDTO linkedObject;
}