package com.staketab.minanames.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IpfsDomainMetadataZkDataDTO {
    private String name;
    private String address;
    private String ipfs;
    private String expiry;
    private String uri;
    private String url;
    private IpfsDomainMetadataNftZkDataDTO nft;
}
