package com.haniitsu.arcanebooks.magic;

import com.haniitsu.arcanebooks.magic.caster.SpellCaster;
import com.haniitsu.arcanebooks.magic.caster.SpellCasterBlock;
import com.haniitsu.arcanebooks.magic.caster.SpellCasterEntity;
import com.haniitsu.arcanebooks.magic.modifiers.effect.AOE;
import com.haniitsu.arcanebooks.magic.modifiers.effect.AOEShape;
import com.haniitsu.arcanebooks.magic.modifiers.effect.AOESize;
import com.haniitsu.arcanebooks.magic.modifiers.effect.SpellEffectModifier;
import com.haniitsu.arcanebooks.magic.modifiers.effect.SpellStrength;
import com.haniitsu.arcanebooks.magic.modifiers.effect.SpellTarget;
import com.haniitsu.arcanebooks.misc.BlockLocation;
import com.haniitsu.arcanebooks.misc.Direction;
import com.haniitsu.arcanebooks.misc.Location;
import com.haniitsu.arcanebooks.misc.UtilMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;

// This is the class that should be contained in signed spellbooks and scrolls.

/**
 * A spell, as contained within a spell-book or scroll. Contains the spell phrases to be cast, each containing spell
 * effects and their effect modifiers.
 */
public class Spell
{
    /**
     * A spell effect, usually configured with modifiers as written into spell book or scroll.
     */
    public static class Phrase
    {
        /**
         * Creates an spell phrase from the spell effect and any possible modifiers.
         * @param effect The main spell effect to be performed.
         * @param modifiers The spell effect modifiers to be passed into the spell effect.
         */
        public Phrase(SpellEffect effect, List<? extends SpellEffectModifier> modifiers)
        {
            List<SpellEffect> effects = new ArrayList<SpellEffect>();
            effects.add(effect);
            this.possibleSpellEffects = Collections.unmodifiableList(effects);
            this.modifiers = Collections.unmodifiableList(new ArrayList<SpellEffectModifier>(modifiers));
        }
        
        /**
         * Creates an spell phrase from the spell effect and any possible modifiers.
         * @param effect The main spell effect to be performed.
         * @param modifiers The spell effect modifiers to be passed into the spell effect.
         */
        public Phrase(SpellEffect effect, SpellEffectModifier... modifiers)
        { this(effect, Arrays.asList(modifiers)); }
        
        public Phrase(List<? extends SpellEffect> effects, List<? extends SpellEffectModifier> modifiers)
        {
            this.possibleSpellEffects = Collections.unmodifiableList(new ArrayList<SpellEffect>(effects));
            this.modifiers = Collections.unmodifiableList(new ArrayList<SpellEffectModifier>(modifiers));
        }
        
        public Phrase(List<? extends SpellEffect> effects, SpellEffectModifier... modifiers)
        { this(effects, Arrays.asList(modifiers)); }
        
        public Phrase(SpellEffect[] effects, List<? extends SpellEffectModifier> modifiers)
        { this(Arrays.asList(effects), modifiers); }
        
        public Phrase(SpellEffect[] effects, SpellEffectModifier... modifiers)
        { this(Arrays.asList(effects), Arrays.asList(modifiers)); }
        
        /** The list of spell effects this phrase could possible invoke. */
        protected final List<SpellEffect> possibleSpellEffects;
        
        /** The modifiers to be passed into the spell effect. */
        protected final List<SpellEffectModifier> modifiers;
        
        // Caches
        private List<AOE>           possibleAOEs      = null;
        private List<AOEShape>      possibleShapes    = null;
        private List<AOESize>       possibleSizes     = null;
        private List<SpellStrength> possibleStrengths = null;
        private List<SpellTarget>   possibleTargets   = null;
        
        /**
         * Gets the spell effects this spell phrase can possibly invoke.
         * @return An unreadable list containing the spell effects this spell phrase can possibly invoke.
         */
        public List<SpellEffect> getPossibleSpellEffect()
        { return possibleSpellEffects; }
        
        public List<SpellEffectModifier> getModifiers()
        { return modifiers; }
        
        public List<AOE> getPossibleAOEs()
        {
            possibleAOEs = initialiseCachedList(AOE.class, possibleAOEs);
            return possibleAOEs;
        }
        
        public List<AOEShape> getPossibleShapes()
        {
            possibleShapes = initialiseCachedList(AOEShape.class, possibleShapes);
            return possibleShapes;
        }
        
        public List<AOESize> getPossibleSizes()
        {
            possibleSizes = initialiseCachedList(AOESize.class, possibleSizes);
            return possibleSizes;
        }
        
        public List<SpellStrength> getPossibleStrengths()
        {
            possibleStrengths = initialiseCachedList(SpellStrength.class, possibleStrengths);
            return possibleStrengths;
        }
        
        public List<SpellTarget> getPossibleTargets()
        {
            possibleTargets = initialiseCachedList(SpellTarget.class, possibleTargets);
            return possibleTargets;
        }
        
        private <T extends SpellEffectModifier> List<T> initialiseCachedList(
                Class<T> modifierType, List<T> cache)
        {
            List<T> list = cache;
            
            if(list == null)
            {
                list = new ArrayList<T>();
                
                for(SpellEffectModifier modifier : modifiers)
                    if(modifier.getClass() == modifierType)
                        list.add((T)modifier);
                
                list = Collections.unmodifiableList(list);
            }
            
            return list;
        }
        
        public void burst(SpellCast cast, BlockLocation blockHit, Location burstLocation, Direction burstDirection, SpellTarget target)
        { this.burst(cast, blockHit, null, burstLocation, burstDirection, target); }
        
        public void burst(SpellCast cast, Entity entityHit, Location burstLocation, Direction burstDirection, SpellTarget target)
        { this.burst(cast, null, entityHit, burstLocation, burstDirection, target); }
        
        public void burst(SpellCast cast,         BlockLocation blockHit,   Entity entityHit,
                          Location burstLocation, Direction burstDirection, SpellTarget target)
        {
            if(possibleSpellEffects.isEmpty())
                return;
            
            Random        rand     = new Random();
            SpellEffect   effect   = possibleSpellEffects.get(rand.nextInt(possibleSpellEffects.size()));
            AOE           aoe      = possibleAOEs     .isEmpty() ? AOE          .defaultValue : possibleAOEs     .get(rand.nextInt(possibleAOEs     .size()));
            AOESize       aoeSize  = possibleSizes    .isEmpty() ? AOESize      .defaultValue : possibleSizes    .get(rand.nextInt(possibleSizes    .size()));
            AOEShape      aoeShape = possibleShapes   .isEmpty() ? AOEShape     .defaultValue : possibleShapes   .get(rand.nextInt(possibleSizes    .size()));
            SpellStrength strength = possibleStrengths.isEmpty() ? SpellStrength.defaultValue : possibleStrengths.get(rand.nextInt(possibleStrengths.size()));
            
            Collection<Entity>        affectedEntities = new HashSet<Entity>();
            Collection<BlockLocation> affectedBlocks   = new HashSet<BlockLocation>();
            
            if(aoe == AOE.targetOnly)
            {
                if(entityHit != null)
                    affectedEntities.add(entityHit);
                
                if(blockHit != null)
                    affectedBlocks.add(blockHit);
            }
            else if(aoe == AOE.aroundTarget || aoe == AOE.targetAndAroundTarget)
            {
                affectedEntities.addAll(aoeShape.getEntitiesInRange(aoeSize.getDistance()*aoeShape.getAOESizeModifier(), burstLocation, burstDirection));
                affectedBlocks  .addAll(aoeShape.getBlocksInRange  (aoeSize.getDistance()*aoeShape.getAOESizeModifier(), burstLocation, burstDirection));
                
                if(aoe == AOE.aroundTarget)
                {
                    if(entityHit != null)
                        affectedEntities.remove(entityHit);
                    
                    if(blockHit != null)
                        affectedBlocks.remove(blockHit);
                }
                else if(aoe == AOE.targetAndAroundTarget)
                {
                    if(entityHit != null)
                        affectedEntities.add(entityHit);
                    
                    if(blockHit != null)
                        affectedBlocks.add(blockHit);
                }
            }
            
            SpellArgs args = new SpellArgs(effect, cast.getCaster(), cast, modifiers,
                                           burstLocation, burstDirection,
                                           aoe, aoeSize, aoeShape, strength, target,
                                           affectedBlocks, affectedEntities, blockHit, entityHit);
            
            cast.addSpellArgs(args);
            effect.performEffect(args);
        }
    }
    
    /**
     * Representation of a single instance of a spell being cast. Serves as the "arguments" class of an entire spell
     * cast.
     */
    public static class SpellCast
    {
        /**
         * Creates a new instance.
         * @param spell The spell that is being cast.
         * @param caster The caster casting the spell.
         * @param location The location where the spell is being cast.
         * @param direction The direction the spell is be cast in.
         */
        public SpellCast(Spell spell, SpellCaster caster, Location location, Direction direction)
        {
            this.spell     = spell;
            this.caster    = caster;
            this.location  = location;
            this.direction = direction;
        }
        
        /** The spell that this is a casting of. */
        final Spell spell;
        
        /** The caster that cast the spell. */
        final SpellCaster caster;
        
        /**
         * The location where the spell was cast. Note that this isn't where the spell effect was burst, but where it
         * was initially cast. e.g. if a player cast the spell, the location of the plate.
         */
        final Location location;
        
        /**
         * The direction the spell was cast in. e.g. if a player cast the spell, the direction the player was facing.
         * (usually)
         */
        final Direction direction;
        
        /**
         * The individual phrase casts caused by this spell cast. This will change as the phrases are cast, and will
         * only ever contain the SpellArgs objects for the phrases already cast.
         */
        final List<SpellArgs> phrasesCast = new ArrayList<SpellArgs>();
        
        /** The phrases being cast that are fired as projectiles. */
        final List<Phrase> projectilePhrases = new ArrayList<Phrase>();
        
        final Map<String, SpellMessage> messages = new HashMap<String, SpellMessage>();
        
        /**
         * Adds a spell phrase's spell args object. i.e. the representation of the phrase being cast.
         * @param args The spell args object to add.
         */
        void addSpellArgs(SpellArgs args)
        { phrasesCast.add(args); }
        
        /**
         * Passes on a message which will be accessible to later spell effect definitions via .getMessage(string);
         * @param message The message to pass.
         * @return The message already present with the same name, or null if none was present. An object (as opposed to
         * null) being returned means the operation failed, as there was already a message with that name.
         */
        public SpellMessage passMessage(SpellMessage message)
        { return passMessage(message, false); }

        /**
         * Passes on a message which will be accessible to later spell effect definitions via .getMessage(string);
         * @param message The message to pass.
         * @param force Whether or not to overwrite any messages already present with the same name.
         * @return The message already present with the same name, or null if none was present. If the message isn't
         * forced, then this will be null if the operation succeeded, or another value otherwise.
         */
        public SpellMessage passMessage(SpellMessage message, boolean force)
        {
            if(force)
                return messages.put(message.getName(), message);

            SpellMessage oldMessage = messages.get(message.getName());

            if(oldMessage == null)
                messages.put(message.getName(), message);

            return oldMessage;
        }
        
        /**
         * Gets the caster that cast the spell.
         * @return The caster that cast the spell.
         */
        public SpellCaster getCaster()
        { return caster; }
        
        /**
         * Gets the spell that was cast.
         * @return The spell that was cast.
         */
        public Spell getSpell()
        { return spell; }
        
        /**
         * Gets the location where the spell was cast.
         * @return The location where the spell was cast.
         */
        public Location getLocation()
        { return location; }
        
        /**
         * Gets the direction that spell was cast in.
         * @return The direction the spell was cast in.
         */
        public Direction getDirection()
        { return direction; }
        
        /**
         * Gets all of the spell args objects for phrases that have already been cast.
         * @return The spell args objects already cast, in order of when they were cast. (first earliest)
         */
        public List<SpellArgs> getSpellArgsCast()
        { return new ArrayList<SpellArgs>(phrasesCast); }
        
        /**
         * Gets all of the spell phrases being cast that target via projectile.
         * @return The projectile spell phrases being cast, in order of when they'll be/they were cast upon bursting.
         */
        public List<Phrase> getProjectilePhrases()
        { return new ArrayList<Phrase>(projectilePhrases); }
        
        /**
         * Gets any previously passed message with the passed name, or null if none exists.
         * @param name The name of the message to get.
         * @return Any previously passed message with the passed name, or null if none exists.
         */
        public SpellMessage getMessage(String name)
        { return messages.get(name); }
        
        /**
         * Specifies that a spell phrase should be treated, for the purposes of this specific spell cast, as targeting
         * via projectile.
         * @param phrase The projectile phrase to demark as such.
         */
        public void markAsProjectilePhrase(Phrase phrase)
        { projectilePhrases.add(phrase); }
    }
    
    /**
     * Creates an instance of a spell.
     * @param phrases The phrases that should make up the spell.
     */
    public Spell(List<? extends Phrase> phrases)
    { this.phrases = Collections.unmodifiableList(new ArrayList<Phrase>(phrases)); }
    
    /**
     * Creates an instance of a spell.
     * @param phrases The phrases that should make up the spell.
     */
    public Spell(Phrase... phrases)
    { this(Arrays.asList(phrases)); }
    
    /** The phrases that make up the spell. That is, each set of spell effect(s) and spell effect modifier(s). */
    protected final List<Phrase> phrases;
    
    public List<Phrase> getPhrases()
    { return phrases; }
    
    /**
     * Performs the spell. That is, performs all of the spell phrases that are part of the spell sequentially.
     * @param caster 
     */
    public void cast(SpellCaster caster)
    {
        SpellCast spellCast = new SpellCast(this, caster, caster.getLocation(), caster.getDirection());
        List<Phrase> projectilePhrases = new ArrayList<Phrase>();
        
        for(Phrase phrase : phrases)
        {
            SpellTarget currentTarget = UtilMethods.getRandomMember(phrase.getPossibleTargets());
            
            if(currentTarget == SpellTarget.projectile)
                projectilePhrases.add(phrase);
            else
            {
                if(caster instanceof SpellCasterEntity)
                    phrase.burst(spellCast, ((SpellCasterEntity)caster).getCasterEntity(), caster.getLocation(), caster.getDirection(), currentTarget);
                else if(caster instanceof SpellCasterBlock)
                    phrase.burst(spellCast, ((SpellCasterBlock)caster).getBlockLocation(), caster.getLocation(), caster.getDirection(), currentTarget);
            }
                //phrase.burst(spellCast, caster.getLocation(), caster.getDirection(), currentTarget);
        }
        
        if(!projectilePhrases.isEmpty())
            caster.launchSpellPhrases(spellCast, projectilePhrases);
    }
}