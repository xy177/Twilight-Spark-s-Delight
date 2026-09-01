package xy177.twilightsparksdelight.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandGiveExperiment250 extends CommandBase
{
    private static final List<String> LEVELS = Arrays.asList("1", "2", "3", "4", "5", "6");

    @Override
    public String getName()
    {
        return "tsd250";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.twilight_spark_delight.tsd250.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2 || args.length > 3) {
            throw new WrongUsageException(getUsage(sender));
        }

        int level = parseInt(args[0], Experiment250Item.MIN_LEVEL, Experiment250Item.MAX_LEVEL);
        double activity = parseDouble(args[1], 0.0D);
        double capacity = Experiment250Item.getCapacity(level);
        if (activity > capacity) {
            throw new CommandException(
                "commands.twilight_spark_delight.tsd250.activity_too_high",
                format(capacity),
                level
            );
        }

        EntityPlayerMP target = args.length == 3
            ? getPlayer(server, sender, args[2])
            : getCommandSenderAsPlayer(sender);
        ItemStack stack = Experiment250Item.createStack(TSDItems.EXPERIMENT_250, level, activity);
        if (target.inventory.addItemStackToInventory(stack)) {
            target.inventoryContainer.detectAndSendChanges();
        } else {
            target.dropItem(stack, false);
        }

        notifyCommandListener(
            sender,
            this,
            "commands.twilight_spark_delight.tsd250.success",
            target.getName(),
            level,
            format(activity),
            format(capacity)
        );
    }

    @Override
    public List<String> getTabCompletions(
        MinecraftServer server,
        ICommandSender sender,
        String[] args,
        @Nullable BlockPos targetPos
    )
    {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, LEVELS);
        }
        if (args.length == 2) {
            try {
                int level = Integer.parseInt(args[0]);
                if (level >= Experiment250Item.MIN_LEVEL && level <= Experiment250Item.MAX_LEVEL) {
                    return getListOfStringsMatchingLastWord(
                        args,
                        Arrays.asList("0", format(Experiment250Item.getCapacity(level)))
                    );
                }
            } catch (NumberFormatException ignored) {
            }
            return Collections.emptyList();
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 2;
    }

    private static String format(double value)
    {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
