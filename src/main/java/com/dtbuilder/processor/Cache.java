package com.dtbuilder.processor;

import com.dtbuilder.artifacts.Artifact;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache to store artifacts.
 * The same artifact will not be looked up twice.
 */
public class Cache {
    /**
     * Maps an artifact to its Group ID, Artifact ID and version
     */
    private Map<String, Artifact> artifactMap;
    private static Cache cache;

    private Cache(){
        artifactMap = new HashMap<>();
    }

    /**
     * Stores the artifact in the map against the key
     * @param key
     * @param artifact
     */
    public void put(String key, Artifact artifact){
        artifactMap.put(key,artifact);
    }

    /**
     * Check if the artifact is already present in the map
     * @param key
     * @return
     */
    public boolean contains(String key){
        return artifactMap.containsKey(key);
    }

    /**
     *
     * @param key
     * @return Artifact corresponding to the key
     */
    public Artifact get(String key){
        return artifactMap.get(key);
    }

    /**
     *
     * @return The instance of this singleton class
     */
    public static Cache getInstance(){
        if(cache == null) {
            synchronized (Cache.class) {
                if(cache == null){
                    cache = new Cache();
                }
            }
        }
        return cache;
    }
}
