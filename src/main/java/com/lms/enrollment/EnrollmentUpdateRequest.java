package com.lms.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentUpdateRequest {
    @NotNull
    private boolean accepted;
    private String cancellationReason;
}
