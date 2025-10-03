package app.features.room.dao;

import app.features.room.dto.RoomDTO;
import app.features.hotel.entities.Hotel;
import app.features.room.entities.Room;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    private static RoomDAO instance;
    private static EntityManagerFactory emf;

    private RoomDAO() {}

    public static RoomDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new RoomDAO();
            emf = _emf;
        }
        return instance;
    }

    public List<RoomDTO> getAllRooms() {
        try (var em = emf.createEntityManager()) {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
            return RoomDTO.toDTOList(query.getResultList());
        }
    }

    public RoomDTO getRoomById(int id) {
        try (var em = emf.createEntityManager()) {
            var entity = em.find(Room.class, id);
            return (entity != null) ? new RoomDTO(entity) : null;
        }
    }

    public RoomDTO createRoom(RoomDTO dto) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            Hotel hotel = em.find(Hotel.class, dto.getHotelId());
            if (hotel == null) {
                tx.rollback();
                return null;
            }

            Room room = new Room();
            room.setNumber(dto.getNumber());
            room.setPrice(dto.getPrice());
            room.setType(dto.getType());
            room.setHotel(hotel);

            em.persist(room);
            tx.commit();

            return new RoomDTO(room);
        }
    }


    public List<RoomDTO> createRoomsFromList(RoomDTO[] dtos) {
        List<RoomDTO> newRoomDTOs = new ArrayList<>();
        for (RoomDTO dto : dtos) {
            newRoomDTOs.add(createRoom(dto));
        }
        return newRoomDTOs;
    }


    public RoomDTO updateRoom(int id, RoomDTO dto) {
        try (var em = emf.createEntityManager()) {
            Room entity = em.find(Room.class, id);
            if (entity != null) {
                var tx = em.getTransaction();
                tx.begin();
                entity.setNumber(dto.getNumber());
                entity.setPrice(dto.getPrice());
                entity.setType(dto.getType());
                em.merge(entity);
                tx.commit();
                return new RoomDTO(entity);
            }
            return null;
        }
    }

    public void deleteRoom(int roomId) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            Room room = em.find(Room.class, roomId);
            if (room != null) {
                Hotel hotel = room.getHotel();
                if (hotel != null) {
                    hotel.getRoomSet().remove(room);
                }
                em.remove(room);
            }

            tx.commit();
        }
    }


    public void deleteAllRooms() {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            tx.commit();
        }
    }
}
