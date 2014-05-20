/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 *
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 *
 */
package l1j.server.server.model.skill;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Awake;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.item.action.Potion;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChangeShape;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBloodstain;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.Random;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1BuffUtil {

    public static void haste(L1PcInstance pc, int timeMillis) {

        int objId = pc.getId();

		/* å·²å­˜åœ¨åŠ é€Ÿç‹€æ…‹æ¶ˆé™¤ */
        if (pc.hasSkillEffect(HASTE) || pc.hasSkillEffect(GREATER_HASTE)
                || pc.hasSkillEffect(STATUS_HASTE)) {
            if (pc.hasSkillEffect(HASTE)) { // åŠ é€Ÿè¡“
                pc.killSkillEffectTimer(HASTE);
            } else if (pc.hasSkillEffect(GREATER_HASTE)) { // å¼·åŠ›åŠ é€Ÿè¡“
                pc.killSkillEffectTimer(GREATER_HASTE);
            } else if (pc.hasSkillEffect(STATUS_HASTE)) { // è‡ªæˆ‘åŠ é€Ÿè—¥æ°´
                pc.killSkillEffectTimer(STATUS_HASTE);
            }
        }
		/* æŠµæ¶ˆç·©é€Ÿé­”æ³•æ•ˆæžœ ç·©é€Ÿè¡“ é›†é«”ç·©é€Ÿè¡“ åœ°é�¢éšœç¤™ */
        if (pc.hasSkillEffect(SLOW) || pc.hasSkillEffect(MASS_SLOW)
                || pc.hasSkillEffect(ENTANGLE)) {
            if (pc.hasSkillEffect(SLOW)) { // ç·©é€Ÿè¡“
                pc.killSkillEffectTimer(SLOW);
            } else if (pc.hasSkillEffect(MASS_SLOW)) { // é›†é«”ç·©é€Ÿè¡“
                pc.killSkillEffectTimer(MASS_SLOW);
            } else if (pc.hasSkillEffect(ENTANGLE)) { // åœ°é�¢éšœç¤™
                pc.killSkillEffectTimer(ENTANGLE);
            }
            pc.sendPackets(new S_SkillHaste(objId, 0, 0));
            pc.broadcastPacket(new S_SkillHaste(objId, 0, 0));
        }

        pc.setSkillEffect(STATUS_HASTE, timeMillis);

        pc.sendPackets(new S_SkillSound(objId, 191));
        pc.broadcastPacket(new S_SkillSound(objId, 191));
        pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
        pc.broadcastPacket(new S_SkillHaste(objId, 1, 0));
        pc.sendPackets(new S_ServerMessage(184)); // \f1ä½ çš„å‹•ä½œçª�ç„¶è®Šå¿«ã€‚ */
        pc.setMoveSpeed(1);
    }

    public static void brave(L1PcInstance pc, int timeMillis) {
        // æ¶ˆé™¤é‡�è¤‡ç‹€æ…‹
        if (pc.hasSkillEffect(STATUS_BRAVE)) { // å‹‡æ•¢è—¥æ°´ 1.33å€�
            pc.killSkillEffectTimer(STATUS_BRAVE);
        }
        if (pc.hasSkillEffect(STATUS_ELFBRAVE)) { // ç²¾é�ˆé¤…ä¹¾ 1.15å€�
            pc.killSkillEffectTimer(STATUS_ELFBRAVE);
        }
        if (pc.hasSkillEffect(HOLY_WALK)) { // ç¥žè�–ç–¾èµ° ç§»é€Ÿ1.33å€�
            pc.killSkillEffectTimer(HOLY_WALK);
        }
        if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // è¡Œèµ°åŠ é€Ÿ ç§»é€Ÿ1.33å€�
            pc.killSkillEffectTimer(MOVING_ACCELERATION);
        }
        if (pc.hasSkillEffect(WIND_WALK)) { // é¢¨ä¹‹ç–¾èµ° ç§»é€Ÿ1.33å€�
            pc.killSkillEffectTimer(WIND_WALK);
        }
        if (pc.hasSkillEffect(STATUS_BRAVE2)) { // è¶…ç´šåŠ é€Ÿ 2.66å€�
            pc.killSkillEffectTimer(STATUS_BRAVE2);
        }

        pc.setSkillEffect(STATUS_BRAVE, timeMillis);

        int objId = pc.getId();
        pc.sendPackets(new S_SkillSound(objId, 751));
        pc.broadcastPacket(new S_SkillSound(objId, 751));
        pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
        pc.broadcastPacket(new S_SkillBrave(objId, 1, 0));
        pc.setBraveSpeed(1);
    }

    public static void thirdSpeed(L1PcInstance pc) {
        if (pc.hasSkillEffect(STATUS_THIRD_SPEED)) {
            pc.killSkillEffectTimer(STATUS_THIRD_SPEED);
        }

        pc.setSkillEffect(STATUS_THIRD_SPEED, 600 * 1000);

        pc.sendPackets(new S_SkillSound(pc.getId(), 8031));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 8031));
        pc.sendPackets(new S_Liquor(pc.getId(), 8)); // äººç‰© * 1.15
        pc.broadcastPacket(new S_Liquor(pc.getId(), 8)); // äººç‰© * 1.15
        pc.sendPackets(new S_ServerMessage(1065)); // å°‡ç™¼ç”Ÿç¥žç§˜çš„å¥‡è¹ŸåŠ›é‡�ã€‚
    }

    public static void bloodstain(L1PcInstance pc, byte type, int time,
                                  boolean showGfx) {
        if (showGfx) {
            pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
        }

        int skillId = EFFECT_BLOODSTAIN_OF_ANTHARAS;
        int iconType = 0;
        if (type == 0) { // å®‰å¡”ç‘žæ–¯
            if (!pc.hasSkillEffect(skillId)) {
                pc.addAc(-2); // é˜²ç¦¦ -2
                pc.addWater(50); // æ°´å±¬æ€§ +50
            }
            iconType = 82;
            // å®‰å¡”ç‘žæ–¯çš„è¡€ç—•
        } else if (type == 1) { // æ³•åˆ©æ˜‚
            skillId = EFFECT_BLOODSTAIN_OF_FAFURION;
            if (!pc.hasSkillEffect(skillId)) {
                pc.addWind(50); // é¢¨å±¬æ€§ +50
            }
            iconType = 85;
        }
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_SkillIconBloodstain(iconType, time));
        pc.setSkillEffect(skillId, (time * 60 * 1000));
    }

    public static void effectBlessOfDragonSlayer(L1PcInstance pc, int skillId,
                                                 int time, int showGfx) {
        if (showGfx != 0) {
            pc.sendPackets(new S_SkillSound(pc.getId(), showGfx));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), showGfx));
        }

        if (!pc.hasSkillEffect(skillId)) {
            switch (skillId) {
                case EFFECT_BLESS_OF_CRAY: // å�¡ç‘žçš„ç¥�ç¦�
                    if (pc.hasSkillEffect(EFFECT_BLESS_OF_SAELL)) {
                        pc.removeSkillEffect(EFFECT_BLESS_OF_SAELL);
                    }
                    pc.addMaxHp(100);
                    pc.addMaxMp(50);
                    pc.addHpr(3);
                    pc.addMpr(3);
                    pc.addEarth(30);
                    pc.addDmgup(1);
                    pc.addHitup(5);
                    pc.addWeightReduction(40);
                    break;
                case EFFECT_BLESS_OF_SAELL: // èŽŽçˆ¾çš„ç¥�ç¦�
                    if (pc.hasSkillEffect(EFFECT_BLESS_OF_CRAY)) {
                        pc.removeSkillEffect(EFFECT_BLESS_OF_CRAY);
                    }
                    pc.addMaxHp(80);
                    pc.addMaxMp(10);
                    pc.addWater(30);
                    pc.addAc(-8);
                    break;
            }
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            pc.sendPackets(new S_OwnCharStatus2(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
        pc.setSkillEffect(skillId, (time * 1000));
    }

    public static int skillEffect(L1Character _user, L1Character cha, L1Character _target, int skillId, int _getBuffIconDuration, int dmg)
    {
        L1PcInstance _player = null;
        if (_user instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _user;
            _player = _pc;
        }

        switch (skillId) {
            case CURE_POISON:
                cha.curePoison();
                break;
            case REMOVE_CURSE:
                cha.curePoison();
                if (cha.hasSkillEffect(STATUS_CURSE_PARALYZING)
                        || cha.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
                    cha.cureParalaysis();
                }
                break;
            // Resurrection has no break so it continues and uses the Gres code.
            case RESURRECTION:
            case GREATER_RESURRECTION:
                //If the target is a player
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (_player.getId() != pc.getId()) {
                        //check to make sure you can res them in that spot.
                        if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(pc, 0)) {
                                if (!visiblePc.isDead()) {
                                    //Cannot res because someone is in the spot the player would be resurrected in
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        //Make sure they are dead
                        if ((pc.getCurrentHp() == 0) && pc.isDead()) {
                            if (pc.getMap().isUseResurrection()) {
                                if (skillId == RESURRECTION) {
                                    pc.setGres(false);
                                } else if (skillId == GREATER_RESURRECTION) {
                                    pc.setGres(true);
                                }
                                pc.setTempID(_player.getId());
                                pc.sendPackets(new S_Message_YN(322, "")); // Do you want to be resurrected? (Y/N)
                            }
                        }
                    }
                }
                //If the target is an NPC
                else if (cha instanceof L1NpcInstance)
                {
                    //If you are in the tower of ins
                    if (!(cha instanceof L1TowerInstance)) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        if (npc.getNpcTemplate().isCantResurrect() && !(npc instanceof L1PetInstance)) {
                            return 0;
                        }
                        //If the target is a pet
                        if ((npc instanceof L1PetInstance) && (L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0)) {
                            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                if (!visiblePc.isDead()) {
                                    //Cannot res because someone is in the spot the player would be resurrected in
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((npc.getCurrentHp() == 0) && npc.isDead()) {
                            if ((npc instanceof L1PetInstance)) {
                                L1PetInstance pet = (L1PetInstance) npc;
                                npc.resurrect(npc.getMaxHp() / 4);
                                npc.setResurrect(true);
                                pet.startFoodTimer(pet);
                                pet.startHpRegeneration();
                                pet.startMpRegeneration();
                            }
                        }
                    }
                }
                break;
            // ç”Ÿå‘½å‘¼å–š
            case CALL_OF_NATURE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    //pc.sendPackets(new S_SystemMessage("This is in the call of nature block"));
                    if (_player.getId() != pc.getId()) {
                        if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                            for (L1PcInstance visiblePc : L1World.getInstance()
                                    .getVisiblePlayer(pc, 0)) {
                                if (!visiblePc.isDead()) {
                                    // Cannot res because someone is in the spot the player would be resurrected in
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((pc.getCurrentHp() == 0) && pc.isDead()) {
                            pc.setTempID(_player.getId());
                            pc.sendPackets(new S_Message_YN(322, "")); // Do you want to be resurrected? (Y/N)
                        }
                    }
                } else if (cha instanceof L1NpcInstance) {
                    //Monkey
                    L1PcInstance pc = (L1PcInstance) cha;
                    //pc.sendPackets(new S_SystemMessage("Else If Is NPC Instance"));
                    if (!(cha instanceof L1TowerInstance)) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        if (npc.getNpcTemplate().isCantResurrect() && !(npc instanceof L1PetInstance)) {
                            return 0;
                        }
                        if ((npc instanceof L1PetInstance) && (L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0)) {
                            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                if (!visiblePc.isDead()) {
                                    // Cannot res because someone is in the spot the player would be resurrected in
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((npc.getCurrentHp() == 0) && npc.isDead()) {
                            if ((npc instanceof L1PetInstance)) {
                                L1PetInstance pet = (L1PetInstance) npc;
                                npc.resurrect(cha.getMaxHp());
                                npc.resurrect(cha.getMaxMp() / 100);
                                npc.setResurrect(true);
                                pet.startFoodTimer(pet);
                                pet.startHpRegeneration();
                                pet.startMpRegeneration();
                            }
                        }
                    }
                }
                else
                {
                    L1PcInstance pc = (L1PcInstance) cha;
                    //pc.sendPackets(new S_SystemMessage("Else Statement"));
                }
                break;
            // ç„¡æ‰€é��å½¢
            case DETECTION:
                if (cha instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    int hiddenStatus = npc.getHiddenStatus();
                    if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                        npc.appearOnGround(_player);
                    }
                }
                break;
            // å¼±åŒ–å±¬æ€§
            case ELEMENTAL_FALL_DOWN:
                if (_user instanceof L1PcInstance) {
                    int playerAttr = _player.getElfAttr();
                    int i = -50;
                    if (playerAttr != 0) {
                        _player.sendPackets(new S_SkillSound(cha.getId(), 4396));
                        _player.broadcastPacket(new S_SkillSound(cha.getId(), 4396));
                    }
                    switch (playerAttr) {
                        case 0:
                            _player.sendPackets(new S_ServerMessage(79));
                            break;
                        case 1:
                            cha.addEarth(i);
                            cha.setAddAttrKind(1);
                            break;
                        case 2:
                            cha.addFire(i);
                            cha.setAddAttrKind(2);
                            break;
                        case 4:
                            cha.addWater(i);
                            cha.setAddAttrKind(4);
                            break;
                        case 8:
                            cha.addWind(i);
                            cha.setAddAttrKind(8);
                            break;
                        default:
                            break;
                    }
                }
                break;

            // ç‰©ç�†æ€§æŠ€èƒ½æ•ˆæžœ
            // ä¸‰é‡�çŸ¢
            case TRIPLE_ARROW:
                boolean gfxcheck = false;
                int[] BowGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105, 3145, 3148,
                        3151, 3871, 4125, 2323, 3892, 3895, 3898, 3901, 4917, 4918,
                        4919, 4950, 6087, 6140, 6145, 6150, 6155, 6160, 6269, 6272,
                        6275, 6278, 6826, 6827, 6836, 6837, 6846, 6847, 6856, 6857,
                        6866, 6867, 6876, 6877, 6886, 6887, 8719, 8786, 8792, 8798,
                        8804, 8808, 8860, 8900, 8913, 9225, 9226 };
                int playerGFX = _player.getTempCharGfx();
                for (int gfx : BowGFX) {
                    if (playerGFX == gfx) {
                        gfxcheck = true;
                        break;
                    }
                }
                if (!gfxcheck) {
                    return 0;
                }

                for (int i = 3; i > 0; i--) {
                    _target.onAction(_player);
                }
                _player.sendPackets(new S_SkillSound(_player.getId(), 4394));
                _player.broadcastPacket(new S_SkillSound(_player.getId(), 4394));
                break;
            case FOE_SLAYER:
			/*
			_player.setFoeSlayer(true);
			for (int i = 3; i > 0; i--) 
			{
				_target.onAction(_player);
			}
			_player.setFoeSlayer(false);

			_player.sendPackets(new S_EffectLocation(_target.getX(), _target.getY(), 6509));
			_player.broadcastPacket(new S_EffectLocation(_target.getX(),_target.getY(), 6509));
			_player.sendPackets(new S_SkillSound(_player.getId(), 7020));
			_player.broadcastPacket(new S_SkillSound(_player.getId(), 7020));

			if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1)) 
			{
				_player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV1);
				_player.sendPackets(new S_SkillIconGFX(75, 0));
			} 
			else if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2)) 
			{
				_player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV2);
				_player.sendPackets(new S_SkillIconGFX(75, 0));
			} 
			else if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3)) 
			{
				_player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV3);
				_player.sendPackets(new S_SkillIconGFX(75, 0));
			}
			break;
		*/
                _player.setFoeSlayer(true);
                for (int i = 3; i > 0; i--)
                {
                    _target.onAction(_player);
                }
                _player.setFoeSlayer(false);
                _player.sendPackets(new S_EffectLocation(_target.getX(), _target.getY(), 6509));
                _player.broadcastPacket(new S_EffectLocation(_target.getX(), _target.getY(), 6509));
                _player.sendPackets(new S_SkillSound(_player.getId(), 7020));
                _player.broadcastPacket(new S_SkillSound(_player.getId(), 7020));

                if (_player.hasSkillEffect(5001))
                {
                    _player.killSkillEffectTimer(5001);
                    _player.sendPackets(new S_SkillIconGFX(75, 0));
                }
                else if (_player.hasSkillEffect(5002))
                {
                    _player.killSkillEffectTimer(5002);
                    _player.sendPackets(new S_SkillIconGFX(75, 0));
                }
                else if (_player.hasSkillEffect(5003))
                {
                    _player.killSkillEffectTimer(5003);
                    _player.sendPackets(new S_SkillIconGFX(75, 0));
                }
                break;
            case SMASH:
                _target.onAction(_player, SMASH);
                break;
            // éª·é«�æ¯€å£ž
            case BONE_BREAK:
                _target.onAction(_player, BONE_BREAK);
                break;

            // æ©ŸçŽ‡æ€§é­”æ³•
            // æ··äº‚
            case CONFUSION:
                // ç™¼å‹•åˆ¤æ–·
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _user;
                    if (!cha.hasSkillEffect(CONFUSION)) {
                        int change = Random.nextInt(100) + 1;
                        if (change < (30 + Random.nextInt(11))) { // 30 ~ 40%
                            pc.sendPackets(new S_SkillSound(cha.getId(), 6525));
                            pc.broadcastPacket(new S_SkillSound(cha.getId(), 6525));
                            cha.setSkillEffect(CONFUSION, 2 * 1000); // ç™¼å‹•å¾Œå†�æ¬¡ç™¼å‹•é–“éš” 2ç§’
                            cha.setSkillEffect(CONFUSION_ING, 8 * 1000);
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance targetPc = (L1PcInstance) cha;
                                targetPc.sendPackets(new S_ServerMessage(1339)); // çª�ç„¶æ„Ÿè¦ºåˆ°æ··äº‚ã€‚
                            }
                        }
                    }
                }
                break;
            // é—‡ç›²å’’è¡“
            // é»‘é—‡ä¹‹å½±
            case CURSE_BLIND:
            case DARKNESS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) { // æ¼‚æµ®ä¹‹çœ¼è‚‰æ•ˆæžœ
                        pc.sendPackets(new S_CurseBlind(2));
                    } else {
                        pc.sendPackets(new S_CurseBlind(1));
                    }
                }
                break;
            // æ¯’å’’
            case CURSE_POISON:
                L1DamagePoison.doInfection(_user, cha, 3000, 5);
                break;
            // æœ¨ä¹ƒä¼Šçš„å’€å’’
            case CURSE_PARALYZE:
            case CURSE_PARALYZE2:
                if (!cha.hasSkillEffect(EARTH_BIND)
                        && !cha.hasSkillEffect(ICE_LANCE)
                        && !cha.hasSkillEffect(FREEZING_BLIZZARD)
                        && !cha.hasSkillEffect(FREEZING_BREATH)) {
                    if (cha instanceof L1PcInstance) {
                        L1CurseParalysis.curse(cha, 8000, 16000);
                    } else if (cha instanceof L1MonsterInstance) {
                        L1CurseParalysis.curse(cha, 8000, 16000);
                    }
                }
                break;
            // å¼±åŒ–è¡“
            case WEAKNESS:
                cha.addDmgup(-5);
                cha.addHitup(-1);
                break;
            // ç–¾ç—…è¡“
            case DISEASE:
                cha.addDmgup(-6);
                cha.addAc(12);
                break;
            // é¢¨ä¹‹æž·éŽ–
            case WIND_SHACKLE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
                            _getBuffIconDuration));
                    pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(),
                            _getBuffIconDuration));
                }
                break;
            // é­”æ³•ç›¸æ¶ˆè¡“
            case CANCELLATION:
                //if the target is dead, just break out.
                if(cha.isDead())
                {
                    break;
                }
                if (cha instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    int npcId = npc.getNpcTemplate().get_npcId();
                    if (npcId == 71092) { // èª¿æŸ»å“¡
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setTempCharGfx(1314);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    1314, npc.getLawful(), npc.getStatus()));
                            return 0;
                        } else {
                            return 0;
                        }
                    }
                    if (npcId == 45640) { // ç�¨è§’ç�¸
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setCurrentHp(npc.getMaxHp());
                            npc.setTempCharGfx(2332);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    2332, npc.getLawful(), npc.getStatus()));
                            npc.setName("$2103");
                            npc.setNameId("$2103");
                            npc.broadcastPacket(new S_ChangeName(npc.getId(),
                                    "$2103"));
                        } else if (npc.getTempCharGfx() == 2332) {
                            npc.setCurrentHp(npc.getMaxHp());
                            npc.setTempCharGfx(2755);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    2755, npc.getLawful(), npc.getStatus()));
                            npc.setName("$2488");
                            npc.setNameId("$2488");
                            npc.broadcastPacket(new S_ChangeName(npc.getId(),
                                    "$2488"));
                        }
                    }
                    if (npcId == 81209) { // ç¾…ä¼Š
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setTempCharGfx(4310);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    4310, npc.getLawful(), npc.getStatus()));
                            return 0;
                        } else {
                            return 0;
                        }
                    }
                    if (npcId == 81352) { // æ­�å§†æ°‘å…µ
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setTempCharGfx(148);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    148, npc.getLawful(), npc.getStatus()));
                            npc.setName("$6068");
                            npc.setNameId("$6068");
                            npc.broadcastPacket(new S_ChangeName(npc.getId(),
                                    "$6068"));
                        }
                    }
                }

                //If the player somehow cast in invis, remove invis
                if ((_player != null) && _player.isInvisble()) {
                    _player.delInvis();
                }

                //If not a player, remove Haste and Brave. Remove Status Effects
                if (!(cha instanceof L1PcInstance)) {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    npc.setMoveSpeed(0);
                    npc.setBraveSpeed(0);
                    npc.broadcastPacket(new S_SkillHaste(cha.getId(), 0, 0));
                    npc.broadcastPacket(new S_SkillBrave(cha.getId(), 0, 0));
                    npc.setWeaponBreaked(false);
                    npc.setParalyzed(false);
                    npc.setParalysisTime(0);
                }

                // Remove Poison, Paralysis, and Frozen
                cha.curePoison();
                cha.cureParalaysis();
                cha.removeSkillEffect(STATUS_FREEZE);

                // Loop through all the players buffs and remove them if they are canceable
                if(cha instanceof L1PcInstance)
                {
                    L1PcInstance pc = (L1PcInstance) cha;
                    L1SkillUse _su = new L1SkillUse();

                    Map<Integer, L1SkillTimer> map = new HashMap<Integer, L1SkillTimer>();
                    map = pc.getBuffs();
                    for (Entry<Integer, L1SkillTimer> entry : map.entrySet())
                    {
                        int skillID = entry.getKey();
                        L1SkillTimer skillDuration = entry.getValue();
                        if (isNotCancelable(skillID))
                        {
                            pc.removeSkillEffect(skillID);
                        }
                        else
                        {
                            pc.setSkillEffect(skillID, skillDuration.getRemainingTime());
                            _su.sendIcon(pc, skillID,skillDuration.getRemainingTime());
                        }
                    }

                    //Remove Polymorph
                    L1PolyMorph.undoPoly(pc);
                    pc.sendPackets(new S_CharVisualUpdate(pc));
                    pc.broadcastPacket(new S_CharVisualUpdate(pc));

                    if (pc.getHasteItemEquipped() > 0) {
                        pc.setMoveSpeed(0);
                        pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                        pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                    }

                    //If they are in shop mode, leave them in shop mode.
                    if (pc.isPrivateShop()) {
                        pc.sendPackets(new S_DoActionShop(pc.getId(),ActionCodes.ACTION_Shop, pc.getShopChat()));
                        pc.broadcastPacket(new S_DoActionShop(pc.getId(),ActionCodes.ACTION_Shop, pc.getShopChat()));
                    }
                    //If the target is pink dont pink them.
                    if (_user instanceof L1PcInstance) {
                        L1PinkName.onAction(pc, _user);
                    }
                }
                break;
            // æ²‰ç�¡ä¹‹éœ§
            case FOG_OF_SLEEPING:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                }
                cha.setSleeped(true);
                break;
            // è­·è¡›æ¯€æ»…
            case GUARD_BRAKE:
                cha.addAc(15);
                break;
            // é©šæ‚šæ­»ç¥ž
            case HORROR_OF_DEATH:
                cha.addStr(-5);
                cha.addInt(-5);
                break;
            // æ��æ…Œ
            case PANIC:
                cha.addStr((byte) -1);
                cha.addCon((byte) -1);
                cha.addDex((byte) -1);
                cha.addWis((byte) -1);
                cha.addInt((byte) -1);
                break;
            // æ��æ‡¼ç„¡åŠ©
            case RESIST_FEAR:
                cha.addNdodge((byte) 5); // é–ƒé�¿çŽ‡ - 50%
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    // æ›´æ–°é–ƒé�¿çŽ‡é¡¯ç¤º
                    pc.sendPackets(new S_PacketBox(101, pc.getNdodge()));
                }
                break;
            // é‡‹æ”¾å…ƒç´ 
            case RETURN_TO_NATURE:
                if (Config.RETURN_TO_NATURE && (cha instanceof L1SummonInstance)) {
                    L1SummonInstance summon = (L1SummonInstance) cha;
                    summon.broadcastPacket(new S_SkillSound(summon.getId(), 2245));
                    summon.returnToNature();
                } else {
                    if (_user instanceof L1PcInstance) {
                        _player.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;
            // å£žç‰©è¡“
            case WEAPON_BREAK:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    L1ItemInstance weapon = pc.getWeapon();
                    if (weapon != null) {
                        int weaponDamage = Random.nextInt(_user.getInt() / 3) + 1;
                        // \f1ä½ çš„%0%så£žäº†ã€‚
                        pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                        pc.getInventory().receiveDamage(weapon, weaponDamage);
                    }
                } else {
                    ((L1NpcInstance) cha).setWeaponBreaked(true);
                }
                break;

            // è¼”åŠ©æ€§é­”æ³•
            // é�¡åƒ�ã€�æš—å½±é–ƒé�¿
            case MIRROR_IMAGE:
            case UNCANNY_DODGE:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _user;
                    pc.addDodge((byte) 5); // é–ƒé�¿çŽ‡ + 50%
                    // æ›´æ–°é–ƒé�¿çŽ‡é¡¯ç¤º
                    pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                }
                break;
            // æ¿€å‹µå£«æ°£
            case GLOWING_AURA:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitup(5);
                    pc.addBowHitup(5);
                    pc.addMr(20);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
                }
                break;
            // é‹¼é�µå£«æ°£
            case SHINING_AURA:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-8);
                    pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
                }
                break;
            // è¡�æ“Šå£«æ°£
            case BRAVE_AURA:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(5);
                    pc.addRegistStun(2);
                    pc.addRegistStone(2);
                    pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
                }
                break;
            // é˜²è­·ç½©
            case SHIELD:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-2);
                    pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
                }
                break;
            // å½±ä¹‹é˜²è­·
            case SHADOW_ARMOR:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-3);
                    pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
                }
                break;
            // å¤§åœ°é˜²è­·
            case EARTH_SKIN:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-6);
                    pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
                }
                break;
            // å¤§åœ°çš„ç¥�ç¦�
            case EARTH_BLESS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-7);
                    pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
                }
                break;
            // é‹¼é�µé˜²è­·
            case IRON_SKIN:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-10);
                    pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
                }
                break;
            // é«”é­„å¼·å�¥è¡“
            case PHYSICAL_ENCHANT_STR:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addStr((byte) 5);
                    pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
                }
                break;
            // é€šæš¢æ°£è„ˆè¡“
            case PHYSICAL_ENCHANT_DEX:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDex((byte) 5);
                    pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
                }
                break;
            // åŠ›é‡�æ��å�‡
            case DRESS_MIGHTY:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addStr((byte) 2);
                    pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
                }
                break;
            // æ•�æ�·æ��å�‡
            case DRESS_DEXTERITY:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDex((byte) 2);
                    pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
                }
                break;
            // é­”æ³•é˜²ç¦¦
            case RESIST_MAGIC:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(10);
                    pc.sendPackets(new S_SPMR(pc));
                }
                break;
            // æ·¨åŒ–ç²¾ç¥ž
            case CLEAR_MIND:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addWis((byte) 3);
                    pc.resetBaseMr();
                }
                break;
            // å±¬æ€§é˜²ç¦¦
            case RESIST_ELEMENTAL:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addWind(10);
                    pc.addWater(10);
                    pc.addFire(10);
                    pc.addEarth(10);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                }
                break;
            // å–®å±¬æ€§é˜²ç¦¦
            case ELEMENTAL_PROTECTION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    int attr = pc.getElfAttr();
                    if (attr == 1) {
                        pc.addEarth(50);
                    } else if (attr == 2) {
                        pc.addFire(50);
                    } else if (attr == 4) {
                        pc.addWater(50);
                    } else if (attr == 8) {
                        pc.addWind(50);
                    }
                }
                break;
            // å¿ƒé�ˆè½‰æ�›
            case BODY_TO_MIND:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setCurrentMp(pc.getCurrentMp() + 2);
                }
                break;
            // é­‚é«”è½‰æ�›
            case BLOODY_SOUL:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setCurrentMp(pc.getCurrentMp() + 18);
                }
                break;
            // éš±èº«è¡“ã€�æš—éš±è¡“
            case INVISIBILITY:
            case BLIND_HIDING:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Invis(pc.getId(), 1));
                    pc.broadcastPacketForFindInvis(new S_RemoveObject(pc), false);
                }
                break;
            // ç�«ç„°æ­¦å™¨
            case FIRE_WEAPON:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(4);
                    pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
                }
                break;
            // çƒˆç‚Žæ°£æ�¯
            case FIRE_BLESS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(4);
                    pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
                }
                break;
            // çƒˆç‚Žæ­¦å™¨
            case BURNING_WEAPON:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(6);
                    pc.addHitup(6);
                    pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
                }
                break;
            // é¢¨ä¹‹ç¥žå°„
            case WIND_SHOT:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(6);
                    pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
                }
                break;
            // æš´é¢¨ä¹‹çœ¼
            case STORM_EYE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(2);
                    pc.addBowDmgup(3);
                    pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
                }
                break;
            // æš´é¢¨ç¥žå°„
            case STORM_SHOT:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowDmgup(6);
                    pc.addBowHitup(6);
                    pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
                }
                break;
            // ç‹‚æš´è¡“
            case BERSERKERS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(10);
                    pc.addDmgup(5);
                    pc.addHitup(2);
                }
                break;
            // è®Šå½¢è¡“
            case SHAPE_CHANGE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_ShowPolyList(pc.getId()));
                    if (!pc.isShapeChange()) {
                        pc.setShapeChange(true);
                    }
                }
                break;
            // é�ˆé­‚æ˜‡è�¯
            case ADVANCE_SPIRIT:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setAdvenHp(pc.getBaseMaxHp() / 5);
                    pc.setAdvenMp(pc.getBaseMaxMp() / 5);
                    pc.addMaxHp(pc.getAdvenHp());
                    pc.addMaxMp(pc.getAdvenMp());
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    if (pc.isInParty()) { // ãƒ‘ãƒ¼ãƒ†ã‚£ãƒ¼ä¸­
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                }
                break;
            // ç¥žè�–ç–¾èµ°ã€�è¡Œèµ°åŠ é€Ÿã€�é¢¨ä¹‹ç–¾èµ°
            case HOLY_WALK:
            case MOVING_ACCELERATION:
            case WIND_WALK:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setBraveSpeed(4);
                    pc.sendPackets(new S_SkillBrave(pc.getId(), 4,_getBuffIconDuration));
                    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
                }
                break;
            // è¡€ä¹‹æ¸´æœ›
            case BLOODLUST: //Update bloodlust to increase attack and movement speed to brave speed -[John]
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance)cha;
                    L1ItemInstance item = new L1ItemInstance();
                    Potion.Brave(pc, item , 999999);
                }
                break;
            case AWAKEN_ANTHARAS:
            case AWAKEN_FAFURION:
            case AWAKEN_VALAKAS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    L1Awake.start(pc, skillId);
                }
                break;
            // å¹»è¦ºï¼šæ­�å�‰
            case ILLUSION_OGRE:
                cha.addDmgup(4);
                cha.addHitup(4);
                cha.addBowDmgup(4);
                cha.addBowHitup(4);
                break;
            // å¹»è¦ºï¼šå·«å¦–
            case ILLUSION_LICH:
                cha.addSp(2);
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SPMR(pc));
                }
                break;
            // å¹»è¦ºï¼šé‘½çŸ³é«˜ä¾–
            case ILLUSION_DIA_GOLEM:
                cha.addAc(-20);
                break;
            // å¹»è¦ºï¼šåŒ–èº«
            case ILLUSION_AVATAR:
                cha.addDmgup(10);
                cha.addBowDmgup(10);
                break;
            // æ´žå¯Ÿ
            case INSIGHT:
                cha.addStr((byte) 1);
                cha.addCon((byte) 1);
                cha.addDex((byte) 1);
                cha.addWis((byte) 1);
                cha.addInt((byte) 1);
                break;
            // çµ•å°�å±�éšœ
            case ABSOLUTE_BARRIER:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.stopHpRegeneration();
                    pc.stopMpRegeneration();
                    pc.stopHpRegenerationByDoll();
                    pc.stopMpRegenerationByDoll();
                }
                break;
            // å†¥æƒ³è¡“
            case MEDITATION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(5);
                }
                break;
            // å°ˆæ³¨
            case CONCENTRATION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(2);
                }
                break;

            // ç›®æ¨™ NPC
            // èƒ½é‡�æ„Ÿæ¸¬
            case WEAK_ELEMENTAL:
                if (cha instanceof L1MonsterInstance) {
                    L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
                    int weakAttr = npcTemp.get_weakAttr();
                    if ((weakAttr & 1) == 1) { // åœ°
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2169));
                    } else if ((weakAttr & 2) == 2) { // ç�«
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2166));
                    } else if ((weakAttr & 4) == 4) { // æ°´
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2167));
                    } else if ((weakAttr & 8) == 8) { // é¢¨
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2168));
                    } else {
                        if (_user instanceof L1PcInstance) {
                            _player.sendPackets(new S_ServerMessage(79));
                        }
                    }
                } else {
                    if (_user instanceof L1PcInstance) {
                        _player.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;

            // å‚³é€�æ€§é­”æ³•
            // ä¸–ç•Œæ¨¹çš„å‘¼å–š
            case TELEPORT_TO_MATHER:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (pc.getMap().isEscapable() || pc.isGm()) {
                        L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5, true);
                    } else {
                        pc.sendPackets(new S_ServerMessage(276)); // \f1åœ¨æ­¤ç„¡æ³•ä½¿ç”¨å‚³é€�ã€‚
                        pc.sendPackets(new S_Paralysis(
                                S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                    }
                }
                break;

            // å�¬å–šã€�è¿·é­…ã€�é€ å±�
            // å�¬å–šè¡“
            case SUMMON_MONSTER:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    int level = pc.getLevel();
                    int[] summons;
                    if (pc.getMap().isRecallPets()) {
                        if (pc.getInventory().checkEquipped(20284)) {
                            pc.sendPackets(new S_ShowSummonList(pc.getId()));
                            if (!pc.isSummonMonster()) {
                                pc.setSummonMonster(true);
                            }
                        } else {
						/*
						 * summons = new int[] { 81083, 81084, 81085, 81086,
						 * 81087, 81088, 81089 };
						 */
                            summons = new int[] { 81210, 81213, 81216, 81219,
                                    81222, 81225, 81228 };
                            int summonid = 0;
                            // int summoncost = 6;
                            int summoncost = 8;
                            int levelRange = 32;
                            for (int i = 0; i < summons.length; i++) { // è©²å½“ï¼¬ï¼¶ç¯„å›²æ¤œç´¢
                                if ((level < levelRange)
                                        || (i == summons.length - 1)) {
                                    summonid = summons[i];
                                    break;
                                }
                                levelRange += 4;
                            }

                            int petcost = 0;
                            Object[] petlist = pc.getPetList().values().toArray();
                            for (Object pet : petlist) {
                                // ç�¾åœ¨ã�®ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆ
                                petcost += ((L1NpcInstance) pet).getPetcost();
                            }
                            int pcCha = pc.getCha();
                            if (pcCha > 34) { // max count = 5
                                pcCha = 34;
                            }
                            int charisma = pcCha + 6 - petcost;
                            // int charisma = pc.getCha() + 6 - petcost;
                            int summoncount = charisma / summoncost;
                            L1Npc npcTemp = NpcTable.getInstance().getTemplate(
                                    summonid);
                            for (int i = 0; i < summoncount; i++) {
                                L1SummonInstance summon = new L1SummonInstance(
                                        npcTemp, pc);
                                summon.setPetcost(summoncost);
                            }
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;
            // å�¬å–šå±¬æ€§ç²¾é�ˆã€�å�¬å–šå¼·åŠ›å±¬æ€§ç²¾é�ˆ
            case LESSER_ELEMENTAL:
            case GREATER_ELEMENTAL:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    int attr = pc.getElfAttr();
                    if (attr != 0) { // ç„¡å±žæ€§ã�§ã�ªã�‘ã‚Œã�°å®Ÿè¡Œ
                        if (pc.getMap().isRecallPets()) {
                            int petcost = 0;
                            for (L1NpcInstance petNpc : pc.getPetList().values()) {
                                // ç�¾åœ¨ã�®ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆ
                                petcost += petNpc.getPetcost();
                            }

                            if (petcost == 0) { // 1åŒ¹ã‚‚æ‰€å±žNPCã�Œã�„ã�ªã�‘ã‚Œã�°å®Ÿè¡Œ
                                int summonid = 0;
                                int summons[];
                                if (skillId == LESSER_ELEMENTAL) { // ãƒ¬ãƒƒã‚µãƒ¼ã‚¨ãƒ¬ãƒ¡ãƒ³ã‚¿ãƒ«[åœ°,ç�«,æ°´,é¢¨]
                                    summons = new int[] { 45306, 45303, 45304,
                                            45305 };
                                } else {
                                    // ã‚°ãƒ¬ãƒ¼ã‚¿ãƒ¼ã‚¨ãƒ¬ãƒ¡ãƒ³ã‚¿ãƒ«[åœ°,ç�«,æ°´,é¢¨]
                                    summons = new int[] { 81053, 81050, 81051,
                                            81052 };
                                }
                                int npcattr = 1;
                                for (int i = 0; i < summons.length; i++) {
                                    if (npcattr == attr) {
                                        summonid = summons[i];
                                        i = summons.length;
                                    }
                                    npcattr *= 2;
                                }
                                // ç‰¹æ®Šè¨­å®šã�®å ´å�ˆãƒ©ãƒ³ãƒ€ãƒ ã�§å‡ºç�¾
                                if (summonid == 0) {

                                    int k3 = Random.nextInt(4);
                                    summonid = summons[k3];
                                }

                                L1Npc npcTemp = NpcTable.getInstance().getTemplate(
                                        summonid);
                                L1SummonInstance summon = new L1SummonInstance(
                                        npcTemp, pc);
                                summon.setPetcost(pc.getCha() + 7); // ç²¾éœŠã�®ä»–ã�«ã�¯NPCã‚’æ‰€å±žã�•ã�›ã‚‰ã‚Œã�ªã�„
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;
            // è¿·é­…è¡“
            case TAMING_MONSTER:
                if (cha instanceof L1MonsterInstance) {
                    L1MonsterInstance npc = (L1MonsterInstance) cha;
                    // å�¯è¿·é­…çš„æ€ªç‰©
                    if (npc.getNpcTemplate().isTamable()) {
                        int petcost = 0;
                        Object[] petlist = _user.getPetList().values().toArray();
                        for (Object pet : petlist) {
                            // ç�¾åœ¨ã�®ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆ
                            petcost += ((L1NpcInstance) pet).getPetcost();
                        }
                        int charisma = _user.getCha();
                        if (_player.isElf()) { // ã‚¨ãƒ«ãƒ•
                            if (charisma > 30) { // max count = 7
                                charisma = 30;
                            }
                            charisma += 12;
                        } else if (_player.isWizard()) { // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰
                            if (charisma > 36) { // max count = 7
                                charisma = 36;
                            }
                            charisma += 6;
                        }
                        charisma -= petcost;
                        if (charisma >= 6) { // ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆã�®ç¢ºèª�
                            L1SummonInstance summon = new L1SummonInstance(npc,
                                    _user, false);
                            _target = summon; // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆå…¥æ›¿ã�ˆ
                        } else {
                            _player.sendPackets(new S_ServerMessage(319)); // \f1ã�“ã‚Œä»¥ä¸Šã�®ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ã‚’æ“�ã‚‹ã�“ã�¨ã�¯ã�§ã��ã�¾ã�›ã‚“ã€‚
                        }
                    }
                }
                break;
            // é€ å±�è¡“
            case CREATE_ZOMBIE:
                if (cha instanceof L1MonsterInstance) {
                    L1MonsterInstance npc = (L1MonsterInstance) cha;
                    int petcost = 0;
                    Object[] petlist = _user.getPetList().values().toArray();
                    for (Object pet : petlist) {
                        // ç�¾åœ¨ã�®ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆ
                        petcost += ((L1NpcInstance) pet).getPetcost();
                    }
                    int charisma = _user.getCha();
                    if (_player.isElf()) { // ã‚¨ãƒ«ãƒ•
                        if (charisma > 30) { // max count = 7
                            charisma = 30;
                        }
                        charisma += 12;
                    } else if (_player.isWizard()) { // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰
                        if (charisma > 36) { // max count = 7
                            charisma = 36;
                        }
                        charisma += 6;
                    }
                    charisma -= petcost;
                    if (charisma >= 6) { // ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆã�®ç¢ºèª�
                        L1SummonInstance summon = new L1SummonInstance(npc, _user,
                                true);
                        _target = summon; // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆå…¥æ›¿ã�ˆ
                    } else {
                        _player.sendPackets(new S_ServerMessage(319)); // \f1ã�“ã‚Œä»¥ä¸Šã�®ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ã‚’æ“�ã‚‹ã�“ã�¨ã�¯ã�§ã��ã�¾ã�›ã‚“ã€‚
                    }
                }
                break;

            // æ€ªç‰©å°ˆå±¬é­”æ³•
            case 10026:
            case 10027:
            case 10028:
            case 10029:
                if (_user instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) _user;
                    _user.broadcastPacket(new S_NpcChatPacket(npc, "$3717", 0)); // ã�•ã�‚ã€�ã�Šã�¾ã�ˆã�«å®‰æ�¯ã‚’ä¸Žã�ˆã‚ˆã�†ã€‚
                } else {
                    _player.broadcastPacket(new S_ChatPacket(_player, "$3717", 0, 0)); // ã�•ã�‚ã€�ã�Šã�¾ã�ˆã�«å®‰æ�¯ã‚’ä¸Žã�ˆã‚ˆã�†ã€‚
                }
                break;
            case 10057:
                L1Teleport.teleportToTargetFront(cha, _user, 1);
                break;
            case STATUS_FREEZE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                }
                break;
            default:
                break;
        }

        return dmg;
    }

    private static boolean isNotCancelable(int skillNum)
    {
        HashMap<Integer,Boolean> map = new HashMap<Integer ,Boolean>();
        map.put(ENCHANT_WEAPON, false);
        map.put(BLESSED_ARMOR, false);
        map.put(ABSOLUTE_BARRIER, false);
        map.put(ADVANCE_SPIRIT, false);
        map.put(SHOCK_STUN, false);
        map.put(REDUCTION_ARMOR, false);
        map.put(SOLID_CARRIAGE, false);
        map.put(COUNTER_BARRIER, false);
        map.put(AWAKEN_ANTHARAS, false);
        map.put(AWAKEN_FAFURION, false);
        map.put(AWAKEN_VALAKAS, false);
        map.put(COOKING_WONDER_DRUG, false);
        map.put(1015, false);
        map.put(1014, false);
        map.put(ENCHANT_VENOM, false);
        map.put(UNCANNY_DODGE, false);
        map.put(DRESS_EVASION, false);
        map.put(VENOM_RESIST, false);
        //map.put(BURNING_SPIRIT, false);
        //map.put(DOUBLE_BRAKE, false);
        //map.put(SHADOW_FANG, false);

        Set set = map.entrySet();
        Iterator i = set.iterator();

        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            if(Integer.parseInt(me.getKey().toString()) ==  skillNum)
            {
                return (Boolean) me.getValue();
            }
        }
        return true;
    }
}