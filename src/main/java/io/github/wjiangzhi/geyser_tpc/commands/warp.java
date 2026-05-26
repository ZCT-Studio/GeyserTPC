package io.github.wjiangzhi.geyser_tpc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.common.NamedLocation;
import io.github.wjiangzhi.geyser_tpc.common.Player;
import io.github.wjiangzhi.geyser_tpc.suggestions.WarpSuggestionProvider;
import io.github.wjiangzhi.geyser_tpc.utils.tools;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.wjiangzhi.geyser_tpc.storage.StorageManager.STORAGE;
import static io.github.wjiangzhi.geyser_tpc.utils.tools.Teleporter;
import static io.github.wjiangzhi.geyser_tpc.utils.tools.getTranslatedText;
import static net.minecraft.commands.Commands.argument;

public class warp {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("setwarp")
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.OWNERS)))
                .then(argument("name", StringArgumentType.string())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                SetWarp(player, name);

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while setting the warp!", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                return 1;
                            }
                            return 0;
                        })));

        commandDispatcher.register(Commands.literal("warp")
                .then(argument("name", StringArgumentType.string())
                        .suggests(new WarpSuggestionProvider())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                GoToWarp(player, name);

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while going to the warp!", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.goError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                return 1;
                            }
                            return 0;
                        })));

        commandDispatcher.register(Commands.literal("delwarp")
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.OWNERS)))
                .then(argument("name", StringArgumentType.string()).suggests(new WarpSuggestionProvider())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                DeleteWarp(player, name);

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while deleting to the warp!", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.deleteError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                return 1;
                            }
                            return 0;
                        })));

        commandDispatcher.register(Commands.literal("renamewarp")
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.OWNERS)))
                .then(argument("name", StringArgumentType.string()).suggests(new WarpSuggestionProvider())
                        .then(argument("newName", StringArgumentType.string())
                                .executes(context -> {
                                    final String name = StringArgumentType.getString(context, "name");
                                    final String newName = StringArgumentType.getString(context, "newName");
                                    final ServerPlayer player = context.getSource().getPlayerOrException();

                                    try {
                                        RenameWarp(player, name, newName);

                                    } catch (Exception e) {
                                        Constants.LOGGER.error("Error while renaming the warp!", e);
                                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.renameError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                        return 1;
                                    }
                                    return 0;
                                }))));

        commandDispatcher.register(Commands.literal("warps")
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();

                    try {
                        PrintWarps(player);

                    } catch (Exception e) {
                        Constants.LOGGER.error("Error while printing warps!", e);
                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warps.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                        return 1;
                    }
                    return 0;
                }));

        commandDispatcher.register(Commands.literal("gwarp")
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();

                    try {
                        if (STORAGE.getWarps().isEmpty()) {
                            player.sendSystemMessage(
                                    getTranslatedText("commands.geyser_tpc.warp.homeless", player)
                                            .withStyle(ChatFormatting.AQUA),
                                    true
                            );
                            return 1;
                        }
                        new WarpGUI(player).open();
                    } catch (Exception e) {
                        Constants.LOGGER.error("Error while opening the warp gui! => ", e);
                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warps.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                        return 1;
                    }
                    return 0;
                }).then(argument("name", StringArgumentType.string())
                        .suggests(new WarpSuggestionProvider())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            if (STORAGE.getWarps().isEmpty()) {
                                player.sendSystemMessage(
                                        getTranslatedText("commands.geyser_tpc.warp.homeless", player)
                                                .withStyle(ChatFormatting.AQUA),
                                        true
                                );
                                return 1;
                            }

                            try {
                                String lowerCaseName = name.toLowerCase();

                                if (STORAGE.getWarp(lowerCaseName).isEmpty()) {
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.notFound", player).withStyle(ChatFormatting.RED), true);
                                    return 1;
                                }

                                new WarpActionGUI(player, STORAGE.getWarp(lowerCaseName).get(), null).open();

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while openning warp gui => ", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warps.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                return 1;
                            }
                            return 0;
                        })));
    }


    private static void SetWarp(ServerPlayer player, String warpName) throws Exception {
        System.out.println(warpName);
        warpName = warpName.toLowerCase();

        BlockPos blockPos = new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
        String worldString = player.level().dimension().identifier().toString();

        // Create the NamedLocation
        NamedLocation warp = new NamedLocation(warpName, blockPos, worldString);

        // Adds the warp, returns true if the warp already exists
        boolean warpExists = STORAGE.addWarp(warp);

        if (warpExists) {
            // Display error message that the warp already exists
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.exists", player).withStyle(ChatFormatting.RED), true);

        } else {
            // Display message that the warp as been set
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.set", player), true);
        }
    }

    private static void GoToWarp(ServerPlayer player, String warpName) {
        warpName = warpName.toLowerCase();

        // Gets warp
        Optional<NamedLocation> optionalWarp = STORAGE.getWarp(warpName);
        if (optionalWarp.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.notFound", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        NamedLocation warp = optionalWarp.get();

        // Get the world, otherwise give a warning and error message
        Optional<ServerLevel> optionalWorld = warp.getWorld();

        if (optionalWorld.isEmpty()) {
            Constants.LOGGER.warn("({}) Error while going to the warp \"{}\"! \nCouldn't find a world with the id: \"{}\" \nAvailable worlds: {}",
                    player.getName().getString(),
                    warp.getName(),
                    warp.getWorldString(),
                    tools.getWorldIds());

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.worldNotFound", player)
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);

            return;
        }

        ServerLevel warpWorld = optionalWorld.get();

        BlockPos teleportBlockPos = warp.getBlockPos();

        // Check if the player is already at this location (in the same world)
        if (player.blockPosition().equals(teleportBlockPos) && player.level() == warpWorld) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.goSame", player).withStyle(ChatFormatting.AQUA), true);

        } else {
            // Teleport the player!
            Vec3 teleportPos = new Vec3(teleportBlockPos.getX() + 0.5, teleportBlockPos.getY(), teleportBlockPos.getZ() + 0.5);

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.go", player), true);
            Teleporter(player, warpWorld, teleportPos);
        }
    }

    private static void DeleteWarp(ServerPlayer player, String warpName) throws Exception {
        warpName = warpName.toLowerCase();

        // get the existing warp
        Optional<NamedLocation> optionalWarp = STORAGE.getWarp(warpName);

        if (optionalWarp.isPresent()) {
            // Delete the warp
            STORAGE.removeWarp(optionalWarp.get());

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.delete", player), true);

        } else {
            // the warp is not found
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.notFound", player).withStyle(ChatFormatting.RED), true);
        }
    }

    private static void RenameWarp(ServerPlayer player, String warpName, String newWarpName) throws Exception {
        warpName = warpName.toLowerCase();
        newWarpName = newWarpName.toLowerCase();

        // check if there is no existing warp with the new name
        if (STORAGE.getWarp(newWarpName).isPresent()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.nameExists", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        // get the existing warp
        Optional<NamedLocation> warpToRename = STORAGE.getWarp(warpName);

        if (warpToRename.isPresent()) {

            // set the new name
            warpToRename.get().setName(newWarpName);
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.rename", player), true);

        } else {
            // the warp is not found
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.notFound", player).withStyle(ChatFormatting.RED), true);
        }
    }

    private static void PrintWarps(ServerPlayer player) {
        // Get warps
        List<NamedLocation> warps = STORAGE.getWarps();

        // Check if there are any warps lol
        if (warps.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        MutableComponent message = Component.empty();

        // make da message
        message.append(getTranslatedText("commands.geyser_tpc.warps.warps", player)
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
        );

        for (NamedLocation currentWarp : warps) {

            String name = String.format("  - %s", currentWarp.getName());
            String coords = String.format("[X%d Y%d Z%d]", currentWarp.getX(), currentWarp.getY(), currentWarp.getZ());
            String dimension = String.format(" [%s]", currentWarp.getWorldString());

            boolean canModify = player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.OWNERS));

            // linebreak
            message.append("\n");

            // Name of the warp
            message.append(Component.literal(name)
                    .withStyle(ChatFormatting.AQUA)
            );

            // linebreak
            message.append("\n");

            // Cords and dimension
            message.append(Component.literal("     | ")
                            .withStyle(ChatFormatting.AQUA)
                    )
                    .append(Component.literal(coords)
                            .withStyle(ChatFormatting.LIGHT_PURPLE)
                            .withStyle(style ->
                                    style.withClickEvent(
                                            new ClickEvent.CopyToClipboard(
                                                    String.format("X%d Y%d Z%d", currentWarp.getX(), currentWarp.getY(), currentWarp.getZ())
                                            )
                                    )
                            )
                            .withStyle(style ->
                                    style.withHoverEvent(
                                            new HoverEvent.ShowText(
                                                    getTranslatedText("commands.geyser_tpc.common.hoverCopy", player)
                                            )
                                    )
                            )
                    )
                    .append(Component.literal(dimension)
                            .withStyle(ChatFormatting.DARK_PURPLE)
                            .withStyle(style ->
                                    style.withClickEvent(
                                            new ClickEvent.CopyToClipboard(
                                                    currentWarp.getWorldString()
                                            )
                                    )
                            )
                            .withStyle(style -> style
                                    .withHoverEvent(
                                            new HoverEvent.ShowText(
                                                    getTranslatedText("commands.geyser_tpc.common.hoverCopy", player)
                                            )
                                    )
                            )
                    );

            // linebreak
            message.append("\n");

            // Teleport button
            message.append(Component.literal("     | ").withStyle(ChatFormatting.AQUA))
                    .append(getTranslatedText("commands.geyser_tpc.common.tp", player)
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(style ->
                                    style.withClickEvent(
                                            new ClickEvent.RunCommand(
                                                    String.format("/warp \"%s\"", currentWarp.getName())
                                            )
                                    )
                            )
                    )
                    .append(" ");

            // Rename and delete buttons if admin
            if (canModify) {
                message.append(getTranslatedText("commands.geyser_tpc.common.rename", player)
                                .withStyle(ChatFormatting.BLUE)
                                .withStyle(style -> style
                                        .withClickEvent(
                                                new ClickEvent.SuggestCommand(
                                                        String.format("/renamewarp \"%s\" ", currentWarp.getName())
                                                )
                                        )
                                )
                        )
                        .append(" ")
                        .append(getTranslatedText("commands.geyser_tpc.common.delete", player)
                                .withStyle(ChatFormatting.RED)
                                .withStyle(style -> style
                                        .withClickEvent(
                                                new ClickEvent.SuggestCommand(
                                                        String.format("/delwarp \"%s\"", currentWarp.getName())
                                                )
                                        )
                                )
                        );
            }

            // linebreak
            message.append("\n");
        }

        // send the message
        player.sendSystemMessage(message, false);
    }

    public static class WarpGUI extends SimpleGui {

        private static final int PAGE_SIZE = 18; // 2行
        public List<NamedLocation> warps = new ArrayList<>();
        private int page = 0;

        public WarpGUI(ServerPlayer player) {
            super(MenuType.GENERIC_9x3, player, false);

            setTitle(Component.literal("Warps GUI"));
            loadWarps();
            init();
        }

        private void loadWarps() {
            warps = STORAGE.getWarps();

            if (warps == null) warps = new ArrayList<>();
        }

        private void init() {

            clearSlots();

            if (warps.isEmpty()) {
                player.sendSystemMessage(
                        getTranslatedText("commands.geyser_tpc.warp.homeless", player)
                                .withStyle(ChatFormatting.AQUA),
                        true
                );
                close();
                return;
            }

            int start = page * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, warps.size());

            int slot = 0;

            for (int idx = start; idx < end; idx++) {

                NamedLocation warp = warps.get(idx);

                Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());

                Player playerStorage = optionalPlayerStorage.orElse(null);

                if (playerStorage != null && STORAGE.getWarp(warp.getName().toLowerCase()).isEmpty()) {
                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.notFound", player).withStyle(ChatFormatting.RED), true);
                }

                this.setSlot(slot,
                        new GuiElementBuilder(Items.AMETHYST_SHARD)
                                .setName(Component.literal(warp.getName()))
                                .setCallback((i, t, a, g) -> {
                                    new WarpActionGUI(player, warp, this).open();
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

            this.setSlot(24,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("X"))
                            .setCallback((i, t, a, g) -> g.close())
            );

            this.setSlot(25,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(Constants.GUI.ARROW_LEFT)
                            .setName(Component.literal("<"))
                            .setCallback((i, t, a, g) -> {
                                if (page > 0) page--;
                                init();
                            })
            );

            this.setSlot(26,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setName(Component.literal(">"))
                            .setProfileSkinTexture(Constants.GUI.ARROW_RIGHT)
                            .setCallback((i, t, a, g) -> {
                                if ((page + 1) * PAGE_SIZE < warps.size()) {
                                    page++;
                                    init();
                                }
                            })
            );
        }

        private void clearSlots() {
            for (int i = 0; i < 27; i++) {
                this.clearSlot(i);
            }
        }
    }

    public static class WarpActionGUI extends SimpleGui {
        private final NamedLocation warp;
        private final ServerPlayer player;
        private final WarpGUI parent;

        public WarpActionGUI(ServerPlayer player, NamedLocation warp, WarpGUI parent) {
            super(MenuType.HOPPER, player, false);

            this.player = player;
            this.warp = warp;
            this.parent = parent;

            init();
        }

        private void init() {
            setTitle(Component.literal(warp.getName()));
            this.setSlot(0,
                    new GuiElementBuilder(Items.ENDER_PEARL)
                            .setName(Component.literal("Teleport"))
                            .setCallback((i, t, a, g) -> {
                                try {
                                    GoToWarp(player, warp.getName());
                                } catch (Exception e) {
                                    Constants.LOGGER.error("Teleport error", e);
                                }
                                g.close();
                            })
            );

            if (player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.OWNERS))) {

                this.setSlot(1,
                        new GuiElementBuilder(Items.TNT)
                                .setName(Component.literal("Delete"))
                                .setCallback((i, t, a, g) -> {
                                    new WarpDeleteGUI(player, warp, this).open();
                                    g.close();
                                })
                );

                this.setSlot(2,
                        new GuiElementBuilder(Items.NAME_TAG)
                                .setName(Component.literal("Rename"))
                                .setCallback((i, t, a, g) -> {
                                    new WarpRenameGUI(player, warp, this).open();
                                    g.close();
                                })
                );
            }

            this.setSlot(3,
                    new GuiElementBuilder(Items.ARROW)
                            .setName(Component.literal("Back"))
                            .setCallback((i, t, a, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.init();
                                    parent.open();
                                }
                            })
            );

            this.setSlot(4,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("Close"))
                            .setCallback((i, t, a, g) -> g.close())
            );
        }
    }

    public static class WarpDeleteGUI extends SimpleGui {

        private final NamedLocation warp;
        private final WarpActionGUI parent;

        public WarpDeleteGUI(ServerPlayer player, NamedLocation warp, WarpActionGUI parent) {
            super(MenuType.HOPPER, player, false);

            this.warp = warp;
            this.parent = parent;

            setTitle(Component.literal("Delete warp(" + warp.getName() + ")?"));

            this.setSlot(0,
                    new GuiElementBuilder(Items.RED_CONCRETE)
                            .setName(Component.literal("No"))
                            .setCallback((i, t, a, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );


            this.setSlot(4,
                    new GuiElementBuilder(Items.LIME_CONCRETE)
                            .setName(Component.literal("Yes"))
                            .setCallback((i, t, a, g) -> {
                                g.close();

                                try {
                                    DeleteWarp(player, warp.getName());

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while deleting a warp! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.deleteError", player)
                                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return;
                                }

                                if (parent != null && parent.parent != null) {
                                    parent.parent.init();
                                    parent.parent.open();
                                }
                            })
            );
        }
    }

    public static class WarpRenameGUI extends AnvilInputGui {

        private final NamedLocation warp;
        private final WarpActionGUI parent;

        public WarpRenameGUI(ServerPlayer player, NamedLocation warp, WarpActionGUI parent) {
            super(player, false);

            this.warp = warp;
            this.parent = parent;

            setDefaultInputValue(warp.getName());

            setTitle(Component.literal("Rename" + " \"" + warp.getName() + "\""));

            this.setSlot(0,
                    new GuiElementBuilder(Items.AMETHYST_SHARD).setName(Component.literal(warp.getName()))
            );

            this.setSlot(1,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("Cancel"))
                            .setCallback((i, t, a, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );

            this.setSlot(2,
                    new GuiElementBuilder(Items.ANVIL)
                            .setName(Component.literal("Rename"))
                            .setCallback((i, t, a, g) -> {
                                String input = getInput();

                                if (input != null && !input.isBlank()) {
                                    try {
                                        RenameWarp(player, warp.getName(), input);

                                    } catch (Exception e) {
                                        Constants.LOGGER.error("Error while renaming a warp! => ", e);
                                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.warp.renameError", player)
                                                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                        return;
                                    }
                                }

                                g.close();
                                if (parent != null) {
                                    parent.init();
                                    parent.open();
                                }
                            })
            );
        }
    }
}
