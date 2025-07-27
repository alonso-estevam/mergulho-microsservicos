package com.algaworks.algadelivery.delivery.tracking.domain.model;

import com.algaworks.algadelivery.delivery.tracking.domain.exception.BusinessException;
import com.algaworks.algadelivery.delivery.tracking.domain.model.enums.DeliveryStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PACKAGE) // Construtor package-private para evitar instâncias externas, forçando o uso do método draft().
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Delivery {

// Delivery é uma entidade, portanto, deve ter uma identidade única e ser comparada usando apenas essa identidade.
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID courierId;

    private DeliveryStatus status;

    private OffsetDateTime placedAt;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expectedDeliveryAt;
    private OffsetDateTime fulfilledAt;

    private BigDecimal distanceFee;
    private BigDecimal courierPayout;
    private BigDecimal totalCost;

    private Integer totalItems;

    private ContactPoint sender;
    private ContactPoint recipient;

    private List<Item> items = new ArrayList<>();

    // Com o método abaixo Static Factory, provemos um ponto de entrada específico, já com os valores iniciais coerentes com nossas regras para uma entrega en rascunho
    public static Delivery draft() {

        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setStatus(DeliveryStatus.DRAFT);
        delivery.setTotalItems(0);
        delivery.setTotalCost(BigDecimal.ZERO);
        delivery.setCourierPayout(BigDecimal.ZERO);
        delivery.setDistanceFee(BigDecimal.ZERO);

        return delivery;
    }

    // apenas o Delivery pode modificar a lista de itens
    public UUID addItem(String name, int quantity) {

        Item item = Item.brandNew(name, quantity);
        items.add(item);
        calculateTotalItems();

        return item.getId();
    }

    public void removeItem(UUID itemId) {

        items.removeIf(item -> item.getId().equals(itemId));
        calculateTotalItems();
    }

    // exemplo de como apenas o root deve poder alterar seus atributos
    public void changeItemQuantity(UUID itemId, int newQuantity) {

        Item item = items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow();

        item.setQuantity(newQuantity);

        calculateTotalItems();
    }

    public void clearItems() {

        items.clear();
        calculateTotalItems();
    }

    public void editPreparationDetails(PreparationDetails details) {
        verifyIfCanBeEdited();

        setSender(details.getSender());
        setRecipient(details.getRecipient());
        setDistanceFee(details.getDistanceFee());
        setCourierPayout(details.getCourierPayout());

        setExpectedDeliveryAt(OffsetDateTime.now().plus(details.getExpectedDeliveryTime()));
        setTotalCost(this.getDistanceFee().add(this.getCourierPayout()));
    }

    private void verifyIfCanBeEdited() {

        if (!Objects.equals(getStatus(), DeliveryStatus.DRAFT)) {
            throw new BusinessException();
        }
    }

    public void place() {

        verifyIfCanBePlaced();
        this.changeStatusTo(DeliveryStatus.WAITING_FOR_COURIER);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void pickUp(UUID courierId) {

        this.setCourierId(courierId);
        this.changeStatusTo(DeliveryStatus.IN_TRANSIT);
        this.setAssignedAt(OffsetDateTime.now());
    }

    public void markAsDelivered() {

        this.changeStatusTo(DeliveryStatus.DELIVERED);
        this.setFulfilledAt(OffsetDateTime.now());
    }

    // para proteger de modificações externas, apenas o aggregate root deve ser capaz de modificar a lista de itens
    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    private void calculateTotalItems() {
        int totalItems = items.stream().mapToInt(Item::getQuantity).sum();
        setTotalItems(totalItems);
    }

    private void verifyIfCanBePlaced() {

        if (!isFilled()) {
            throw new BusinessException();
        }
        if (!Objects.equals(getStatus(), DeliveryStatus.DRAFT)) {
            throw new BusinessException();
        }
    }

    private boolean isFilled() {

        return Objects.nonNull(this.getSender())
                && Objects.nonNull(this.getRecipient())
                && Objects.nonNull(this.getTotalCost());
    }

    private void changeStatusTo(DeliveryStatus newStatus) {

        if (Objects.nonNull(newStatus) && this.getStatus().canNotChangeTo(newStatus)) {

            throw new BusinessException(format("Invalid status transition from %s to %s",
                    this.getStatus(), newStatus));
        }
        this.setStatus(newStatus);
    }

    // classe apenas para passagem de parâmetro
    @Getter
    @AllArgsConstructor
    @Builder
    public static class PreparationDetails {

        private ContactPoint sender;
        private ContactPoint recipient;
        private BigDecimal distanceFee;
        private BigDecimal courierPayout;
        private Duration expectedDeliveryTime;
    }

}