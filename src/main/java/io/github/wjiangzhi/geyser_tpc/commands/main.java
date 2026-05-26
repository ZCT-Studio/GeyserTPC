package io.github.wjiangzhi.geyser_tpc.commands;//package io.github.wjiangzhi.geyser_tpc.commands;
//
//import com.mojang.brigadier.arguments.BoolArgumentType;
//import io.github.wjiangzhi.geyser_tpc.TeleportCommands;
//import io.github.wjiangzhi.geyser_tpc.common.DeathLocation;
//import io.github.wjiangzhi.geyser_tpc.storage.DeathLocationStorage;
//import net.minecraft.ChatFormatting;
//import net.minecraft.commands.Commands;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.ClickEvent;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.phys.Vec3;
//
//import java.util.Optional;
//
//import static io.github.wjiangzhi.geyser_tpc.utils.tools.*;
//import static net.minecraft.commands.Commands.argument;
//
// TODO! add option to reload registered commands!
//
//public class main {
//
//    public static void register(Commands commandManager) {
//
//        commandManager.getDispatcher().register(Commands.literal("teleportcommands")
//            .then(Commands.literal("help")
//            .executes(context -> {
//                final ServerPlayer player = context.getSource().getPlayerOrException();
//
//                try {
//                    printCommands(player);
//
//                } catch (Exception e) {
//                    TeleportCommands.LOGGER.error("Error while going back! => ", e);
//                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
//                    return 1;
//                }
//                return 0;
//            }))
//            .then(argument("Disable Safety", BoolArgumentType.bool())
//                .executes(context -> {
//                    final boolean safety = BoolArgumentType.getBool(context, "Disable Safety");
//                    final ServerPlayer player = context.getSource().getPlayerOrException();
//
//                    try {
////                        ToDeathLocation(player, safety);
//
//                    } catch (Exception e) {
//                        TeleportCommands.LOGGER.error("Error while going back! => ", e);
//                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
//                        return 1;
//                    }
//                    return 0;
//                }))
//        );
//    }
//
//
//    // -----
//
//
//    // Gets the DeathLocation of the player and teleports the player to it
//    private static void printCommands(ServerPlayer player) throws Exception {
//
//        player.sendSystemMessage(Component.literal("Thank you for using Teleport Commands (V)!").withStyle(ChatFormatting.AQUA), false);
//        player.sendSystemMessage(Component.literal("Teleport Commands is a server-side mod that adds various teleportation related commands").withStyle(ChatFormatting.AQUA), false);
//
//        player.sendSystemMessage(Component.literal("----").withStyle(ChatFormatting.AQUA), false);
//
//        player.sendSystemMessage(Component.literal("Usage:").withStyle(ChatFormatting.AQUA), false);
//    }
//}
