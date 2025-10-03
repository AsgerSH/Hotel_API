package app.features.hotel.dao;

import app.features.hotel.dto.HotelDTO;
import app.features.room.dto.RoomDTO;
import app.features.hotel.entities.Hotel;
import app.features.room.entities.Room;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class HotelDAO implements IHotelDAO {

    private static HotelDAO instance;
    private static EntityManagerFactory emf;

    private HotelDAO() {}

    public static HotelDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new HotelDAO();
            emf = _emf;
        }
        return instance;
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        try (var em = emf.createEntityManager()) {
            TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h", Hotel.class);
            return HotelDTO.toDTOList(query.getResultList());
        }
    }

    @Override
    public HotelDTO getHotelById(int id) {
        try (var em = emf.createEntityManager()) {
            var entity = em.find(Hotel.class, id);
            return (entity != null) ? new HotelDTO(entity) : null;
        }
    }

    @Override
    public HotelDTO createHotel(HotelDTO dto) {
        var entity = new Hotel(dto);
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return new HotelDTO(entity);
        }
    }

    public List<HotelDTO> createHotelsFromList(HotelDTO[] dtos) {
        List<HotelDTO> hotelDTOS = new ArrayList<>();
        for (HotelDTO dto : dtos) {
            hotelDTOS.add(createHotel(dto));
        }
        return hotelDTOS;
    }

    @Override
    public HotelDTO updateHotel(int id, HotelDTO dto) {
        try (var em = emf.createEntityManager()) {
            Hotel entity = em.find(Hotel.class, id);
            if (entity != null) {
                var tx = em.getTransaction();
                tx.begin();
                entity.setName(dto.getName());
                entity.setAddress(dto.getAddress());
                entity.setRooms(dto.getRooms());
                entity.setStars(dto.getStars());
                em.merge(entity);
                tx.commit();
                return new HotelDTO(entity);
            }
            return null;
        }
    }

    @Override
    public void deleteHotel(int id) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            var entity = em.find(Hotel.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        }
    }

    @Override
    public HotelDTO addRoom(int hotelId, RoomDTO roomDto) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            Hotel hotel = em.find(Hotel.class, hotelId);
            if (hotel == null) {
                tx.rollback();
                return null;
            }

            Room room = new Room();
            room.setNumber(roomDto.getNumber());
            room.setPrice(roomDto.getPrice());
            room.setType(roomDto.getType());
            room.setHotel(hotel);

            // Add room to hotel’s collection
            hotel.getRoomSet().add(room);

            // Persist the new room explicitly
            em.persist(room);

            tx.commit();
            return new HotelDTO(hotel);
        }
    }


    @Override
    public HotelDTO removeRoom(int hotelId, int roomId) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            Hotel hotel = em.find(Hotel.class, hotelId);
            if (hotel == null) {
                tx.rollback();
                return null;
            }

            Room room = em.find(Room.class, roomId);
            if (room != null && room.getHotel().getId() == hotelId) {
                hotel.getRoomSet().remove(room);
                em.remove(room);
            }

            em.merge(hotel);
            tx.commit();
            return new HotelDTO(hotel);
        }
    }

    @Override
    public List<RoomDTO> getRoomsForHotel(int hotelId) {
        try (var em = emf.createEntityManager()) {
            Hotel hotel = em.find(Hotel.class, hotelId);
            if (hotel == null) {
                return List.of();
            }
            return RoomDTO.toDTOList(hotel.getRoomSet().stream().toList());
        }
    }

    // Extra helper (not in interface)
    public void deleteAll() {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            tx.commit();
        }
    }
}
