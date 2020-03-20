// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.managers;

import java.util.Iterator;
import lucemans.protect.obj.LandClaim;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import lucemans.protect.obj.SLandClaim;
import java.util.ArrayList;
import java.io.FileOutputStream;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import java.io.File;

public class FileManager
{
    public static File path;
    public static File claimsFile;
    
    public static void saveFile() {
        if (!FileManager.path.exists()) {
            FileManager.path.mkdir();
        }
        if (!FileManager.claimsFile.exists()) {
            try {
                FileManager.claimsFile.createNewFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        final ArrayList<SLandClaim> data = LandManager.serialize();
        Bukkit.getLogger().info("R " + data.size());
        final String s = new Gson().toJson(data);
        try {
            final FileOutputStream outputStream = new FileOutputStream(FileManager.claimsFile);
            outputStream.write(s.getBytes());
            outputStream.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public static void loadFile() {
        try {
            final FileInputStream fis = new FileInputStream(FileManager.claimsFile);
            final InputStreamReader isr = new InputStreamReader(fis);
            final BufferedReader bufferedReader = new BufferedReader(isr);
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            final String json = sb.toString();
            final Gson gson = new Gson();
            final List<SLandClaim> v = gson.fromJson(json, new TypeToken<List<SLandClaim>>() {}.getType());
            Bukkit.getLogger().info("Loaded " + v.size());
            for (final SLandClaim sl : v) {
                final LandClaim lc = LandClaim.deserialize(sl);
                LandManager.claims.add(lc);
            }
        }
        catch (Exception e) {
            Bukkit.getLogger().info("Yh sorry there was an error loading stuff");
            Bukkit.getLogger().info(e.toString());
        }
    }
    
    static {
        FileManager.path = new File("plugins//NProtect");
        FileManager.claimsFile = new File("plugins//NProtect//claims.json");
    }
}
