package com.cyprias.Lifestones;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YML {
	private File file = null;;
	public FileConfiguration config = new YamlConfiguration();
	private InputStream fileStream;

	public YML(InputStream fileStream) {
		//load yml from resources. 
		this.fileStream = fileStream;
		load();
	}
	
	public YML(File pluginDur, String fileName) {
		//Load yml from directory.
		this.file = new File(pluginDur, fileName);
		load();
	}
	
	public YML(InputStream fileStream, File pluginDur, String fileName) {
		//Copy yml resource to directory then load it.
		this.fileStream = fileStream;
		
		this.file = new File(pluginDur, fileName);
		if (!this.file.exists())
			this.file = toFile(fileStream, pluginDur, fileName);
		
		load();
	}

	//Load yml from disk.
	public void load(){
		if (this.file != null){
			try {
				this.config.load(this.file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			config = getResourceYMLFile(this.fileStream);
		}
	}
	
	//Save yml to disk.
	public void save(){
		try {
			this.config.save(this.file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getString(String key){
		return config.getString(key);
	}
	
	//Write a stream to file on disk, return the file object.  
	private static File toFile(InputStream in, File pluginDur, String fileName) {
		File file = new File(pluginDur, fileName);
		file.getParentFile().mkdirs();
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
		return file;
	}
	
	//Get the yml file inside the jar. 
	private static FileConfiguration getResourceYMLFile(InputStream fileStream){
		YamlConfiguration file = new YamlConfiguration();
		try {
			file.load(fileStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return file;
	}
}
