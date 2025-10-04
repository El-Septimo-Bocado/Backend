package com.example.Backend.controllers;

import com.example.Backend.modelos.SeatStatus;
import com.example.Backend.service.SeatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
class SeatingController {
    private final SeatingService seating;
    @Autowired
    SeatingController(SeatingService s){ this.seating = s; }

    // mapa de asientos
    @GetMapping("/{showtimeId}/seats")
    public ResponseEntity<List<SeatStatus>> seats(@PathVariable String showtimeId){
        return ResponseEntity.ok(seating.getMap(showtimeId));
    }

    // Bloquear asientos
    record HoldReq(List<String> seatCodes){}
    record HoldRes(String holdId, long expiresAt){}
    @PostMapping("/{showtimeId}/seats/hold")
    public ResponseEntity<HoldRes> hold(@PathVariable String showtimeId, @RequestBody HoldReq req){
        String hid = seating.hold(showtimeId, req.seatCodes(), 5 * 60_000); // 5 min
        return ResponseEntity.ok(new HoldRes(hid, System.currentTimeMillis() + 5*60_000));
    }

    // Liberar
    record ReleaseReq(String holdId){}
    @PostMapping("/{showtimeId}/seats/release")
    public ResponseEntity<Void> release(@PathVariable String showtimeId, @RequestBody ReleaseReq req){
        seating.release(showtimeId, req.holdId());
        return ResponseEntity.noContent().build();
    }
}
