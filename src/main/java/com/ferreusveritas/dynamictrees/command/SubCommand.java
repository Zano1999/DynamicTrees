package com.ferreusveritas.dynamictrees.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;

public abstract class SubCommand {

    protected abstract String getName ();
    protected abstract int execute (CommandContext<CommandSource> context);

    public ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal(this.getName())
                .executes(this::execute);
    }

    protected void sendMessage (CommandContext<CommandSource> context, ITextComponent message) {
        context.getSource().sendFeedback(message, true);
    }

}