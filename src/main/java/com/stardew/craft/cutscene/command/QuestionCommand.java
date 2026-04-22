package com.stardew.craft.cutscene.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * question: presents choices to the player and branches to different command sequences.
 * JSON:
 * {
 *   "cmd": "question",
 *   "text": "stardewcraft.event.wizard.question1",
 *   "choices": [
 *     {
 *       "text": "stardewcraft.event.wizard.choice1",
 *       "commands": [...]
 *     },
 *     {
 *       "text": "stardewcraft.event.wizard.choice2",
 *       "commands": [...]
 *     }
 *   ]
 * }
 *
 * The event pauses until the player picks a choice, then the chosen branch's
 * commands are executed as a sub-sequence.
 */
@OnlyIn(Dist.CLIENT)
public class QuestionCommand implements EventCommand {

    private final String questionText;
    private final List<ChoiceBranch> branches;
    private boolean waitingForChoice = true;
    private List<EventCommand> chosenCommands = null;
    private int subIndex = 0;

    public QuestionCommand(String questionText, List<ChoiceBranch> branches) {
        this.questionText = questionText;
        this.branches = branches;
    }

    public record ChoiceBranch(String text, List<EventCommand> commands) {}

    /**
     * Parse from JSON.
     */
    public static QuestionCommand fromJson(JsonObject obj) {
        String text = obj.has("text") ? obj.get("text").getAsString() : "";
        JsonArray choicesArr = obj.getAsJsonArray("choices");
        List<ChoiceBranch> branches = new ArrayList<>();

        for (var el : choicesArr) {
            JsonObject choiceObj = el.getAsJsonObject();
            String choiceText = choiceObj.get("text").getAsString();
            JsonArray cmdsArr = choiceObj.getAsJsonArray("commands");
            List<EventCommand> cmds = new ArrayList<>();
            for (var cmdEl : cmdsArr) {
                EventCommand cmd = EventCommandFactory.create(cmdEl.getAsJsonObject());
                if (cmd != null) cmds.add(cmd);
            }
            branches.add(new ChoiceBranch(choiceText, cmds));
        }

        return new QuestionCommand(text, branches);
    }

    @Override
    public void start(EventPlayer player) {
        waitingForChoice = true;
        chosenCommands = null;
        subIndex = 0;

        // Open choice screen
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ChoiceScreen(
                Component.translatable(questionText),
                branches,
                this::onChoiceSelected,
                player
        ));
    }

    private void onChoiceSelected(int choiceIndex, EventPlayer player) {
        if (choiceIndex >= 0 && choiceIndex < branches.size()) {
            chosenCommands = branches.get(choiceIndex).commands();
        } else {
            chosenCommands = List.of();
        }
        waitingForChoice = false;
        subIndex = 0;

        // Start first sub-command
        if (chosenCommands != null && !chosenCommands.isEmpty()) {
            chosenCommands.get(0).start(player);
        }
    }

    @Override
    public void tick(EventPlayer player) {
        if (waitingForChoice) return;
        if (chosenCommands == null || chosenCommands.isEmpty()) return;

        if (subIndex >= chosenCommands.size()) return;

        EventCommand current = chosenCommands.get(subIndex);
        current.tick(player);

        if (current.isComplete()) {
            subIndex++;
            if (subIndex < chosenCommands.size()) {
                chosenCommands.get(subIndex).start(player);
            }
        }
    }

    @Override
    public boolean isComplete() {
        if (waitingForChoice) return false;
        if (chosenCommands == null || chosenCommands.isEmpty()) return true;
        return subIndex >= chosenCommands.size();
    }

    @Override
    public void onSkip(EventPlayer player) {
        waitingForChoice = false;
        // On skip, auto-pick first choice and execute state commands
        if (!branches.isEmpty()) {
            for (EventCommand cmd : branches.get(0).commands()) {
                if (cmd.isStateCommand()) {
                    cmd.onSkip(player);
                }
            }
        }
        chosenCommands = List.of(); // clear so isComplete returns true
    }

    // ─── Inner Screen ───

    @OnlyIn(Dist.CLIENT)
    private static class ChoiceScreen extends Screen {

        private final List<ChoiceBranch> branches;
        private final ChoiceCallback callback;
        private final EventPlayer eventPlayer;

        @FunctionalInterface
        interface ChoiceCallback {
            void accept(int index, EventPlayer player);
        }

        protected ChoiceScreen(Component title, List<ChoiceBranch> branches,
                               ChoiceCallback callback, EventPlayer eventPlayer) {
            super(title);
            this.branches = branches;
            this.callback = callback;
            this.eventPlayer = eventPlayer;
        }

        @Override
        protected void init() {
            int buttonWidth = 220;
            int buttonHeight = 20;
            int gap = 6;
            int totalHeight = branches.size() * (buttonHeight + gap) - gap;
            int startY = (this.height - totalHeight) / 2 + 20;

            // Render question text above buttons
            for (int i = 0; i < branches.size(); i++) {
                final int index = i;
                String text = branches.get(i).text();
                Component label = Component.translatable(text);
                this.addRenderableWidget(Button.builder(label, btn -> {
                    callback.accept(index, eventPlayer);
                    Minecraft.getInstance().setScreen(null);
                }).bounds((this.width - buttonWidth) / 2, startY + i * (buttonHeight + gap),
                        buttonWidth, buttonHeight).build());
            }
        }

        @Override
        public void renderBackground(@javax.annotation.Nonnull net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Semi-transparent dark overlay
            graphics.fill(0, 0, this.width, this.height, 0x80000000);

            // Draw question text
            graphics.drawCenteredString(this.font, this.title,
                    this.width / 2, this.height / 2 - 50, 0xFFFFFFFF);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false; // must pick a choice
        }
    }
}
