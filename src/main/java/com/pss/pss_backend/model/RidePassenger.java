package com.pss.pss_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "ride_passengers")
@Data
public class RidePassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    @ToString.Exclude
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String role; // "driver" or "passenger"

    @Column(nullable = false)
    private String status = "PENDING"; // "pending", "approved", "denied"

}
