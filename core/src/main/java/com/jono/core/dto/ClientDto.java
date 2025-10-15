package com.jono.core.dto;

import com.jono.core.IdDesc;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ClientDto {

    public Integer id;
    @NonNull
    @NotBlank
    public String accountNo;
    @NonNull
    @NotBlank
    public String name;
    @NonNull
    public IdDesc status;

}
