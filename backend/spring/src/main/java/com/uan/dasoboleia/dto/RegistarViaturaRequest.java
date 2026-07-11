package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistarViaturaRequest {
    
    @NotBlank(message = "O nome/marca é obrigatório")
    private String nome;

    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;

    @NotBlank(message = "A mátricula á obrigado")
    @Pattern(regexp = "^[A-Z]{3}-\\d{2}-\\d{2}-[A-Z]{2}$",
         message = "Matrícula inválida. Formato esperado: LDA-00-00-ZZ")
    private String matricula;

    @NotNull(message = "A capacidade é obrigatória")
    @Min(value = 2, message = "A capacidade mínima é 2 lugares")
    @Max(value = 20, message = "A capacidade máxima é 20 lugares")
    private Integer capacidade;
}
