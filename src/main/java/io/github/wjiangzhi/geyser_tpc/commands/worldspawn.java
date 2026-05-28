package io.github.wjiangzhi.geyser_tpc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.GeyserTPC;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.Objects;
import java.util.Optional;

import static io.github.wjiangzhi.geyser_tpc.utils.tools.*;
import static net.minecraft.commands.Commands.argument;

import static net.minecraft.world.level.Level.OVERWORLD;

public class worldspawn {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        if (!GeyserTPC.TELEPORT_COMMANDS_LOADED) {
            commandDispatcher.register(Commands.literal("worldspawn")
                    .executes(context -> {
                        final ServerPlayer player = context.getSource().getPlayerOrException();

                        try {
                            toWorldSpawn(player, false);

                        } catch (Exception error) {
                            Constants.LOGGER.error("Error while going to the worldspawn! => ", error);
                            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                            return 1;
                        }
                        return 0;
                    })
                    .then(argument("Disable Safety", BoolArgumentType.bool())
                            .executes(context -> {
                                final boolean safety = BoolArgumentType.getBool(context, "Disable Safety");
                                final ServerPlayer player = context.getSource().getPlayerOrException();

                                try {
                                    toWorldSpawn(player, safety);

                                } catch (Exception error) {
                                    Constants.LOGGER.error("Error while going to the worldspawn! => ", error);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return 1;
                                }
                                return 0;
                            })));
        }

    }

    private static void toWorldSpawn(ServerPlayer player, boolean safetyDisabled) throws NullPointerException {
        // todo! make the dimension customizable
        ServerLevel world = GeyserTPC.SERVER.getLevel(OVERWORLD);
        BlockPos worldSpawn = Objects.requireNonNull(world, "Overworld cannot be null!").getLevelData().getRespawnData().pos();

        if (!safetyDisabled) {
            Optional<BlockPos> teleportData = getSafeBlockPos(worldSpawn, world);

            if (teleportData.isPresent()) {
                BlockPos safeBlockPos = teleportData.get();

                // check if the player is already at this location
                if (player.blockPosition().equals(safeBlockPos) && player.level() == world) {

                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.worldspawn.same", player).withStyle(ChatFormatting.AQUA), true);
                } else {
                    Vec3 teleportPos = new Vec3(safeBlockPos.getX() + 0.5, safeBlockPos.getY(), safeBlockPos.getZ() + 0.5);

                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.worldspawn.go", player), true);
                    Teleporter(player, world, teleportPos);
                }

            } else {
                //noinspection ConstantValue
                if (GeyserTPC.GEYSER_LOADED && GeyserApi.api() != null && GeyserApi.api().connectionByUuid(player.getUUID()) != null) {
                    if (GeyserTPC.FLOODGATE_LOADED) {
                        FloodgateApi.getInstance().sendForm(
                                player.getUUID(),
                                ModalForm.builder()
                                        .title(getTranslatedText("commands.geyser_tpc.common.noSafeLocation", player).getString())
                                        .content(getTranslatedText("commands.geyser_tpc.common.safetyIsForLosers", player).getString())
                                        .button1(getTranslatedText("commands.geyser_tpc.common.forceTeleport", player).getString())
                                        .button2(getTranslatedText("gui.geyser_tpc.universal.gui.cancel", player).getString())

                                        .validResultHandler(response -> {
                                            if (response.clickedFirst()) {
                                                var dispatcher = GeyserTPC.SERVER.getCommands().getDispatcher();
                                                try {
                                                    dispatcher.execute(dispatcher.parse("worldspawn true", player.createCommandSourceStack()));
                                                } catch (CommandSyntaxException _) {
                                                }

                                            }
                                        })
                        );
                        return;
                    } else {
                        player.sendSystemMessage(getTranslatedText("mod.geyser_tpc.dependencies.floodgate.noload", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                    }
                }
                player.sendSystemMessage(
                        Component.empty()
                                .append(getTranslatedText("commands.geyser_tpc.common.noSafeLocation", player)
                                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                                )
                                .append("\n")
                                .append(getTranslatedText("commands.geyser_tpc.common.safetyIsForLosers", player)
                                        .withStyle(ChatFormatting.WHITE)
                                )
                                .append("\n")
                                .append(getTranslatedText("commands.geyser_tpc.common.forceTeleport", player)
                                        .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD)
                                        .withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/back true")))
                                )
                                .append("\n"), false);
            }
        } else {
            if (player.blockPosition().equals(worldSpawn) && player.level() == world) {

                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.worldspawn.same", player).withStyle(ChatFormatting.AQUA), true);
            } else {

                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.worldspawn.go", player), true);
                Teleporter(player, world, new Vec3(worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5));
            }
        }
    }
}
