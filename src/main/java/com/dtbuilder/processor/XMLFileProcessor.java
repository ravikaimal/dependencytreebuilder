package com.dtbuilder.processor;

import com.dtbuilder.artifacts.Artifact;

public abstract class XMLFileProcessor implements  DependencyFileProcessor{
    public abstract void processDepedencyData(Artifact parent, String content);
}
