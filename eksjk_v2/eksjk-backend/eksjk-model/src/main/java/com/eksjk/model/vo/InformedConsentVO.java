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

    private String pdfFileName;

    private String pdfPath;

    private String signStatus;

    private String content;

    private String patientSignImage;

    private String guardianSignImage;

    private String doctorSignImage;

    private LocalDateTime patientSignTime;

    private LocalDateTime guardianSignTime;

    private LocalDateTime doctorSignTime;
}
