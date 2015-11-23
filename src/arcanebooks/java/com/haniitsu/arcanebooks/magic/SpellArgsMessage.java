package com.haniitsu.arcanebooks.magic;

/**
 * A message to be passed through a SpellArgs object from one spell effect definition onto any future one that's
 * looking for any messages with the same name.
 */
public class SpellArgsMessage
{
    /**
     * Creates a message with the passed name.
     * @param name The name of the message.
     */
    public SpellArgsMessage(String name)
    { this.name = name.trim().toLowerCase(); }
    
    /** The name of the message */
    final String name;
    
    /**
     * Gets the name of the message.
     * @return The name.
     */
    public String getName()
    { return name; }
}