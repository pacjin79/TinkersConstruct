package tinker.tconstruct.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SearedBrickItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "Brick"
    };

    public SearedBrickItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata(int meta)
    {
        return meta;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append("SearedBrick.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
