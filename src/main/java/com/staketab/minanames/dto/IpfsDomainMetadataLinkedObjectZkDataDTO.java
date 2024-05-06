package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IpfsDomainMetadataLinkedObjectZkDataDTO {
    private String type;
    private String fileMerkleTreeRoot;
    private int merkleTreeHeight;
    private int size;
    private String mimeType;
    private String SHA3_512;
    private String filename;
    private String storage;
    private String fileType;
    private String metadata;
    private String text;

}