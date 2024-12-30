package com.pss.pss_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "ride_id", referencedColumnName = "ride_id")
    @JsonBackReference
    @ToString.Exclude
    @JsonIgnore
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "passenger_id", referencedColumnName = "user_id")
    @JsonBackReference
    @ToString.Exclude
    @JsonIgnore
    private User passenger;

    @Column(name="booked_seats", nullable = false)
    private int bookedSeats;

    @Column(name ="reservationStatus", nullable = false)
    private String reservationStatus = "Pending";

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
