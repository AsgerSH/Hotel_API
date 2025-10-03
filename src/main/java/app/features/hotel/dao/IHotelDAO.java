package app.features.hotel.dao;

import app.features.hotel.dto.HotelDTO;
import app.features.room.dto.RoomDTO;
import java.util.List;

public interface IHotelDAO {

    // Hotel CRUD
    List<HotelDTO> getAllHotels();
    HotelDTO getHotelById(int id);
    HotelDTO createHotel(HotelDTO dto);
    HotelDTO updateHotel(int id, HotelDTO dto);
    void deleteHotel(int id);

    // Room management
    HotelDTO addRoom(int hotelId, RoomDTO roomDto);
    HotelDTO removeRoom(int hotelId, int roomId);
    List<RoomDTO> getRoomsForHotel(int hotelId);
}
