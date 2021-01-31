package no.sonkin.ticketscore.controllers;

import com.j256.ormlite.dao.Dao;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Ticket;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class TicketController {

    private final Dao<Ticket, Integer> ticketDao;

    public TicketController(Dao<Ticket, Integer> ticketDao) {
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

    public Ticket closeTicket(Ticket ticket, String closedBy) throws TicketException {
        return markTicketClosed(ticket.getID(), closedBy);
    }

    public Ticket closeTicket(int id, String closedBy) throws TicketException {
        return markTicketClosed(id, closedBy);
    }

    private Ticket markTicketClosed(int id, String closedBy) throws TicketException {
        try {
            Ticket ticket = ticketDao.queryForId(id);
            if (ticket != null) {
                ticket.close();
                ticket.setClosedBy(closedBy);
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

    public Ticket reopenTicket(int id, String openedBy) throws TicketException {
        return markTicketOpen(id, openedBy);
    }

    private Ticket markTicketOpen(int id, String openedBy) throws TicketException {
        try {
            Ticket ticket = ticketDao.queryForId(id);
            if (ticket != null) {
                ticket.open();
                ticket.setClosedBy(null);
                ticketDao.update(ticket);

                return ticket;

            } else {
                throw new TicketException("Could not find the requested ticket");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while opening ticket: " + ex.getMessage());
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

    public List<Ticket> getClosedTickets() throws TicketException {
        try {
            return ticketDao.queryForEq("closed", true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }

    public Ticket getTicketById(int id) throws TicketException {
        try {
            return ticketDao.queryForId(id);

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

    public List<Ticket> getTicketsByPlayer(String playerName, boolean includeClosed) throws TicketException {
        try {
            if (includeClosed) {
                return getTicketsByPlayer(playerName);
            } else {
                return ticketDao.queryBuilder().where()
                        .eq("playerName", playerName)
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

    public List<Ticket> getTicketsByPlayer(String playerName) throws TicketException {
        try {
            return ticketDao.queryForEq("playerName", playerName);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }

    public List<Ticket> getFilteredTickets(HashMap<String, Object> filter) throws TicketException {
        try {
            return ticketDao.queryForFieldValues(filter);
        } catch (SQLException ex) {
            throw new TicketException("Encountered sql error while fetching tickets: " + ex.getMessage());
        }
    }

    public List<Ticket> getPlayersWithOpenTickets() throws TicketException {
        try {
            return ticketDao.queryBuilder().selectColumns("playerName").distinct().where().eq("closed", false).query();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TicketException("Encountered sql error while trying to get players with open tickets: " + ex.getMessage());
        }
    }

    public Ticket addComment(Comment comment, int ticketID) throws TicketException {
        try {
            Ticket ticket = ticketDao.queryForId(ticketID);

            if (ticket.isClosed()) {
                throw new TicketException("Could not add comment: ticket §a" + ticketID + " §ris closed");
            }

            ticket.getComments().add(comment);

            return ticket;
        } catch (SQLException ex) {
            throw new TicketException("Could not add comment: " + ex.getMessage());
        }
    }

    public void updateTicket(Ticket ticket) throws TicketException {
        try {
            ticketDao.update(ticket);
        } catch (SQLException ex) {
            throw new TicketException("Could not update ticket: " + ex.getMessage());
        }
    }
}
