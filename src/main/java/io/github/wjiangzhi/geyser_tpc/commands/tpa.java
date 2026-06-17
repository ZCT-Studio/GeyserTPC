package io.github.wjiangzhi.geyser_tpc.commands;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.GeyserTPC;
import io.github.wjiangzhi.geyser_tpc.suggestions.tpaSuggestionProvider;
import io.github.wjiangzhi.geyser_tpc.utils.tools;
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
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.*;

import static io.github.wjiangzhi.geyser_tpc.utils.tools.*;

public class tpa {

    public static final ArrayList<tpaArrayClass> tpaList = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        if (!GeyserTPC.TELEPORT_COMMANDS_LOADED) {
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
        }
        commandDispatcher.register(Commands.literal("gtpa")
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();

                    try {
                        if (tools.isBEPlayer(player)) {
                            new BE.TpaGUI(player);
                        } else {
                            new TpaGUI(player).open();
                        }
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

            if (tools.isBEPlayer(ToPlayer)) {
                ToPlayer.sendSystemMessage(getTranslatedText("commands.geyser_tpc.tpa.received", ToPlayer, Component.literal(hereText), Component.literal(ReceivedFromPlayer).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)).withStyle(ChatFormatting.AQUA)
                                .append("\n")
                                .append(getTranslatedText("commands.geyser_tpc.tpa.be.prompt", ToPlayer)
                                        .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD)
                                ),
                        false
                );
            } else {
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
            }

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

    public static class TpaGUI extends SimpleGui {

        private static final int PAGE_SIZE = 9;

        private int page = 0;

        public TpaGUI(ServerPlayer player) {
            super(MenuType.GENERIC_9x3, player, false);

            setTitle(getTranslatedText("gui.geyser_tpc.tpa.tpagui.title", player));

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

                tpaArrayClass tpaRequest = normalRequests.get(idx);

                ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                        .getPlayer(UUID.fromString(tpaRequest.InitPlayer));

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

                tpaArrayClass tpaRequest = hereRequests.get(idx);

                ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                        .getPlayer(UUID.fromString(tpaRequest.InitPlayer));

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
                        new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray())
                                .setName(Component.empty())
                );
            }

            this.setSlot(18,
                    new GuiElementBuilder(Items.ENDER_PEARL)
                            .setName(Component.literal("Tpa"))
                            .setCallback((_, _, _, g) -> {

                                new TpaSelectPlayerGUI(player, false, this).open();
                                g.close();
                            })
            );

            this.setSlot(19,
                    new GuiElementBuilder(Items.ENDER_EYE)
                            .setName(Component.literal("TpaHere"))
                            .setCallback((_, _, _, g) -> {

                                new TpaSelectPlayerGUI(player, true, this).open();
                                g.close();
                            })
            );

            this.setSlot(23,
                    new GuiElementBuilder(Items.LAVA_BUCKET)
                            .setName(getTranslatedText("gui.geyser_tpc.tpa.tpagui.denyall", player))
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
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.close", player))
                            .setCallback((_, _, _, g) -> g.close())
            );

            this.setSlot(25,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(Constants.GUI.ARROW_LEFT)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.pgup", player))
                            .setCallback((_, _, _, _) -> {
                                if (page > 0) {
                                    page--;
                                    init();
                                }
                            })
            );

            this.setSlot(26,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(Constants.GUI.ARROW_RIGHT)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.pgdn", player))
                            .setCallback((_, _, _, _) -> {
                                page++;
                                init();
                            })
            );
        }

        private void clearSlots() {
            for (int idx = 0; idx < 27; idx++) {
                this.clearSlot(idx);
            }
        }
    }

    public static class TpaSelectPlayerGUI extends SimpleGui {
        private static final int PAGE_SIZE = 18;
        private final boolean here;
        private final TpaGUI parent;
        private int page = 0;
        private List<ServerPlayer> players = new ArrayList<>();

        public TpaSelectPlayerGUI(
                ServerPlayer player,
                boolean here,
                TpaGUI parent
        ) {
            super(MenuType.GENERIC_9x3, player, false);

            this.here = here;
            this.parent = parent;

            setTitle(Component.literal(
                    here ? "TpaHere..." : "Tpa..."
            ));

            players = GeyserTPC.SERVER.getPlayerList()
                    .getPlayers()
                    .stream()
                    .filter(p -> p != player)
                    .toList();

            init();
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
                                .setName(target.getName())
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
                                Items.STAINED_GLASS_PANE.gray()
                        ).setName(Component.empty())
                );
            }

            this.setSlot(23,
                    new GuiElementBuilder(Items.ARROW)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.back", player))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );

            this.setSlot(24,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.close", player))
                            .setCallback((_, _, _, g) -> g.close())
            );

            this.setSlot(25,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(
                                    Constants.GUI.ARROW_LEFT
                            )
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.pgup", player))
                            .setCallback((_, _, _, _) -> {
                                if (page > 0) {
                                    page--;
                                    init();
                                }
                            })
            );

            this.setSlot(26,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(
                                    Constants.GUI.ARROW_RIGHT
                            )
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.pgdn", player))
                            .setCallback((_, _, _, _) -> {
                                if ((page + 1) * PAGE_SIZE < players.size()) {
                                    page++;
                                    init();
                                }
                            })
            );
        }

        private void clearSlots() {
            for (int idx = 0; idx < 27; idx++) {
                this.clearSlot(idx);
            }
        }
    }

    public static class TpaReqDialog extends SimpleGui {
        private final boolean no_java_err = true;
        private final TpaGUI parent;

        public TpaReqDialog(
                ServerPlayer player,
                ServerPlayer fromPlayer,
                boolean here,
                TpaGUI parent
        ) {
            super(MenuType.HOPPER, player, false);

            this.parent = parent;

            setTitle(Component.literal((here ? "[TPAHERE]" : "[TPA]") + fromPlayer.getName().getString()));

            this.setSlot(0,
                    new GuiElementBuilder(Items.CONCRETE.red())
                            .setName(getTranslatedText("gui.geyser_tpc.tpa.tpareqdialog.deny", player))
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
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.back", player))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );


            this.setSlot(3,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.close", player))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                            })
            );
            this.setSlot(4,
                    new GuiElementBuilder(Items.CONCRETE.lime())
                            .setName(getTranslatedText("gui.geyser_tpc.tpa.tpareqdialog.accept", player))
                            .setCallback((_, _, _, g) -> {
                                g.close();
                                try {
                                    tpaAccept(player, fromPlayer); // 这里的formPlayer是请求来源

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while accepting a tpa{} request! => ", here ? "here" : "", e);
                                }
                            })
            );
        }
    }

    public static class BE {
        public static class TpaGUI {
            private static final int PAGE_SIZE = 5;
            private List<tpaArrayClass> tpaRequests = new ArrayList<>();
            private final ServerPlayer player;
            private int page = 0;

            public TpaGUI(ServerPlayer player) {
                this.player = player;
                this.page = 0;

                init();
            }

            public TpaGUI(ServerPlayer player, int page) {
                this.player = player;
                this.page = page;

                init();
            }

            private int maxPage() {
                return (tpaRequests.size() - 1) / PAGE_SIZE;
            }

            public void init() {
                tpaRequests = tpaList.stream()
                        .filter(tpa -> Objects.equals(player.getStringUUID(), tpa.RecPlayer))
                        .toList();

                int start = page * PAGE_SIZE;
                int end = Math.min(start + PAGE_SIZE, tpaRequests.size());
                int onum = 1;

                var form_builder = SimpleForm.builder();

                form_builder.title(getTranslatedText("gui.geyser_tpc.tpa.tpagui.title", player).getString());

                form_builder.button("Tpa", FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.ENDER_PEARL));

                form_builder.button("TpaHere", FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.ENDER_EYE));

                for (int idx = start; idx < end; idx++) {
                    tpaArrayClass tpaRequest = tpaRequests.get(idx);

                    ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                            .getPlayer(UUID.fromString(tpaRequest.InitPlayer));
                    if (fromPlayer == null) return;
                    form_builder.button(
                            (tpaRequest.here ? "[TPAHERE]" : "[TPA]") + fromPlayer.getName().getString(),
                            FormImage.of(FormImage.Type.PATH, tpaRequest.here ? Constants.GUI.BE.ENDER_EYE : Constants.GUI.BE.ENDER_PEARL)
                    );

                    onum++;
                }

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.universal.gui.pgup", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.ARROW_LEFT)
                ); // 上一页

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.universal.gui.pgdn", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.ARROW_RIGHT)
                ); // 下一页

                final int finalOnum = onum;

                form_builder.validResultHandler(res -> {
                    /*
                     * 0 -> Tpa
                     * 1 -> TpaHere
                     * 2based page -> Requests
                     * finalOnum + 1 -> 上一页
                     * finalOnum + 2 -> 下一页
                     */
                    int id = res.clickedButtonId();

                    if (id == 0) { // Tpa
                        new TpaSelectPlayerGUI(player, false);
                    } else if (id == 1) { // TpaHere
                        new TpaSelectPlayerGUI(player, true);
                    } else if (id >= 2 && id <= finalOnum) {
                        int index = start + (id - 2);
                        var tpaRequest = tpaRequests.get(index);
                        ServerPlayer fromPlayer = GeyserTPC.SERVER.getPlayerList()
                                .getPlayer(UUID.fromString(tpaRequest.InitPlayer));
                        if (fromPlayer == null) return;
                        new TpaReqDialog(player, fromPlayer, tpaRequest.here);
                    } else if (id == finalOnum + 1) { // 上一页
                        int newPage = page;
                        if (page > 0) {
                            newPage--;
                        } else {
                            newPage = maxPage();
                        }
                        new TpaGUI(player, newPage);
                    } else if (id == finalOnum + 2) { // 下一页
                        int newPage = page;
                        if (page < maxPage()) {
                            newPage++;
                        } else {
                            newPage = 0;
                        }
                        new TpaGUI(player, newPage);
                    } else {
                    }
                });

                FloodgateApi.getInstance().sendForm(player.getUUID(), form_builder.build());
            }
        }

        public static class TpaSelectPlayerGUI {
            private static final int PAGE_SIZE = 6;
            private final boolean here;
            private final int page;
            private final ServerPlayer player;
            private List<ServerPlayer> players = new ArrayList<>();

            public TpaSelectPlayerGUI(
                    ServerPlayer player,
                    boolean here
            ) {
                this.player = player;
                this.here = here;
                this.page = 0;

                init();
            }

            public TpaSelectPlayerGUI(
                    ServerPlayer player,
                    boolean here,
                    int page
            ) {
                this.player = player;
                this.here = here;
                this.page = page;

                init();
            }

            private int maxPage() {
                return (players.size() - 1) / PAGE_SIZE;
            }

            public void init() {
                players = GeyserTPC.SERVER.getPlayerList()
                        .getPlayers()
                        .stream()
                        .filter(p -> p != player)
                        .toList();

                int start = page * PAGE_SIZE;
                int end = Math.min(start + PAGE_SIZE, players.size());
                int onum = 0;

                var form_builder = SimpleForm.builder();

                form_builder.title(here ? "TpaHere..." : "Tpa...");

                form_builder.button(
                        "TODO",
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.SEARCH)
                ); // 搜索

                for (int i = start; i < end; i++) {

                    ServerPlayer target = players.get(i);

                    if (target == null || target == player) continue;

                    form_builder.button(target.getName().getString());

                    onum++;
                }

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.universal.gui.pgup", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.ARROW_LEFT)
                ); // 上一页

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.universal.gui.pgdn", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.ARROW_RIGHT)
                ); // 下一页

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.universal.gui.back", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.BACK)
                ); // 返回

                final int finalOnum = onum;

                form_builder.validResultHandler(res -> {
                    /*
                     * 0 -> 搜索
                     * 1based page -> Players
                     * finalOnum + 1 -> 上一页
                     * finalOnum + 2 -> 下一页
                     * finalOnum + 3 -> 返回
                     */
                    int id = res.clickedButtonId();

                    if (id == 0) { // 搜索
                        // TODO 搜索
                    } else if (id >= 1 && id <= finalOnum) {
                        int index = start + (id - 1);
                        try {
                            tpaCommandHandler(
                                    player,
                                    players.get(index),
                                    here
                            );
                        } catch (Exception e) {
                            if (here) {
                                Constants.LOGGER.error("Error while sending a tpahere request! => ", e);
                            } else {
                                Constants.LOGGER.error("Error while sending a tpa request! => ", e);
                            }
                        }
                    } else if (id == finalOnum + 1) { // 上一页
                        int newPage = page;
                        if (page > 0) {
                            newPage--;
                        } else {
                            newPage = maxPage();
                        }
                        new TpaSelectPlayerGUI(player, here, newPage);
                    } else if (id == finalOnum + 2) { // 下一页
                        int newPage = page;
                        if (page < maxPage()) {
                            newPage++;
                        } else {
                            newPage = 0;
                        }
                        new TpaSelectPlayerGUI(player, here, newPage);
                    } else if (id == finalOnum + 3) { // 返回
                        new TpaGUI(player, 0);
                    } else {
                    }
                });

                FloodgateApi.getInstance().sendForm(player.getUUID(), form_builder.build());
            }
        }

        public static class TpaReqDialog {
            public TpaReqDialog(
                    ServerPlayer player,
                    ServerPlayer fromPlayer,
                    boolean here
            ) {
                var form_builder = SimpleForm.builder();

                form_builder.title((here ? "[TPAHERE]" : "[TPA]") + fromPlayer.getName().getString());

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.tpa.tpareqdialog.deny", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.NO)
                );

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.tpa.tpareqdialog.accept", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.YES)
                );

                form_builder.button(
                        getTranslatedText("gui.geyser_tpc.universal.gui.back", player).getString(),
                        FormImage.of(FormImage.Type.PATH, Constants.GUI.BE.BACK)
                );

                form_builder.validResultHandler(res -> {
                    int id = res.clickedButtonId();
                    switch (id) {
                        case 0: {
                            try {
                                tpaDeny(player, fromPlayer); // 这里的formPlayer是请求来源

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while denying a tpa{} request! => ", here ? "here" : "", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                            }
                        }
                        break;
                        case 1: {
                            try {
                                tpaAccept(player, fromPlayer); // 这里的formPlayer是请求来源

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while accepting a tpa{} request! => ", here ? "here" : "", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                            }
                        }
                        break;
                        case 2: {
                            new TpaGUI(player, 0);
                        }
                        break;
                        default:
                            return;
                    }
                });

                FloodgateApi.getInstance().sendForm(player.getUUID(), form_builder.build());
            }
        }
    }
}
