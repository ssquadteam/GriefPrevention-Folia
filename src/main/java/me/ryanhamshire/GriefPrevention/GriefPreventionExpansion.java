package me.ryanhamshire.GriefPrevention;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;

/**
 * PAPI placeholder expansion for GriefPrevention.
 */
public class GriefPreventionExpansion extends PlaceholderExpansion implements Configurable {

    private final GriefPrevention plugin;

    /**
     * Main constructor for the expansion.
     *
     * @param plugin {@link GriefPrevention} instance.
     */
    public GriefPreventionExpansion(@NotNull GriefPrevention plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /**
     * Since this expansion requires api access to the plugin "SomePlugin" we must
     * check if "SomePlugin" is on the server in this method
     */
    @Override
    public boolean canRegister() {
        return plugin.isEnabled();
    }

    /**
     * The name of the person who created this expansion should go here
     */
    @Override
    public @NotNull String getAuthor() {
        return "pixar02";
    }

    /**
     * The placeholder identifier should go here This is what tells PlaceholderAPI
     * to call our onPlaceholderRequest method to obtain a value if a placeholder
     * starts with our identifier. This must be unique and can not contain % or _
     */
    @Override
    public @NotNull String getIdentifier() {
        return "griefprevention";
    }

    /**
     * if an expansion requires another plugin as a dependency, the proper name of
     * the dependency should go here. Set this to null if your placeholders do not
     * require another plugin be installed on the server for them to work. This is
     * extremely important to set if you do have a dependency because if your
     * dependency is not loaded when this hook is registered, it will be added to a
     * cache to be registered when plugin: "getPlugin()" is enabled on the server.
     */
    @Override
    public @NotNull String getRequiredPlugin() {
        return "GriefPrevention";
    }

    /**
     * This is the version of this expansion
     */
    @Override
    public @NotNull String getVersion() {
        return "1.6.0";
    }

    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<String, Object>();
        defaults.put("formatting.thousands", "k");
        defaults.put("formatting.millions", "M");
        defaults.put("formatting.billions", "B");
        defaults.put("formatting.trillions", "T");
        defaults.put("formatting.quadrillions", "Q");

        defaults.put("color.enemy", "&4");
        defaults.put("color.trusted", "&a");
        defaults.put("color.neutral", "&7");

        defaults.put("translate.unclaimed", "Unclaimed");
        defaults.put("translate.not-owner", "You don't own this claim!");
        return defaults;
    }

    @Override
    public List<String> getPlaceholders() {
        List<String> placeholders = new ArrayList<>();
        placeholders.add("%griefprevention_totalclaims%");
        placeholders.add("%griefprevention_totalclaims_formatted%");
        placeholders.add("%griefprevention_usedclaims%");
        placeholders.add("%griefprevention_usedclaims_formatted%");
        placeholders.add("%griefprevention_claims%");
        placeholders.add("%griefprevention_claims_formatted%");
        placeholders.add("%griefprevention_bonusclaims%");
        placeholders.add("%griefprevention_bonusclaims_formatted%");
        placeholders.add("%griefprevention_accruedclaims%");
        placeholders.add("%griefprevention_accruedclaims_formatted%");
        placeholders.add("%griefprevention_accruedclaims_limit%");
        placeholders.add("%griefprevention_claimedblocks_total%");
        placeholders.add("%griefprevention_claimedblocks_current%");
        placeholders.add("%griefprevention_remainingclaims%");
        placeholders.add("%griefprevention_remainingclaims_formatted%");
        placeholders.add("%griefprevention_currentclaim_ownername%");
        placeholders.add("%griefprevention_currentclaim_ownername_color%");
        return placeholders;
    }

    /**
     * This is the method called when a placeholder with our identifier is found and
     * needs a value We specify the value identifier in this method
     */
    @Override
    public String onRequest(OfflinePlayer p, @NotNull String identifier) {
        if (!p.isOnline()) {
            return "";
        }

        Player player = p.getPlayer();
        if (player == null) {
            return "";
        }

        DataStore DataS = plugin.dataStore;
        PlayerData pd = DataS.getPlayerData(player.getUniqueId());

        /*
         %griefprevention_totalclaims%
         %griefprevention_totalclaims_formatted%
        */

        if (identifier.startsWith("totalclaims")) {
            int totalClaims = pd.getBonusClaimBlocks() + pd.getAccruedClaimBlocks();
            return identifier.endsWith("formatted") ? fixMoney(totalClaims) : String.valueOf(totalClaims);
        }

        /*
         %griefprevention_usedclaims%
         %griefprevention_usedclaims_formatted%
        */

        if (identifier.startsWith("usedclaims")) {
            int totalClaims = pd.getBonusClaimBlocks() + pd.getAccruedClaimBlocks();
            int remainingClaims = pd.getRemainingClaimBlocks();

            int usedClaims = totalClaims - remainingClaims;
            return identifier.endsWith("formatted") ? fixMoney(usedClaims) : String.valueOf(usedClaims);
        }

        /*
         %griefprevention_claims%
         %griefprevention_claims_formatted%
        */

        if (identifier.equals("claims")) {
            return String.valueOf(pd.getClaims().size());
        } else if (identifier.equals("claims_formatted")) {
            return fixMoney(pd.getClaims().size());
        }

        // %griefprevention_bonusclaims%
        // %griefprevention_bonusclaims_formatted%
        if (identifier.equals("bonusclaims")) {
            return String.valueOf(pd.getBonusClaimBlocks());
        } else if (identifier.equals("bonusclaims_formatted")) {
            return fixMoney(pd.getBonusClaimBlocks());
        }

        /*
         %griefprevention_accruedclaims%
         %griefprevention_accruedclaims_formatted%
        */
        if (identifier.equals("accruedclaims")) {
            return String.valueOf(pd.getAccruedClaimBlocks());
        } else if (identifier.equals("accruedclaims_formatted")) {
            return fixMoney(pd.getAccruedClaimBlocks());
        }

        // %griefprevention_accruedclaims_limit%
        if (identifier.equals("accruedclaims_limit")) {
            return String.valueOf(pd.getAccruedClaimBlocksLimit());
        }

        //%griefprevention_claimedblocks_total%
        if (identifier.equals("claimedblocks_total")) {
            int blocks = 0;
            for (Claim c : pd.getClaims()) {
                blocks += c.getArea();
            }
            return String.valueOf(blocks);
        }

        //%griefprevention_claimedblocks_current%
        if (identifier.equals("claimedblocks_current")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("translate.unclaimed", "Unclaimed!"));
            } else if (Objects.equals(claim.getOwnerName(), p.getName())) {
                return String.valueOf(claim.getArea());
            }
            return ChatColor.translateAlternateColorCodes('&',
                    getString("translate.not-owner", "You don't own this claim!"));
        }

        /*
         %griefprevention_remainingclaims%
         %griefprevention_remainingclaims_formatted%
        */
        if (identifier.equals("remainingclaims")) {
            return String.valueOf(pd.getRemainingClaimBlocks());
        } else if (identifier.equals("remainingclaims_formatted")) {
            return fixMoney(pd.getRemainingClaimBlocks());
        }

        // %griefprevention_currentclaim_ownername_color%
        // %griefprevention_currentclaim_ownername%
        if (identifier.equals("currentclaim_ownername")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("translate.unclaimed", "Unclaimed!"));
            } else {
                return String.valueOf(ChatColor.translateAlternateColorCodes('&', claim.getOwnerName()));
            }
        } else if (identifier.equals("currentclaim_ownername_color")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("color.neutral", "")
                                + getString("translate.unclaimed", "Unclaimed!"));
            } else {
                if (claim.allowAccess(player) == null){
                    //Trusted
                    return ChatColor.translateAlternateColorCodes('&',
                            getString("color.trusted", "") + String.valueOf(claim.getOwnerName()));
                }else{
                    // not trusted
                    return ChatColor.translateAlternateColorCodes('&',
                            getString("color.enemy", "") + String.valueOf(claim.getOwnerName()));

                }
            }
        }
        return null;
    }

    private String fixMoney(double d) {
        if (d < 1000L) {
            return format(d);
        }
        if (d < 1000000L) {
            return format(d / 1000L) + getString("formatting.thousands", "k");
        }
        if (d < 1000000000L) {
            return format(d / 1000000L) + getString("formatting.millions", "m");
        }
        if (d < 1000000000000L) {
            return format(d / 1000000000L) + getString("formatting.billions", "b");
        }
        if (d < 1000000000000000L) {
            return format(d / 1000000000000L) + getString("formatting.trillions", "t");
        }
        if (d < 1000000000000000000L) {
            return format(d / 1000000000000000L) + getString("formatting.quadrillions", "q");
        }
        return toLong(d);
    }

    private String toLong(double amt) {
        long send = (long) amt;
        return String.valueOf(send);
    }

    private String format(double d) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(d);
    }

}
