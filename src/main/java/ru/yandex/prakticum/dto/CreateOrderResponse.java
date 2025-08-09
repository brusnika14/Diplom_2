package ru.yandex.prakticum.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderResponse {
    private boolean success;
    private OrderData order;
    private String name;

    @Data
    public static class OrderData {
        private List<String> ingredients;
        private String _id;
        private String status;
        private String name;
        private String createdAt;
        private String updatedAt;
        private int number;

    }
}