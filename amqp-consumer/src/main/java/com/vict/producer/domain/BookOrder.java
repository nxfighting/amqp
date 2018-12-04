package com.vict.producer.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author
 * @create 2018-12-04 15:11
 */
@Data
public class BookOrder implements Serializable {
    private String orderId;
    private String orderName;
    private LocalDateTime bookedTime;
}
