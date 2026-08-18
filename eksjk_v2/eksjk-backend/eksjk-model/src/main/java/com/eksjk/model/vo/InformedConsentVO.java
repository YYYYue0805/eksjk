package com.eksjk.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知情同意书视图对象
 *
 * @author eksjk
 */
@Data
public class InformedConsentVO {

    private String id;

    private String patientId;

    private String externalFileId;

    private String fileName;

    private LocalDateTime signTime;

    private String patientName;

    private String doctorName;

    private String source;

    private String ossUrl;
}
