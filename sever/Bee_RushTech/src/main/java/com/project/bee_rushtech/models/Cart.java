package com.project.bee_rushtech.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    // Getters và Setters
}
