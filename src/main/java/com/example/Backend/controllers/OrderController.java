package com.example.Backend.controllers;

import com.example.Backend.dto.ReceiptDto;
import com.example.Backend.modelos.Order;
import com.example.Backend.modelos.OrderLine;
import com.example.Backend.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Órdenes", description = "Crear, pagar y obtener recibo")
public class OrderController {
    @Autowired
    private OrderService orders;

    record CreateOrderDto(String showtimeId, String holdId,
                          java.util.List<LineDto> items) {}
    record LineDto(String reference, int qty) {}

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody CreateOrderDto dto) {
        // items: [{reference:"<menuItemId>", qty:2}, ...]
        var itemLines = (dto.items()==null) ? java.util.List.<OrderLine>of()
                : dto.items().stream().map(d -> {
            OrderLine l = new OrderLine();
            l.setType("MENU_ITEM"); l.setReference(d.reference()); l.setQty(d.qty());
            return l;
        }).toList();

        return new ResponseEntity<>(orders.createFromHold(dto.showtimeId(), dto.holdId(), itemLines),
                HttpStatus.CREATED);
    }

    record PayDto(String holdId) {}
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Order> pay(@PathVariable String orderId, @RequestBody PayDto dto) {
        return new ResponseEntity<>(orders.pay(orderId, dto.holdId()), HttpStatus.OK);
    }

    @GetMapping("/{orderId}/receipt")
    public ResponseEntity<ReceiptDto> receipt(@PathVariable String orderId) {
        return new ResponseEntity<>(orders.receipt(orderId), HttpStatus.OK);
    }
}
