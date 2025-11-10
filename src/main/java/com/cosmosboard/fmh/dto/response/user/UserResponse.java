package com.cosmosboard.fmh.dto.response.user;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.address.AddressResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import com.cosmosboard.fmh.entity.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "lorem@ipsum.com", description = "Email of the user", name = "email", type = "String")
    private String email;

    @Schema(example = "Dr.", description = "Title of the user", name = "name", type = "String", nullable = true)
    private String title;

    @Schema(example = "John", description = "Name of the user", name = "name", type = "String")
    private String name;

    @Schema(example = "DOE", description = "Lastname of the user", name = "lastName", type = "String")
    private String lastName;

    @Schema(example = "+905321234567", description = "GSM of the user", name = "gsm", type = "String", nullable = true)
    private String gsm;

    @Schema(example = "lorem.jpg", description = "Avatar of the user", name = "avatar", type = "String", nullable = true)
    private String avatar;

    @ArraySchema(schema = @Schema(example = "USER", description = "role of the user", required = true, name = "roles", type = "String"))
    private List<String> roles;

    @ArraySchema(schema = @Schema(description = "Addresses of the user", name = "companies", type = "AddressResponse"))
    private List<AddressResponse> addresses;

    @ArraySchema(schema = @Schema(description = "companies of the user", name = "companies", type = "UserCompanyResponse"))
    private List<UserCompanyResponse> companies;

    @Schema(example = "2022-09-29T22:42:14", description = "Email activation time", name = "emailActivatedAt", type = "LocalDateTime", nullable = true)
    private Long emailActivatedAt;

    @Schema(example = "2022-09-29T22:42:14", description = "Gsm activation time", name = "emailActivatedAt", type = "LocalDateTime", nullable = true)
    private Long gsmActivatedAt;

    @Schema(example = "2022-09-29T22:42:14", description = "Blocked time", name = "blockedAt", type = "Long", nullable = true)
    private Long blockedAt;

    @Schema(example = "2022-09-29T22:37:31", description = "Created time", name = "createdAt", type = "Long")
    private Long createdAt;

    @Schema(example = "2022-09-29T22:37:31", description = "Updated time", name = "updatedAt", type = "Long")
    private Long updatedAt;

    @Schema(example = "true", description = "Whether the user has an AutoCheck account or not")
    private boolean hasAutoCheck;

    private String mailCode;

    private String smsCode;

    public static UserResponse convert(final User user) {
        return convert(user, true);
    }

    public static UserResponse convert(final User user, final boolean showCompany) {
        if (user == null)
            return null;
        final UserResponse build = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .title(user.getTitle())
                .name(user.getName())
                .lastName(user.getLastName())
                .gsm(user.getGsm())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).toList())
                .emailActivatedAt(user.getEmailActivatedAt() != null ? user.getEmailActivatedAt().toInstant(ZoneOffset.UTC).toEpochMilli(): null)
                .gsmActivatedAt(user.getGsmActivatedAt() != null ? user.getGsmActivatedAt().toInstant(ZoneOffset.UTC).toEpochMilli(): null)
                .blockedAt(user.getBlockedAt() != null ? user.getBlockedAt().toInstant(ZoneOffset.UTC).toEpochMilli(): null)
                .createdAt(user.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                .updatedAt(user.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                .addresses(user.getAddresses().stream().map(a -> AddressResponse.convert(a, false)).toList())
                .hasAutoCheck(user.isHasAutoCheck())
                .build();
        if (showCompany) {
            List<UserCompanyResponse> companies = user.getEmployees().stream()
                    .map(e -> {
                        CompanyResponse company = CompanyResponse.convert(e.getCompany(), false,
                                false, false, true);
                        return UserCompanyResponse.convert(company, e.isOwner());
                    })
                    .toList();
            build.setCompanies(companies);
        }
        return build;
    }

    public static UserResponse convertUpdated(final User user) {
        if (user == null)
            return null;
        final UserResponse convert = convert(user, true);
        convert.setTitle(null);
        convert.setGsm(null);
        convert.setAvatar(null);
        convert.setRoles(null);
        convert.setEmailActivatedAt(null);
        convert.setGsmActivatedAt(null);
        convert.setUpdatedAt(null);
        convert.setBlockedAt(null);
        convert.setCreatedAt(null);
        convert.setCompanies(null);
        return convert;
    }
}
