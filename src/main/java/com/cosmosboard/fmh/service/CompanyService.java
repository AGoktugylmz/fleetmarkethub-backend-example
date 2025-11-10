package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.auth.RegisterRequest;
import com.cosmosboard.fmh.dto.request.company.CreateCompanyRequest;
import com.cosmosboard.fmh.dto.request.company.UpdateCompanyRequest;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.specification.CompanyFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.CompanyCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.ExpectationException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.repository.jpa.CompanyRepository;
import com.cosmosboard.fmh.service.storage.StorageService;
import com.cosmosboard.fmh.service.storage.StorageThumbnailService;
import com.cosmosboard.fmh.util.AppConstants;
import com.cosmosboard.fmh.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {
    public static final String PICTURES_PATH = "pictures";

    public static final String AVATARS_PATH = String.format("%s/avatars", PICTURES_PATH);

    public static final String BANNERS_PATH = String.format("%s/banners", PICTURES_PATH);

    public static final int BANNER_WIDTH = 1280;

    public static final int BANNER_HEIGHT = 360;

    public static final int AVATAR_SIZE = 620;

    private final CompanyRepository companyRepository;

    private final MessageSourceService messageSourceService;

    private final CategoryService categoryService;

    private final StorageService storageService;

    private final StorageThumbnailService storageThumbnailService;

    /**
     * Count all companies.
     *
     * @return long
     */
    public long count() {
        return companyRepository.count();
    }

    /**
     * Find all companies.
     *
     * @return List of companies
     */
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    /**
     * Find all companies with pagination.
     *
     * @param companyCriteria    CompanyCriteria
     * @param paginationCriteria PaginationCriteria
     * @return Page of Company
     */
    public Page<Company> findAll(CompanyCriteria companyCriteria, PaginationCriteria paginationCriteria) {
        return companyRepository.findAll(new CompanyFilterSpecification(companyCriteria),
                PageRequestBuilder.build(paginationCriteria));
    }

    /**
     * Check if a company exists by name.
     *
     * @param companyName String
     * @return boolean indicating if the company exists
     */
    public boolean existsByName(String companyName) {
        return companyRepository.existsByName(companyName);
    }

    /**
     * Find one Company by ID.
     *
     * @param id String
     * @return Company
     */
    public Company findOneById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("company_not_found") + ": " + id));
    }

    /**
     * Find one Company by ID.
     *
     * @param name String
     * @return Company
     */
    public Company findOneByName(String name) {
        return companyRepository.findByName((messageSourceService.get("company_not_found") + ": " + name));
    }

    /**
     * Create Company from request.
     *
     * @param request CreateCompanyRequest
     * @return Company
     */
    @Transactional
    public Company create(CreateCompanyRequest request) {

        Company savedCompany = save(Company.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isDealer(request.getIsDealer() != null ? request.getIsDealer() : false)
                .build());

        if (request.getCategoryId() == null) {
            String message = messageSourceService.get("company_must_have_category", new String[]{"category"});
            log.error(message);
            throw new BadRequestException(message);
        }

        Category category = categoryService.findOneById(request.getCategoryId());
        savedCompany.setCategory(category);

        return savedCompany;
    }

    /**
     * Create a new company based on the provided request.
     * This method validates the category, creates a new Company object,
     * and saves it to the database.
     *
     * @param request RegisterRequest object containing the company details
     * @return The created Company object
     */
    public Company createCompany(RegisterRequest request) {
        Category category = categoryService.findOneById(request.getCategoryId());

        if (request.getCategoryId() == null) {
            String message = messageSourceService.get("company_must_have_category", new String[]{"category"});
            log.error(message);
            throw new BadRequestException(message);
        }

        Company newCompany = save(Company.builder()
                .name(request.getCompanyName())
                .category(category)
                .status(AppConstants.CompanyStatusEnum.APPROVED)
                .retail(request.getCompanyRetail())
                .isDealer(request.getIsDealer() != null ? request.getIsDealer() : false)
                .build());

        return companyRepository.save(newCompany);
    }

    /**
     * Update Company from request.
     *
     * @param id      String
     * @param request UpdateCompanyRequest
     * @return Company
     */
    public Company update(String id, UpdateCompanyRequest request) {
        Company company = findOneById(id);
        return save(updateCompanyFromRequest(company, request));
    }

    /**
     * Save Company from request.
     *
     * @return Company
     */
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    /**
     * Delete company by ID.
     *
     * @param id String
     */
    public void delete(String id) {
        companyRepository.delete(findOneById(id));
    }

    /**
     * Update company entity from request.
     *
     * @param company Company
     * @param request UpdateCompanyRequest
     * @return Company
     */
    private Company updateCompanyFromRequest(Company company, UpdateCompanyRequest request) {

        if (existsByName(request.getName())) {
            String message = messageSourceService.get("company_already_exists");
            log.error(message);
            throw new BadRequestException(message);
        }

        if (request.getName() != null)
            company.setName(request.getName());

        if (request.getDescription() != null)
            company.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryService.findOneById(request.getCategoryId());
            company.setCategory(category);
        }

        if (request.getIsDealer() != null) {
            company.setIsDealer(request.getIsDealer());
        }

        return company;
    }

    /**
     * Update the avatar of a company.
     * This method stores the new avatar image, creates a thumbnail, deletes the old avatar,
     * and saves the new avatar file name in the company's record.
     *
     * @param companyId The ID of the company whose avatar is being updated
     * @param avatar    The new avatar image file
     */
    public void updateAvatar(String companyId, MultipartFile avatar) {
        Company company = findOneById(companyId);

        String filename = storageService.store(avatar, AVATARS_PATH, true);
        String path = Paths.get(AVATARS_PATH, filename).toString();

        try {
            storageThumbnailService.make(path)
                    .resize(AVATAR_SIZE, AVATAR_SIZE)
                    .save(path);
        } catch (ExpectationException e) {
            log.warn("Error while creating thumbnail: {}", e.getMessage());
        }

        deleteAvatarFromStorage(company);

        company.setAvatar(filename);
        companyRepository.save(company);
    }

    /**
     * Update the banner of a company.
     * This method stores the new banner image, creates a resized banner,
     * deletes the old banner, and saves the new banner file name in the company's record.
     *
     * @param companyId The ID of the company whose banner is being updated
     * @param banner    The new banner image file
     */
    public void updateBanner(String companyId, MultipartFile banner) {
        Company company = findOneById(companyId);

        String filename = storageService.store(banner, BANNERS_PATH, true);
        String path = Paths.get(BANNERS_PATH, filename).toString();

        try {
            storageThumbnailService.make(path)
                    .resize(BANNER_WIDTH, BANNER_HEIGHT)
                    .save(path);
        } catch (ExpectationException e) {
            log.warn("Error while creating banner thumbnail: {}", e.getMessage());
        }

        deleteBannerFromStorage(company);

        company.setBanner(filename);
        companyRepository.save(company);
    }

    /**
     * Delete the old avatar image from the storage.
     * This method checks if the company has an existing avatar file,
     * and if so, it deletes the file from storage.
     *
     * @param company The company whose avatar file is to be deleted
     */
    private void deleteAvatarFromStorage(Company company) {
        if (company.getAvatar() != null) {
            try {
                storageService.delete(Paths.get(AVATARS_PATH, company.getAvatar()).toString());
            } catch (Exception e) {
                log.warn("Error deleting old avatar: {}", e.getMessage());
            }
        }
    }

    /**
     * Delete the old banner image from the storage.
     * This method checks if the company has an existing banner file,
     * and if so, deletes the file from the storage.
     *
     * @param company The company whose banner file is to be deleted
     */
    private void deleteBannerFromStorage(Company company) {
        if (company.getBanner() != null) {
            try {
                storageService.delete(Paths.get(BANNERS_PATH, company.getBanner()).toString());
            } catch (Exception e) {
                log.warn("Error deleting old banner: {}", e.getMessage());
            }
        }
    }
}