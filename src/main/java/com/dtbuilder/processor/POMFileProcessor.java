package com.dtbuilder.processor;

import com.dtbuilder.artifacts.Artifact;
import com.dtbuilder.repository.Repository;
import com.dtbuilder.repository.RepositoryMaster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

public class POMFileProcessor extends XMLFileProcessor {
    private static Log logger = LogFactory.getLog(DependencyProcessor.class);
    @Autowired
    private DependencyProcessor processor;
    @Autowired
    private RepositoryMaster repositoryMaster;

    public POMFileProcessor(){
//        processor = DependencyProcessor.getInstance();

//        repositoryMaster = RepositoryMaster.getInstance();
    }

    public void init(){
//        processor.start();
        logger.info("Bean init method called.");
    }

    public void processDepedencyData(Artifact parent, String content) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(new StringReader(content));
            Repository repository = null;
            if(reader != null){
                while (reader.hasNext()){
                    int eventType = reader.next();
                    switch (eventType){
                        case XMLStreamReader.START_ELEMENT:
                            if("dependencies".equals(reader.getLocalName())){
                                processDepedencies(parent,reader);
                            }else if("repositories".equals(reader.getLocalName())){
                                processRepositories(reader);
                            }else if("groupId".equals(reader.getLocalName())){
                                String groupId = readCharacters(reader);
                                parent.setGroupId(groupId);
                            }else if("plugins".equals(reader.getLocalName())){
                                processPlugins(reader);
                            }else if("extensions".equals(reader.getLocalName())){
                                processExtensions(reader);
                            }else if("artifactId".equals(reader.getLocalName())){
                                String artifactId = readCharacters(reader);
                                parent.setArtifactId(artifactId);
                            }else if("version".equals(reader.getLocalName())){
                                String version = readCharacters(reader);
                                parent.setVersion(version);
                            }
                            break;
                        }
                    }
                }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private void processExtensions(XMLStreamReader reader) throws XMLStreamException{
        while (reader.hasNext()){
            int eventType = reader.next();
            switch (eventType){
                case XMLStreamReader.START_ELEMENT:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if("extensions".equals(reader.getLocalName())){
                        return;
                    }
            }
        }
    }

    private void processPlugins(XMLStreamReader reader) throws XMLStreamException{
        while (reader.hasNext()){
            int eventType = reader.next();
            switch (eventType){
                case XMLStreamReader.START_ELEMENT:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if("plugins".equals(reader.getLocalName())){
                        return;
                    }
            }
        }
    }
    private void processRepositories(XMLStreamReader reader) throws XMLStreamException{
        Repository repository = null;
        while(reader.hasNext()){
            int eventType = reader.next();
            switch (eventType){
                case XMLStreamReader.START_ELEMENT:
                    if("repository".equals(reader.getLocalName())){
                        repository = new Repository();
                    }else if("id".equals(reader.getLocalName())){
                        String id = readCharacters(reader);
                        repository.setId(id);
                    }else if("name".equals(reader.getLocalName())){
                        String name = readCharacters(reader);
                        repository.setName(name);
                    }else if("url".equals(reader.getLocalName())){
                        String url = readCharacters(reader);
                        repository.setUrl(url);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if("repository".equals(reader.getLocalName())) {
                        repositoryMaster.addRepository(repository);
                    }else if("repositories".equals(reader.getLocalName())){
                        return;
                    }
                    break;
            }
        }
    }

    private void processDepedencies(Artifact parent, XMLStreamReader reader) throws XMLStreamException {
        Artifact artifact = null;

        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    if ("dependency".equals(reader.getLocalName())) {
                        artifact = new Artifact();
                    } else if ("groupId".equals(reader.getLocalName())) {
                        String groupId = readCharacters(reader);
                        artifact.setGroupId(groupId);
                    } else if ("artifactId".equals(reader.getLocalName())) {
                        String artifactId = readCharacters(reader);
                        artifact.setArtifactId(artifactId);
                    } else if ("version".equals(reader.getLocalName())) {
                        String version = readCharacters(reader);
                        artifact.setVersion(version);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if ("dependency".equals(reader.getLocalName())) {
                        String key = artifact.getGroupId() + artifact.getArtifactId() + artifact.getVersion();
                        if (!Cache.getInstance().contains(key)) {
                            Cache.getInstance().put(key, artifact);
                            parent.addDependency(artifact);
                            processor.enqueue(artifact);
                        }else{
                            parent.addDependency((Artifact) Cache.getInstance().get(key).clone());
                        }
                    }else if("dependencies".equals(reader.getLocalName())){
                        return;
                    }
                    break;
            }
        }

    }

    private String readCharacters(XMLStreamReader reader) throws XMLStreamException{
        while (reader.hasNext()){
            int eventType = reader.next();
            switch (eventType){
                case XMLStreamReader.CHARACTERS:
                    return reader.getText();
            }
        }
        return null;
    }


}
