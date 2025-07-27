package com.algaworks.algadelivery.delivery.tracking.domain.model.enums;

import java.util.List;

public enum DeliveryStatus {

    DRAFT,
    WAITING_FOR_COURIER(DRAFT),
    IN_TRANSIT(WAITING_FOR_COURIER),
    DELIVERED(IN_TRANSIT);

    private final List<DeliveryStatus> previousStatuses;

    DeliveryStatus(DeliveryStatus... previousStatuses) {

        this.previousStatuses = List.of(previousStatuses);
    }

    public boolean canNotChangeTo(DeliveryStatus newStatus) {

        DeliveryStatus current = this;
        return !newStatus.previousStatuses.contains(current);
    }

    public boolean canChangeTo(DeliveryStatus newStatus) {

        return !canNotChangeTo(newStatus);
    }
}
