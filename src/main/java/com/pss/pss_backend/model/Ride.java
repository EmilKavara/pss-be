package com.pss.pss_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ride_id")
    private Long rideId;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "user_id")
    @JsonBackReference
    @ToString.Exclude
    @JsonIgnore
    private User driver;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(name="departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name="available_seats", nullable = false)
    private int availableSeats;

    @Column(nullable = false)
    private String status = "Active";

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<RidePassenger> ridePassengers = new ArrayList<>();

}
