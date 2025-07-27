package com.algaworks.algadelivery.delivery.tracking.domain.model;

import com.algaworks.algadelivery.delivery.tracking.domain.exception.BusinessException;
import com.algaworks.algadelivery.delivery.tracking.domain.model.enums.DeliveryStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    @Test
    void shouldChangeStatusToPlaced() {

        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(creatValidPreparationDetails());

        delivery.place();

        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, delivery.getStatus());
        assertNotNull(delivery.getPlacedAt());
    }

    @Test
    void shouldNotPlace() {

        Delivery delivery = Delivery.draft();

        assertThrows(BusinessException.class, delivery::place);

        assertEquals(DeliveryStatus.DRAFT, delivery.getStatus());
        assertNull(delivery.getPlacedAt());
    }

    private Delivery.PreparationDetails creatValidPreparationDetails() {
        ContactPoint sender = ContactPoint.builder()
                .zipCode("12345-678")
                .street("Rua São Paulo")
                .number("100")
                .complement("Sala 401")
                .name("Francisca Beyoncé")
                .phone("(11) 99000-1234")
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode("99999-110")
                .street("Avenida A")
                .number("1564")
                .complement("Apto 12")
                .name("Antônia Rihanna")
                .phone("(11) 98000-1111")
                .build();

        return Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("15.00"))
                .courierPayout(new BigDecimal("5.00"))
                .expectedDeliveryTime(Duration.ofHours(5))
                .build();
    }

}