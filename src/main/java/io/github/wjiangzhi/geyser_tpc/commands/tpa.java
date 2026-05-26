package io.github.wjiangzhi.geyser_tpc.commands;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.GeyserTPC;
import io.github.wjiangzhi.geyser_tpc.suggestions.tpaSuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static io.github.wjiangzhi.geyser_tpc.utils.tools.*;

public class tpa {

    public static final ArrayList<tpaArrayClass> tpaList = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            final ServerPlayer TargetPlayer = EntityArgument.getPlayer(context, "player");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                tpaCommandHandler(player, TargetPlayer, false);

                            } catch (Exception e) {
                                // this shouldn't happen with any of these commands, but if it does happen I am at least printing it to the logs and catching it.
                                // if it appears that this can happen then I'll add error messages for the client, for now the default minecraft ones will do
                                Constants.LOGGER.error("Error while sending a tpa request! => ", e);
                                return 1;
                            }
                            return 0;
                        })));

        commandDispatcher.register(Commands.literal("tpahere")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            final ServerPlayer TargetPlayer = EntityArgument.getPlayer(context, "player");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                tpaCommandHandler(player, TargetPlayer, true);

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while sending a tpahere request! => ", e);
                                return 1;
                            }
                            return 0;
                        })));

        commandDispatcher.register(Commands.literal("tpaaccept")
                .then(Commands.argument("player", EntityArgument.player()).suggests(new tpaSuggestionProvider())
                        .executes(context -> {
                            final ServerPlayer TargetPlayer = EntityArgument.getPlayer(context, "player");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                tpaAccept(player, TargetPlayer);

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while accepting a tpa(here) request! => ", e);
                                return 1;
                            }

                            return 0;
                        })));

        commandDispatcher.register(Commands.literal("tpadeny")
                .then(Commands.argument("player", EntityArgument.player()).suggests(new tpaSuggestionProvider())
                        .executes(context -> {
                            final ServerPlayer TargetPlayer = EntityArgument.getPlayer(context, "player");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                tpaDeny(player, TargetPlayer);

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while denying a tpa(here) request! => ", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                return 1;
                            }

                            return 0;
                        })));
        commandDispatcher.register(Commands.literal("gtpa")
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();

                    try {
                        new TpaGenGUI(player).open();

                    } catch (Exception e) {
                        Constants.LOGGER.error("Error while denying a tpa(here) request! => ", e);
                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                        return 1;
                    }

                    return 0;
                }));
    }

    private static void tpaCommandHandler(ServerPlayer FromPlayer, ServerPlayer ToPlayer, boolean here) throws NullPointerException {
        long playerTpaList = tpa.tpaList.stream()
                .filter(tpa -> Objects.equals(FromPlayer.getStringUUID(), tpa.InitPlayer))
                .filter(tpa -> Objects.equals(ToPlayer.getStringUUID(), tpa.RecPlayer))
                .count();

        if (FromPlayer == ToPlayer) {
            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.self", FromPlayer).withStyle(ChatFormatting.AQUA), true);

        } else if (playerTpaList >= 1) {
            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.alreadySent", FromPlayer, Component.literal(Objects.requireNonNull(ToPlayer.getName().getString(), "ToPlayer name cannot be null")).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.AQUA)
                    , true
            );

        } else {
            String hereText = here ? "Here" : "";

            // Store da request
            tpaArrayClass tpaRequest = new tpaArrayClass(FromPlayer.getStringUUID(), ToPlayer.getStringUUID(), here);

            String ReceivedFromPlayer = Objects.requireNonNull(FromPlayer.getName().getString(), "FromPlayer name cannot be null");
            String SentToPlayer = Objects.requireNonNull(ToPlayer.getName().getString(), "ToPlayer name cannot be null");

            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.sent", FromPlayer, Component.literal(hereText), Component.literal(SentToPlayer).withStyle(ChatFormatting.BOLD))
                    //                            .append(Text.literal("\n[Cancel]").formatted(Formatting.BLUE, Formatting.BOLD))
                    , true
            );

            ToPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.received", ToPlayer, Component.literal(hereText), Component.literal(ReceivedFromPlayer).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)).withStyle(ChatFormatting.AQUA)
                            .append("\n")
                            .append(getTranslatedText("commands.geyser_tpc.tpa.accept", ToPlayer)
                                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                                    .withStyle(style -> style
                                            .withClickEvent(
                                                    new ClickEvent.RunCommand(
                                                            String.format("/tpaaccept %s", ReceivedFromPlayer)
                                                    )
                                            )
                                    )
                            )
                            .append(" ")
                            .append(getTranslatedText("commands.geyser_tpc.tpa.deny", ToPlayer)
                                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                                    .withStyle(style -> style
                                            .withClickEvent(
                                                    new ClickEvent.RunCommand(
                                                            String.format("/tpadeny %s", ReceivedFromPlayer)
                                                    )
                                            )
                                    )
                            ),
                    false
            );

            Timer timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            boolean successful = tpaList.remove(tpaRequest);
                            if (successful) {
                                FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.expired", FromPlayer, Component.literal(hereText)).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                ToPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.expired", ToPlayer, Component.literal(hereText)).withStyle(ChatFormatting.WHITE), true);
                            }
                            // else not needed since it may be denied/canceled
                        }
                    }, 30 * 1000 // 30 seconds
            );
        }
    }

    private static void tpaAccept(ServerPlayer FromPlayer, ServerPlayer ToPlayer) {
        if (FromPlayer == ToPlayer) {
            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.self", FromPlayer).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        // Check if there is a request
        Optional<tpaArrayClass> tpaStorage = tpaList.stream()
                .filter(tpa -> Objects.equals(ToPlayer.getStringUUID(), tpa.InitPlayer))
                .filter(tpa -> Objects.equals(FromPlayer.getStringUUID(), tpa.RecPlayer))
                .findFirst();

        if (tpaStorage.isPresent()) {
            // Request found
            ServerPlayer destinationPlayer = tpaStorage.get().here ? ToPlayer : FromPlayer;
            ServerPlayer toSentPlayer = tpaStorage.get().here ? FromPlayer : ToPlayer;

            Optional<BlockPos> teleportData = getSafeBlockPos(destinationPlayer.blockPosition(), destinationPlayer.level());

            if (teleportData.isPresent()) {
                BlockPos safeBlockPos = teleportData.get();
                Vec3 teleportPos = new Vec3(safeBlockPos.getX() + 0.5, safeBlockPos.getY(), safeBlockPos.getZ() + 0.5);

                Teleporter(toSentPlayer, destinationPlayer.level(), teleportPos);
            } else {
                // if no safe location then just teleport to the player
                Teleporter(toSentPlayer, destinationPlayer.level(), destinationPlayer.position());
            }

            // if the player teleported then these messages get sent && the request gets removed
            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.accepted", FromPlayer).withStyle(ChatFormatting.WHITE), true);
            ToPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.accepted", ToPlayer).withStyle(ChatFormatting.GREEN), true);
            tpaList.remove(tpaStorage.get());

        } else {
            // No request found
            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.notFound", FromPlayer).withStyle(ChatFormatting.RED), true);
        }
    }

    private static void tpaDeny(ServerPlayer FromPlayer, ServerPlayer ToPlayer) {
        if (FromPlayer == ToPlayer) {
            FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.self", FromPlayer).withStyle(ChatFormatting.AQUA), true);

        } else {
            Optional<tpaArrayClass> tpaStorage = tpaList.stream()
                    .filter(tpa -> Objects.equals(ToPlayer.getStringUUID(), tpa.InitPlayer))
                    .filter(tpa -> Objects.equals(FromPlayer.getStringUUID(), tpa.RecPlayer))
                    .findFirst();

            if (tpaStorage.isPresent()) {
                tpaList.remove(tpaStorage.get());

                ToPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.denied", ToPlayer).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.denied", FromPlayer).withStyle(ChatFormatting.WHITE), true);

            } else {
                FromPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.notFound", FromPlayer).withStyle(ChatFormatting.RED), true);
            }
        }
    }

    public static class tpaArrayClass {
        public final String InitPlayer;
        public final String RecPlayer;
        final boolean here;

        public tpaArrayClass(String initPlayer, String recPlayer, boolean here) {
            InitPlayer = initPlayer;
            RecPlayer = recPlayer;
            this.here = here;
            tpaList.add(this);
        }
    }

    public static class TpaGenGUI extends SimpleGui {

        private static final int PAGE_SIZE = 9;

        private int page = 0;

        public TpaGenGUI(ServerPlayer player) {
            super(MenuType.GENERIC_9x3, player, false);

            setTitle(Component.literal("Tpa GUI"));

            init();
        }

        public void init() {

            clearSlots();

            List<tpaArrayClass> normalRequests = tpaList.stream()
                    .filter(tpa -> !tpa.here)
                    .filter(tpa -> Objects.equals(player.getStringUUID(), tpa.RecPlayer))
                    .toList();

            List<tpaArrayClass> hereRequests = tpaList.stream()
                    .filter(tpa -> tpa.here)
                    .filter(tpa -> Objects.equals(player.getStringUUID(), tpa.RecPlayer))
                    .toList();

            int normalStart = page * PAGE_SIZE;
            int normalEnd = Math.min(normalStart + PAGE_SIZE, normalRequests.size());

            int slot = 0;

            for (int idx = normalStart; idx < normalEnd; idx++) {

                tpaArrayClass request = normalRequests.get(idx);

                ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                        .getPlayer(UUID.fromString(request.InitPlayer));

                if (fromPlayer == null) continue;

                this.setSlot(slot,
                        new GuiElementBuilder(Items.ENDER_PEARL)
                                .setName(Component.literal("[TPA]" + fromPlayer.getName().getString()))
                                .setCallback((_, _, _, g) -> {

                                    new TpaReqDialog(player, fromPlayer, true, this).open();
                                    g.close();
                                })
                );

                slot++;
            }

            int hereStart = page * PAGE_SIZE;
            int hereEnd = Math.min(hereStart + PAGE_SIZE, hereRequests.size());

            slot = 9;

            for (int idx = hereStart; idx < hereEnd; idx++) {

                tpaArrayClass request = hereRequests.get(idx);

                ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                        .getPlayer(UUID.fromString(request.InitPlayer));

                if (fromPlayer == null) continue;

                this.setSlot(slot,
                        new GuiElementBuilder(Items.ENDER_EYE)
                                .setName(Component.literal("[TPAHERE]" + fromPlayer.getName().getString()))
                                .setCallback((_, _, _, g) -> {
                                    new TpaReqDialog(player, fromPlayer, true, this).open();
                                    g.close();
                                })
                );

                slot++;
            }

            for (int i = 18; i < 27; i++) {
                this.setSlot(i,
                        new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
                                .setName(Component.empty())
                );
            }

            this.setSlot(18,
                    new GuiElementBuilder(Items.ENDER_PEARL)
                            .setName(Component.literal("Tpa"))
                            .setCallback((_, _, _, g) -> {

                                new TpaSelectTpaPlayerGUI(player, false, this).open();
                                g.close();
                            })
            );

            this.setSlot(19,
                    new GuiElementBuilder(Items.ENDER_EYE)
                            .setName(Component.literal("TpaHere"))
                            .setCallback((_, _, _, g) -> {

                                new TpaSelectTpaPlayerGUI(player, true, this).open();
                                g.close();
                            })
            );

            this.setSlot(23,
                    new GuiElementBuilder(Items.LAVA_BUCKET)
                            .setName(Component.literal("Clear"))
                            .setCallback((_, _, _, _) -> {

                                List<tpaArrayClass> removeList = tpaList.stream()
                                        .filter(tpa ->
                                                Objects.equals(player.getStringUUID(), tpa.RecPlayer)
                                        )
                                        .toList();

                                for (tpaArrayClass request : removeList) {

                                    ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                                            .getPlayer(UUID.fromString(request.InitPlayer));

                                    if (fromPlayer != null) {
                                        tpaDeny(player, fromPlayer);
                                    }
                                }

                                init();
                            })
            );

            this.setSlot(24,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(Constants.GUI.ARROW_LEFT)
                            .setName(Component.literal("<"))
                            .setCallback((_, _, _, _) -> {
                                if (page > 0) {
                                    page--;
                                    init();
                                }
                            })
            );

            this.setSlot(25,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(Constants.GUI.ARROW_RIGHT)
                            .setName(Component.literal(">"))
                            .setCallback((_, _, _, _) -> {

                                page++;
                                init();
                            })
            );

            this.setSlot(26,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("Close"))
                            .setCallback((_, _, _, g) -> g.close())
            );
        }

        private void clearSlots() {
            for (int i = 0; i < 27; i++) {
                this.clearSlot(i);
            }
        }
    }

    public static class TpaSelectTpaPlayerGUI extends SimpleGui {

        private static final int PAGE_SIZE = 18;

        private int page = 0;

        private final boolean here;

        private final TpaGenGUI parent;

        private List<ServerPlayer> players = new ArrayList<>();

        public TpaSelectTpaPlayerGUI(
                ServerPlayer player,
                boolean here,
                TpaGenGUI parent
        ) {

            super(MenuType.GENERIC_9x3, player, false);

            this.here = here;
            this.parent = parent;

            setTitle(Component.literal(
                    here ? "TpaHere from ..." : "Tpa to ..."
            ));

            loadPlayers();

            init();
        }

        private void loadPlayers() {

            players = GeyserTPC.SERVER.getPlayerList()
                    .getPlayers()
                    .stream()
                    .filter(p -> p != player)
                    .toList();
        }

        public void init() {

            clearSlots();

            int start = page * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, players.size());

            int slot = 0;

            for (int i = start; i < end; i++) {

                ServerPlayer target = players.get(i);

                if (target == null || target == player) continue;

                this.setSlot(slot,
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setProfile(target.getUUID())
                                .setName(
                                        Component.literal(
                                                target.getName().getString()
                                        )
                                )
                                .setCallback((_, _, _, g) -> {

                                    try {

                                        tpaCommandHandler(
                                                player,
                                                target,
                                                here
                                        );

                                    } catch (Exception e) {
                                        if (here) {
                                            Constants.LOGGER.error("Error while sending a tpahere request! => ", e);
                                        } else {
                                            Constants.LOGGER.error("Error while sending a tpa request! => ", e);
                                        }
                                    }

                                    g.close();
                                })
                );

                slot++;
            }

            for (int i = 18; i < 27; i++) {

                this.setSlot(i,
                        new GuiElementBuilder(
                                Items.GRAY_STAINED_GLASS_PANE
                        ).setName(Component.empty())
                );
            }

            this.setSlot(23,
                    new GuiElementBuilder(Items.ARROW)
                            .setName(Component.literal("Back"))
                            .setCallback((_, _, _, g) -> {

                                g.close();

                                parent.open();

                            })
            );

            this.setSlot(24,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(
                                    Constants.GUI.ARROW_LEFT
                            )
                            .setName(Component.literal("<"))
                            .setCallback((_, _, _, _) -> {

                                if (page > 0) {

                                    page--;

                                    init();
                                }
                            })
            );

            this.setSlot(25,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(
                                    Constants.GUI.ARROW_RIGHT
                            )
                            .setName(Component.literal(">"))
                            .setCallback((_, _, _, _) -> {

                                if ((page + 1) * PAGE_SIZE < players.size()) {

                                    page++;

                                    init();
                                }
                            })
            );

            this.setSlot(26,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("X"))
                            .setCallback((_, _, _, g) -> g.close())
            );
        }

        private void clearSlots() {

            for (int i = 0; i < 27; i++) {

                this.clearSlot(i);
            }
        }
    }
    public static class TpaReqDialog extends SimpleGui {

        public TpaReqDialog(
                ServerPlayer player,
                ServerPlayer fromPlayer,
                boolean  here,
                TpaGenGUI parent
        ) {
            super(MenuType.HOPPER, player, false);

            setTitle(Component.literal(fromPlayer.getName() + " " + (here ? "tpaHere from" : "tpa to") + " " + "you"));

            this.setSlot(0,
                    new GuiElementBuilder(Items.RED_CONCRETE)
                            .setName(Component.literal("No"))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                                try {
                                    tpaDeny(player, fromPlayer); // 这里的formPlayer是请求来源

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while denying a tpa{} request! => ", here ? "here" : "", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                }
                            })
            );

            this.setSlot(1,
                    new GuiElementBuilder(Items.ARROW)
                            .setName(Component.literal("Back"))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );


            this.setSlot(3,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("Close"))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                            })
            );
            this.setSlot(4,
                    new GuiElementBuilder(Items.LIME_CONCRETE)
                            .setName(Component.literal("Yes"))
                            .setCallback((_, _, _, g) -> {
                                g.close();

                                try {
                                    tpaAccept(player, fromPlayer); // 这里的formPlayer是请求来源

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while accepting a tpa{} request! => ", here ? "here" : "", e);
                                    return;
                                }
                            })
            );
        }
    }
}
