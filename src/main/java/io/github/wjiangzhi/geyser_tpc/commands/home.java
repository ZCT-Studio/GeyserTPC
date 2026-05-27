package io.github.wjiangzhi.geyser_tpc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.GeyserTPC;
import io.github.wjiangzhi.geyser_tpc.common.NamedLocation;
import io.github.wjiangzhi.geyser_tpc.common.Player;
import io.github.wjiangzhi.geyser_tpc.storage.StorageManager;
import io.github.wjiangzhi.geyser_tpc.suggestions.HomeSuggestionProvider;
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
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.wjiangzhi.geyser_tpc.storage.StorageManager.STORAGE;
import static io.github.wjiangzhi.geyser_tpc.utils.tools.Teleporter;
import static io.github.wjiangzhi.geyser_tpc.utils.tools.getTranslatedText;
import static net.minecraft.commands.Commands.argument;

public class home {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        if (!GeyserTPC.TELEPORT_COMMANDS_LOADED) {
            commandDispatcher.register(Commands.literal("sethome")
                    .then(argument("name", StringArgumentType.string())
                            .executes(context -> {
                                final String name = StringArgumentType.getString(context, "name");
                                final ServerPlayer player = context.getSource().getPlayerOrException();

                                try {
                                    SetHome(player, name);

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while setting a home! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.setError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return 1;
                                }
                                return 0;
                            })));


            commandDispatcher.register(Commands.literal("home")
                    .executes(context -> {
                        final ServerPlayer player = context.getSource().getPlayerOrException();

                        try {
                            GoHome(player, "");

                        } catch (Exception e) {
                            Constants.LOGGER.error("Error while going home! => ", e);
                            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.goError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                            return 1;
                        }
                        return 0;
                    })
                    .then(argument("name", StringArgumentType.string())
                            .suggests(new HomeSuggestionProvider())
                            .executes(context -> {
                                final String name = StringArgumentType.getString(context, "name");
                                final ServerPlayer player = context.getSource().getPlayerOrException();

                                try {
                                    GoHome(player, name);

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while going to a specific home! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.goError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return 1;
                                }
                                return 0;
                            })));

            commandDispatcher.register(Commands.literal("delhome")
                    .then(argument("name", StringArgumentType.string())
                            .suggests(new HomeSuggestionProvider())
                            .executes(context -> {
                                final String name = StringArgumentType.getString(context, "name");
                                final ServerPlayer player = context.getSource().getPlayerOrException();

                                try {
                                    DeleteHome(player, name);

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while deleting a home! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.deleteError", player)
                                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return 1;
                                }
                                return 0;
                            })));

            commandDispatcher.register(Commands.literal("renamehome")
                    .then(argument("name", StringArgumentType.string())
                            .suggests(new HomeSuggestionProvider())
                            .then(argument("newName", StringArgumentType.string())
                                    .executes(context -> {
                                        final String name = StringArgumentType.getString(context, "name");
                                        final String newName = StringArgumentType.getString(context, "newName");
                                        final ServerPlayer player = context.getSource().getPlayerOrException();

                                        try {
                                            RenameHome(player, name, newName);

                                        } catch (Exception e) {
                                            Constants.LOGGER.error("Error while renaming a home! => ", e);
                                            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.renameError", player)
                                                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                            return 1;
                                        }
                                        return 0;
                                    }))));


            commandDispatcher.register(Commands.literal("defaulthome")
                    .then(argument("name", StringArgumentType.string()).suggests(new HomeSuggestionProvider())
                            .executes(context -> {
                                final String name = StringArgumentType.getString(context, "name");
                                final ServerPlayer player = context.getSource().getPlayerOrException();

                                try {
                                    SetDefaultHome(player, name);

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while setting the default home! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.defaultError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return 1;
                                }
                                return 0;
                            })));

            commandDispatcher.register(Commands.literal("homes")
                    .executes(context -> {
                        final ServerPlayer player = context.getSource().getPlayerOrException();

                        try {
                            PrintHomes(player);

                        } catch (Exception e) {
                            Constants.LOGGER.error("Error while printing the homes! => ", e);
                            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.homes.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                            return 1;
                        }
                        return 0;
                    }));
        }
        commandDispatcher.register(Commands.literal("ghome")
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();

                    Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
                    if (optionalPlayerStorage.isEmpty()) {
                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
                        return 1;
                    }

                    try {
                        new HomeGUI(player).open();

                    } catch (Exception e) {
                        Constants.LOGGER.error("Error while opening the home gui! => ", e);
                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.homes.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                        return 1;
                    }
                    return 0;
                }).then(argument("name", StringArgumentType.string())
                        .suggests(new HomeSuggestionProvider())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");
                            final ServerPlayer player = context.getSource().getPlayerOrException();

                            try {
                                String lowerCaseName = name.toLowerCase();

                                Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
                                if (optionalPlayerStorage.isEmpty()) {
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
                                    return 1;
                                }

                                Player playerStorage = optionalPlayerStorage.get();

                                if (playerStorage.getHome(lowerCaseName).isEmpty()) {
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.RED), true);
                                    return 1;
                                }

                                new HomeActionGUI(player, playerStorage.getHome(lowerCaseName).get(), null).open();

                            } catch (Exception e) {
                                Constants.LOGGER.error("Error while openning home gui => ", e);
                                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.homes.error", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                return 1;
                            }
                            return 0;
                        })));
    }


    // -----

    // Adds a new home to the homeList of a player
    private static void SetHome(ServerPlayer player, String homeName) throws Exception {
        homeName = homeName.toLowerCase();
        BlockPos blockPos = player.blockPosition();
        String worldString = player.level().dimension().identifier().toString();

        // Gets the player's storage and creates it if it doesn't exist
        Player playerStorage = StorageManager.STORAGE.addPlayer(player.getStringUUID());

        // Create the NamedLocation
        NamedLocation warp = new NamedLocation(homeName, blockPos, worldString);

        // Adds the home, returns true if the home already exists
        boolean homeExists = playerStorage.addHome(warp);

        if (homeExists) {
            // Display error message that the home already exists
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.exists", player).withStyle(ChatFormatting.RED), true);

        } else {
            // Set it as the default if there are no other homes
            if (playerStorage.getHomes().size() == 1) {
                playerStorage.setDefaultHome(homeName);
            }

            // Display message that the home has been set
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.set", player), true);
        }
    }

    // Teleports the player to the home. It will go to the defaultHome if homeName is empty
    private static void GoHome(ServerPlayer player, String homeName) throws Exception {
        homeName = homeName.toLowerCase();

        // Get player storage
        Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
        if (optionalPlayerStorage.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        Player playerStorage = optionalPlayerStorage.get();

        // If homeName is empty, get the default home
        if (homeName.isEmpty()) {
            String defaultHome = playerStorage.getDefaultHome();

            if (defaultHome.isEmpty()) {
                // No default home set!
                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.defaultNone", player).withStyle(ChatFormatting.AQUA), true);

                return;
            } else {
                homeName = defaultHome;
            }
        }

        // Get the home (if it exists)
        Optional<NamedLocation> optionalHome = playerStorage.getHome(homeName);
        if (optionalHome.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        NamedLocation home = optionalHome.get();

        // Get the world, otherwise give a warning and error message
        Optional<ServerLevel> optionalWorld = home.getWorld();

        if (optionalWorld.isEmpty()) {
            Constants.LOGGER.warn("({}) Error while going to the home \"{}\"! \nCouldn't find a world with the id: \"{}\" \nAvailable worlds: {}",
                    player.getName().getString(),
                    home.getName(),
                    home.getWorldString(),
                    tools.getWorldIds());

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.worldNotFound", player)
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);

            return;
        }

        ServerLevel homeWorld = optionalWorld.get();

        BlockPos teleportBlockPos = home.getBlockPos();

        // Check if the player is already at this location (in the same world)
        if (player.blockPosition().equals(teleportBlockPos) && player.level() == homeWorld) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.goSame", player).withStyle(ChatFormatting.AQUA), true);

        } else {
            // Teleport the player!
            Vec3 teleportPos = new Vec3(teleportBlockPos.getX() + 0.5, teleportBlockPos.getY(), teleportBlockPos.getZ() + 0.5);

            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.go", player), true);
            Teleporter(player, homeWorld, teleportPos);
        }
    }

    private static void DeleteHome(ServerPlayer player, String homeName) throws Exception {
        homeName = homeName.toLowerCase();

        // Gets player storage
        Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
        if (optionalPlayerStorage.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        Player playerStorage = optionalPlayerStorage.get();

        // Get the home from the player
        Optional<NamedLocation> optionalHome = playerStorage.getHome(homeName);
        if (optionalHome.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        // delete the home
        playerStorage.deleteHome(optionalHome.get());

        // check if it's the default home, if it is set it to the default value
        if (playerStorage.getDefaultHome().equals(homeName)) {
            playerStorage.setDefaultHome("");

            // todo! maybe ask the player if they want to set a new default home? :3
        }

        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.delete", player), true);
    }

    private static void RenameHome(ServerPlayer player, String homeName, String newHomeName) throws Exception {
        homeName = homeName.toLowerCase();
        newHomeName = newHomeName.toLowerCase();

        // Gets player storage
        Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
        if (optionalPlayerStorage.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        Player playerStorage = optionalPlayerStorage.get();

        // Check if there already is a home with the new name
        if (playerStorage.getHome(newHomeName).isPresent()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.common.nameExists", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        // Get the home that needs to be renamed
        Optional<NamedLocation> optionalHome = playerStorage.getHome(homeName);
        if (optionalHome.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        // Rename home
        optionalHome.get().setName(newHomeName);

        // check if the current home is the default, then change it to the new name
        if (playerStorage.getDefaultHome().equals(homeName)) {
            playerStorage.setDefaultHome(newHomeName);
        }

        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.rename", player), true);
    }

    private static void SetDefaultHome(ServerPlayer player, String homeName) throws Exception {
        homeName = homeName.toLowerCase();

        // Gets player storage
        Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
        if (optionalPlayerStorage.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        Player playerStorage = optionalPlayerStorage.get();

        // Check if the new default home exists
        if (playerStorage.getHome(homeName).isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.RED), true);
            return;
        }

        // Check if the home is already the default
        if (playerStorage.getDefaultHome().equals(homeName)) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.defaultSame", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        // set the new default
        playerStorage.setDefaultHome(homeName);
        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.default", player), true);
    }

    private static void PrintHomes(ServerPlayer player) throws Exception {
        // Gets player storage, if no storage then the player is homeless!
        Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
        if (optionalPlayerStorage.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        Player playerStorage = optionalPlayerStorage.get();

        List<NamedLocation> homes = playerStorage.getHomes();

        // Check if there are any homes lol
        if (homes.isEmpty()) {
            player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.homeless", player).withStyle(ChatFormatting.AQUA), true);
            return;
        }

        MutableComponent message = Component.empty();

        // make da message
        message.append(getTranslatedText("commands.geyser_tpc.homes.homes", player)
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
        );


        for (NamedLocation currentHome : homes) {
            String name = String.format("  - %s", currentHome.getName());
            String coords = String.format("[X%d Y%d Z%d]", currentHome.getX(), currentHome.getY(), currentHome.getZ());
            String dimension = String.format(" [%s]", currentHome.getWorldString());

            // linebreak
            message.append("\n");

            // Name of the home
            message.append(Component.literal(name)
                    .withStyle(ChatFormatting.AQUA)
            );

            // If it is the default home, show that it is
            if (playerStorage.getDefaultHome().equals(currentHome.getName())) {

                message.append(" ")
                        .append(getTranslatedText("commands.geyser_tpc.common.default", player)
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                        );
            }

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
                                                    String.format("X%d Y%d Z%d", currentHome.getX(), currentHome.getY(), currentHome.getZ())
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
                                                    currentHome.getWorldString()
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
                    );

            // linebreak
            message.append("\n");

            //noinspection ConstantValue
            if (GeyserApi.api() != null && GeyserApi.api().connectionByUuid(player.getUUID()) != null) {
                message.append(getTranslatedText("commands.geyser_tpc.common.be.prompt", player))
                        .append(Component.literal(" /ghome"));
            } else {
                // Teleport, rename, set default and delete buttons
                message.append(Component.literal("     | ")
                                .withStyle(ChatFormatting.AQUA)
                        )
                        .append(getTranslatedText("commands.geyser_tpc.common.tp", player)
                                .withStyle(ChatFormatting.GREEN)
                                .withStyle(style ->
                                        style.withClickEvent(
                                                new ClickEvent.RunCommand(
                                                        String.format("/home \"%s\"", currentHome.getName())
                                                )
                                        )
                                )
                        )
                        .append(" ")
                        .append(getTranslatedText("commands.geyser_tpc.common.rename", player)
                                .withStyle(ChatFormatting.BLUE)
                                .withStyle(style ->
                                        style.withClickEvent(
                                                new ClickEvent.SuggestCommand(
                                                        String.format("/renamehome \"%s\" ", currentHome.getName())
                                                )
                                        )
                                )
                        )
                        .append(" ");

                // add set default button if it isn't the default home
                if (!playerStorage.getDefaultHome().equals(currentHome.getName())) {
                    message.append(getTranslatedText("commands.geyser_tpc.common.defaultPrompt", player)
                                    .withStyle(ChatFormatting.DARK_AQUA)
                                    .withStyle(style ->
                                            style.withClickEvent(
                                                    new ClickEvent.RunCommand(
                                                            String.format("/defaulthome \"%s\"", currentHome.getName())
                                                    )
                                            )
                                    )
                            )
                            .append(" ");
                }

                message.append(getTranslatedText("commands.geyser_tpc.common.delete", player)
                        .withStyle(ChatFormatting.RED)
                        .withStyle(style ->
                                style.withClickEvent(
                                        new ClickEvent.SuggestCommand(
                                                String.format("/delhome \"%s\"", currentHome.getName())
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

    public static class HomeGUI extends SimpleGui {

        private static final int PAGE_SIZE = 18; // 2行
        public List<NamedLocation> homes = new ArrayList<>();
        private int page = 0;

        public HomeGUI(ServerPlayer player) {
            super(MenuType.GENERIC_9x3, player, false);

            setTitle(getTranslatedText("gui.geyser_tpc.home.homegui.title", player));
            loadHomes();
            init();
        }

        private void loadHomes() {
            Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());
            if (optionalPlayerStorage.isEmpty()) {
                player.sendSystemMessage(
                        getTranslatedText("commands.geyser_tpc.home.homeless", player)
                                .withStyle(ChatFormatting.AQUA),
                        true
                );
                close();
                return;
            }

            Player playerStorage = optionalPlayerStorage.get();
            homes = playerStorage.getHomes();

            if (homes == null) homes = new ArrayList<>();
        }

        private void init() {

            clearSlots();

            if (homes.isEmpty()) {
                player.sendSystemMessage(
                        getTranslatedText("commands.geyser_tpc.home.homeless", player)
                                .withStyle(ChatFormatting.AQUA),
                        true
                );
                close();
                return;
            }

            int start = page * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, homes.size());

            int slot = 0;

            for (int idx = start; idx < end; idx++) {

                NamedLocation home = homes.get(idx);

                Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());

                Player playerStorage = optionalPlayerStorage.orElse(null);

                if (playerStorage != null && playerStorage.getHome(home.getName().toLowerCase()).isEmpty()) {
                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.RED), true);
                }

                boolean isDefault = playerStorage != null && playerStorage.getDefaultHome().equals(home.getName().toLowerCase());

                this.setSlot(slot,
                        new GuiElementBuilder(isDefault ? Items.NETHER_STAR : Items.RED_BED)
                                .setName(Component.literal(home.getName()).append(isDefault ? Component.literal("[").append(getTranslatedText("gui.geyser_tpc.home.homegui.defaulthome", player)).append("]") : Component.empty()))
                                .setCallback((i, t, a, g) -> {
                                    new HomeActionGUI(player, home, this).open();
                                    g.close();

                                })
                );

                slot++;
            }

            for (int i = 18; i <= 23; i++) {
                this.setSlot(i,
                        new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
                                .setName(Component.empty())
                );
            }

            this.setSlot(24,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.close", player))
                            .setCallback((i, t, a, g) -> g.close())
            );

            this.setSlot(25,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setProfileSkinTexture(Constants.GUI.ARROW_LEFT)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.pgup", player))
                            .setCallback((i, t, a, g) -> {
                                if (page > 0) page--;
                                init();
                            })
            );

            this.setSlot(26,
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.pgdn", player))
                            .setProfileSkinTexture(Constants.GUI.ARROW_RIGHT)
                            .setCallback((i, t, a, g) -> {
                                if ((page + 1) * PAGE_SIZE < homes.size()) {
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

    public static class HomeActionGUI extends SimpleGui {
        private final NamedLocation home;
        private final ServerPlayer player;
        private final HomeGUI parent;

        public HomeActionGUI(ServerPlayer player, NamedLocation home, HomeGUI parent) {
            super(MenuType.GENERIC_9x1, player, false);

            this.player = player;
            this.home = home;
            this.parent = parent;

            init();
        }

        private void init() {
            setTitle(Component.literal(home.getName()));
            this.setSlot(0,
                    new GuiElementBuilder(Items.ENDER_PEARL)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.tp", player))
                            .setCallback((i, t, a, g) -> {
                                try {
                                    GoHome(player, home.getName());
                                } catch (Exception e) {
                                    Constants.LOGGER.error("Teleport error", e);
                                }
                                g.close();
                            })
            );

            this.setSlot(1,
                    new GuiElementBuilder(Items.TNT)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.del", player))
                            .setCallback((i, t, a, g) -> {
                                new HomeDeleteGUI(player, home, this).open();
                                g.close();
                            })
            );

            this.setSlot(2,
                    new GuiElementBuilder(Items.NAME_TAG)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.rename", player))
                            .setCallback((i, t, a, g) -> {
                                new HomeRenameGUI(player, home, this).open();
                                g.close();
                            })
            );

            this.setSlot(3,
                    new GuiElementBuilder(Items.NETHER_STAR)
                            .setName(getTranslatedText("gui.geyser_tpc.home.homeactiongui.defaulthome", player))
                            .setCallback((i, t, a, g) -> {
                                try {
                                    SetDefaultHome(player, home.getName());

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while setting the default home! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.defaultError", player).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return;
                                }
                                g.close();
                                if (parent != null) {
                                    parent.init();
                                    parent.open();
                                }
                            })
            );

            for (int i = 4; i <= 6; i++) {
                this.setSlot(i,
                        new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
                                .setName(Component.empty())
                );
            }

            this.setSlot(7,
                    new GuiElementBuilder(Items.ARROW)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.back", player))
                            .setCallback((i, t, a, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.init();
                                    parent.open();
                                }
                            })
            );

            this.setSlot(8,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.close", player))
                            .setCallback((i, t, a, g) -> g.close())
            );
        }
    }

    public static class HomeDeleteGUI extends SimpleGui {

        private final NamedLocation home;
        private final HomeActionGUI parent;

        public HomeDeleteGUI(ServerPlayer player, NamedLocation home, HomeActionGUI parent) {
            super(MenuType.HOPPER, player, false);

            this.home = home;
            this.parent = parent;

            setTitle(getTranslatedText("gui.geyser_tpc.universal.gui.del", player).append(" Home \"" + home.getName() + "\""));

            this.setSlot(0,
                    new GuiElementBuilder(Items.RED_CONCRETE)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.cancel", player))
                            .setCallback((i, t, a, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );

            this.setSlot(4,
                    new GuiElementBuilder(Items.LIME_CONCRETE)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.yes", player))
                            .setCallback((i, t, a, g) -> {
                                g.close();

                                try {
                                    DeleteHome(player, home.getName());

                                } catch (Exception e) {
                                    Constants.LOGGER.error("Error while deleting a home! => ", e);
                                    player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.deleteError", player)
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

    public static class HomeRenameGUI extends AnvilInputGui {

        private final NamedLocation home;
        private final HomeActionGUI parent;

        public HomeRenameGUI(ServerPlayer player, NamedLocation home, HomeActionGUI parent) {
            super(player, false);

            this.home = home;
            this.parent = parent;

            setDefaultInputValue(home.getName());

            setTitle(getTranslatedText("gui.geyser_tpc.universal.gui.rename", player).append(" Home \"" + home.getName() + "\""));

            Optional<Player> optionalPlayerStorage = STORAGE.getPlayer(player.getStringUUID());

            Player playerStorage = optionalPlayerStorage.orElse(null);

            if (playerStorage != null && playerStorage.getHome(home.getName().toLowerCase()).isEmpty()) {
                player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.notFound", player).withStyle(ChatFormatting.RED), true);
            }

            boolean isDefault = playerStorage != null && playerStorage.getDefaultHome().equals(home.getName().toLowerCase());

            this.setSlot(0,
                    new GuiElementBuilder(isDefault ? Items.NETHER_STAR : Items.RED_BED).setName(Component.literal(home.getName()))
            );

            this.setSlot(1,
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.cancel", player))
                            .setCallback((i, t, a, g) -> {
                                g.close();
                                if (parent != null) {
                                    parent.open();
                                }
                            })
            );

            this.setSlot(2,
                    new GuiElementBuilder(Items.ANVIL)
                            .setName(getTranslatedText("gui.geyser_tpc.universal.gui.save", player))
                            .setCallback((i, t, a, g) -> {
                                String input = getInput();

                                if (input != null && !input.isBlank()) {
                                    try {
                                        RenameHome(player, home.getName(), input);

                                    } catch (Exception e) {
                                        Constants.LOGGER.error("Error while renaming a home! => ", e);
                                        player.sendSystemMessage(getTranslatedText("commands.geyser_tpc.home.renameError", player)
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