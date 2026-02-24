package com.shards.plugin.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private UUID uuid;
    private String name;
    private long balance;
    private long lastUpdated;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.balance = 0;
        this.lastUpdated = System.currentTimeMillis();
    }
}