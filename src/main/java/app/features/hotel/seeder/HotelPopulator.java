package app.features.hotel.seeder;

import app.config.HibernateConfig;
import app.features.hotel.entities.Hotel;
import app.features.room.entities.Room;

public class HotelPopulator {

    public static void populateHotels() {
        var emf = HibernateConfig.getEntityManagerFactory();
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            // --- 1. Hotel d'Angleterre ---
            Hotel dAngleterre = Hotel.builder()
                    .name("Hotel d'Angleterre")
                    .address("Kongens Nytorv 34, 1050 København")
                    .rooms(30)
                    .stars(5)
                    .build();

            Room daRoom1 = new Room();
            daRoom1.setNumber(101);
            daRoom1.setPrice(2800);
            daRoom1.setType("Standard");
            daRoom1.setHotel(dAngleterre);

            Room daRoom2 = new Room();
            daRoom2.setNumber(201);
            daRoom2.setPrice(5200);
            daRoom2.setType("Suite");
            daRoom2.setHotel(dAngleterre);

            Room daRoom3 = new Room();
            daRoom3.setNumber(301);
            daRoom3.setPrice(7500);
            daRoom3.setType("Honeymoon Suite");
            daRoom3.setHotel(dAngleterre);

            dAngleterre.getRoomSet().add(daRoom1);
            dAngleterre.getRoomSet().add(daRoom2);
            dAngleterre.getRoomSet().add(daRoom3);

            // --- 2. Nimb Hotel ---
            Hotel nimb = Hotel.builder()
                    .name("Nimb Hotel")
                    .address("Bernstorffsgade 5, 1577 København")
                    .rooms(38)
                    .stars(5)
                    .build();

            Room nimbRoom1 = new Room();
            nimbRoom1.setNumber(101);
            nimbRoom1.setPrice(2400);
            nimbRoom1.setType("Standard");
            nimbRoom1.setHotel(nimb);

            Room nimbRoom2 = new Room();
            nimbRoom2.setNumber(201);
            nimbRoom2.setPrice(4200);
            nimbRoom2.setType("Suite");
            nimbRoom2.setHotel(nimb);

            nimb.getRoomSet().add(nimbRoom1);
            nimb.getRoomSet().add(nimbRoom2);

            // --- 3. Copenhagen Admiral Hotel ---
            Hotel admiral = Hotel.builder()
                    .name("Copenhagen Admiral Hotel")
                    .address("Toldbodgade 24-28, 1253 København")
                    .rooms(36)
                    .stars(4)
                    .build();

            Room admRoom1 = new Room();
            admRoom1.setNumber(101);
            admRoom1.setPrice(1600);
            admRoom1.setType("Standard");
            admRoom1.setHotel(admiral);

            Room admRoom2 = new Room();
            admRoom2.setNumber(201);
            admRoom2.setPrice(2800);
            admRoom2.setType("Suite");
            admRoom2.setHotel(admiral);

            admiral.getRoomSet().add(admRoom1);
            admiral.getRoomSet().add(admRoom2);

            // --- 4. Radisson Collection Royal Hotel ---
            Hotel radisson = Hotel.builder()
                    .name("Radisson Collection Royal Hotel")
                    .address("Hammerichsgade 1, 1611 København")
                    .rooms(26)
                    .stars(5)
                    .build();

            Room radRoom1 = new Room();
            radRoom1.setNumber(101);
            radRoom1.setPrice(2200);
            radRoom1.setType("Standard");
            radRoom1.setHotel(radisson);

            Room radRoom2 = new Room();
            radRoom2.setNumber(201);
            radRoom2.setPrice(4500);
            radRoom2.setType("Suite");
            radRoom2.setHotel(radisson);

            Room radRoom3 = new Room();
            radRoom3.setNumber(301);
            radRoom3.setPrice(6900);
            radRoom3.setType("Honeymoon Suite");
            radRoom3.setHotel(radisson);

            radisson.getRoomSet().add(radRoom1);
            radisson.getRoomSet().add(radRoom2);
            radisson.getRoomSet().add(radRoom3);

            // --- 5. Scandic Palace Hotel ---
            Hotel scandic = Hotel.builder()
                    .name("Scandic Palace Hotel")
                    .address("Rådhuspladsen 57, 1550 København")
                    .rooms(17)
                    .stars(4)
                    .build();

            Room scRoom1 = new Room();
            scRoom1.setNumber(101);
            scRoom1.setPrice(1500);
            scRoom1.setType("Standard");
            scRoom1.setHotel(scandic);

            Room scRoom2 = new Room();
            scRoom2.setNumber(201);
            scRoom2.setPrice(2600);
            scRoom2.setType("Suite");
            scRoom2.setHotel(scandic);

            scandic.getRoomSet().add(scRoom1);
            scandic.getRoomSet().add(scRoom2);

            // --- 6. Copenhagen Marriott Hotel ---
            Hotel marriott = Hotel.builder()
                    .name("Copenhagen Marriott Hotel")
                    .address("Kalvebod Brygge 5, 1560 København")
                    .rooms(40)
                    .stars(5)
                    .build();

            Room marRoom1 = new Room();
            marRoom1.setNumber(101);
            marRoom1.setPrice(2100);
            marRoom1.setType("Standard");
            marRoom1.setHotel(marriott);

            Room marRoom2 = new Room();
            marRoom2.setNumber(201);
            marRoom2.setPrice(3900);
            marRoom2.setType("Suite");
            marRoom2.setHotel(marriott);

            Room marRoom3 = new Room();
            marRoom3.setNumber(301);
            marRoom3.setPrice(6200);
            marRoom3.setType("Honeymoon Suite");
            marRoom3.setHotel(marriott);

            marriott.getRoomSet().add(marRoom1);
            marriott.getRoomSet().add(marRoom2);
            marriott.getRoomSet().add(marRoom3);

            // --- 7. Absalon Hotel ---
            Hotel absalon = Hotel.builder()
                    .name("Absalon Hotel")
                    .address("Helgolandsgade 15, 1653 København")
                    .rooms(17)
                    .stars(4)
                    .build();

            Room abRoom1 = new Room();
            abRoom1.setNumber(101);
            abRoom1.setPrice(1300);
            abRoom1.setType("Standard");
            abRoom1.setHotel(absalon);

            Room abRoom2 = new Room();
            abRoom2.setNumber(201);
            abRoom2.setPrice(2300);
            abRoom2.setType("Suite");
            abRoom2.setHotel(absalon);

            absalon.getRoomSet().add(abRoom1);
            absalon.getRoomSet().add(abRoom2);

            // --- 8. Imperial Hotel ---
            Hotel imperial = Hotel.builder()
                    .name("Imperial Hotel")
                    .address("Vester Farimagsgade 9, 1606 København")
                    .rooms(30)
                    .stars(4)
                    .build();

            Room impRoom1 = new Room();
            impRoom1.setNumber(101);
            impRoom1.setPrice(1400);
            impRoom1.setType("Standard");
            impRoom1.setHotel(imperial);

            Room impRoom2 = new Room();
            impRoom2.setNumber(201);
            impRoom2.setPrice(2600);
            impRoom2.setType("Suite");
            impRoom2.setHotel(imperial);

            imperial.getRoomSet().add(impRoom1);
            imperial.getRoomSet().add(impRoom2);

            // --- 9. Scandic Falkoner ---
            Hotel scandicFalkoner = Hotel.builder()
                    .name("Scandic Falkoner")
                    .address("Falkoner Allé 9, 2000 Frederiksberg")
                    .rooms(33)
                    .stars(4)
                    .build();

            Room sfRoom1 = new Room();
            sfRoom1.setNumber(101);
            sfRoom1.setPrice(1200);
            sfRoom1.setType("Standard");
            sfRoom1.setHotel(scandicFalkoner);

            Room sfRoom2 = new Room();
            sfRoom2.setNumber(201);
            sfRoom2.setPrice(2400);
            sfRoom2.setType("Suite");
            sfRoom2.setHotel(scandicFalkoner);

            scandicFalkoner.getRoomSet().add(sfRoom1);
            scandicFalkoner.getRoomSet().add(sfRoom2);

            // --- 10. Wakeup Copenhagen ---
            Hotel wakeup = Hotel.builder()
                    .name("Wakeup Copenhagen")
                    .address("Carsten Niebuhrs Gade 11, 1577 København")
                    .rooms(51)
                    .stars(3)
                    .build();

            Room wuRoom1 = new Room();
            wuRoom1.setNumber(101);
            wuRoom1.setPrice(900);
            wuRoom1.setType("Standard");
            wuRoom1.setHotel(wakeup);

            Room wuRoom2 = new Room();
            wuRoom2.setNumber(201);
            wuRoom2.setPrice(1200);
            wuRoom2.setType("Standard+");
            wuRoom2.setHotel(wakeup);

            wakeup.getRoomSet().add(wuRoom1);
            wakeup.getRoomSet().add(wuRoom2);

            // --- 11. Axel Guldsmeden ---
            Hotel axelGuldsmeden = Hotel.builder()
                    .name("Axel Guldsmeden")
                    .address("Helgolandsgade 11, 1653 København")
                    .rooms(21)
                    .stars(4)
                    .build();

            Room axRoom1 = new Room();
            axRoom1.setNumber(101);
            axRoom1.setPrice(1700);
            axRoom1.setType("Standard");
            axRoom1.setHotel(axelGuldsmeden);

            Room axRoom2 = new Room();
            axRoom2.setNumber(201);
            axRoom2.setPrice(2900);
            axRoom2.setType("Suite");
            axRoom2.setHotel(axelGuldsmeden);

            Room axRoom3 = new Room();
            axRoom3.setNumber(301);
            axRoom3.setPrice(5100);
            axRoom3.setType("Honeymoon Suite");
            axRoom3.setHotel(axelGuldsmeden);

            axelGuldsmeden.getRoomSet().add(axRoom1);
            axelGuldsmeden.getRoomSet().add(axRoom2);
            axelGuldsmeden.getRoomSet().add(axRoom3);

            // --- persist all hotels (rooms cascade) ---
            em.persist(dAngleterre);
            em.persist(nimb);
            em.persist(admiral);
            em.persist(radisson);
            em.persist(scandic);
            em.persist(marriott);
            em.persist(absalon);
            em.persist(imperial);
            em.persist(scandicFalkoner);
            em.persist(wakeup);
            em.persist(axelGuldsmeden);

            tx.commit();
        }
    }
}
