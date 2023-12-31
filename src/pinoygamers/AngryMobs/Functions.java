package pinoygamers.AngryMobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import java.lang.Math;
import java.util.LinkedList;
import java.util.List;

/**
 * All functions that will be used in more than one class should be put
 * here for easy access and central management.
 * @author Tux2
 *
 */
public class Functions {
	
	/**
	 * Spawns a mob at the block specified.
	 * 
	 * @param spawnblock The specific block that you want the mob to spawn at
	 * @param type A string with any valid minecraft mob type
	 * @return True if spawn was successful, false if the spawn wasn't.
	 */
	public static LivingEntity spawnMob(Block spawnblock, String type) {
    	return spawnMob(spawnblock.getLocation(), type);
    }
	
	/**
	 * Spawns a mob at the location specified.
	 * 
	 * @param loc Location to spawn monster
	 * @param type A string with any valid minecraft mob type
	 * @return True if spawn was successful, false if the spawn wasn't.
	 */
	public static LivingEntity spawnMob(Location loc, String type) {
		boolean makeAngry = false;
    	type = properMonsterCase(type);
    	if(type.equalsIgnoreCase("AngryWolf")) {
    		type = "Wolf";
    		makeAngry = true;
    	}
    	CreatureType ct = CreatureType.fromName(type);
    	
    	if (ct == null) {
        	return null;
        }
    	
    	LivingEntity creature = loc.getWorld().spawnCreature(loc, ct);
    	
    	if(makeAngry) {
			Wolf wolf = (Wolf) creature;
			wolf.setAngry(true);
    	}
        return creature;
    }
	
	/**
	 * Spawns a mob ontop of a mob, ontop of a mob...
	 * @param loc Where you want to spawn this madness
	 * @param type The mobs you want to spawn from bottom up, seperated by "<". Example: "Skeleton<Giant<AngryWolf<Creeper"
	 * @return Returns an array of the LivingEntities spawned (and stacked).
	 */
	public static LivingEntity[] spawnStackedMob(Location loc, String type) {
		
		String[] types = type.split("<");
		LivingEntity[] mobs = new LivingEntity[types.length];
		mobs[0] = spawnMob(loc, types[0]);
		for (int i = 1; i < types.length; i++) {
			
			mobs[i] = spawnMob(loc, types[i]);
			mobs[i-1].setPassenger(mobs[i]);
		}
		return mobs;
	}
	
	/**
	 * Returns the string with the first letter capitalized, the rest lowercase.
	 * @param s The String to capitalize
	 * @return The capitalized string
	 */
    public static String capitalCase(String s)
    {
        return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
    }
    
    /**
     * Get the proper captialization of any monster type.
     * @param type Monster type
     * @return correct capitalization
     */
    public static String properMonsterCase(String type) {
		if (type.equalsIgnoreCase("PigZombie")) {
    		return "PigZombie";
    	}else if (type.equalsIgnoreCase("AngryWolf")) {
    		return "AngryWolf";
    	}else {
    		return capitalCase(type);
    	}
    }
    
    /**
     * Returns the distance between two points in a 3D world.
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return Distance between 1 and 2.
     */
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
    	double distance=0;

        distance=Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2)+Math.pow((z2-z1),2));
        return distance;
    }
    
    /**
     * Returns the distance between two locations. Assumes the locations
     * are in the same world.
     * @param a
     * @param b
     * @return
     */
    public static double distance(Location a, Location b) {
    	return distance(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
    }
    
    /**
     * Returns a random chunk from a given world.
     * @param w The world
     * @return
     */
    public static Chunk randomChunk(World w){
    	java.util.Random generator = new java.util.Random();
    	Chunk[] loadedChunks = w.getLoadedChunks(); // an array of loaded chunks
    	
    	return loadedChunks[generator.nextInt(loadedChunks.length)];
    }
    
    /**
     * Returns a random location, on the ground, from a given world, with no players within minDistance.
     * @param w the world
     * @param s the server
     * @param minDistance minimum distance to players for enemies to spawn at.
     * @return
     */
    public static Location randomGroundLocation(World w, Server s, int minDistance){
    	Location l = randomLocation(randomChunk(w));
    	while(playersInProximity(s, l, minDistance) && !safeSpawn(l.getBlock())){
    		l = randomLocation(randomChunk(w));
    		while(!isOnGround(l.getBlock())){
    			l.setY(l.getY()+1);
    		}
    	}
    	return l;
    }
    
    /**
     * Returns a random aerial location from a given world, with no players within minDistance.
     * @param w the world
     * @param s the server
     * @return
     */
    public static Location randomAirLocation(World w, Server s, int minDistance){
    	Location l = randomLocation(randomChunk(w));
    	while(playersInProximity(s, l, minDistance) && !safeSpawn(l.getBlock()) && !isAir(l.getBlock())){
    		l = randomLocation(randomChunk(w));
    	}
    	return l;
    }
    
    public static Block randomBigAirBlock(World w, Server s, int minDistance) {
    	return randomBigAirLocation(w, s, minDistance).getBlock();
    }
    
    /**
     * Returns a random aerial location from a given world that will fit a ghast, with no players within minDistance.
     * @param w the world
     * @param s the server
     * @return
     */
    public static Location randomBigAirLocation(World w, Server s, int minDistance){
    	Location l = randomLocation(randomChunk(w));
    	while(playersInProximity(s, l, minDistance) && !safeGhast(l.getBlock()) && !isAir(l.getBlock())){
    		l = randomLocation(randomChunk(w));
    	}
    	return l;
    }
    
    /**
     * Returns a random location from a given chunk.
     * @param c The chunk
     * @return Returns a random location
     */
    public static Location randomLocation(Chunk c){
    	java.util.Random generator = new java.util.Random();
    	double randX = (c.getX()*16) + 16 * generator.nextDouble();
    	double randZ = (c.getZ()*16) + 16 * generator.nextDouble();
    	double randY = 128 * generator.nextDouble();
    	
    	return new Location(c.getWorld(), randX, randY, randZ);
    }
    
    /**
     * Sees if there are any players in a proximity to a certain location
     * @param s Server we are checking
     * @param l The location are we checking
     * @param minDistance The radius from the radius to the players we are looking for
     * @return True if there were players, false if not.
     */
    public static boolean playersInProximity(Server s, Location l, int minDistance){
    	for(Player p : s.getOnlinePlayers()){
    		if(distance(l, p.getLocation())<minDistance){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Returns a random block from a given world.
     * @param w the world
     * @return
     */
    public static Block randomGroundBlock(World w, Server s, int minDistance){
    	Location l = randomLocation(randomChunk(w));
    	Block blockLocation = l.getBlock();
    	while(playersInProximity(s, l, minDistance) && !safeSpawn(blockLocation) && !isOnGround(blockLocation)){
    		l = randomLocation(randomChunk(w));
    		while(!isOnGround(blockLocation)){
    			l.setY(l.getY()+1);
    		}
    	}
    	return blockLocation;
    }
    
    /**
     * Returns a random aerial block from a given world.
     * @param w the world
     * @return
     */
    public static Block randomAirBlock(World w, Server s, int minDistance){
    	return randomAirLocation(w, s, minDistance).getBlock();
    }
    
    /**
     * Returns a random block, above ground, from a given chunk.
     * @param c The chunk
     * @return
     */
    public static Block randomBlock(Chunk c){
    	java.util.Random generator = new java.util.Random();
    	int randX = generator.nextInt(16);
    	int randZ = generator.nextInt(16);
    	int randY = generator.nextInt(128);
    	
    	return c.getBlock(randX, randY, randZ);
    }
    
    /**
     * Makes all monsters within the configured range of a player begin to attack him.
     * @param player The unlucky individual
     * @param range The dimensions of the safety box around the player.
     */
    public static void alertNearbyMonsters(Player player, int range) {
    	Entity[] theCrowd = (Entity[]) player.getNearbyEntities(range, range, range).toArray(); // Radius will be configurable later on
    	for (int i=0; i<theCrowd.length; i++) {
			if (theCrowd[i] instanceof Monster) {
				if(((Creature) theCrowd[i]).getTarget() == null) {
					((Creature) theCrowd[i]).setTarget(player);
				}
			}
    	}
    }
    
    /**
     * Changes the type of a certain monster
     * @param e1 The mob we want to change
     * @param mtype What are we going to change it to
     */
    public static void changeMob(Entity e1, String mtype) {
    	Location location = e1.getLocation();
    	e1.remove();
    	spawnMob(location, mtype);
    }
    
    /**
     * Changes all the mobs in the player's area from one type to another
     * @param player The unlucky individual
     * @param ctype Type you want all the mobs to change to.
     */
    public static void changeAllNearbyMobs(Player player, CreatureType ctype, int range) {
    	
    	Entity[] ents = (Entity[]) player.getNearbyEntities(range, range, range).toArray(); // "Nearby" will be the same as "nearby" for alertNearbyMonsters
    	
    	for (int i = 0; i < ents.length; i++) {
    		changeMob(ents[i], ctype.getName());
		}
	}
    
    /**
     * Changes all the mobs in a world from one type to another
     * @param w The world
     * @param ctype Type you want all the mobs to change to
     */
    public static void changeAllMobs(World w, CreatureType ctype) {
    	
    	Entity[] ents = (Entity[]) w.getEntities().toArray();
    	
    	for (int i = 0; i < ents.length; i++) {
			if(isCreatureType(ents[i], ctype)) {
				changeMob(ents[i], ctype.getName());
			}
		}
    }
    
    /**
     * To determine if an Entity is of a certain Creature type DOES NOT WORK WITH PLAYERS ONLY CREATURES 
     * @param e The entity we want to determine
     * @param c The creature type we want to compare
     * @return True if the entity is of the creature type, false if not
     */
    public static boolean isCreatureType(Entity e, CreatureType c) {
    	switch(c) {
    		case CHICKEN: return(e instanceof Chicken);
    		case COW: return(e instanceof Cow);
    		case GHAST: return(e instanceof Ghast);
    		case GIANT: return(e instanceof Giant);
    		case MONSTER: return(e instanceof Monster);
    		case PIG: return(e instanceof Pig);
    		case PIG_ZOMBIE: return(e instanceof PigZombie);
    		case SHEEP: return(e instanceof Sheep);
    		case SKELETON: return(e instanceof Skeleton);
    		case SLIME: return(e instanceof Slime);
    		case SPIDER: return(e instanceof Spider);
    		case SQUID: return(e instanceof Squid);
    		case WOLF: return(e instanceof Wolf);
    		case ZOMBIE: return(e instanceof Zombie);
    	}
		return false;
    }
    
    /**
     * Returns whether or not a spot is big enough for a monster to spawn on.
     * @param theBlock The block which you're checking is safe or not.
     * @return True if the spawn isn't blocked.
     */
    public static Boolean safeSpawn(Block theBlock) {
		if (!isAir(theBlock.getRelative(BlockFace.NORTH))) {
			return false;
		}
		if (!isAir(theBlock.getRelative(BlockFace.EAST))) {
			return false;
		}
		if (!isAir(theBlock.getRelative(BlockFace.SOUTH))) {
			return false;
		}
		if (!isAir(theBlock.getRelative(BlockFace.WEST))) {
			return false;
		}
		if (!isAir(theBlock.getRelative(BlockFace.UP))) {
			return false;
		}
		/*
		if (isAir(theBlock.getRelative(BlockFace.DOWN))) {
			isSafe = false;
		}
		*/
		return true;
	}
    
    /**
     * Checks if the block is air.
     * @param theBlock The block which you're checking.
     * @return True if the block consists of air.
     */
    public static boolean isAir(Block theBlock) {
    	if (theBlock.getType() == Material.AIR) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Checks if the block is water.
     * @param theBlock The block which you're checking.
     * @return True if the block consists of water.
     */
    public static boolean isWater(Block theBlock) {
    	if (theBlock.getTypeId() == 8 || theBlock.getTypeId() == 9) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Checks if the block is lava.
     * @param theBlock The block which you're checking.
     * @return True if the block consists of lava.
     */
    public static boolean isLava(Block theBlock) {
    	if (theBlock.getTypeId() == 10 || theBlock.getTypeId() == 11) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Checks if the block is fire.
     * @param theBlock The block which you're checking.
     * @return True if the block consists of exothermic energy from a combustion reaction.
     */
    public static boolean isFire(Block theBlock) {
    	if (theBlock.getTypeId() == 51) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public static boolean isNight(long time) {
    	return time > 12000;
    }

    /**
     * Checks to see if the light level of a block is below a certain number
     * @param theBlock The block we are checking
     * @param lightLevel The maximum light level
     * @return 
     */
    public static boolean isLowerThanLightLevel(Block theBlock, int lightLevel) {
    	return lightLevel > theBlock.getLightLevel();
    }
    
    /**
     * Checks to see if the air block is right above a regular block
     * @param block Block we're checking
     * @return True if the block is right above the ground, false if not.
     */
    public static boolean isOnGround(Block block) {
    	Block downBlock = block.getRelative(BlockFace.DOWN);
    	if(isAir(block)){
        	if(isAir(downBlock) || isWater(downBlock) || isLava(downBlock) || isFire(downBlock)) {
        		return false;
        	} else {
        		return true;
        	}
    	} else {
    		return false;
    	}

    }
    
    /**
     * Sees if block area is safe for ghast
     * @param block The block area
     * @return True if its safe, false if not.
     */
    public static boolean safeGhast(Block block) {
    	boolean isItSafe = true;
    	for (int x=-2; x<3; x++) {
   			for (int y=-2; y<5; y++) {
   				for (int z=-2; z<5; z++) {
   					Block currentBlock = block.getRelative(x, y, z);
   					if(!isAir(currentBlock)) {
   						isItSafe = false;
   					}
   				}
   	    	}
       	}
    	return isItSafe;
   	}
    
    /**
     * Sees if block area is safe for giant
     * @param block The block area
     * @return True if its safe, false if not.
     */
    public static boolean safeGiant(Block block) {
    	boolean isItSafe = true;
    	for (int x=-2; x<3; x++) {
   			for (int y=-2; y<3; y++) {
   				for (int z=-6; z<6; z++) {
   					Block currentBlock = block.getRelative(x, y, z);
   					if(!isAir(currentBlock)) {
   						isItSafe = false;
   					}
   				}
   	    	}
       	}
    	return isItSafe;
    }
    
    
    /**
     * Gets an entity from its ID
     * @param id The ID of the entity you are trying to obtain
     * @param world The world to get the entity from
     * @return The entity with the given ID. If no entity in the world has that ID, then returns null.
     */
    public static Entity getEntityFromID(int id, World world) {
    	Entity[] worldEntities = (Entity[]) world.getEntities().toArray();
    	for (int i=0; i<worldEntities.length; i++) {
    		if (worldEntities[i].getEntityId() == id) {
    			return worldEntities[i];
    		}
    	}
    	return null;
    }
    
    /**
     * Turns in an array into a string
     * @param objects The array to turn into a string
     * @param seperator A string that seperates each item in the array
     * @return The array in string form
     */
    public static String arrayToString(Object[] objects, String separator) {
    	String toReturn = "";
    	for (int i = 0; i < objects.length; i++) {
    		if (i == 0) {
    			toReturn = objects[i].toString();
    		} else {
    			toReturn = toReturn + separator + objects[i].toString();
    		}
		}
    	return toReturn;
    }
    
    /**
     * Gets the CreatureType of an Entity
     * @param e The entity that we are checking for
     * @return Returns the CreatureType of the Entity
     */
    public static CreatureType getCreatureType(LivingEntity e) {
    	CreatureType toReturn = null;
    	for (CreatureType c : CreatureType.values()) {
			if(isCreatureType(e, c)) {
				toReturn = c;
				break;
			}
		}
    	return toReturn;
    }
    
    /**
     * Changes the specified entities health by a certain number
     * @param e The entity
     * @param value How much we want to add or subtract to the entities health
     * @return What the health was changed to.
     */
    public static int changeHealth(LivingEntity e, int value) {
    	int changedHealth = e.getHealth() + value;
    	e.setHealth(changedHealth);
    	return changedHealth;
    	
    }
    
   /**
    * Changes the speed of any given entity
    * @param e The entity
    * @param multiplier What we're multiplying the speed by.
    */
   public static void changeSpeed(LivingEntity e, double multiplier) {
	   
	   Vector v = e.getVelocity();
	   
	   v.setX(v.getX() * multiplier);
	   v.setY(v.getY() * multiplier);
	   v.setZ(v.getZ() * multiplier);
	   
	   e.setVelocity(v);
   }
   
   /**
    * Checks to see if Entity is part of a stacked mob
    * @param e The entity
    * @return True if the entity is part of a stacked mob, false otherwise.
    */
   public static boolean isStackedMob(LivingEntity e) {
	   
	   if(e.getPassenger().isEmpty()) return false;
	   if(e.isInsideVehicle()) return false;
	   return true;
   }

}
