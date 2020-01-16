package com.dtbuilder.processor;

import com.dtbuilder.artifacts.Artifact;

/**
 * Interace to process incoming files
 */
public interface DependencyFileProcessor {
    public void processDepedencyData(Artifact parent, String content) ;
    public void init();
}
