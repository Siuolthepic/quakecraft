/*
 *  Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lambdaurora.quakecraft.game;

import me.lambdaurora.quakecraft.game.map.QuakecraftMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.util.BlockBounds;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

/**
 * Represents the Quakecraft spawn logic.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class QuakecraftSpawnLogic
{
    private static final Random        RANDOM    = new Random();
    private final        GameWorld     world;
    private final        QuakecraftMap map;
    private              int           lastSpawn = -1;

    public QuakecraftSpawnLogic(@NotNull GameWorld world, @NotNull QuakecraftMap map)
    {
        this.world = world;
        this.map = map;
    }

    public void spawnPlayer(@NotNull ServerPlayerEntity player)
    {
        int index = 0;
        if (this.map.getSpawnCount() > 1) {
            index = RANDOM.nextInt(this.map.getSpawnCount() - 1);
            if (index >= lastSpawn)
                index++;
            if (index >= this.map.getSpawnCount())
                index = 0;
        }

        this.lastSpawn = index;

        Pair<BlockPos, Direction> spawnPos = this.map.getSpawn(index);
        player.teleport(this.world.getWorld(), spawnPos.getFirst().getX(), spawnPos.getFirst().getY(), spawnPos.getFirst().getZ(), spawnPos.getSecond().asRotation(), 0.f);
    }

    public void resetWaitingPlayer(@NotNull ServerPlayerEntity player)
    {
        player.setGameMode(GameMode.ADVENTURE);
        player.inventory.clear();

        ItemStack leaveGame = ItemStackBuilder.of(Items.RED_BED)
                .setName(new LiteralText("Leave Lobby").formatted(Formatting.YELLOW))
                .build();
        player.inventory.insertStack(8, leaveGame);
    }

    /**
     * Spawns a player in the waiting room.
     *
     * @param player The player to spawn.
     */
    public void spawnWaitingPlayer(@NotNull ServerPlayerEntity player)
    {
        ServerWorld world = this.world.getWorld();

        BlockBounds bounds = this.map.waitingSpawn;
        BlockPos min = bounds.getMin();
        BlockPos max = bounds.getMax();

        double x = MathHelper.nextDouble(player.getRandom(), min.getX(), max.getX());
        double z = MathHelper.nextDouble(player.getRandom(), min.getZ(), max.getZ());
        double y = min.getY() + 0.5;

        player.teleport(world, x, y, z, 0.f, 0.f);
    }
}