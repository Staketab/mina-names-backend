package com.staketab.minanames.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IpfsDomainMetadataNftZkDataDTO {
    private String name;
    private String address;
    private String image;
    private long time;
    private IpfsDomainMetadataNftMetadataZkDataDTO metadata;
    private Map<String, IpfsDomainMetadataNftMetadataZkDataDTO> properties;
}