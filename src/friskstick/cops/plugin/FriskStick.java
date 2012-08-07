package friskstick.cops.plugin;

import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FriskStick extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	int index = 0;
	
	public void onEnable(){
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " v" + pdffile.getVersion() + " has been enabled!");
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	public void onDisable(){
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " has been disabled.");
	}
	@EventHandler
	public void Stick(PlayerInteractEntityEvent event){
		if(event.getRightClicked() instanceof Player && event.getPlayer().getItemInHand().getType() == Material.STICK){
			Player frisked = (Player)event.getRightClicked();
			Player cop = event.getPlayer();
			if(cop.hasPermission("friskstick.use")){
				PlayerInventory inventory = frisked.getInventory();
				boolean found = false;
				for(String drug: getConfig().getStringList("drug-ids")){
					if(drug.contains(":")){
						String firsthalf = drug.split(":")[0];
						String lasthalf = drug.split(":")[1];
						for(int i = 1; i <= getConfig().getInt("amount-to-search-for"); i++){
							if(inventory.contains(new ItemStack(Integer.parseInt(firsthalf), i, Short.parseShort(lasthalf)))){
								cop.getInventory().addItem(new ItemStack(Integer.parseInt(firsthalf), getConfig().getInt("amount-confiscated"), Short.parseShort(lasthalf)));
								inventory.removeItem(new ItemStack(Integer.parseInt(firsthalf), 2305, Short.parseShort(lasthalf)));
								cop.sendMessage(getConfig().getString("cop-found-msg").replaceAll("&", "§").replaceAll("%itemname%", getConfig().getStringList("drug-names").toArray()[index].toString()).replaceAll("%player%", frisked.getName()));
								frisked.sendMessage(getConfig().getString("player-found-msg").replaceAll("&", "§").replaceAll("%cop%", cop.getName()).replaceAll("%itemname%", getConfig().getStringList("drug-names").toArray()[index].toString()));
								found = true;
							}
						}
					}else{
						if(inventory.contains(Integer.parseInt(drug))){
							int drugid = Integer.parseInt(drug);
							cop.getInventory().addItem(new ItemStack(drugid, getConfig().getInt("amount-confiscated")));
							inventory.removeItem(new ItemStack(drugid, 2305));
							cop.sendMessage(getConfig().getString("cop-found-msg").replaceAll("&", "§").replaceAll("%itemname%", getConfig().getStringList("drug-names").toArray()[index].toString()).replaceAll("%player%", frisked.getName()));
							frisked.sendMessage(getConfig().getString("player-found-msg").replaceAll("&", "§").replaceAll("%cop%", cop.getName()).replaceAll("%itemname%", getConfig().getStringList("drug-names").toArray()[index].toString()));
							found = true;
						}
					}
					index++;
				}
				index = 0;
				if(!found){
					cop.sendMessage(getConfig().getString("cop-not-found-msg").replaceAll("&", "§").replaceAll("%player%", frisked.getName()));
					frisked.sendMessage(getConfig().getString("player-not-found-msg").replaceAll("&", "§").replaceAll("%cop%", cop.getName()));
					cop.setHealth(cop.getHealth() - 2);
				}
			}
		}
	}
}
