package uf;

import arc.math.Mathf;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.UnitType; // 补上 UnitType 导入
import mindustry.world.Block; // 补上 Block 导入
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.Team; // 补上 Team 导入（注意可能是 mindustry.game.Team 或 mindustry.gen.Team，取决于版本，如果继续报错请换成 mindustry.game.Team）

public class ForgeBlock extends PayloadBlock {
    public ForgeBlock(String name) {
        super(name);
    }

    @Override
    public Building create(Block block, Team team) {
        return new ForgeBuild();
    }

    public class ForgeBuild extends PayloadBlockBuild<UnitPayload> {
        public int forgeCount = 0;

        @Override
        public boolean acceptUnitPayload(Unit unit) {
            return UF.upgrade(unit.type) != null;
        }

        @Override
        public void configured(Unit player, Object value) {
            if (payload == null) {
                // 修复 player.sendMessage 报错：把 player 当作 player 玩家控制单位，使用 Vars.player 或对应 Player 对象
                if (player instanceof Player p) p.sendMessage("[scarlet]请先将一个单位放入锻造器！");
                return;
            }
            showForgeDialog(payload.unit, player);
        }

        private void showForgeDialog(Unit unit, Unit player) {
            BaseDialog dialog = new BaseDialog("单位锻造器");
            dialog.addCloseButton();

            dialog.cont.add("当前单位: " + unit.type.localizedName).row();
            dialog.cont.add("锻造消耗: 10 铜 + 10 铅").row();
            dialog.cont.add("已锻造次数: " + forgeCount).row();

            dialog.cont.button("锻造", () -> {
                // 核心资源扣除需要 Player 对象
                Player p = player instanceof Player pl ? pl : null;
                if (p == null) return;
                
                var core = p.core();
                if (core == null || !core.items.has(Items.copper, 10) || !core.items.has(Items.lead, 10)) {
                    p.sendMessage("[scarlet]核心资源不足！需要 10 铜 + 10 铅");
                    return;
                }
                core.items.remove(Items.copper, 10);
                core.items.remove(Items.lead, 10);
                doForge(unit, p);
                dialog.hide();
            }).size(200f, 50f);

            dialog.show();
        }

        private void doForge(Unit unit, Player player) {
            UnitType target = UF.upgrade(unit.type);

            // 满级：只加伤
            if (target == null) {
                forgeCount++;
                player.sendMessage("[accent]锻造成功！" + unit.type.localizedName + " 伤害提升 5%（累计 " + (forgeCount * 5) + "%）");
                return;
            }

            // T1 特殊概率
            boolean isT1 = unit.type == UnitTypes.dagger || unit.type == UnitTypes.nova || unit.type == UnitTypes.crawler
                || unit.type == UnitTypes.flare || unit.type == UnitTypes.mono
                || unit.type == UnitTypes.risso || unit.type == UnitTypes.retusa;

            if (isT1) {
                float roll = Mathf.random();
                if (roll < 0.1f) {
                    payload.unit.kill();
                    payload = null;
                    player.sendMessage("[scarlet]锻造失败！单位被销毁了……");
                    return;
                } else if (roll < 0.2f) {
                    UnitType first = UF.upgrade(unit.type);
                    UnitType second = first == null ? null : UF.upgrade(first);
                    UnitType result = second == null ? first : second;
                    if (result != null) {
                        payload.unit = result.create(payload.unit.team());
                        player.sendMessage("[green]大成功！连续升 2 级 → " + result.localizedName);
                        return;
                    }
                }
            } else {
                // T2+ 10% 降级
                if (Mathf.random() < 0.1f) {
                    UnitType down = UF.downgrade(unit.type);
                    if (down != null) {
                        payload.unit = down.create(payload.unit.team());
                        player.sendMessage("[scarlet]锻造失败，单位降级为 " + down.localizedName);
                        return;
                    }
                }
            }

            payload.unit = target.create(payload.unit.team());
            player.sendMessage("[green]锻造成功！升级为 " + target.localizedName);
        }
    }
}
