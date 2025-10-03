package app.features.room.dto;

import app.features.room.entities.Room;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDTO {

    private Integer id;
    private Integer hotelId;
    private Integer number;
    private double price;
    private String type;


    public RoomDTO(Room room) {
        this.id = room.getId();
        this.hotelId = room.getHotel().getId();
        this.number = room.getNumber();
        this.price = room.getPrice();
        this.type = room.getType();
    }

    public RoomDTO(Integer id, Integer hotelId, double price, Integer number, String type) {
        this.id = id;
        this.hotelId = hotelId;
        this.price = price;
        this.number = number;
        this.type = type;
    }

    public static List<RoomDTO> toDTOList(List<Room> resultList) {
        return resultList.stream().map(RoomDTO::new).toList();
    }
}
