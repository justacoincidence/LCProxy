package me.lcproxy.jb.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LunarLogoColors {
    WHITE(16777215),
    OWNER(11141120),
    PARTNER(16558080),
    TESTER(16558080),
    STAFF(43690),
    ADMIN(16274259),
    UHCLAND(16711680),
    // STAFF(1703936),
    DEV(5636095);

    private int Color;
}
