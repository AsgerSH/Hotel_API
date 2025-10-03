package app.features.hotel.dto;

import app.features.hotel.entities.Hotel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HotelDTO {

    private int id;
    private String name;
    private String address;
    private Integer rooms;
    private Integer stars;

    // Entity -> DTO
    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.rooms = hotel.getRooms();
        this.stars = hotel.getStars();
    }

    // Convert list of entities to DTOs
    public static List<HotelDTO> toDTOList(List<Hotel> hotels) {
        return hotels.stream().map(HotelDTO::new).toList();
    }
}
