package com.cosmosboard.fmh.factory;

import com.cosmosboard.fmh.dto.request.address.CreateAddressRequest;
import com.cosmosboard.fmh.dto.request.address.UpdateAddressRequest;
import com.cosmosboard.fmh.dto.request.auth.LoginRequest;
import com.cosmosboard.fmh.dto.request.auth.RefreshRequest;
import com.cosmosboard.fmh.dto.request.auth.RegisterRequest;
import com.cosmosboard.fmh.dto.request.car.brand.CreateCarBrandRequest;
import com.cosmosboard.fmh.dto.request.car.brand.UpdateCarBrandRequest;
import com.cosmosboard.fmh.dto.request.category.CreateCategoryRequest;
import com.cosmosboard.fmh.dto.request.category.UpdateCategoryRequest;
import com.cosmosboard.fmh.dto.request.city.CreateCityRequest;
import com.cosmosboard.fmh.dto.request.city.UpdateCityRequest;
import com.cosmosboard.fmh.dto.request.company.CompanyOperationRequest;
import com.cosmosboard.fmh.dto.request.company.CreateCompanyRequest;
import com.cosmosboard.fmh.dto.request.company.UpdateCompanyRequest;
import com.cosmosboard.fmh.dto.request.company.UpdateCompanyWithStatusAndFmsCompany;
import com.cosmosboard.fmh.dto.request.district.CreateDistrictRequest;
import com.cosmosboard.fmh.dto.request.district.UpdateDistrictRequest;
import com.cosmosboard.fmh.dto.request.employee.EmployeeRegisterRequest;
import com.cosmosboard.fmh.dto.request.location.CreateLocationRequest;
import com.cosmosboard.fmh.dto.request.location.UpdateLocationRequest;
import com.cosmosboard.fmh.dto.request.offer.AssignOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CancelOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.ConfirmOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CreateOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.UpdateOfferRequest;
import com.cosmosboard.fmh.dto.request.user.ActivateGsmRequest;
import com.cosmosboard.fmh.dto.request.user.ChangePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.CreateUserRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateAvatarRequest;
import com.cosmosboard.fmh.dto.request.user.UpdatePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateProfileRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateUserRequest;
import com.cosmosboard.fmh.dto.response.auth.TokenResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferStatisticsResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferStatusInfo;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.Address;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.CarBrand;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.entity.EmailActivationToken;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.GsmActivationToken;
import com.cosmosboard.fmh.entity.Location;
import com.cosmosboard.fmh.entity.Message;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.PasswordResetToken;
import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.UserCriteria;
import org.instancio.Instancio;
import org.springframework.mail.javamail.JavaMailSender;

public class Factory {  public static UserInvite createUserInvite () {
    return Instancio.create(UserInvite.class);
}

    public static Offer createOffer () {
        return Instancio.create(Offer.class);
    }

    public static OfferResponse createOfferResponse () {
        return Instancio.create(OfferResponse.class);
    }

    public static Company createCompany () {
        return Instancio.create(Company.class);
    }

    public static User createUser () {
        return  Instancio.create(User.class);
    }

    public static Car createCar () {
        return  Instancio.create(Car.class);
    }

    public static UserCriteria createUserCriteria () {
        return  Instancio.create(UserCriteria.class);
    }

    public static PaginationCriteria createPaginationCriteria () {
        return  Instancio.create(PaginationCriteria.class);
    }

    public static Location createLocation () {
        return Instancio.create(Location.class);
    }

    public static Address createAddress () {
        return Instancio.create(Address.class);
    }

    public static Category createCategory () {
        return Instancio.create(Category.class);
    }

    public static CarBrand createCarBrand () {
        return Instancio.create(CarBrand.class);
    }

    public static City createCity () {
        return Instancio.create(City.class);
    }

    public static District createDistrict () {
        return Instancio.create(District.class);
    }

    public static Role createRole () {
        return Instancio.create(Role.class);
    }

    public static Employee createEmployee () {
        return Instancio.create(Employee.class);
    }

    public static Message createMessage () {
        return Instancio.create(Message.class);
    }

    public static UpdateOfferRequest createUpdateOfferRequest () {
        return Instancio.create(UpdateOfferRequest.class);
    }

    public static UpdateLocationRequest createUpdateLocationRequest () {
        return Instancio.create(UpdateLocationRequest.class);
    }

    public static ConfirmOfferRequest createConfirmOfferRequest () {
        return Instancio.create(ConfirmOfferRequest.class);
    }

    public static CreateLocationRequest createCreateLocationRequest () {
        return Instancio.create(CreateLocationRequest.class);
    }

    public static CreateOfferRequest createCreateOfferRequest () {
        return Instancio.create(CreateOfferRequest.class);
    }

    public static CreateCityRequest createCreateCityRequest () {
        return Instancio.create(CreateCityRequest.class);
    }

    public static CreateUserRequest createCreateUserRequest () {
        return Instancio.create(CreateUserRequest.class);
    }

    public static CreateCompanyRequest createCreateCompanyRequest () {
        return Instancio.create(CreateCompanyRequest.class);
    }

    public static CreateCarBrandRequest createCreateCarBrandRequest () {
        return Instancio.create(CreateCarBrandRequest.class);
    }

    public static UpdateCarBrandRequest createUpdateCarBrandRequest () {
        return Instancio.create(UpdateCarBrandRequest.class);
    }

    public static CreateCategoryRequest createCreateCategoryRequest () {
        return Instancio.create(CreateCategoryRequest.class);
    }

    public static CreateDistrictRequest createCreateDistrictRequest () {
        return Instancio.create(CreateDistrictRequest.class);
    }

    public static CreateAddressRequest createCreateAddressRequest () {
        return Instancio.create(CreateAddressRequest.class);
    }

    public static UpdateCategoryRequest createUpdateCategoryRequest () {
        return Instancio.create(UpdateCategoryRequest.class);
    }

    public static AssignOfferRequest createAssignOfferRequest () {
        return Instancio.create(AssignOfferRequest.class);
    }

    public static CompanyOperationRequest createCompanyOperationRequest () {
        return Instancio.create(CompanyOperationRequest.class);
    }

    public static UpdateCityRequest createUpdateCityRequest () {
        return Instancio.create(UpdateCityRequest.class);
    }

    public static UpdateDistrictRequest createUpdateDistrictRequest () {
        return Instancio.create(UpdateDistrictRequest.class);
    }

    public static UpdateCompanyRequest createUpdateCompanyRequest () {
        return Instancio.create(UpdateCompanyRequest.class);
    }

    public static CancelOfferRequest createCancelOfferRequest () {
        return Instancio.create(CancelOfferRequest.class);
    }

    public static UpdateAddressRequest createUpdateAddressRequest () {
        return Instancio.create(UpdateAddressRequest.class);
    }

    public static UpdateUserRequest createUpdateUserRequest () {
        return Instancio.create(UpdateUserRequest.class);
    }

    public static UpdateAvatarRequest createUpdateAvatarRequest () {
        return Instancio.create(UpdateAvatarRequest.class);
    }

    public static UpdateProfileRequest createUpdateProfileRequest () {
        return Instancio.create(UpdateProfileRequest.class);
    }

    public static UpdatePasswordRequest createUpdatePasswordRequest () {
        return Instancio.create(UpdatePasswordRequest.class);
    }

    public static ChangePasswordRequest createChangePasswordRequest () {
        return Instancio.create(ChangePasswordRequest.class);
    }

    public static ActivateGsmRequest createActivateGsmRequest () {
        return Instancio.create(ActivateGsmRequest.class);
    }

    public static EmailActivationToken createEmailActivationToken () {
        return Instancio.create(EmailActivationToken.class);
    }

    public static LoginRequest createLoginRequest () {
        return Instancio.create(LoginRequest.class);
    }

    public static TokenResponse createTokenResponse () {
        return Instancio.create(TokenResponse.class);
    }

    public static UserResponse createUserResponse () {
        return Instancio.create(UserResponse.class);
    }

    public static RefreshRequest createRefreshRequest () {
        return Instancio.create(RefreshRequest.class);
    }

    public static RegisterRequest createRegisterRequest () {
        return Instancio.create(RegisterRequest.class);
    }

    public static EmployeeRegisterRequest createEmployeeRegisterRequest () {
        return Instancio.create(EmployeeRegisterRequest.class);
    }

    public static OfferStatisticsResponse createOfferStatisticsResponse () {
        return Instancio.create(OfferStatisticsResponse.class);
    }

    public static OfferStatusInfo createOfferStatusInfo() {
        return Instancio.create(OfferStatusInfo.class);
    }

    public static GsmActivationToken createGsmActivationToken() {
        return Instancio.create(GsmActivationToken.class);
    }

    public static PasswordResetToken createPasswordResetToken() {
        return Instancio.create(PasswordResetToken.class);
    }

    public static JavaMailSender createJavaMailSender() {
        return Instancio.create(JavaMailSender.class);
    }

    public static UpdateCompanyWithStatusAndFmsCompany createUpdateCompanyWithStatusAndFmsCompany () {
        return Instancio.create(UpdateCompanyWithStatusAndFmsCompany.class);
    }
}
