package com.cosmosboard.fmh.dto.response.company;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.car.CarResponse;
import com.cosmosboard.fmh.dto.response.category.CategoryResponse;
import com.cosmosboard.fmh.dto.response.employee.EmployeeResponse;
import com.cosmosboard.fmh.dto.response.location.LocationResponse;
import com.cosmosboard.fmh.dto.response.subscription.SubscriptionResponse;
import com.cosmosboard.fmh.entity.Company;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class CompanyResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of Company", name = "name", type = "String")
    private String name;

    @Schema(example = "Lorem", description = "Description of Company", name = "description", type = "String")
    private String description;

    @ArraySchema(schema = @Schema(description = "Locations of Company", name = "locations", type = "Set"))
    private List<LocationResponse> locations;

    @Schema(name = "employees", description = "Employees of Company", type = "List")
    private List<EmployeeResponse> employees;

    @Schema(name = "category", description = "Category of Company", type = "CategoryResponse")
    private CategoryResponse category;

    @Schema(name = "cars", description = "cars of Company", type = "List")
    private List<CarResponse> cars;

    @Schema(name = "fmsCompanyId", description = "FMS Company ID", type = "String")
    private String fmsCompanyId;

    @Schema(name = "subscription", description = "Subscription of Company", type = "SubscriptionResponse")
    private SubscriptionResponse subscription;

    @Schema(example = "WAITING", description = "Status of Company", name = "status", type = "String")
    private String status;

    @Schema(example = "lorem.jpg", description = "Avatar of the user", name = "avatar", type = "String", nullable = true)
    private String avatar;

    @Schema(example = "banner.jpg", description = "Banner of the company", name = "banner", type = "String", nullable = true)
    private String banner;

    @Schema(example = "false", description = "Is company a dealer with car upload permissions", name = "isDealer", type = "Boolean")
    private Boolean isDealer;

    public static CompanyResponse convert(final Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .fmsCompanyId(company.getFmsCompanyId())
                .status(company.getStatus().name())
                .category(CategoryResponse.convert(company.getCategory()))
                .avatar(company.getAvatar())
                .banner(company.getBanner())
                .isDealer(company.getIsDealer())
            .build();
    }

    public static CompanyResponse convert(final Company company,
                                          final boolean showLocations,
                                          final boolean showEmployees,
                                          final boolean showCars) {
        final CompanyResponse response = convert(company);
        if (showLocations)
            response.setLocations(company.getLocations().stream().map(LocationResponse::convert).toList());
        if (showEmployees)
            response.setEmployees(company.getEmployees().stream().map(e -> EmployeeResponse.convert(e, false)).toList());
        if (showCars)
            response.setCars(company.getCars().stream().map(CarResponse::convert).toList());
        return response;
    }

    public static CompanyResponse convert(final Company company,
                                          final boolean showLocations,
                                          final boolean showEmployees,
                                          final boolean showCars,
                                          final boolean showSubscription) {
        final CompanyResponse response = convert(company, showLocations, showEmployees, showCars);

        if (showSubscription && company.getSubscription() != null)
            response.setSubscription(SubscriptionResponse.convert(company.getSubscription()));

        return response;
    }

    public static CompanyResponse convert(final Company company, final boolean isRelation) {
        if (isRelation)
            return convert(company, true, true, true);
        return convert(company);
    }
}
