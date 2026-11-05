package uf;

import arc.struct.*;
import mindustry.content.*;
import mindustry.mod.Mod;
import mindustry.type.UnitType;
import mindustry.type.Category;

import static mindustry.type.ItemStack.with;

public class UF extends Mod {
    // 【修改这里】把 PayloadBlock 改成 ForgeBlock
    public static ForgeBlock forgeBlock; 
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
        
        // 现在 forgeBlock 是 ForgeBlock 类型了，它就知道自己里面的 ForgeBuild 是什么了
        forgeBlock.buildType = () -> forgeBlock.new ForgeBuild();
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
