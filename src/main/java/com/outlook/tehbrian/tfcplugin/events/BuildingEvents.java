package com.outlook.tehbrian.tfcplugin.events;

import com.outlook.tehbrian.tfcplugin.TFCPlugin;
import com.outlook.tehbrian.tfcplugin.util.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@SuppressWarnings({"unused", "deprecation"})
public class BuildingEvents implements Listener {

    private final TFCPlugin main;

    public BuildingEvents(TFCPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (main.getConfig().getBoolean("options.explosions_disabled")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            if (main.getConfig().getBoolean("options.explosions_damage_disabled")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (main.getConfig().getBoolean("options.leaves_decay_disabled")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCropsTrample(PlayerInteractEvent event) {
        if (main.getConfig().getBoolean("options.crop_trampling_disabled")) {
            if (event.getAction() == Action.PHYSICAL) {
                if (event.getClickedBlock().getType() == Material.SOIL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDragonEggTeleport(BlockFromToEvent event) {
        if (main.getConfig().getBoolean("options.dragon_egg_teleporting_disabled")) {
            if (event.getBlock().getType() == Material.DRAGON_EGG) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getPlayer().hasPermission("tfcplugin.signcolor")) {
            String[] lines = event.getLines();
            for (int l = 0; l < 4; l++) {
                event.setLine(l, MiscUtils.color(lines[l]));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
        if (event.getPlayer().isSneaking()) return;

        BlockState blockState = event.getClickedBlock().getState();
        if (!(blockState instanceof Sign)) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.SIGN) return;

        Bukkit.getScheduler().runTask(main, () -> {
            Sign sign = (Sign) blockState;
            event.getPlayer().openSign(sign);
        });
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onIronTrapDoorInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
        if (event.getPlayer().isSneaking()) return;
        if (event.getClickedBlock().getType() != Material.IRON_TRAPDOOR) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;

        Bukkit.getScheduler().runTask(main, () -> {
            Block block = event.getClickedBlock();
            byte data = block.getData();
            byte newData = 0;
            if (data >= 0 && data < 4) {
                newData = (byte) (data + 4);
            } else if (data >= 4 && data < 8) {
                newData = (byte) (data - 4);
            } else if (data >= 8 && data < 12) {
                newData = (byte) (data + 4);
            } else if (data >= 12 && data < 16) {
                newData = (byte) (data - 4);
            }
            block.setData(newData, true);
        });
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGlazedTerracottaInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
        if (!event.getPlayer().isSneaking()) return;
        if (!event.getClickedBlock().getType().name().contains("GLAZED")) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;

        Bukkit.getScheduler().runTask(main, () -> {
            Block block = event.getClickedBlock();
            byte data = block.getData();
            byte newData = (byte) (data + 1);
            if (data < 0 || data >= 4) {
                newData = 0;
            }
            block.setData(newData, true);
        });
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSlabBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) return;

        Material material = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (material != Material.STEP && material != Material.WOOD_STEP && material != Material.STONE_SLAB2 && material != Material.PURPUR_SLAB) return;

        Block block = event.getClickedBlock();
        byte data = block.getData();

        if (data <= 7) {
            if (block.getType() == Material.DOUBLE_STEP) {
                event.setCancelled(true);
                if (isTop(event.getPlayer(), block)) {
                    block.setType(Material.STEP);
                    block.setData(data);
                } else {
                    block.setType(Material.STEP);
                    block.setData((byte) (data + 8));
                }
            }

            if (block.getType() == Material.WOOD_DOUBLE_STEP) {
                event.setCancelled(true);
                if (isTop(event.getPlayer(), block)) {
                    block.setType(Material.WOOD_STEP);
                    block.setData(data);
                } else {
                    block.setType(Material.WOOD_STEP);
                    block.setData((byte) (data + 8));
                }
            }

            if (block.getType() == Material.DOUBLE_STONE_SLAB2) {
                event.setCancelled(true);
                if (isTop(event.getPlayer(), block)) {
                    block.setType(Material.STONE_SLAB2);
                    block.setData(data);
                } else {
                    block.setType(Material.STONE_SLAB2);
                    block.setData((byte) (data + 8));
                }
            }

            if (block.getType() == Material.PURPUR_DOUBLE_SLAB) {
                event.setCancelled(true);
                if (isTop(event.getPlayer(), block)) {
                    block.setType(Material.PURPUR_SLAB);
                    block.setData(data);
                } else {
                    block.setType(Material.PURPUR_SLAB);
                    block.setData((byte) (data + 8));
                }
            }
        }
    }

    private boolean isTop(Player player, Block block) {
        Location start = player.getEyeLocation().clone();
        while (!start.getBlock().equals(block) && start.distance(player.getEyeLocation()) < 6.0D) {
            start.add(player.getEyeLocation().getDirection().multiply(0.05D));
        }
        return start.getY() % 1.0D > 0.5D;
    }
}
