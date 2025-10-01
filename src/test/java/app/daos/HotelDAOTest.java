package app.daos;

import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.PersistentObjectException;
import org.junit.jupiter.api.*;

import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;

class HotelDAOTest {

    private static EntityManagerFactory emf;
    private static HotelDAO dao;

    @BeforeAll
    static void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = HotelDAO.getInstance(emf);
    }

    @BeforeEach
    void clearDb() {
        dao.deleteAll();
    }

    @Test
    void testCreateAndGetById() {
        HotelDTO dto = new HotelDTO(0, "Hotel California", "California st. 8840", 221, 5);
        HotelDTO saved = dao.createHotel(dto);

        assertNotNull(saved.getId());
        assertEquals("Hotel California", saved.getName());

        HotelDTO fetched = dao.getHotelById(saved.getId());
        assertEquals(saved.getName(), fetched.getName());
    }

    @Test
    void testGetAllHotels() {
        dao.createHotel(new HotelDTO(0, "Hotel California", "California st. 8840", 221, 5));
        dao.createHotel(new HotelDTO(0, "Hotel Balifornia", "Balifornia st. 8840", 221, 5));

        List<HotelDTO> list = dao.getAllHotels();
        assertEquals(2, list.size());
    }

    @Test
    void testUpdateHotel() {
        HotelDTO saved = dao.createHotel(
                new HotelDTO(0, "Hotel Aalifornia", "Aalifornia st. 8840", 221, 5)
        );

        HotelDTO updated = dao.updateHotel(
                saved.getId(),
                new HotelDTO(0, "Botel", "California st. 8840", 33, 5)
        );

        assertNotNull(updated);
        assertEquals("Botel", updated.getName());
        assertEquals(33, updated.getRooms());
    }

    @Test
    void testDeleteHotel() {
        HotelDTO saved = dao.createHotel(new HotelDTO(0, "Hotel Danglatere", "Et sted i københavn", 221, 5));
        dao.deleteHotel(saved.getId());

        assertNull(dao.getHotelById(saved.getId()));
    }

    @Test
    void testCreateHotelsFromList() {
        HotelDTO[] hotels = {
                new HotelDTO(0, "Hotel One", "Street 1", 10, 3),
                new HotelDTO(0, "Hotel Two", "Street 2", 20, 4)
        };
        List<HotelDTO> created = dao.createHotelsFromList(hotels);

        assertEquals(2, created.size());
        assertEquals("Hotel One", created.get(0).getName());
    }

    @Test
    void testAddRoomAndGetRoomsForHotel() {
        HotelDTO hotel = dao.createHotel(new HotelDTO(0, "Hotel With Rooms", "Room St. 5", 5, 4));

        RoomDTO room = new RoomDTO(0, hotel.getId(), 100, 101, "Standard");
        dao.addRoom(hotel.getId(), room);

        List<RoomDTO> rooms = dao.getRoomsForHotel(hotel.getId());

        assertEquals(1, rooms.size());
        assertEquals(101, rooms.get(0).getNumber());
    }

    @Test
    void testRemoveRoom() {
        HotelDTO hotel = dao.createHotel(new HotelDTO(0, "Hotel Removal", "Remove St. 1", 3, 3));

        RoomDTO room = new RoomDTO(0, hotel.getId(), 201, 150, "Suite");
        dao.addRoom(hotel.getId(), room);

        List<RoomDTO> rooms = dao.getRoomsForHotel(hotel.getId());
        assertEquals(1, rooms.size());

        int roomId = rooms.get(0).getId();
        dao.removeRoom(hotel.getId(), roomId);

        List<RoomDTO> afterRemove = dao.getRoomsForHotel(hotel.getId());
        assertEquals(0, afterRemove.size());
    }

    @Test
    void testCreateHotelWithOtherIdThanZero(){
        int id = 1;
        assertThrows(PersistentObjectException.class, () -> dao.createHotel(
                        new HotelDTO(
                                id,
                                "Hotel no ID",
                                "Test street",
                                3,
                                3)),
                "Hotel couldn't be created with other ID than 0");

    }
}


