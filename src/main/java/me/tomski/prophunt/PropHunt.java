package me.tomski.prophunt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.tomski.prophuntstorage.ArenaStorage;
import me.tomski.arenas.ArenaManager;
import me.tomski.bungee.Pinger;
import me.tomski.classes.HiderClass;
import me.tomski.language.LanguageManager;
import me.tomski.listeners.SetupListener;
import me.tomski.blocks.ProtocolTask;
import me.tomski.classes.SeekerClass;
import me.tomski.listeners.PropHuntListener;
import me.tomski.utils.LogFilter;
import me.tomski.utils.MetricsLite;
import me.tomski.utils.PingTimer;
import me.tomski.utils.PropHuntMessaging;
import me.tomski.utils.Reason;
import me.tomski.utils.SideBarStats;
import me.tomski.utils.SideTabTimer;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class PropHunt extends JavaPlugin{


	public static DisguiseCraftAPI dc;
	public static DisguiseManager dm;
	public static ProtocolManager protocolManager;
	
	public ArenaStorage AS;
	public GameManager GM;
	public ArenaManager AM;
	private LanguageManager LM;
	public SideBarStats SBS;
	public static boolean usingTABAPI = false;
	
	
	public void onEnable(){
		getConfig().options().copyDefaults(true);
		saveConfig();
		try {
			init();
		} catch (IOException e1) {
			e1.printStackTrace();
            		}
		try {
		    MetricsLite metrics = new MetricsLite(this);
		    metrics.start();
		} catch (IOException e) {
            e.printStackTrace();
		}
		 getServer().getLogger().setFilter(new LogFilter());
	}
	
	public void onDisable(){
		if(GameManager.gameStatus){
			try {
				GM.endGame(Reason.HOSTENDED,true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		AS.saveData();
	}
	
	public void init() throws IOException{
		GM = new GameManager(this);
		AS = new ArenaStorage(this,GM);
		AM = new ArenaManager(this);
		
		dc = DisguiseCraft.getAPI();

		dm = new DisguiseManager(this);
		loadProtocolManager();
		ProtocolTask pt = new ProtocolTask(this);
		pt.initProtocol();
		getServer().getPluginManager().registerEvents(pt,this);

		loadConfigSettings();
		AutomationSettings.initSettings(this);
		LM = new LanguageManager(this);
		
		AS.loadData();
		AM.resetCounterAndArray();
		if(GameManager.useSideStats){
            SBS = new SideBarStats();

			SideTabTimer stt = new SideTabTimer(SBS);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, stt, 20L, 20L);
		}


		getServer().getPluginManager().registerEvents(new PropHuntListener(this,GM), this);
		getServer().getPluginManager().registerEvents(new SetupListener(this), this);
		getServer().getPluginManager().registerEvents(new ServerManager(this), this);

		usingCustomTab();
		
		if(usingPropHuntSigns()){
			getServer().getMessenger().registerOutgoingPluginChannel(this,"BungeeCord");
			Pinger ping = new Pinger(this);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new PingTimer(ping), 20L, BungeeSettings.pingInterval);
			getLogger().info("Ping timer initiated at " +BungeeSettings.pingInterval +" ticks per ping");
		}
		
		if(GameManager.automatic){
			checkAUTOReady();
		}
	}

	  
	public void loadProtocolManager() {
	    protocolManager = ProtocolLibrary.getProtocolManager();
	}
	
	private boolean checkAUTOReady() {
		if(ArenaManager.arenasInRotation==null){
			getLogger().log(Level.WARNING, "Arena Not Setup, automatic hosting disabled");
			return false;
		}
		if(ArenaManager.arenasInRotation.length==0){
			GameManager.automatic=false;
			getLogger().log(Level.WARNING, "No arena setup, automatic hosting disabled");
			return false;
		}
		if(!GM.checkReady(ArenaManager.arenasInRotation[0])){
			GameManager.automatic=false;
			getLogger().log(Level.WARNING, "Arena Not Setup, automatic hosting disabled");
			return false;
		}else{
			GM.hostGame(null,ArenaManager.arenasInRotation[0]);
			return true;
		}
	}
	private boolean usingPropHuntSigns(){
		if(getConfig().getBoolean("BungeeSettings.using-bungee")){
			BungeeSettings.usingBungee = true;
			getLogger().log(Level.INFO,"Using PropHunt bungee signs :)");
			BungeeSettings.usingPropHuntSigns = true;
			BungeeSettings.hubname = getConfig().getString("BungeeSettings.hub-name");
			BungeeSettings.pingInterval = getConfig().getInt("BungeeSettings.ping-interval-ticks");
			BungeeSettings.bungeeName = getConfig().getString("BungeeSettings.this-bungee-server-name");
			BungeeSettings.kickToHub = getConfig().getBoolean("BungeeSettings.kick-back-to-hub");

			return true;
		}else{
			return false;
		}

	}

	private void usingCustomTab(){
		Plugin p = getServer().getPluginManager().getPlugin("TabAPI");
		if(p==null){
			usingTABAPI=false;
			return;
		}
		if(p.isEnabled()&&getConfig().getBoolean("using-custom-tab")){
			usingTABAPI = true;
			getLogger().log(Level.INFO,"Using Custom TAB :)");
			return;
		}
		if(GameManager.blowDisguises){
			getLogger().log(Level.INFO, "Preventing blowing disguises as you are not using TabAPI");
			GameManager.blowDisguises = false;
		}else{
			getLogger().log(Level.INFO,"Not using Custom TAB");
		}
		return;
	}
	
	private void loadConfigSettings() {
		if(getConfig().contains("automatic")){
			GameManager.automatic = getConfig().getBoolean("automatic");
			if(GameManager.automatic){
				if(getConfig().contains("dedicated")){
					GameManager.dedicated = getConfig().getBoolean("dedicated");
				}
			}
		}
		if(getConfig().contains("players-to-start")){
			GameManager.playersToStartGame = getConfig().getInt("players-to-start");
		}
		if(getConfig().contains("starting-time")){
			GameManager.starting_time = getConfig().getInt("starting-time");
		}
		if(getConfig().contains("interval")){
			GameManager.interval = getConfig().getInt("interval");
		}
		if(getConfig().contains("lobby-time")){
			GameManager.lobbyTime = getConfig().getInt("lobby-time");
		}
		if (getConfig().contains("seeker-damage")) {
			GameManager.seeker_damage = getConfig().getDouble("seeker-damage");
		}
		if(getConfig().contains("time-reward")){
			GameManager.time_reward = getConfig().getInt("time-reward");
		}
		if(getConfig().contains("blow-disguises-last-30-seconds")){
			GameManager.blowDisguises = getConfig().getBoolean("blow-disguises-last-30-seconds");
		}
		getLogger().log(Level.INFO, "Prop Hunt settings Loaded");

		if(getConfig().contains("crouching-block-lock")){
			GameManager.crouchBlockLock = getConfig().getBoolean("crouching-block-lock");
		}
		
		if(getConfig().contains("use-solid-block")){
			GameManager.usingSolidBlock = getConfig().getBoolean("use-solid-block");
			GameManager.solidBlockTime = getConfig().getInt("solid-block-time");
		}
		
		if(getConfig().contains("seeker-delay-time")){
			GameManager.seekerDelayTime = getConfig().getInt("seeker-delay-time");
		}
		if(getConfig().contains("seeker-lives")){
			GameManager.seekerLivesAmount = getConfig().getInt("seeker-lives");
		}
		if(getConfig().contains("using-custom-tab")){
			usingTABAPI = getConfig().getBoolean("using-custom-tab");
		}
		if(getConfig().contains("use-hitmarkers")){
			GameManager.usingHitmarkers = getConfig().getBoolean("use-hitmarkers");
		}
		if(getConfig().contains("use-hitsounds")){
			GameManager.usingHitsounds = getConfig().getBoolean("use-hitsounds");
		}
		if(getConfig().contains("blind-seeker-in-delay")){
			GameManager.blindSeeker = getConfig().getBoolean("blind-seeker-in-delay");
		}
		if(getConfig().contains("auto-respawn")){
			GameManager.autoRespawn = getConfig().getBoolean("auto-respawn");
		}
		if(getConfig().contains("use-side-scoreboard-stats")){
			GameManager.useSideStats = getConfig().getBoolean("use-side-scoreboard-stats");
		}
		if(getConfig().contains("ServerSettings")){
			ServerManager.forceMOTD = getConfig().getBoolean("ServerSettings.force-motd-prophunt");
			ServerManager.forceMaxPlayers = getConfig().getBoolean("ServerSettings.force-max-players");
			ServerManager.forceMaxPlayersSize = getConfig().getInt("ServerSettings.force-max-players-size");
			ServerManager.blockAccessWhilstInGame = getConfig().getBoolean("ServerSettings.block-access-whilst-in-game");
		}
	}
	


	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player p = (Player)sender;			
			if(cmd.getName().equalsIgnoreCase("prophunt")){
				
				if(args.length==0){
					if(p.hasPermission("prophunt.hostcommand.host")){
						PropHuntMessaging.sendMessage(p, "Use /ph host <ArenaName>");
						PropHuntMessaging.sendAvailableArenas(p, ArenaManager.playableArenas);
						return true;
					}
					PropHuntMessaging.sendPlayerHelp(p);
					return true;
				}
				
				if(args.length==1){
					
					if(args[0].equalsIgnoreCase("configreload")){
						if(!sender.hasPermission("prophunt.admin.configreload")){
							PropHuntMessaging.sendMessage(p, "You do not have permission to reload the config");
							return true;
						}
						reloadConfig();
						loadConfigSettings();
						loadBlockDisguises();
						setupClasses();
						AS.loadData();
						LM.initfile();

						if(GameManager.automatic){
							if(!checkAUTOReady()){
								PropHuntMessaging.sendMessage(p,"Arena not setup, automatic hosting disabled");
							}
						}
						PropHuntMessaging.sendMessage(p, "Config reloaded");
						return true;
					}
					if(args[0].equalsIgnoreCase("start")){
						if(!sender.hasPermission("prophunt.hostcommand.start")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to start a PropHunt Game");
							return true;
						}
						GM.startGame(p);
						return true;
					}
					if(args[0].equalsIgnoreCase("stop")){
						if(!sender.hasPermission("prophunt.hostcommand.stop")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to stop a PropHunt Game");
							return true;
						}
						try {
							GM.endGame(Reason.HOSTENDED,false);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("join")){
						
						if(!sender.hasPermission("prophunt.command.join")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to join a PropHunt Game");
							return true;
						}
						if(GameManager.dedicated){
							PropHuntMessaging.sendMessage(p,"Disabled in dedicated mode");
							return true;
						}
						if(GameManager.gameStatus){
							PropHuntMessaging.sendMessage(p, "Game is in progress");

							return true;
						}
						if(GameManager.isHosting){
							GM.addPlayerToGame(p.getName());
						}else{
							PropHuntMessaging.sendMessage(p, "There is no game being hosted");
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("leave")){
						if(!sender.hasPermission("prophunt.command.leave")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to leave a PropHunt Game");
							return true;
						}
						if(GameManager.dedicated){
							PropHuntMessaging.sendMessage(p,"Disabled in dedicated mode");
							return true;
						}
						if(GameManager.gameStatus){
							PlayerManagement.gameRestorePlayer(p);
							try {
								GM.kickPlayer(p.getName());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else if(GameManager.playersWaiting.contains(p.getName())){
							GameManager.playersWaiting.remove(p.getName());
							try {
								GM.kickPlayer(p.getName());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("status")){
						if(!sender.hasPermission("prophunt.command.status")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to check the status of a PropHunt Game");
							return true;
						}
						PropHuntMessaging.sendGameStatus(p);
						return true;
					}
					if(args[0].equalsIgnoreCase("debug")&& sender.isOp()){
						sender.sendMessage("Debug for Tomski");
						return true;
					}
					if(args[0].equalsIgnoreCase("spectate")){
						if(!sender.hasPermission("prophunt.command.spectate")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to spectate a PropHunt Game");
							return true;
						}
						if(GameManager.gameStatus){
							GM.spectateGame(p);
						}else{
							PropHuntMessaging.sendMessage(p, "There is no game in progress");
						}
						return true;
					}
					if(p.hasPermission("prophunt.hostcommand.host")){
						PropHuntMessaging.sendHostHelp(p);
						return true;
					}
					PropHuntMessaging.sendPlayerHelp(p);
					return true;
				}
				
				if(args.length==2){
					if(args[0].equalsIgnoreCase("setup")){
						if(!sender.hasPermission("prophunt.admin.setup")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to setup a PropHunt Arena");
							return true;
						}
						if(!AM.hasInvetorySpace(p)){
							PropHuntMessaging.sendMessage(p,"Please empty your inventory, you dont have enough space for the setup items!");
							return true;
						}
						AM.addSettingUp(p, args[1]);
						PropHuntMessaging.sendMessage(p, "You are setting up the arena: " + args[1]);
						return true;
					}
					if(args[0].equalsIgnoreCase("host")){
						if(!sender.hasPermission("prophunt.hostcommand.host")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to host a PropHunt Game");
							return true;
						}
						if(ArenaManager.playableArenas.containsKey(args[1])){
							GM.hostGame(p,ArenaManager.playableArenas.get(args[1]));
						}else{
							PropHuntMessaging.sendAvailableArenas(p,ArenaManager.playableArenas);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("delete")){
						if(!sender.hasPermission("prophunt.admin.delete")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to delete a PropHunt Arena");
							return true;
						}
						if(AM.deleteArena(args[1])){
							PropHuntMessaging.sendMessage(p,"Arena deleted");
							return true;
						}else{
							PropHuntMessaging.sendMessage(p,"That arena does not exist");
							PropHuntMessaging.sendAvailableArenas(p, ArenaManager.playableArenas);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("kick")){
						if(!sender.hasPermission("prophunt.hostcommand.kick")){
							PropHuntMessaging.sendMessage(p, "You do not have the permission to kick a player from Prophunt");
							return true;
						}
						if(this.getServer().getPlayer(args[1])!=null){
							try {
								GM.kickPlayer(args[1]);
							} catch (IOException e) {
								e.printStackTrace();
							}
							PropHuntMessaging.sendMessage(p, "You have kicked :&f"+ args[1] +"&a from the game");
							return true;
						}
					}

					}
				}
			if(p.hasPermission("prophunt.hostcommand.host")){
				PropHuntMessaging.sendHostHelp(p);
				return true;
			}
			PropHuntMessaging.sendPlayerHelp(p);
			return true;

		}
		return false;
	}
	
	
	
	public int loadBlockDisguises() {
		int i = 0;
		if(getConfig().contains("block-disguises")){
			List<String> blockIds = getConfig().getStringList("block-disguises");
			for(String item : blockIds){
				if(item.startsWith("e:")){
					String[] split = item.split(":");
					if(split.length==2){
						DisguiseType dt = DisguiseType.fromString(split[1]);
						if(dt!=null){
							i++;
							DisguiseManager.blockDisguises.put(i, item);
							continue;
						}else{
							getLogger().log(Level.WARNING, "Error with custom Disguise id: " +item);
							continue;
						}
					}else{
						getLogger().log(Level.WARNING, "Error with custom Disguise id: " +item);
						continue;
					}
				}
				String disguise = parseDisguise(item);
				
				if(disguise!=null){
					i++;
					DisguiseManager.blockDisguises.put(i,disguise);
					continue;
				}else{
					getLogger().log(Level.WARNING,"PropHunt: Invalid disguise id : " + item);
				}

			}
		}
		return i;
	}
	
	private String parseDisguise(String item) {
		String[] split = item.split(":");
		if(split.length==2){
			if(isInt(split[0]) && isInt(split[1])){
				return item;
			}
		}
		if(isInt(item)){
			return item;
		}
		return null;
	}

	private boolean isInt(String item){
		int i = 0;
		try{
			i = Integer.parseInt(item);
		}catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
	
	
	private boolean isItem(int item){
		if(Material.getMaterial(item)!=null){
			if(Material.getMaterial(item).isBlock()){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	public void setupClasses() {
		if(getConfig().contains("SeekerClass")){
			ItemStack helmet = null;
			ItemStack chest = null;
			ItemStack legs = null;
			ItemStack boots = null;
			List<ItemStack> inv = new ArrayList<ItemStack>();
			List<PotionEffect> thislist = null;
			
			if(loadEffectsList("SeekerClass")!=null){
				thislist = loadEffectsList("SeekerClass");
				getLogger().log(Level.INFO, "loaded Seeker Effects List");
			}
			if(parseITEMStringToStack(getConfig().getString("SeekerClass.Helmet"))!=null){
				helmet = parseITEMStringToStack(getConfig().getString("SeekerClass.Helmet"));
				getLogger().log(Level.INFO, "loaded Seeker Helmet");
			}
			if(parseITEMStringToStack(getConfig().getString("SeekerClass.Chestplate"))!=null){
				chest = parseITEMStringToStack(getConfig().getString("SeekerClass.Chestplate"));
				getLogger().log(Level.INFO, "loaded Seeker Chestplate");
			}
			if(parseITEMStringToStack(getConfig().getString("SeekerClass.Leggings"))!=null){
				legs = parseITEMStringToStack(getConfig().getString("SeekerClass.Leggings"));
				getLogger().log(Level.INFO, "loaded Seeker Leggings");
			}
			if(parseITEMStringToStack(getConfig().getString("SeekerClass.Boots"))!=null){
				boots = parseITEMStringToStack(getConfig().getString("SeekerClass.Boots"));
				getLogger().log(Level.INFO, "loaded Seeker Boots");
			}
			int itemstacksize;
			String path = "SeekerClass.Inventory";
			String items = getConfig().getString(path);
			String[] SplitItems = items.split("\\,");
			itemstacksize = SplitItems.length;
			for(int i = 0; i< itemstacksize;){
				inv.add(parseITEMStringToStack(SplitItems[i]));
				i++;
			}

			if(helmet!=null && chest !=null && legs !=null && boots !=null && inv !=null && thislist != null){
				GameManager.seekerCLASS = new SeekerClass(helmet, chest, legs, boots, thislist, inv);
				getLogger().log(Level.INFO, "Loaded Seeker Class fully");

			}else{
				if(helmet==null){
					getLogger().log(Level.WARNING,"Incorrect Helmet");
				}
				if(chest==null){
					getLogger().log(Level.WARNING,"Incorrect chest");
				}
				if(boots==null){
					getLogger().log(Level.WARNING,"Incorrect boots");
				}
				if(legs==null){
					getLogger().log(Level.WARNING,"Incorrect legs");
				}
				if(inv==null){
					getLogger().log(Level.WARNING,"Incorrect inventory");
				}
				if(thislist==null){
					getLogger().log(Level.WARNING,"Incorrect potion effects");
				}
				getLogger().log(Level.WARNING, "Incorrect config for SeekerClass, re read instructions");
			}
			
		}
		if(getConfig().contains("HiderClass")){
			ItemStack helmet = null;
			ItemStack chest = null;
			ItemStack legs = null;
			ItemStack boots = null;
			List<ItemStack> inv = new ArrayList<ItemStack>();
			List<PotionEffect> thislist = null;
			
			if(loadEffectsList("HiderClass")!=null){
				thislist = loadEffectsList("HiderClass");
				getLogger().log(Level.INFO, "loaded hider Effects List");
			}
			if(parseITEMStringToStack(getConfig().getString("HiderClass.Helmet"))!=null){
				helmet = parseITEMStringToStack(getConfig().getString("HiderClass.Helmet"));
				getLogger().log(Level.INFO, "loaded hider Helmet");
			}
			if(parseITEMStringToStack(getConfig().getString("HiderClass.Chestplate"))!=null){
				chest = parseITEMStringToStack(getConfig().getString("HiderClass.Chestplate"));
				getLogger().log(Level.INFO, "loaded hider Chestplate");
			}
			if(parseITEMStringToStack(getConfig().getString("HiderClass.Leggings"))!=null){
				legs = parseITEMStringToStack(getConfig().getString("HiderClass.Leggings"));
				getLogger().log(Level.INFO, "loaded hider Leggings");
			}
			if(parseITEMStringToStack(getConfig().getString("HiderClass.Boots"))!=null){
				boots = parseITEMStringToStack(getConfig().getString("HiderClass.Boots"));
				getLogger().log(Level.INFO, "loaded hider Boots");
			}
			
			int itemstacksize;
			String path = "HiderClass.Inventory";
			String items = getConfig().getString(path);
			String[] SplitItems = items.split("\\,");
			itemstacksize = SplitItems.length;
			for(int i = 0; i< itemstacksize;){
				inv.add(parseITEMStringToStack(SplitItems[i]));
				i++;
			}

			if(helmet!=null && chest !=null && legs !=null && boots !=null && inv !=null && thislist != null){
				GameManager.hiderCLASS = new HiderClass(helmet, chest, legs, boots, thislist, inv);
				getLogger().log(Level.INFO, "Loaded hider Class fully");

			}else{
				if(helmet==null){
					getLogger().log(Level.WARNING,"Incorrect Helmet");
				}
				if(chest==null){
					getLogger().log(Level.WARNING,"Incorrect chest");
				}
				if(boots==null){
					getLogger().log(Level.WARNING,"Incorrect boots");
				}
				if(legs==null){
					getLogger().log(Level.WARNING,"Incorrect legs");
				}
				if(inv==null){
					getLogger().log(Level.WARNING,"Incorrect inventory");
				}
				if(thislist==null){
					getLogger().log(Level.WARNING,"Incorrect potion effects");
				}
				getLogger().log(Level.WARNING, "Incorrect config for HiderClass, re read instructions");
			}
			
		}
		
	}

	private ItemStack parseITEMStringToStack(String s){
		ItemStack stack = null;
		String[] enchantsplit = s.split(" ");
		if(enchantsplit.length > 1){
			//get enchants
			String item = enchantsplit[0];
			String enchants = enchantsplit[1];
			String[] totalenchants = enchants.split(";");
			int ENCHANTID = 0;
			int ENCHANTLEVEL = 0;
			Map<Enchantment,Integer> TOTEnchants = new HashMap<Enchantment,Integer>();
			int itemint = 0;
			try{
				itemint = Integer.parseInt(item);
			}catch(NumberFormatException nfe){
				return null;
			}
			int i = totalenchants.length;
			for(int z= 0; z < i;){
				String[] subsplit = totalenchants[z].split(":");
				try{
					ENCHANTID = Integer.parseInt(subsplit[0]);
					ENCHANTLEVEL = Integer.parseInt(subsplit[1]);
				}catch(NumberFormatException nfe){
					return null;
				}
				TOTEnchants.put(Enchantment.getById(ENCHANTID), ENCHANTLEVEL);
				 z++;
				
			}
			stack = new ItemStack(itemint,1);
			stack.addUnsafeEnchantments(TOTEnchants);
			return stack;
		}
		String[] damagesplit = s.split(":");
		if(damagesplit.length > 2){
			//get damage value
			String id = damagesplit[0];
			String damage = damagesplit[1];
			String amount = damagesplit[2];
			int ID = 0;
			short DAMAGE = 0;
			int AMOUNT = 0;
			try{
				ID= Integer.parseInt(id);
				DAMAGE=Short.parseShort(damage);
				AMOUNT=Integer.parseInt(amount);
			}catch(NumberFormatException NFE){
				return null;
			}
			stack = new ItemStack(Material.getMaterial(ID),AMOUNT,DAMAGE);
			return stack;
		}
		//simple stack
		String[] normalsplit = s.split(":");
		String id = normalsplit[0];
		String amount = normalsplit[1];
		int ID = 0;
		int AMOUNT = 0;
		try{
			ID = Integer.parseInt(id);
			AMOUNT = Integer.parseInt(amount);
		}catch(NumberFormatException NFE){
			return null;
		}

		stack = new ItemStack(Material.getMaterial(ID),AMOUNT);
		return stack;
	}
	
	private List<PotionEffect> loadEffectsList(String path) {
		List<PotionEffect> plist = new ArrayList<PotionEffect>();
		if (getConfig().contains(path + ".Effects")) {
			String effects = getConfig().getString(path + ".Effects");
			String[] effectsplit = effects.split("\\,");
			for (int i = 0; i < effectsplit.length;) {
				String[] singlesplit = effectsplit[i].split(":");
				String id = singlesplit[0];
				String duration = singlesplit[1];
				String potency = singlesplit[2];
				int ID = 0;
				int DURATION = 0;
				int POTENCY = 0;
				try {
					ID = Integer.parseInt(id);
					DURATION = Integer.parseInt(duration);
					POTENCY = Integer.parseInt(potency);

				} catch (NumberFormatException nfe) {
					System.out.print("Wrong effect format");
					return null;
				}
				PotionEffect pe = new PotionEffect(
						PotionEffectType.getById(ID), DURATION, POTENCY);
				plist.add(pe);
				i++;
			}
			return plist;
		}
		return null;
	}

	public Map<Integer, String> getCustomDisguises(String arenaName) {
		int i = 0;
		Map<Integer,String> disguiseMap = new HashMap<Integer,String>();
		if(getConfig().contains("CustomArenaConfigs."+arenaName+".block-disguises")){
			List<String> blockIds = getConfig().getStringList("CustomArenaConfigs."+arenaName+".block-disguises");
			for(String item : blockIds){
				if(item.startsWith("e:")){
					String[] split = item.split(":");
					if(split.length==2){
						DisguiseType dt = DisguiseType.fromString(split[1]);
						if(dt!=null){
							i++;
							disguiseMap.put(i, item);
							continue;
						}else{
							getLogger().log(Level.WARNING, "Error with custom Disguise id: " +item);
							continue;
						}
					}else{
						getLogger().log(Level.WARNING, "Error with custom Disguise id: " +item);
						continue;
					}
				}
				String disguise = parseDisguise(item);
				
				if(disguise!=null){
					i++;
					disguiseMap.put(i,disguise);
					continue;
				}else{
					getLogger().log(Level.WARNING,"PropHunt: Invalid disguise id : " + item);
				}
			}
		}
		return disguiseMap;
	}

	public HiderClass getCustomHiderClass(String arenaName) {
		String path = "CustomArenaConfigs."+arenaName+".";
		HiderClass hc = null;
		if(getConfig().contains(path+"HiderClass")){
			ItemStack helmet = null;
			ItemStack chest = null;
			ItemStack legs = null;
			ItemStack boots = null;
			List<ItemStack> inv = new ArrayList<ItemStack>();
			List<PotionEffect> thislist = null;
			
			if(loadEffectsList(path+"HiderClass")!=null){
				thislist = loadEffectsList(path+"HiderClass");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"HiderClass.Helmet"))!=null){
				helmet = parseITEMStringToStack(getConfig().getString(path+"HiderClass.Helmet"));
				getLogger().log(Level.INFO, "loaded Hider Helmet");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"HiderClass.Chestplate"))!=null){
				chest = parseITEMStringToStack(getConfig().getString(path+"HiderClass.Chestplate"));
				getLogger().log(Level.INFO, "loaded Hider Chestplate");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"HiderClass.Leggings"))!=null){
				legs = parseITEMStringToStack(getConfig().getString(path+"HiderClass.Leggings"));
				getLogger().log(Level.INFO, "loaded Hider Leggings");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"HiderClass.Boots"))!=null){
				boots = parseITEMStringToStack(getConfig().getString(path+"HiderClass.Boots"));
				getLogger().log(Level.INFO, "loaded Hider Boots");
			}
			int itemstacksize;
			String path2 = path+"HiderClass.Inventory";
			String items = getConfig().getString(path2);
			String[] SplitItems = items.split("\\,");
			itemstacksize = SplitItems.length;
			for(int i = 0; i< itemstacksize;){
				inv.add(parseITEMStringToStack(SplitItems[i]));
				i++;
			}

			if(helmet!=null && chest !=null && legs !=null && boots !=null && inv !=null && thislist != null){
				hc = new HiderClass(helmet, chest, legs, boots, thislist, inv);
				getLogger().log(Level.INFO, "Loaded Hider Class fully");

			}else{
				if(helmet==null){
					getLogger().log(Level.WARNING,"Incorrect Helmet");
				}
				if(chest==null){
					getLogger().log(Level.WARNING,"Incorrect chest");
				}
				if(boots==null){
					getLogger().log(Level.WARNING,"Incorrect boots");
				}
				if(legs==null){
					getLogger().log(Level.WARNING,"Incorrect legs");
				}
				if(inv==null){
					getLogger().log(Level.WARNING,"Incorrect inventory");
				}
				if(thislist==null){
					getLogger().log(Level.WARNING,"Incorrect potion effects");
				}
				if(hc==null){
					getLogger().log(Level.WARNING, "Incorrect config for HiderClass, re read instructions");
					return null;
					
				}
			}
			
		}
		return hc;
	}

	public SeekerClass getCustomSeekerClass(String arenaName) {
		String path = "CustomArenaConfigs."+arenaName+".";
		SeekerClass sc = null;
		if(getConfig().contains(path+"SeekerClass")){
			ItemStack helmet = null;
			ItemStack chest = null;
			ItemStack legs = null;
			ItemStack boots = null;
			List<ItemStack> inv = new ArrayList<ItemStack>();
			List<PotionEffect> thislist = null;
			
			if(loadEffectsList(path+"SeekerClass")!=null){
				thislist = loadEffectsList(path+"SeekerClass");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Helmet"))!=null){
				helmet = parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Helmet"));
				getLogger().log(Level.INFO, "loaded Seeker Helmet");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Chestplate"))!=null){
				chest = parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Chestplate"));
				getLogger().log(Level.INFO, "loaded Seeker Chestplate");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Leggings"))!=null){
				legs = parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Leggings"));
				getLogger().log(Level.INFO, "loaded Seeker Leggings");
			}
			if(parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Boots"))!=null){
				boots = parseITEMStringToStack(getConfig().getString(path+"SeekerClass.Boots"));
				getLogger().log(Level.INFO, "loaded Seeker Boots");
			}
			int itemstacksize;
			String path2 = path+"SeekerClass.Inventory";
			String items = getConfig().getString(path2);
			String[] SplitItems = items.split("\\,");
			itemstacksize = SplitItems.length;
			for(int i = 0; i< itemstacksize;){
				inv.add(parseITEMStringToStack(SplitItems[i]));
				i++;
			}

			if(helmet!=null && chest !=null && legs !=null && boots !=null && inv !=null && thislist != null){
				sc = new SeekerClass(helmet, chest, legs, boots, thislist, inv);
				getLogger().log(Level.INFO, "Loaded Seeker Class fully");

			}else{
				if(helmet==null){
					getLogger().log(Level.WARNING,"Incorrect Helmet");
				}
				if(chest==null){
					getLogger().log(Level.WARNING,"Incorrect chest");
				}
				if(boots==null){
					getLogger().log(Level.WARNING,"Incorrect boots");
				}
				if(legs==null){
					getLogger().log(Level.WARNING,"Incorrect legs");
				}
				if(inv==null){
					getLogger().log(Level.WARNING,"Incorrect inventory");
				}
				if(thislist==null){
					getLogger().log(Level.WARNING,"Incorrect potion effects");
				}
				if(sc==null){
					getLogger().log(Level.WARNING, "Incorrect config for SeekerClass, re read instructions");
					return null;
					
				}
			}
			
		}
		return sc;
	}

	@SuppressWarnings("deprecation")
	public void hidePlayer(final Player owner, ItemStack[] armour) {
		final ItemStack[] armourclone = armour.clone();
		owner.getInventory().setArmorContents(null);
		owner.updateInventory();
		owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,4,1));
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

			@Override
			public void run() {
				PropHunt.dc.undisguisePlayer(owner);	
			}}, 1L);
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

			@Override
			public void run() {
				for(Player viewer : owner.getServer().getOnlinePlayers()){
					if(viewer!=owner){
						viewer.hidePlayer(owner);
					}
				}		
				owner.getInventory().setArmorContents(armourclone);
			}}, 2L);
	}
	
	public void showPlayer(final Player owner) {
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

			@Override
			public void run() {
				for(Player viewer : owner.getServer().getOnlinePlayers()){
					if(viewer!=owner){
						viewer.showPlayer(owner);

					}
				}				
			}}, 1L);
	}
	
	
}
