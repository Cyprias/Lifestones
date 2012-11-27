package com.cyprias.Lifestones;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YML {
	static HashMap<String, File> Files = new HashMap<String, File>();
	static HashMap<String, FileConfiguration> FileConfigs = new HashMap<String, FileConfiguration>();
	private JavaPlugin plugin;
	static File pluginDur;
	public YML(JavaPlugin plugin2) {
		this.plugin = plugin2;
		pluginDur = plugin.getDataFolder();
		
	}
	public static boolean reloadYMLConfig(String fileName, InputStream fileStream) {
		if (!FileConfigs.containsKey(fileName)){
			FileConfigs.put(fileName, new YamlConfiguration());
		}
		
		try {
			Files.put(fileName, new File(pluginDur, fileName));

			
			
			if (!Files.get(fileName).exists()) {
				InputStream r = fileStream;
				if (r == null)
					return false;
				
				Files.get(fileName).getParentFile().mkdirs();
				copy(fileStream, Files.get(fileName));
			}

			FileConfigs.get(fileName).load(Files.get(fileName));
		} catch (Exception e) {e.printStackTrace();
		}
		return true;
	}
	
	public static void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static FileConfiguration getYMLConfig(String fileName, InputStream fileStream, Boolean loadNewKeys) {
		if (!Files.containsKey(fileName)) {
			if (reloadYMLConfig(fileName, fileStream) == false)
				return null;
		}
		
		if (loadNewKeys == true)
			copyNewKeysToDisk(fileName, fileStream);
		
		return FileConfigs.get(fileName);
	}
	public static FileConfiguration getYMLConfig(String fileName, InputStream fileStream) {
		return getYMLConfig(fileName, fileStream, false);
	}
	
	public void saveYMLFile(String fileName) {
		if (!FileConfigs.containsKey(fileName)) {
			FileConfigs.put(fileName, new YamlConfiguration());
		}

		try {
			FileConfigs.get(fileName).save(Files.get(fileName));
		} catch (IOException e) {e.printStackTrace();
		}
	}
	
	private Logger log = Logger.getLogger("Minecraft");
	public static void copyNewKeysToDisk(String fileName, InputStream fileStream){

		if (fileStream == null)//File isn't in our jar, exit.
			return;
		
		//Load the stream to a ymlconfig object.
		YamlConfiguration locales = new YamlConfiguration();
		try {
			locales.load(fileStream);
		} catch (IOException e1) {e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {e1.printStackTrace();
		}
		
		//Load the file from disk.
		FileConfiguration targetConfig = FileConfigs.get(fileName);
		Boolean save = false;
		String value;
		for (String key : locales.getKeys(false)) {
			value = locales.getString(key);

			if (targetConfig.getString(key) == null){
				targetConfig.set(key, value);
				save = true; //Only save if we make changes.
			}
			
		}

		if (save == true){
			//Save changes to disk.
			try {
				targetConfig.save(Files.get(fileName));
			} catch (IOException e) {e.printStackTrace();
			}
		}
	}
	
}
