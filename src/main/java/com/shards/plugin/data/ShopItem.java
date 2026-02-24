package com.shards.plugin.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShopItem {
    private String id;
    private int slot;
    private String type;
    private String material;
    private String name;
    private List<String> lore = new ArrayList<>();
    private long cost;
    private List<String> commands = new ArrayList<>();
    private int amount;
    private String spawnerType;
    private String crateName;
}