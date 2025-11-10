package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.response.message.MessageGroupResponse;
import com.cosmosboard.fmh.dto.response.message.MessageResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Message;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.repository.jpa.MessageRepository;
import com.cosmosboard.fmh.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private static final String ID = "id";

    private final MessageRepository messageRepository;

    private final CompanyService companyService;

    /**
     * Save a message to the database.
     *
     * @param message Message
     * @return message
     */
    public Message save(final Message message) {
        return messageRepository.save(message);
    }

    /**
     * Save a list of messages to the database.
     *
     * @param messages List of messages
     * @return list of messages
     */
    public List<Message> save(final List<Message> messages) {
        return messageRepository.saveAll(messages);
    }

    /**
     * Find a message by its ID.
     *
     * @param id The ID of the message
     * @return The found message
     * @throws BadRequestException If message is not found
     */
    public Message findOneById(final String id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Message not found with id: " + id));
    }

    /**
     * Get grouped messages between the given company and others, optionally filtered by title.
     *
     * @param company The company to group messages for
     * @param title   Optional filter by message title
     * @return Grouped messages by company
     */
    public List<MessageGroupResponse> getGroupedMessages(Company company, String title) {
        List<Message> allMessages = messageRepository.findMessagesByCompanyId(company.getId());

        if (title != null && !title.isBlank()) {
            String lowerCaseTitle = title.toLowerCase();
            allMessages = allMessages.stream()
                    .filter(message -> message.getTitle() != null && message.getTitle().toLowerCase().contains(lowerCaseTitle))
                    .toList();
        }

        Map<String, List<Message>> groupedMessages = allMessages.stream()
                .collect(Collectors.groupingBy(message -> {
                    if (message.getFrom().getId().equals(company.getId())) {
                        return message.getTo().getId();
                    } else {
                        return message.getFrom().getId();
                    }
                }));

        return groupedMessages.entrySet().stream()
                .map(entry -> {
                    String otherCompanyId = entry.getKey();
                    List<MessageResponse> messages = entry.getValue().stream()
                            .sorted(Comparator.comparing(Message::getCreatedAt))
                            .map(MessageResponse::convert)
                            .toList();

                    Company otherCompany = companyService.findOneById(otherCompanyId);

                    return new MessageGroupResponse(otherCompany.getId(), otherCompany.getName(), messages);
                })
                .toList();
    }

    /**
     * Build a message specification for filtering messages sent or received by the given user ID.
     *
     * @param userId The user ID to filter messages
     * @return Specification for querying messages
     */
    public Specification<Message> specification(final String userId) {
        return (Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.and(criteriaBuilder.equal(root.get("from").get(ID), userId)),
                    criteriaBuilder.and(criteriaBuilder.equal(root.get("to").get(ID), userId))
                ));
            }
            return query.where(predicates.toArray(new Predicate[0])).distinct(true).getRestriction();
        };
    }

    /**
     * Find all messages using the given specification and pagination.
     *
     * @param specification      The filtering specification
     * @param paginationCriteria The pagination settings
     * @return A page of messages
     */
    public Page<Message> findAll(final Specification<Message> specification, final PaginationCriteria paginationCriteria) {
        return messageRepository.findAll(specification, PageRequestBuilder.build(paginationCriteria));
    }

    /**
     * Get messages between two companies, optionally filtered by title.
     *
     * @param userCompany     The current user's company
     * @param targetCompanyId The ID of the target company
     * @param title           Optional title filter
     * @return List of messages between the companies
     */
    public List<MessageResponse> getMessagesBetweenCompanies(Company userCompany, String targetCompanyId, String title) {
        Company targetCompany = companyService.findOneById(targetCompanyId);

        List<Message> messages = messageRepository.findMessagesBetweenCompanies(userCompany.getId(), targetCompany.getId());

        if (title != null && !title.isBlank()) {
            String lowerCaseTitle = title.toLowerCase();
            messages = messages.stream()
                    .filter(message -> message.getTitle() != null && message.getTitle().toLowerCase().contains(lowerCaseTitle))
                    .toList();
        }

        return messages.stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(MessageResponse::convert)
                .toList();
    }

    /**
     * Delete a message by its ID.
     *
     * @param id The ID of the message to delete
     */
    public void deleteById(final String id) {
        Message message = findOneById(id);
        messageRepository.delete(message);
    }
}