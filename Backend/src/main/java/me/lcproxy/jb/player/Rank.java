package me.lcproxy.jb.player;

import lombok.Getter;
import me.lcproxy.jb.object.CC;
import me.lcproxy.jb.object.LunarLogoColors;

import java.util.Arrays;

@Getter
public enum Rank {
    CUSTOM(6, LunarLogoColors.WHITE.getColor(), "Custom", CC.WHITE),
    OWNER(5, LunarLogoColors.OWNER.getColor(), CC.RED.getCode() + "Owner", CC.RED),
    LEAD_DEV(4, LunarLogoColors.DEV.getColor(), CC.AQUA.getCode() + "Lead Dev", CC.AQUA),
    DEV(3, LunarLogoColors.DEV.getColor(), CC.AQUA.getCode() + "Dev", CC.AQUA),
    PARTNER(2, LunarLogoColors.PARTNER.getColor(), CC.GOLD.getCode() + "Partner", CC.GOLD),
    UHCLAND(6, LunarLogoColors.UHCLAND.getColor(), CC.RED.getCode() + "UHCLand", CC.RED),
    VIP(1, LunarLogoColors.TESTER.getColor(), "VIP", CC.YELLOW),
    USER(0, LunarLogoColors.WHITE.getColor(), "User", CC.BLUE);

    public int id;
    public int color;
    public String name;
    public CC FColor;

    Rank(int id, int color, String name, CC FColor) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.FColor = FColor;
    }

    public static Rank getRankById(int id) {
        for (Rank rank : Rank.values()) {
            if (id == rank.id) {
                return rank;
            }
        }
        return USER;
    }

    public static Rank getByName(String input) {
        return Arrays.stream(values()).filter((type) -> {
            return type.name().equalsIgnoreCase(input) || type.getName().equalsIgnoreCase(input);
        }).findFirst().orElse(null);
    }

    public static boolean isRankOverId(Rank rank, Rank neededRank) {
        return rank.id >= neededRank.id;
    }
}
