package com.doitteam.foodstore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String address;
}