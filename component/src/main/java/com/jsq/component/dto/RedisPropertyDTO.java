package com.jsq.component.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jsq
 * created on 2021/4/8
 **/
@Data
@AllArgsConstructor
public class RedisPropertyDTO {
    private String fieldName;
    private String outPutKey;
    private String redisKey;
}
