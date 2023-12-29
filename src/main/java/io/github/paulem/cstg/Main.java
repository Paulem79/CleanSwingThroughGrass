package io.github.paulem.cstg;

import io.github.paulem.cstg.papi.Version;
import io.github.paulem.cstg.papi.VersionMethod;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class Main extends JavaPlugin implements Listener {
    public static List<Material> grasses = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("CleanSwingThroughGrass is enabled!");
        getServer().getPluginManager().registerEvents(this, this);
        Version version = Version.getVersion(VersionMethod.SERVER);
        grasses.add(Material.TALL_GRASS);
        if(version.getMinor() >= 13) {
            grasses.add(Material.SEAGRASS);
            grasses.add(Material.TALL_SEAGRASS);
        }
        if(version.getMinor() > 20 || (version.getMinor() == 20 && version.getRevision() >= 4)) {
            grasses.add(Material.getMaterial("short_grass"));
        } else grasses.add(Material.GRASS);
    }

    @Override
    public void onDisable() {
        getLogger().info("CleanSwingThroughGrass is disabled!");
    }

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Entity nearestEntityInSight = getNearestEntityInSight(player, 6);
        if(!(nearestEntityInSight instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) nearestEntityInSight;

        if(!entity.getUniqueId().equals(player.getUniqueId()) && onlyGrassBetween(player, entity)){
            event.setCancelled(true);
            int attackCooldown = 5;
            Collection<AttributeModifier> foundAttributes = player.getInventory().getItemInMainHand().getType().getDefaultAttributeModifiers(EquipmentSlot.HAND).get(Attribute.GENERIC_ATTACK_SPEED);
            if(!foundAttributes.isEmpty()) attackCooldown = (int) (1 / (foundAttributes.stream().findFirst().get().getAmount() + 4) * 20);
            player.setCooldown(player.getInventory().getItemInMainHand().getType(), attackCooldown);
            player.attack(entity);
        }
    }

    public static boolean onlyGrassBetween(Player player, LivingEntity entity){
        Location playerLocation = player.getLocation();
        Location playerEyeLocation = player.getEyeLocation();

        Location entityPlayerFacingLocation = entity.getLocation();
        entityPlayerFacingLocation.setDirection(playerLocation.getDirection().multiply(-1));

        Location entityEyePlayerFacingLocation = entity.getEyeLocation();
        entityEyePlayerFacingLocation.setDirection(playerLocation.getDirection().multiply(-1));

        return (isOnlyGrassObstructed(playerLocation, entityPlayerFacingLocation) && isOnlyGrassObstructed(entityPlayerFacingLocation, playerLocation)) ||
                (isOnlyGrassObstructed(playerEyeLocation, entityEyePlayerFacingLocation) && isOnlyGrassObstructed(entityEyePlayerFacingLocation, playerEyeLocation));
    }

    public static boolean isOnlyGrassObstructed(Location start, Location end){
        if(start.getWorld() == null) return false;
        RayTraceResult raytrace = start.getWorld().rayTraceBlocks(start, start.getDirection(), start.distance(end));
        if(raytrace == null) return false;
        Block block = raytrace.getHitBlock();
        if(block == null || block.getType().isAir()) return true;
        return grasses.contains(block.getType());
    }

    public static Entity getNearestEntityInSight(Player player, int range) {
        List<org.bukkit.entity.Entity> entities = player.getNearbyEntities(range, range, range);

        entities.removeIf(next -> !(next instanceof LivingEntity) || next == player);

        List<Block> sight = player.getLineOfSight(new HashSet<>(Arrays.asList(Material.values())), range);

        for (Block block : sight) {
            Location low = block.getLocation();
            Location high = low.clone().add(1, 1, 1);
            BoundingBox blockBoundingBox = new BoundingBox(low.getX(), low.getY(), low.getZ(), high.getX(), high.getY(), high.getZ());

            for (org.bukkit.entity.Entity entity : entities) {
                if (entity.getLocation().distance(player.getEyeLocation()) <= range && entity.getBoundingBox().overlaps(blockBoundingBox)) {
                    return entity;
                }
            }
        }
        return null;
    }
}
