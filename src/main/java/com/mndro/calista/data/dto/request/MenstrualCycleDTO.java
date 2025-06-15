package com.mndro.calista.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenstrualCycleDTO {
    private String start_date;
    private int durasi;
    private int hari_max_volume;
    private int panjang_siklus;
}
