package app.features.room.rest;

import app.config.HibernateConfig;
import app.features.room.dao.RoomDAO;
import app.features.room.dto.RoomDTO;
import app.core.exceptions.RoomNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RoomController {

    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final RoomDAO roomDAO = RoomDAO.getInstance(emf);

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    public void createRoom(Context ctx) {
        RoomDTO[] roomDTOS = ctx.bodyAsClass(RoomDTO[].class);
        List<RoomDTO> newRoomDTOs = roomDAO.createRoomsFromList(roomDTOS);

        ctx.status(HttpStatus.CREATED);
        ctx.json(newRoomDTOs);

        logger.info("Created {} new rooms", newRoomDTOs.size());
    }

    public void getAllRooms(Context ctx) {
        List<RoomDTO> roomDTOS = roomDAO.getAllRooms();
        ctx.status(HttpStatus.OK);
        ctx.json(roomDTOS);

        logger.info("Fetched all rooms, count: {}", roomDTOS.size());
    }

    public void getRoomById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        RoomDTO roomDTO = roomDAO.getRoomById(id);

        if (roomDTO == null) {
            logger.warn("Room id {} not found", id);
            throw new RoomNotFoundException("Room id " + id + " not found");
        }

        logger.info("Fetched room id {}", id);
        ctx.status(HttpStatus.OK);
        ctx.json(roomDTO);
    }

    public void updateRoom(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        RoomDTO dto = ctx.bodyAsClass(RoomDTO.class);

        RoomDTO updated = roomDAO.updateRoom(id, dto);
        if (updated == null) {
            logger.warn("Cannot update, room id {} not found", id);
            throw new RoomNotFoundException("Room id " + id + " not found");
        }

        logger.info("Updated room id {}", id);
        ctx.status(HttpStatus.OK);
        ctx.json(updated);
    }

    public void deleteRoom(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        RoomDTO room = roomDAO.getRoomById(id);
        if (room == null) {
            logger.warn("Cannot delete, room id {} not found", id);
            throw new RoomNotFoundException("Room id " + id + " not found");
        }

        roomDAO.deleteRoom(id);
        logger.info("Deleted room id {}", id);
        ctx.json(room);
    }
}
