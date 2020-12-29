package no.sonkin.ticketscore.controllers;

import com.j256.ormlite.dao.Dao;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Ticket;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class TicketController {

    private Dao<Ticket, String> ticketDao;

    public TicketController(Dao<Ticket, String> ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Ticket createTicket(Ticket ticket) throws TicketException {
        try {
            if (ticket.getCreated() == null) {
                ticket.setCreated(new Timestamp(System.currentTimeMillis()));
            }
            ticketDao.create(ticket);
            return ticket;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while creating ticket: " + ex.getMessage());
        }
    }

    public Ticket closeTicket(Ticket ticket) throws TicketException {
        return markTicketClosed(ticket.getID());
    }

    public Ticket closeTicket(int id) throws TicketException {
        return markTicketClosed(id);
    }

    private Ticket markTicketClosed(int id) throws TicketException {
        try {
            Ticket ticket = ticketDao.queryForId(String.valueOf(id));
            if (ticket != null) {
                ticket.close();
                ticketDao.update(ticket);
                return ticket;

            } else {
                throw new TicketException("Could not find the requested ticket");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while closing ticket: " + ex.getMessage());
        }
    }

    public List<Ticket> getAllTickets() throws TicketException {
        try {
            return ticketDao.queryForAll();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }

    public List<Ticket> getOpenTickets() throws TicketException {
        try {
            return ticketDao.queryForEq("closed", false);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }

    public Ticket getTicketById(int id) throws TicketException {
        try {
            return ticketDao.queryForId(String.valueOf(id));

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching ticket with id " + id + ": " + ex.getMessage());
        }
    }

    public Ticket getTicketByPlayerAndId(int id, UUID uuid) throws TicketException {
        try {
            List<Ticket> tickets = ticketDao.queryBuilder().where().eq("ID", id).and().eq("playerUUID", uuid).queryBuilder().limit(1L).query();
            if (tickets.isEmpty()) {
                return null;
            } else {
                return tickets.get(0);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching ticket with id " + id + ": " + ex.getMessage());
        }
    }

    public Ticket getLatestTicket(UUID uuid) throws TicketException {
        try {
            List<Ticket> tickets = ticketDao.queryBuilder().where().eq("playerUUID", uuid).queryBuilder().orderBy("created", false).limit(1L).query();
            if (tickets.isEmpty()) {
                return null;
            } else {
                return tickets.get(0);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching the latest ticket of player: " + ex.getMessage());
        }
    }

    public List<Ticket> getTicketsByPlayer(UUID playerUUID, boolean includeClosed) throws TicketException {
        try {
            if (includeClosed) {
                return getTicketsByPlayer(playerUUID);
            } else {
                return ticketDao.queryBuilder().where()
                        .eq("playerUUID", playerUUID)
                        .and()
                        .eq("closed", false)
                        .query();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }

    public List<Ticket> getTicketsByPlayer(UUID playerUUID) throws TicketException {
        try {
            return ticketDao.queryForEq("playerUUID", playerUUID);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }
}
