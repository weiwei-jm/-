package uf;

import arc.struct.*;
import mindustry.content.*;
import mindustry.mod.Mod;
import mindustry.type.UnitType;
import mindustry.type.Category; // 修正：Category 在 mindustry.type 包下
import mindustry.world.blocks.payloads.PayloadBlock;

import static mindustry.type.ItemStack.with;

public class UF extends Mod {
    public static PayloadBlock forgeBlock;
    public static Seq<UnitType[]> upgrades = new Seq<>();

    @Override
    public void loadContent() {
        addChain(UnitTypes.dagger, UnitTypes.mace, UnitTypes.fortress, UnitTypes.scepter, UnitTypes.reign);
        addChain(UnitTypes.nova, UnitTypes.pulsar, UnitTypes.quasar, UnitTypes.vela);
        addChain(UnitTypes.crawler, UnitTypes.atrax, UnitTypes.spiroct, UnitTypes.arkyid, UnitTypes.toxopid);
        addChain(UnitTypes.flare, UnitTypes.horizon, UnitTypes.zenith, UnitTypes.antumbra, UnitTypes.eclipse);
        addChain(UnitTypes.mono, UnitTypes.poly, UnitTypes.mega, UnitTypes.quad, UnitTypes.oct);
        addChain(UnitTypes.risso, UnitTypes.minke, UnitTypes.bryde, UnitTypes.sei, UnitTypes.omura);
        addChain(UnitTypes.retusa, UnitTypes.oxynoe, UnitTypes.cyerce, UnitTypes.aegires, UnitTypes.navanax);

        forgeBlock = new ForgeBlock("unit-forge");
        forgeBlock.size = 2;
        forgeBlock.health = 500;
        forgeBlock.configurable = true;
        forgeBlock.requirements(Category.effect, with(Items.copper, 20, Items.lead, 30));
    }

    private void addChain(UnitType... chain) {
        for (int i = 0; i < chain.length - 1; i++) {
            upgrades.add(new UnitType[]{chain[i], chain[i + 1]});
        }
    }

    public static UnitType upgrade(UnitType from) {
        UnitType[] r = upgrades.find(u -> u[0] == from);
        return r == null ? null : r[1];
    }

    public static UnitType downgrade(UnitType from) {
        UnitType[] r = upgrades.find(u -> u[1] == from);
        return r == null ? null : r[0];
    }
}
