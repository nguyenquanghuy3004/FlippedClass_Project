package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferLeaderRequest {
    @NotNull
    private Long newLeaderId;
}
