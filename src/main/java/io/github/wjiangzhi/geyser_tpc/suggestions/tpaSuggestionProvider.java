package io.github.wjiangzhi.geyser_tpc.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.wjiangzhi.geyser_tpc.Constants;
import io.github.wjiangzhi.geyser_tpc.commands.tpa;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class tpaSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();

            List<tpa.tpaArrayClass> playerTpaList = tpa.tpaList.stream()
                    .filter(tpa -> Objects.equals(player.getStringUUID(), tpa.RecPlayer))
                    .toList();

            for (tpa.tpaArrayClass tpaEntry : playerTpaList) {

                Optional<String> recPlayerName = Optional.ofNullable(context.getSource().getServer().getPlayerList().getPlayer(UUID.fromString(tpaEntry.InitPlayer)).getName().getString());

                if (recPlayerName.isPresent()) {
                    builder.suggest(recPlayerName.get());
                }
            }

            // Build and return the suggestions
            return builder.buildFuture();
        } catch (Exception e) {
            Constants.LOGGER.error("Error getting tpa suggestions! ", e);
            return null;
        }
    }
}
