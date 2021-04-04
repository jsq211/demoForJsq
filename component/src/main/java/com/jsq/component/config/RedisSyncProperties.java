package com.jsq.component.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jsq
 * created on 2021/4/4
 **/
@Getter
@Setter
public class RedisSyncProperties {
    private int database = 7;
    private String url;
    private String host = "localhost";
    private String password;
    private int port = 6379;
}
