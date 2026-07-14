package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalizacaoMessage {

    private Long idBoleia;
    private Double latitude;
    private Double longitude;
    private String timestamp;
}