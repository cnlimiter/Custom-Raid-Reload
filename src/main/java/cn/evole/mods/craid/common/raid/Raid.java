package cn.evole.mods.craid.common.raid;

import cn.evole.mods.craid.CRaid;
import cn.evole.mods.craid.CRaidUtil;
import cn.evole.mods.craid.api.IPlacementComponent;
import cn.evole.mods.craid.api.IRaidComponent;
import cn.evole.mods.craid.api.ISpawnComponent;
import cn.evole.mods.craid.api.events.RaidEvent;
import cn.evole.mods.craid.common.advancement.RaidTrigger;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;

public class Raid {

	private static final Component RAID_NAME_COMPONENT = Component.translatable("event.minecraft.raid");
	private static final Component RAID_WARN = Component.translatable("raid.craid.warn").withStyle(ChatFormatting.RED);
	private static final Component RAID_TELEPORT = Component.translatable("raid.craid.teleport").withStyle(ChatFormatting.YELLOW);
	private final ServerBossEvent raidBar = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
	private final int id;//unique specify id.
	public final ServerLevel world;
	public final ResourceLocation resource;//res to read raid component.
	protected IRaidComponent raid;
	protected BlockPos center;//raid center block position.
	protected Status status = Status.PREPARE;
	protected int tick = 0;
	protected int stopTick = 0;
	protected int currentWave = 0;
	protected int currentSpawn = 0;
	protected Set<Entity> raiders = new HashSet<>();
	protected Set<UUID> heroes = new HashSet<>();


	public Raid(int id, ServerLevel world, ResourceLocation res, BlockPos pos) {
		this.id = id;
		this.world = world;
		this.resource = res;
		this.center = pos;
	}

	public Raid(ServerLevel world, CompoundTag nbt) {
		this.world = world;
		this.id = nbt.getInt("raid_id");
		this.status = Status.values()[nbt.getInt("raid_status")];
		this.resource = new ResourceLocation(nbt.getString("raid_resource"));
		this.tick = nbt.getInt("raid_tick");
		this.stopTick = nbt.getInt("stop_tick");
		this.currentWave = nbt.getInt("current_wave");
		{// for raid center position.
			CompoundTag tmp = nbt.getCompound("center_pos");
			this.center = new BlockPos(tmp.getInt("pos_x"), tmp.getInt("pos_y"), tmp.getInt("pos_z"));
		}
		{// for raiders entity id.
			ListTag list = nbt.getList("raiders", 10);
			for(int i = 0; i < list.size(); ++ i) {
				final int id = list.getInt(i);
				final Entity entity = world.getEntity(id);
				if(entity != null) {
					this.raiders.add(entity);
				}
			}
		}
		{// for heroes uuid.
			ListTag list = nbt.getList("heroes", 10);
			for(int i = 0; i < list.size(); ++ i) {
				final UUID uuid = NbtUtils.loadUUID(list.getCompound(i));
				if(uuid != null) {
					this.heroes.add(uuid);
				}
			}
		}
	}

	public void save(CompoundTag nbt) {
		nbt.putInt("raid_id", this.id);
		nbt.putInt("raid_status", this.status.ordinal());
		nbt.putString("raid_resource", this.resource.toString());
		nbt.putInt("raid_tick", this.tick);
		nbt.putInt("stop_tick", this.stopTick);
		nbt.putInt("current_wave", this.currentWave);
		{// for raid center position.
			CompoundTag tmp = new CompoundTag();
			tmp.putInt("pos_x", this.center.getX());
		    tmp.putInt("pos_y", this.center.getY());
			tmp.putInt("pos_z", this.center.getZ());
			nbt.put("center_pos", tmp);
		}
		{// for raiders entity id.
			ListTag list = new ListTag();
			for(Entity entity : this.raiders) {
				list.add(IntTag.valueOf(entity.getId()));
			}
			nbt.put("raiders", list);
		}
		{// for heroes uuid.
			ListTag list = new ListTag();
			for(UUID uuid : this.heroes) {
				list.add(NbtUtils.createUUID(uuid));
			}
			nbt.put("heroes", list);
		}
	}

	public void tick() {
		/* skip tick */
		if(this.isRemoving() || this.world.players().isEmpty()) {
			return ;
		}
		/* is raid component valid */
		if(this.getRaidComponent() == null) {
			this.remove();
			CRaid.LOGGER.warn("Raid Tick Error : Where is the raid component ?");
			return ;
		}
		/* not allow to be peaceful */
		if(this.world.getDifficulty() == Difficulty.PEACEFUL) {
			this.remove();
			return ;
		}
		this.tickBar();
		if(this.isStopping()) {
			/* has stopped */
			if(++ this.stopTick >= CRaidUtil.getRaidWaitTime()) {
				this.remove();
			}
		}
//		System.out.println(this.tick + " " + this.stopTick + " " + this.status + " " + this.center);
		if(this.isPreparing()) {
			/* prepare state */
			this.prepareState();
			if(this.tick >= this.raid.getPrepareCD(this.currentWave)) {
				this.waveStart();
			}
		} else if(this.isRunning()) {
			/* running state */
			if(this.tick >= this.raid.getLastDuration(this.currentWave)
					|| (this.raiders.isEmpty() && this.raid.isWaveFinish(this.currentWave, this.currentSpawn))) {
				this.checkNextWave();
			}
			if(this.isLoss()) {
				//fail to start next wave.
				this.onLoss();
				return ;
			}
			if(this.isVictory()) {
				this.onVictory();
				return ;
			}
			this.tickWave();
		} else if(this.isLoss()) {
			/* loss state */
			if(this.tick >= this.raid.getLossCD()) {
				this.remove();
			}
		} else if(this.isVictory()) {
			/* running state */
			if(this.tick >= this.raid.getWinCD()) {
				this.remove();
			}
		}
		++ this.tick;
	}

	/**
	 * {@link #tick()}
	 */
	protected void tickWave() {
		/* check spawn entities */
		final List<ISpawnComponent> spawns = this.raid.getSpawns(this.currentWave);
		while(this.currentSpawn < spawns.size() && this.tick >= spawns.get(this.currentSpawn).getSpawnTick()) {
			this.spawnEntities(spawns.get(this.currentSpawn ++));
		}

		/* update raiders list */
		this.raiders.removeIf(entity -> !entity.isAlive());
	}

	protected void spawnEntities(ISpawnComponent spawn) {
		final int count = spawn.getSpawnAmount();
		for(int i = 0; i < count; ++ i) {
			Entity entity = this.createEntity(spawn);
			if(entity != null) {
				this.raiders.add(entity);
				if(entity instanceof Mob) {
					// avoid despawn.
					((Mob) entity).setPersistenceRequired();
				}
			}
		}
	}

	/**
	 * copy from {@link }
	 */
	private Entity createEntity(ISpawnComponent spawn) {
		final IPlacementComponent placement = spawn.getPlacement() != null ? spawn.getPlacement() : this.raid.getPlacement(this.currentWave);
		final BlockPos pos = placement.getPlacePosition(this.world, this.center);
		if(! Level.isInSpawnableBounds(pos)) {
			CRaid.LOGGER.error("Invalid position when trying summon entity !");
			return null;
		}
		final CompoundTag compound = spawn.getNBT().copy();

		compound.putString("id", Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(spawn.getSpawnType())).toString());
		Entity entity = EntityType.loadEntityRecursive(compound, world, e -> {
			e.moveTo(pos, e.getXRot(), e.getYRot());
			e.setGlowingTag(spawn.glowing());
			return e;
		});
		if(entity == null) {
			CRaid.LOGGER.error("summon esntity failed !");
			return null;
		} else {
			if(! world.tryAddFreshEntityWithPassengers(entity)) {
				CRaid.LOGGER.error("summon entity duplicated uuid !");
				return null;
			}
		}
		return entity;
	}

	/**
	 * {@link #tick()}
	 */
	protected void tickBar() {
		if(this.tick % 10 == 0 && ! this.world.players().isEmpty()) {
			this.updatePlayers();
		}
		this.raidBar.setColor(this.raid.getBarColor());
		if(this.isPreparing()) {
			this.raidBar.setName(this.raid.getRaidTitle());
			this.raidBar.setProgress(this.tick * 1.0F / this.raid.getPrepareCD(this.currentWave));
		} else if(this.isRunning()) {
			this.raidBar.setName(this.raid.getRaidTitle().copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", this.raiders.size())));
			this.raidBar.setProgress(1 - this.tick * 1.0F / this.raid.getLastDuration(this.currentWave));
		} else if(this.isVictory()) {
			this.raidBar.setName(this.raid.getRaidTitle().copy().append(" - ").append(this.raid.getWinTitle()));
			this.raidBar.setProgress(1F);
		} else if(this.isLoss()) {
			this.raidBar.setName(this.raid.getRaidTitle().copy().append(" - ").append(this.raid.getLossTitle()));
			this.raidBar.setProgress(1F);
		}
	}

	/**
	 * player who is alive and in suitable range can be tracked.
	 */
	private Predicate<ServerPlayer> validPlayer() {
		return (player) -> {
			final int range = CRaidUtil.getRaidRange();
			return player.isAlive() && innerPlayer(player, range);
		};
	}

	private boolean innerPlayer(Player player, int range) {
		return Math.abs(player.getX() - this.center.getX()) < range
				&& Math.abs(player.getY() - this.center.getY()) < range
				&& Math.abs(player.getZ() - this.center.getZ()) < range;
	}

	/**
	 * {@link #tickBar()}
	 */
	protected void updatePlayers() {
		final Set<ServerPlayer> oldPlayers = Sets.newHashSet(this.raidBar.getPlayers());
		final Set<ServerPlayer> newPlayers = Sets.newHashSet(this.world.getPlayers(this.validPlayer()));

		/* add new join players */
		//进入袭击范围的人无法退出
		newPlayers.forEach(p -> {
			if(! oldPlayers.contains(p)) {
				this.raidBar.addPlayer(p);
			}
		});

		/* remove offline players */
//		oldPlayers.forEach(p -> {
//			if(! newPlayers.contains(p)) {
//
//				this.raidBar.removePlayer(p);
//			}
//		});

		/* add heroes */
		this.raidBar.getPlayers().forEach(p -> {
			this.heroes.add(p.getUUID());
		});

		this.heroes.forEach(uuid -> {
			Player player = this.world.getPlayerByUUID(uuid);
			if(player != null) {
				if (!player.isAlive()) {
					this.setStatus(Status.LOSS);//玩家死亡后将判定失败
				} else if (!innerPlayer(player, CRaidUtil.getRaidRange())) {
					CRaidUtil.sendMsgTo(player, RAID_TELEPORT);
					player.teleportTo(this.center.getX(), this.center.getY(), this.center.getZ());
					//当玩家远离袭击区域后，会强行将玩家拉到袭击中心
				}
			}
		});

		if(this.raidBar.getPlayers().isEmpty()){
			if(! this.isStopping()) {
				++ this.stopTick;
				this.heroes.forEach(uuid -> {
					Player player = this.world.getPlayerByUUID(uuid);
					if(player != null) {
						CRaidUtil.sendMsgTo(player, RAID_WARN);
					}
				});
			}
		} else {
			this.stopTick = 0;
		}
	}
	/**
	 * run when prepare time is not finished.
	 */
	protected void prepareState() {
		this.getPlayers().forEach(p -> CRaidUtil.playClientSound(p, this.raid.getPrepareSound()));
	}
	/**
	 * run when prepare time is finished.
	 */
	protected void waveStart() {
		this.tick = 0;
		this.status = Status.RUNNING;
		this.getPlayers().forEach(p -> CRaidUtil.playClientSound(p, this.raid.getStartWaveSound()));
	}

	/**
	 * check can start next wave or not.
	 */
	public boolean canNextWave() {
		return this.raiders.isEmpty();
	}

	/**
	 * {@link #tick()}
	 */
	protected void checkNextWave() {
		this.tick = 0;
		if(this.canNextWave()) {
			this.currentSpawn = 0;
			if(++ this.currentWave >= this.raid.getMaxWaveCount()) {
				this.status = Status.VICTORY;
			} else {
				this.status = Status.PREPARE;
			}
		} else {
			this.status = Status.LOSS;
		}
	}

	/**
	 * run when raid is not defeated.
	 */
	protected void onLoss() {
		this.tick = 0;
		this.getPlayers().forEach(p -> CRaidUtil.playClientSound(p, this.raid.getLossSound()));
		onPost();
		MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidLossEvent(this));
	}

	/**
	 * run when raid is defeated.
	 */
	protected void onVictory() {
		this.tick = 0;
		this.getPlayers().forEach(p -> {
			CRaidUtil.playClientSound(p, this.raid.getWinSound());
			RaidTrigger.INSTANCE.trigger(p, this.resource.toString());
		});
		if(! MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidWinEvent(this))) {
			this.getPlayers().forEach(p -> {
				this.raid.getRewards().forEach(r -> r.reward(p));
			});
			onPost();
		}
	}

	protected void onPost() {
		this.raid.getRewards().forEach(r -> r.rewardGlobally(world));
	}

	public void remove() {
		this.status = Status.REMOVING;
		this.raidBar.removeAllPlayers();
		this.raiders.forEach(e -> e.remove(Entity.RemovalReason.KILLED));
	}

	public int getId() {
		return this.id;
	}

	public BlockPos getCenter() {
		return this.center;
	}

	public boolean isRaider(Entity raider) {
		return this.raiders.contains(raider);
	}

	public boolean isStopping() {
		return this.stopTick > 0;
	}

	public boolean isPreparing() {
		return this.status == Status.PREPARE;
	}

	public boolean isRunning() {
		return this.status == Status.RUNNING;
	}

	public boolean isRemoving() {
		return this.status == Status.REMOVING;
	}

	public boolean isLoss() {
		return this.status == Status.LOSS;
	}

	public boolean isVictory() {
		return this.status == Status.VICTORY;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * get raid component by resource.
	 */
	public IRaidComponent getRaidComponent() {
		return this.raid != null ? this.raid : (this.raid = RaidManager.getRaidComponent(this.resource));
	}

	/**
	 * get tracked players by raid bar.
	 */
	public List<ServerPlayer> getPlayers(){
		return new ArrayList<>(this.raidBar.getPlayers());
	}

	public boolean hasTag(String tag) {
		return this.raid.hasTag(tag);
	}

	public List<String> getAuthors(){
		return this.raid.getAuthors();
	}

	public static enum Status {
		  PREPARE,
	      RUNNING,
	      VICTORY,
	      LOSS,
	      REMOVING;
	}

}
