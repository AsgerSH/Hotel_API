package app.controllers;

import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.exceptions.HotelNotFoundException;
import jakarta.persistence.EntityManagerFactory;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


public class HotelController {

    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    HotelDAO hotelDAO = HotelDAO.getInstance(emf);

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    public void createHotel(Context ctx) {
        HotelDTO dto = ctx.bodyAsClass(HotelDTO.class);
        HotelDTO newHotel = hotelDAO.createHotel(dto);

        ctx.status(HttpStatus.CREATED);
        ctx.json(newHotel);

        logger.info("Created hotel '{}'", newHotel.getName());
    }


    public void getAllHotels(Context ctx) {
        List<HotelDTO> hotelDTOS = hotelDAO.getAllHotels();
        ctx.status(HttpStatus.OK);
        ctx.json(hotelDTOS);

        logger.info("Fetched all hotels, count: {}", hotelDTOS.size());
    }

    public void getHotelById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = hotelDAO.getHotelById(id);

        if (hotelDTO == null) {
            logger.warn("Hotel id {} not found", id);
            throw new HotelNotFoundException("Hotel id " + id + " not found");
        }

        logger.info("Fetched hotel id {}", id);
        ctx.status(HttpStatus.OK).json(hotelDTO);
    }


    public void updateHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);

        HotelDTO updated = hotelDAO.updateHotel(id, hotelDTO);
        if (updated == null) {
            logger.warn("Cannot update, hotel id {} not found", id);
            throw new HotelNotFoundException("Hotel " + id + " not found");
        }

        logger.info("Updated hotel id {}", id);
        ctx.status(HttpStatus.OK);
        ctx.json(updated);
    }

    public void deleteHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        HotelDTO hotel = hotelDAO.getHotelById(id);
        if (hotel == null) {
            logger.warn("Cannot delete, hotel {} not found", id);
            throw new HotelNotFoundException("Hotel " + id + " not found");
        }

        hotelDAO.deleteHotel(id);
        logger.info("Deleted hotel id {}", id);
        ctx.json(hotel);

    }

    public void getRoomsForHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        List<RoomDTO> roomDTOS = hotelDAO.getRoomsForHotel(id);

        ctx.status(HttpStatus.OK);
        ctx.json(roomDTOS);
        logger.info("Fetched {} rooms for hotel id {}", roomDTOS.size(), id);
    }
}

