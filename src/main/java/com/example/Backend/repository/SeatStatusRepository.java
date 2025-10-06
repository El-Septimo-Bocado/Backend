package com.example.Backend.repository;

import com.example.Backend.modelos.SeatStatus;
import org.springframework.stereotype.Repository;

@Repository
public class SeatStatusRepository {
    // clave compuesta: showtimeId + seatCode
    private final java.util.Map<String, SeatStatus> db = new java.util.concurrent.ConcurrentHashMap<>();

    private String key(String showtimeId, String code) { return showtimeId + "::" + code; }

    public SeatStatus find(String showtimeId, String code) {
        return db.get(key(showtimeId, code));
    }

    public void save(SeatStatus s) {
        db.put(key(s.getShowtimeId(), s.getSeatCode()), s);
    }

    public java.util.List<SeatStatus> findAllByShowtime(String showtimeId) {
        return db.values().stream().filter(ss -> ss.getShowtimeId().equals(showtimeId)).toList();
    }

    public void deleteByShowtime(String showtimeId) {
        java.util.List<String> keysToRemove = new java.util.ArrayList<>();
        for (String k : db.keySet()) {
            if (k.startsWith(showtimeId + "::")) keysToRemove.add(k);
        }
        for (String k : keysToRemove) db.remove(k);
    }

    public void delete(String showtimeId, String seatCode) {
        db.remove(key(showtimeId, seatCode));
    }
}
