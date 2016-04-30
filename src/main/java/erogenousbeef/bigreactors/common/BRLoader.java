package erogenousbeef.bigreactors.common;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import erogenousbeef.bigreactors.GuiHandler;
import erogenousbeef.bigreactors.api.imc.IMC;
import erogenousbeef.bigreactors.common.machine.PacketRedstoneMode;
import erogenousbeef.bigreactors.common.tileentity.liquidizer.LiquidizerRecipeManager;
import erogenousbeef.bigreactors.core.network.MessageTileNBT;
import erogenousbeef.bigreactors.core.util.Lang;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.core.multiblock.MultiblockEventHandler;

@Mod(modid = BRLoader.MOD_ID, name = BigReactors.NAME, version = BRConfig.VERSION, acceptedMinecraftVersions = BRConfig.MINECRAFT_VERSION, dependencies = BRLoader.DEPENDENCIES)
public class BRLoader {

	public static final String MOD_ID = BigReactors.MODID;
    public static final String DEPENDENCIES = "required-after:Forge@[10.13.2.1291,);required-after:CoFHCore;after:ThermalExpansion";
	
	@Instance(MOD_ID)
	public static BRLoader instance;

	@SidedProxy(clientSide = "erogenousbeef.bigreactors.client.ClientProxy", serverSide = "erogenousbeef.bigreactors.common.CommonProxy")
	public static CommonProxy proxy;

    public static GuiHandler guiHandler = new GuiHandler();
    public static final Lang lang = new Lang("bigreactors");
	
	@Mod.Metadata(MOD_ID)
	public static ModMetadata metadata;
	
	private MultiblockEventHandler multiblockEventHandler;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
        proxy.loadIcons();

		BigReactors.registerOres(0, true);
		BigReactors.registerIngots(0);
		BigReactors.registerFuelRods(0, true);
		BigReactors.registerReactorPartBlocks(0, true);
		BigReactors.registerTurbineParts();
		BigReactors.registerDevices(0,  true);
		BigReactors.registerFluids(0,  true);
		BigReactors.registerCreativeParts(0, true);
		BigReactors.registerItems();
		BigReactors.registerExchangerPartBlocks(0, true);

		BigReactors.registerMachines();

		StandardReactants.register();
		
		BigReactors.eventHandler = new BREventHandler();
		MinecraftForge.EVENT_BUS.register(BigReactors.eventHandler);
		MinecraftForge.EVENT_BUS.register(proxy);
		
		multiblockEventHandler = new MultiblockEventHandler();
		MinecraftForge.EVENT_BUS.register(multiblockEventHandler);
		
		proxy.preInit();
		
		Fluid waterFluid = FluidRegistry.WATER; // Force-load water to prevent startup crashes
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

        CommonPacketHandler.INSTANCE.registerMessage(MessageTileNBT.class, MessageTileNBT.class, CommonPacketHandler.nextID(), Side.SERVER);
        CommonPacketHandler.INSTANCE.registerMessage(PacketRedstoneMode.class, PacketRedstoneMode.class, CommonPacketHandler.nextID(), Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

		BigReactors.register(this);

        proxy.load();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {

		//This must be loaded before parsing the recipes so we get the preferred outputs
		OreDictionaryPreferences.loadConfig();

		LiquidizerRecipeManager.getInstance().loadRecipesFromConfig();

		proxy.postInit();
	}
	
	@EventHandler
	public void onIMCEvent(FMLInterModComms.IMCEvent event) {
		// TODO
	}
	
	// GAME EVENT HANDLERS
	// FORGE EVENT HANDLERS

	// Handle bucketing of reactor fluids
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent e)
    {
        if(e.current.getItem() != Items.bucket)
        {
            return;
        }
        ItemStack filledBucket = fillBucket(e.world, e.target);
        if(filledBucket != null)
        {
            e.world.setBlockToAir(e.target.blockX, e.target.blockY, e.target.blockZ);
            e.result = filledBucket;
            e.setResult(Result.ALLOW);
        }
    }
    
    private ItemStack fillBucket(World world, MovingObjectPosition mop)
    {
        Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
        if(block == BigReactors.fluidCyaniteStill) return new ItemStack(BigReactors.fluidCyaniteBucketItem);
        else if(block == BigReactors.fluidYelloriumStill) return new ItemStack(BigReactors.fluidYelloriumBucketItem);
        else return null;
    }

    @EventHandler
    public void onImc(FMLInterModComms.IMCEvent evt) {
        processImc(evt.getMessages());
    }

    private void processImc(ImmutableList<FMLInterModComms.IMCMessage> messages) {
        for (FMLInterModComms.IMCMessage msg : messages) {
            String key = msg.key;
            try {
                if(msg.isStringMessage()) {
                    String value = msg.getStringValue();
                    if (IMC.LIQUIDIZER_RECIPE.equals(key)) {
                        LiquidizerRecipeManager.getInstance().addCustomRecipes(value);
                    }
                }
            } catch (Exception e) {
                BRLog.error("Error occured handling IMC message " + key + " from " + msg.getSender());
            }
        }
    }
}
