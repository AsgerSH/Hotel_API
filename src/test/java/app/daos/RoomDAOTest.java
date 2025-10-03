package app.daos;

import app.config.HibernateConfig;
import app.features.hotel.dao.HotelDAO;
import app.features.hotel.dto.HotelDTO;
import app.features.room.dao.RoomDAO;
import app.features.room.dto.RoomDTO;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomDAOTest {

    private static EntityManagerFactory emf;
    private static RoomDAO dao;
    private static HotelDAO hotelDAO;

    @BeforeAll
    static void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = RoomDAO.getInstance(emf);
        hotelDAO = HotelDAO.getInstance(emf);
    }

    @BeforeEach
    void clearDb() {
        dao.deleteAllRooms();
        hotelDAO.deleteAll();
    }

    private HotelDTO createTestHotel() {
        return hotelDAO.createHotel(
                new HotelDTO(0, "Test Hotel", "Test Street 1", 20, 4)
        );
    }

    @Test
    void testCreateAndGetById() {
        HotelDTO hotel = createTestHotel();

        RoomDTO dto = new RoomDTO(0, hotel.getId(), 100.0, 101, "Standard");
        RoomDTO saved = dao.createRoom(dto);

        assertNotNull(saved.getId());
        assertEquals("Standard", saved.getType());

        RoomDTO fetched = dao.getRoomById(saved.getId());
        assertEquals(saved.getNumber(), fetched.getNumber());
    }

    @Test
    void testGetAllRooms() {
        HotelDTO hotel = createTestHotel();

        dao.createRoom(new RoomDTO(0, hotel.getId(), 100.0, 101, "Standard"));
        dao.createRoom(new RoomDTO(0, hotel.getId(), 150.0, 102, "Suite"));

        List<RoomDTO> list = dao.getAllRooms();
        assertEquals(2, list.size());
    }

    @Test
    void testUpdateRoom() {
        HotelDTO hotel = createTestHotel();

        RoomDTO saved = dao.createRoom(new RoomDTO(0, hotel.getId(), 100.0, 101, "Standard"));
        RoomDTO updated = dao.updateRoom(saved.getId(),
                new RoomDTO(saved.getId(), hotel.getId(), 200.0, 202, "Suite"));

        assertEquals("Suite", updated.getType());
        assertEquals(202, updated.getNumber());
        assertEquals(200.0, updated.getPrice());
    }

    @Test
    void testDeleteRoom() {
        HotelDTO hotel = createTestHotel();

        RoomDTO saved = dao.createRoom(new RoomDTO(0, hotel.getId(), 100.0, 101, "Standard"));
        dao.deleteRoom(saved.getId());

        assertNull(dao.getRoomById(saved.getId()));
    }

    @Test
    void testDeleteAllRooms() {
        HotelDTO hotel = createTestHotel();

        dao.createRoom(new RoomDTO(0, hotel.getId(), 100.0, 101, "Standard"));
        dao.createRoom(new RoomDTO(0, hotel.getId(), 150.0, 102, "Suite"));

        dao.deleteAllRooms();

        List<RoomDTO> list = dao.getAllRooms();
        assertEquals(0, list.size());
    }
}
