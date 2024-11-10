package com.project.bee_rushtech.models;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Date;

//Xác định nó là thực thể
@Entity
//Bảng trong db là categories mà class chúng ta lại là Category
// --> dùng @Table để ánh xạ
@Table(name= "orders")
@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id //primary key
    // No same instance --> when add a new instance --> auto increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name= "phone_number", nullable = false, length = 100)
    private String phoneNumber;

    @Column(name= "address", nullable = false, length = 100)
    private String address;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name ="order_date")
    private Date orderDate;

    @Column(name="status")
    private String status;

    @Column(name = "total_money")
    private Float totalMoney;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "active")
    private Boolean active;//thuộc về admin

}
