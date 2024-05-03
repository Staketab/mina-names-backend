package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OldMetadataDTO {
    private String domainMetadata;
    private String ipfsImg;
    private String website;
    private String email;
    private String discord;
    private String github;
    private String xTwitter;
    private String telegram;
    private String description;
}
