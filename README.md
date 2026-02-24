# Shards Plugin

**Shards** is a fully configurable Minecraft server plugin focused on custom shops, AFK reward zones, and persistent data storage.  
It’s designed for server owners who want a flexible system without obfuscation — the source is open and editable.

This project is **open source**, not obfuscated, and intended to be modified if needed.

---

## ✨ Features

### 🛒 Shop System
- Full in-game shop GUI
- Buy items such as spawners and crate keys
- Create, edit, and delete shop items
- `/shardshopedit` GUI editor with chat-based value input
- Fully customizable shop layout

### 🧱 Configuration
- Everything configurable in `config.yml`
- Editable messages with color code support
- Flexible pricing and shop setup

### 🗄 Database Support
- Supports SQLite
- Supports MySQL
- Persistent storage for shops and player data

### 💤 AFK Zone System
- Create AFK reward zones using WorldEdit selections
- Automatically reward players inside zones
- Designed for prisons, SMPs, and economy servers

### 🔌 Integrations
Supports hooking into:

- Vault
- EssentialsX
- WorldEdit
- SmartSpawners

---

## ⚠️ Placeholder Support

This plugin **does NOT support PlaceholderAPI directly**.

If you want placeholders, you **must install the Placeholder Plugin** alongside PlaceholderAPI.

---

## 📜 Commands

| Command | Description | Permission |
|--------|-------------|------------|
| `/shardshop` | Opens the main shop | `shards.shop` |
| `/shards` | Admin management command | `shards.admin` |
| `/afkzone` | Manage AFK zones | `shards.afk` |
| `/shardshopedit` | Opens shop editor GUI | `shards.shopedit` |
| `/shardshopeditor` | Alias of shop editor | `shards.shopedit` |

---

## 🔑 Permissions

---

## 💬 Messages

- All messages are configurable
- Supports Minecraft color codes
- Edit inside the config file

---

## 📦 Installation

1. Download the plugin `.jar`
2. Place it into your server’s `/plugins` folder
3. Install required dependencies (Vault, etc.)
4. Start the server
5. Configure `config.yml`
6. Reload or restart

---

## 🛠 Requirements

- Paper / Spigot server (1.21.11)
- Java 21 required
- Vault for economy support
- Optional integrations listed above

---

## 📖 Open Source Notice

This plugin is:
- ✔ Open source  
- ✔ Not obfuscated  
- ✔ Free to modify  
- ✔ Intended for learning and customization  

If you improve it, consider contributing back 🙂
