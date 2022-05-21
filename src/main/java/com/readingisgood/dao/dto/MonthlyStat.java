package com.readingisgood.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyStat {
    private Integer year;
    private Integer month;
    private Short status;
    private Number count;
}