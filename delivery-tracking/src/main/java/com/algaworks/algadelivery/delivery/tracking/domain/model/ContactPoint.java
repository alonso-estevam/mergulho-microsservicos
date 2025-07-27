package com.algaworks.algadelivery.delivery.tracking.domain.model;

import lombok.*;

// Lembrando que por ser um Value Object, precisa de um construtor já com todos os argumentos, pois a ideia é ser instanciado apenas uma vez e não ser modificado posteriormente.
@AllArgsConstructor // Value Object não precisa de uma factory, assim, podemos usar o construtor diretamente para criar instâncias com todos os atributos necessários.
@Builder // uma alternativa ao construtor, já que o Value Object pode encapsular diversas propriedades
@EqualsAndHashCode // por se tratar de um ValueObject, deve ser comparado usando todos os atributos disponíveis.
@Getter
public class ContactPoint {

    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String name;
    private String phone;

}
