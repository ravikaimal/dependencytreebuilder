package com.dtbuilder.artifacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Maven artifact with its Group Id, Artifact Id and Version number.
 */
public class Artifact {
    private String groupId;
    private String artifactId;
    private String version;
    /*
     * List of dependencies of the artifact.
     */
    private List<Artifact> dependencies;

    public Artifact(){

    }

    /**
     * @return Group ID
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the Group ID
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return Artifact ID
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Sets the Artifact ID
     * @param artifactId
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return Artifact Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Add an artifact to the list of dependencies
     * @param artifact
     */
    public synchronized void addDependency(Artifact artifact){
        if(dependencies == null){
            dependencies = new ArrayList<>();
        }
        dependencies.add(artifact);
    }

    /**
     * @return list of dependencies
     */
    public List<Artifact> getDependencies(){
        return dependencies;
    }

    /**
     * @return Duplicate copy of the current artifact
     */
    public Artifact clone()  {
        Artifact _artifact = new Artifact();
        _artifact.setVersion(this.version);
        _artifact.setArtifactId(this.artifactId);
        _artifact.setGroupId(this.groupId);

        return _artifact;
    }
}
