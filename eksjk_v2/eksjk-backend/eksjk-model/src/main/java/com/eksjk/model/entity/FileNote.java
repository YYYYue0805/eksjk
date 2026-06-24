package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件备注实体
 *
 * @author eksjk
 */
@Data
@TableName("file_note")
public class FileNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String filePath;

    private String note;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
