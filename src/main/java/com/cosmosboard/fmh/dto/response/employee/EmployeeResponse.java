package com.cosmosboard.fmh.dto.response.employee;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Offer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class EmployeeResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(name = "user", description = "User of Employee", type = "UserResponse")
    private UserResponse user;

    @Schema(name = "company", description = "Company of Location", type = "CompanyResponse")
    private CompanyResponse company;

    @Schema(name = "isOwner", description = "Is owner or not", type = "Boolean")
    private boolean isOwner;

    @Schema(name = "offers", description = "List of Offer IDs associated with the employee", type = "List<String>")
    private List<String> offers;

    public static EmployeeResponse convert(final Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .isOwner(employee.isOwner())
                .offers(employee.getOffers().stream().map(Offer::getId).toList())
                .build();
    }

    public static EmployeeResponse convert(final Employee employee, final boolean isRelation) {
        final EmployeeResponse employeeResponse = convert(employee);
        employeeResponse.setUser(UserResponse.convert(employee.getUser(), isRelation));
        if (isRelation)
            employeeResponse.setCompany(CompanyResponse.convert(employee.getCompany()));
        return employeeResponse;
    }
}
