package app.features.room.entities;

import app.features.room.dto.RoomDTO;
import app.features.hotel.entities.Hotel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "hotel_id", insertable = false, updatable = false)
    private int hotelId;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "type", nullable = false)
    private String type;

    // Relationer m:1
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Hotel hotel;

    public Room(RoomDTO dto, Hotel hotel) {
        this.id = dto.getId();
        this.number = dto.getNumber();
        this.type = dto.getType();
        this.price = dto.getPrice();
        this.hotel = hotel;
    }

    // Convenience getter for hotelId (useful for DTO mapping)
    public int getHotelId() {
        return hotel != null ? hotel.getId() : 0;
    }
}

