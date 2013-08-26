package me.tomski.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import me.tomski.prophunt.PropHunt;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageManager {
	
	static FileConfiguration currentLanguageFile;
	private PropHunt plugin;
	private File customConfigFile;
	private String fileName;
	
	private FileConfiguration languageConfig;
	private File customLanguageFile;
	
	public LanguageManager(PropHunt ph) throws IOException{
		this.plugin = ph;
		fileName = plugin.getConfig().getString("UseLanguageFile");
		getLanguageFile().options().copyDefaults(true);
		saveLanguageFile();
		initfile();
		copyDefaultsToCurrentfile();
	}
	
	
	
	
	private void copyDefaultsToCurrentfile() throws IOException {
		for(String key : languageConfig.getKeys(false)){
			if(currentLanguageFile.contains(key)){
				continue;
			}else{
				currentLanguageFile.set(key, languageConfig.getString(key));
			}
		}
		currentLanguageFile.save(customConfigFile);
	}




	public void initfile() {
		getLanguageFileInUse(fileName);
	}


	public static String regex(String msg, String regex,String replacement){
		return msg.replaceAll(regex, replacement);
	}

	private void getLanguageFileInUse(String name){
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), name);
		}
		currentLanguageFile = YamlConfiguration.loadConfiguration(customConfigFile);
	}
	
	public void reloadLanguage() {
		if (customLanguageFile == null) {
			customLanguageFile = new File(plugin.getDataFolder(), "Language.yml");
		}
		languageConfig = YamlConfiguration.loadConfiguration(customLanguageFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource("Language.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			languageConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getLanguageFile() {
		if (languageConfig == null) {
			this.reloadLanguage();
		}
		return languageConfig;
	}

	public void saveLanguageFile() {
		if (languageConfig == null || customLanguageFile == null) {
			return;
		}
		try {
			getLanguageFile().save(customLanguageFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"Could not save config to " + customLanguageFile, ex);
		}
	}


	public static String getMessageFromFile(String string) {
		return currentLanguageFile.getString(string);
	}
}
