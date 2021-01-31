package no.sonkin.ticketscore.controllers;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import no.sonkin.ticketscore.exceptions.BufferException;
import no.sonkin.ticketscore.models.BufferItem;

import java.sql.SQLException;

public final class BufferController {
    private final Dao<BufferItem, Integer> bufferDao;

    public BufferController(Dao<BufferItem, Integer> bufferDao) {
        this.bufferDao = bufferDao;
    }

    public CloseableWrappedIterable<BufferItem> getIterable() {
        return bufferDao.getWrappedIterable();
    }

    public BufferItem addToBuffer(BufferItem bufferItem) throws BufferException {
        try {
            bufferDao.create(bufferItem);
            return bufferItem;
        } catch (SQLException e) {
            throw new BufferException("BufferException while adding to buffer: " + e.getMessage());
        }
    }
}
