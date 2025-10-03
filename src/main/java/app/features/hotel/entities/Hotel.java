package app.features.hotel.entities;

import app.features.hotel.dto.HotelDTO;
import app.features.room.entities.Room;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "rooms", nullable = false)
    private Integer rooms;

    @Column(name = "stars", nullable = false)
    private Integer stars;


    public Hotel(HotelDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.rooms = dto.getRooms();
        this.stars = dto.getStars();
        this.roomSet = new HashSet<>();
    }

    // 1:m relationer
    // Bør være {CascadeType.PERSIST, CascadeType.MERGE} - ændrer tilbage senere.
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Room> roomSet = new HashSet<>();

}

