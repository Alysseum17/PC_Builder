package com.pcbuilder.core.modules.build.model;

import lombok.Getter;

@Getter
public enum CompatibilityRule {
    CPU_MB_SOCKET("cpu", "motherboard", "Socket", RuleType.EQUALS),
    MB_RAM_TYPE("motherboard", "ram", "Memory Type", RuleType.EQUALS),

    GPU_CASE_LENGTH("gpu", "case", "Length", "Max GPU Length", RuleType.LTE),

    PSU_GPU_POWER("psu", "gpu", "Wattage", "Recommended PSU", RuleType.GTE);

    private final String category1;
    private final String category2;
    private final String attr1;
    private final String attr2;
    private final RuleType type;

    CompatibilityRule(String c1, String c2, String a1, String a2, RuleType type) {
        this.category1 = c1;
        this.category2 = c2;
        this.attr1 = a1;
        this.attr2 = a2;
        this.type = type;
    }

    CompatibilityRule(String c1, String c2, String attr, RuleType type) {
        this(c1, c2, attr, attr, type);
    }

    public enum RuleType { EQUALS, LTE, GTE }
}