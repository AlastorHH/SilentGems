package net.silentchaos512.gems.util;

import net.silentchaos512.gems.SilentGems;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PatronColors {

  public static PatronColors instance = new PatronColors();

  Map<String, String> map = new HashMap<>();

  public PatronColors() {

    InputStream input = getClass().getResourceAsStream("/assets/silentgems/patrons");
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    String line = null;
    try {
      while((line = reader.readLine()) != null) {
        String[] array = line.split(",");
        if (array.length > 1) {
          map.put(array[0], array[1].replaceAll("&", "\u00a7"));
//          Skulls.putPlayer(array[0]);
        } else {
          SilentGems.log.warning("Malformed entry in Patrons file: " + line);
        }
      }
    } catch (IOException e) {
      SilentGems.log.warning("IOException when reading Patrons file");
      e.printStackTrace();
    }
  }

  public @Nonnull String getColor(String playerName) {

    String val = map.get(playerName);
    return val == null ? "" : val;
  }
}
