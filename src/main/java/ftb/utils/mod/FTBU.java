package ftb.utils.mod;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.*;
import ftb.lib.api.cmd.CommandLM;
import ftb.utils.mod.cmd.*;
import ftb.utils.mod.cmd.admin.CmdAdmin;
import ftb.utils.mod.config.FTBUConfig;
import ftb.utils.mod.handlers.*;
import ftb.utils.mod.handlers.ftbl.FTBLIntegration;
import ftb.utils.net.FTBUNetHandler;
import ftb.utils.world.Backups;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.*;

import java.util.Map;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP, acceptedMinecraftVersions = "1.7.10")
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "ftb.utils.mod.client.FTBUClient", serverSide = "ftb.utils.mod.FTBUCommon")
	public static FTBUCommon proxy;
	
	@SidedProxy(clientSide = "ftb.utils.mod.handlers.ftbl.FTBLIntegrationClient", serverSide = "ftb.utils.mod.handlers.ftbl.FTBLIntegration")
	public static FTBLIntegration ftbl_int;
	
	public static LMMod mod;
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = LMMod.create(FTBUFinals.MOD_ID);
		FTBLib.ftbu = ftbl_int;
		logger = LogManager.getLogger(FTBUFinals.MOD_ID);
		
		FTBUConfig.load();
		
		EventBusHelper.register(new FTBUPlayerEventHandler());
		EventBusHelper.register(new FTBUWorldEventHandler());
		EventBusHelper.register(new FTBUChatEventHandler());
		FTBUChunkEventHandler.instance.init();
		
		FTBUNetHandler.init();
		Backups.init();
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		addCmd(e, new CmdAdmin());
		addCmd(e, new CmdBack());
		addCmd(e, new CmdHome());
		addCmd(e, new CmdSpawn());
		addCmd(e, new CmdTplast());
		addCmd(e, new CmdWarp());
		addCmd(e, new CmdLMPlayerSettings());
	}
	
	private void addCmd(FMLServerStartingEvent e, CommandLM c)
	{ if(!c.commandName.isEmpty()) e.registerServerCommand(c); }
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(FTBLib.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(null))
				FTBUPlayerEventHandler.playerLoggedOut(ep);
		}
	}
	
	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> m, Side side)
	{
		String s = m.get(FTBUFinals.MOD_ID);
		return s == null || s.equals(FTBUFinals.MOD_VERSION);
	}
}