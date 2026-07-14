package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListaBoleiaResponse {

    private final List<BoleiaResponse> boleias;
    private final Integer pagina;
    private final Integer totalPaginas;
    private final Long totalRegistos;
}