package me.bl0m1.warning;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class warningmain extends JavaPlugin implements Listener {
	
	String targetPlayer = "";
	String server = "Server";
	int arg = 0;
	
    @Override
	public void onEnable()
    {
    	saveDefaultConfig();
    	System.out.println("[Warning] enabled");
    	PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}
    
    @Override
	public void onDisable()
    {
    	System.out.println("[Warning] Disabled!");
    }
    
    @Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("warn"))
		{
			if(s instanceof Player)
			{
				Player player = (Player) s;
				if(args.length == 0)
				{
					player.sendMessage("§cUnknown command, use /warn help for help or /warn check to check why you were warned");
				}
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("check"))
					{
						check(player, args);
					}
					else if(args[0].equalsIgnoreCase("help"))
					{
						help(player);
					}
					else
					{
						player.sendMessage("§cUnknown command, use /warn help for help or /warn check to check why you were warned");
					}
				}
				if(args.length >= 2)
				{
					if(args[0].equalsIgnoreCase("check"))
					{
						checkOther(player, args);
					}
					else if(args[0].equalsIgnoreCase("remove"))
					{
						remove(player, args);
					}
					else
					{
						warn(player, args);
					}
				}
			}
			else
			{
				if(args[0].equalsIgnoreCase("server"))
				{
					serverWarn(args);
				}
				else
				{
				System.out.println("unknown command");
				}
			}
		}
		return true;
	} 
	
	public void check(Player sender, String[] args) 
	{
    	if(sender.hasPermission("warning.check"))
    	{
    		sender.sendMessage("§9You §chave §9"+getWarnings(sender.getName())+" §cwarnings.");
    		getReasons(sender, sender.getName());
    	}
		else
		{
			sender.sendMessage("§cYou dont have permission to do this.");
		}
	}
	
	public void help(Player sender) 
	{
    	if(sender.hasPermission("warning.help"))
    	{
			ShowHelp(sender);
    	}
		else
		{
			sender.sendMessage("§cYou dont have permission to do this.");
		}
	}   
		    	
	public void checkOther(Player sender, String[] args) 
	{
		targetPlayer = NameCheck(1,args);
		if (sender.hasPermission("warning.check.others"))
		{
			sender.sendMessage("§9"+targetPlayer+" §chas §9"+getWarnings(targetPlayer)+" §cwarnings.");
			getReasons(sender, targetPlayer);
		} 		
		else
		{
			sender.sendMessage("§cYou dont have permission to do this.");
		}
	
	}
	
	public void remove(Player sender, String[] args) 
	{
		targetPlayer = NameCheck(1,args);
		if (sender.hasPermission("warning.remove"))
		{
			if(args.length == 2)
			{
				for (int i = 0 ; i < getWarnings(targetPlayer); i++)
				{
					remReason(targetPlayer, ""+i);
				}
				setWarning(targetPlayer, 0);
				sender.sendMessage("§6You §chave removed §9all §cwarnings for player §9" + targetPlayer + "§c.");
			}
			if(args.length == 3)
			{
				if(Integer.parseInt(args[2]) == getWarnings(targetPlayer))
				{
					setWarning(targetPlayer, getWarnings(targetPlayer)-1);
					remReason(targetPlayer, args[2]);
				}
				else
				{
					int OrigWarnings = getWarnings(targetPlayer);
					String[] reasontemp = {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",};
					int i = Integer.parseInt(args[2]) ;
					for (; i < OrigWarnings; i++)
					{
						reasontemp[i] = getConfig().getString(targetPlayer + ".reason" + (i + 1));
						setWarning(targetPlayer, (i));
						addReason(targetPlayer, reasontemp[i]) ;
					}
					remReason(targetPlayer, ""+ OrigWarnings);
				}
				sender.sendMessage("§6You§c have removed warning §9#" + args[2] + "§c For player §9" + targetPlayer + "§c.");
			}
		}	
		else
		{
			sender.sendMessage("§cYou dont have permission to do this.");
		}
	}
	
	public void warn(Player sender, String[] args) 
		{
		if(sender.hasPermission("warning.warn"))
			{
				targetPlayer = NameCheck(0,args);
				if(sender.getServer().getPlayer(targetPlayer) == null) 
				{ 					 
					String message = "";
					for (int i = 1; i < args.length; i++)
					{
						message = message + args[i] + ' ';
					}
					getConfig().set(targetPlayer + ".online", 1);
					setWarning(targetPlayer, getWarnings(targetPlayer)+1);
					addReason(targetPlayer, message + "§cby §6" + sender.getName() +"§2 "+ getTime());
					saveConfig();
					sender.sendMessage("§6You §chave warned §9"+targetPlayer+"§c for §9" + message);
				}
				else 
				{
					Player ta = Bukkit.getPlayer(targetPlayer);
					String message = "";
					for (int i = 1; i < args.length; i++)
					{
						message = message + args[i] + ' ';
					}
				setWarning(targetPlayer, getWarnings(targetPlayer)+1);
				addReason(targetPlayer, message + "§cby §6" + sender.getName() +"§2 "+ getTime());
				saveConfig();
				ta.sendMessage("§9You §chave been warned by §6"+sender.getName()+" §cfor §9" + message);
				sender.sendMessage("§6You §chave warned §9"+targetPlayer+"§c for §9" + message);
				}
			} 	
		else
		{
			sender.sendMessage("§cYou dont have permission to do this.");
		}
	}
	
	public void serverWarn(String[] args) 
	{
		targetPlayer = NameCheck(1,args);
		if(this.getServer().getPlayer(targetPlayer) == null) 
		{ 					 
			String message = "";
			for (int i = 2; i < args.length; i++)
			{
				message = message + args[i] + ' ';
			}
			getConfig().set(targetPlayer + ".online", 1);
			setWarning(targetPlayer, getWarnings(targetPlayer)+1);
			addReason(targetPlayer, message + "§cby §6" + server +"§2 "+ getTime());
			saveConfig();
		}
		else 
		{
			Player ta = Bukkit.getPlayer(targetPlayer);
			String message = "";
			for (int i = 2; i < args.length; i++)
			{
				message = message + args[i] + ' ';
			}
		setWarning(targetPlayer, getWarnings(targetPlayer)+1);
		addReason(targetPlayer, message + "§cby §6" + server +"§2 "+ getTime());
		saveConfig();
		ta.sendMessage("§9You §chave been warned by §6"+server+" §cfor §9" + message);
		}
	}
		
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) 
    {
    	Player JoinPlayer = event.getPlayer();
    	String p = JoinPlayer.getName();
    	if (getConfig().getInt(p + ".online") == 0)
    	{
    	}
    	else
    	{
    		JoinPlayer.sendMessage("§9You §chave gotten §9"+getWarnings(p)+" §cwarnings since last time you played!");
    		JoinPlayer.sendMessage("§9You §c can check your warnings at any time with /warn check!");
    		getReasons(JoinPlayer, p);
    		getConfig().set(p + ".online", 0);
    		saveConfig();
    	}
    }

    public String NameCheck(int arg, String[] args){
    	String target = "";
		Player online = this.getServer().getPlayer(args[arg]);
		if (online == null) 
		{
			target = args[arg];
		} 
		else 
		{
			target = online.getName();
		}
    	return target;
    }

	public void ShowHelp(Player sender)
	{
		if(sender.hasPermission("warning.help")){
			sender.sendMessage("§9==========[ §6WarningHelp §9]==========");
			sender.sendMessage("§b/warn help §8: §3Shows this.");
			sender.sendMessage("§b/warn <player> <reason> §8: §3Warn a player.");
			sender.sendMessage("§b/warn check <player> §8: §3Check a player's warnings.");
			sender.sendMessage("§b/warn remove <player> [number] §8: §3Remove a player's warning.");
			sender.sendMessage("§9==========[ §6WarningHelp §9]==========");
		} 
			else sender.sendMessage("§cYou don't have permission to do this.");
	}
    
	public void setWarning(String p, int warnings)
	{
		getConfig().set(p + ".warnings", warnings);
		saveConfig();
	}
	
	public void addReason(String p, String reason)
	{
		getConfig().set(p + ".reason"+ getWarnings(p), reason);
	}
	
	public void remReason(String p, String number)
	{
		getConfig().set(p + ".reason"+ number, "");
	}
	
	public void getReasons(Player sender, String targetPlayer)
	{
		String reason = "";
		for (int i = 1; i < getWarnings(targetPlayer)+1; i++)
		{
			reason = getConfig().getString(targetPlayer + ".reason"+i);
			sender.sendMessage("§cReason " + i +":§9 "+ reason);
		}
	}
	
	public int getWarnings(String p)
	{
		if(getConfig().get(p + ".warnings") != null)
		{
			return getConfig().getInt(p + ".warnings");
		} 
		else 
		{
			return 0;
		}
	}
	
	public String getTime()
	{
		Calendar currentDate = Calendar.getInstance();
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
	    String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}
}