package com.jsq.component.dto;

import lombok.Data;

/**
 * 数据同步
 * @author jsq
 */
@Data
public class DataSyncDTO {
    private String tableName;
    private String databaseName;
    private Long id;
}
