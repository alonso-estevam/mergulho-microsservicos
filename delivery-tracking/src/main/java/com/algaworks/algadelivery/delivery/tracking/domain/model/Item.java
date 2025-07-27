package com.algaworks.algadelivery.delivery.tracking.domain.model;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Item {

// Mesmo caso do Delivery: é uma entidade, e deve ser comparada usando apenas a identidade única (id).
    @EqualsAndHashCode.Include
    private UUID id;
    private String name;

    @Setter(AccessLevel.PACKAGE)
    private Integer quantity;

// omitindo o modificador public, o acesso passa a ser package-private
    static Item brandNew(String name, Integer quantity) {

        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setName(name);
        item.setQuantity(quantity);

        return item;
    }

}
