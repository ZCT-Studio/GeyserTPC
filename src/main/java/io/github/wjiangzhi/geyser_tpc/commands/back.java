package io.github.wjiangzhi.geyser_tpc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.GeyserTPC;
import io.github.wjiangzhi.geyser_tpc.common.DeathLocation;
import io.github.wjiangzhi.geyser_tpc.storage.DeathLocationStorage;
import io.github.wjiangzhi.geyser_tpc.utils.tools;
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

import java.util.Optional;

import static io.github.wjiangzhi.geyser_tpc.utils.tools.getSafeBlockPos;
import static io.github.wjiangzhi.geyser_tpc.utils.tools.getTranslatedText;
import static net.minecraft.commands.Commands.argument;

public class back {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        if (!GeyserTPC.TELEPORT_COMMANDS_LOADED) {
            commandDispatcher.register(Commands.literal("back")
                    .executes(context -> {
                        final ServerPlayer player = context.getSource().getPlayerOrException();

                        try {
                            ToDeathLocation(player, false);

                        } catch (Exception e) {
                            Constants.LOGGER.error("Error while going back! => ", e);
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
                                    ToDeathLocation(player, safety);

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while going back! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return 1;
                                }
                                return 0;
                            }))
            );
        }
    }


    // -----

    // Gets the DeathLocation of the player and teleports the player to it
    private static void ToDeathLocation(ServerPlayer player, boolean safetyDisabled) throws Exception {
        DeathLocation deathLocation = DeathLocationStorage
                .getDeathLocation(player.getStringUUID())
                .orElse(null);

        if (deathLocation == null) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.noLocation", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        // Get the world, otherwise give a warning and error message
        ServerLevel deathLocationWorld = deathLocation.getWorld().orElse(null);

        if (deathLocationWorld == null) {
            Constants.LOGGER.warn("({}) Error while going back! \nCouldn't find a world with the id: \"{}\" \nAvailable worlds: {}",
                    player.getName().getString(),
                    deathLocation.getWorldString(),
                    tools.getWorldIds());

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.worldNotFound", player)
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);

            return;
        }

        BlockPos teleportBlockPos;

        // Sets the teleportBlockPos based on if it should do safety checking
        if (!safetyDisabled) {
            Optional<BlockPos> safeBlockPos = getSafeBlockPos(deathLocation.getBlockPos(), deathLocationWorld);

            // Check if there is a safe BlockPos
            if (safeBlockPos.isEmpty()) {
                // asks the player if they want to teleport anyway
                //noinspection ConstantValue
                if (GeyserTPC.GEYSER_LOADED && GeyserApi.api() != null && GeyserApi.api().connectionByUuid(player.getUUID()) != null) {
                    if (GeyserTPC.FLOODGATE_LOADED) {
                        FloodgateApi.getInstance().sendForm(
                                player.getUUID(),
                                ModalForm.builder()
                                        .title(getTranslatedText("commands.geyser_tpc.common.noSafeLocation", player).toString())
                                        .content(getTranslatedText("commands.geyser_tpc.common.safetyIsForLosers", player).toString())
                                        .button1(getTranslatedText("commands.geyser_tpc.common.forceTeleport", player).toString())
                                        .button2(getTranslatedText("gui.geyser_tpc.universal.gui.cancel", player).toString())

                                        .validResultHandler(response -> {
                                            if (response.clickedFirst()) {
                                                var dispatcher = GeyserTPC.SERVER.getCommands().getDispatcher();
                                                try {
                                                    dispatcher.execute(dispatcher.parse("back true", player.createCommandSourceStack()));
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
                return;
            } else {
                teleportBlockPos = safeBlockPos.get();
            }
        } else {
            // no checking needed, just set it.
            teleportBlockPos = deathLocation.getBlockPos();
        }

        // check if the player is already at this location (in the same world)
        if (player.blockPosition().equals(teleportBlockPos) && player.level() == deathLocationWorld) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.back.same", player).withStyle(ChatFormatting.AQUA), true);

        } else {
            // teleport the player!
            Vec3 teleportPos = new Vec3(teleportBlockPos.getX() + 0.5, teleportBlockPos.getY(), teleportBlockPos.getZ() + 0.5);

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.back.go", player), true);
            tools.Teleporter(player, deathLocationWorld, teleportPos);
        }
    }
}
