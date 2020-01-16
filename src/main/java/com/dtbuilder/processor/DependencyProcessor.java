package com.dtbuilder.processor;

import com.dtbuilder.artifacts.Artifact;
import com.dtbuilder.repository.Repository;
import com.dtbuilder.repository.RepositoryMaster;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

@Component("dependencyProcessor")
public class DependencyProcessor {
    private static Log logger = LogFactory.getLog(DependencyProcessor.class);

    @Autowired
    private RepositoryMaster repositoryMaster;
    @Autowired
    private DependencyFileProcessor pomFileProcessor;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);

    private enum Status{
        RUNNING,
        STOPPED
    }

    private Status status;


    public void start(){
        if(status != Status.RUNNING) {
            status = Status.RUNNING;
        }
    }

    public void stop(){
        if(status == Status.RUNNING) {
            status = Status.STOPPED;
        }
    }

    /**
     *
     * @return Count of currently active threads.
     */
    public int getActiveCount(){
        return executor.getActiveCount();
    }

    /**
     * Enqueue an artifact
     * @param artifact
     */
    public void enqueue(Artifact artifact){
        Future<String> pom = executor.submit(new RemotePOMReader(artifact));
        try {
            if(!StringUtils.isEmpty(pom.get())){
                pomFileProcessor.processDepedencyData(artifact,pom.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Looks up and read the remote POM file corresponding to an artifact.
     */
    private class RemotePOMReader implements Callable<String>{

        private Artifact artifact;

        public RemotePOMReader(Artifact artifact){
            this.artifact = artifact;
        }

        @Override
        public String call() {
            return readRemotePOM(artifact);
        }

        /**
         *
         * @param artifact
         * @return remote POM file corresponding to the artifact.
         */
        public String readRemotePOM(Artifact artifact){
            List<Repository> list = repositoryMaster.getRepositories();

            //If the repository tag is not processed yet, there is nothing much to do right now.
            if(list == null || list.isEmpty()){
                return null;
            }
            logger.info("Checking artifact "+String.format("[%s,%s,%s]",artifact.getArtifactId(),artifact.getGroupId()
                    ,artifact.getVersion()));
            for(Repository repository : list){
                logger.info("Checking repository "+String.format("[%s,%s]",repository.getName(),repository.getUrl()));

                String finalUrl =  generatePOMURL(artifact,repository);
                logger.info("Final URL "+finalUrl);
                String pom = readPOMContent(finalUrl);
                return pom;
            }

            return null;
        }

        private String generatePOMURL(Artifact artifact, Repository repository){
            if(artifact.getVersion() == null){
                String url = new StringBuilder().append(repository.getUrl()).append("/")
                        .append(StringUtils.replace(artifact.getGroupId(),".","/")).append("/")
                        .append(artifact.getArtifactId()).append("/").toString();
                Document doc = null ;
                try {
                    doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    String version = null;
                    List<Element> versions = new ArrayList<>();

                    if(links != null){
                        int size = links.size();
                        for(int i = size-1 ; i>=0 ; i--){
                            Element element = links.get(i);
                            if(element.hasText() && element.text().endsWith("/")){
                                versions.add(links.get(i));
                                break;
                            }
                        }
                        for(int i = 0 ; i < size  ; i++){
                            Element element = links.get(i);
                            if(element.hasText() && element.text().endsWith("/")){
                                versions.add(links.get(i));
                                break;
                            }
                        }
                        Collections.sort(versions, Comparator.comparing(Element::text));
                    }
                    version = versions.get(versions.size()-1).text();
                    url = url + version +  artifact.getArtifactId() + "-"+ version.substring(0,version.length()-1) + ".pom";
                    logger.info(version);
                    return url;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                return new StringBuilder().append(repository.getUrl()).append("/")
                        .append(StringUtils.replace(artifact.getGroupId(),".","/")).append("/")
                        .append(artifact.getArtifactId()).append("/").append(artifact.getVersion()).append("/")
                        .append(artifact.getArtifactId()).append("-").append(artifact.getVersion()).append(".pom").toString() ;
            }

            return null;
        }

        private String readPOMContent(String _url) {
            URL url = null;
            ReadableByteChannel readableByteChannel = null;
            StringBuilder data = new StringBuilder();
            try {
                url = new URL(_url);
                readableByteChannel = Channels.newChannel(url.openStream());
                int size = 128;
                ByteBuffer buffer = ByteBuffer.allocate(size);
                int bytes = readableByteChannel.read(buffer);

                while(bytes != -1){
                    buffer.flip();

                    while(buffer.hasRemaining()){
                        data.append((char)buffer.get());
                    }

                    buffer.clear();
                    bytes = readableByteChannel.read(buffer);
                }
            } catch (MalformedURLException e) {
                logger.error(String.format("Invalid URL [%s]",url));
            } catch (IOException e) {
                logger.error(String.format("Not able to retrieve dependency information for the URL [%s]",url));
            }
            return data.toString();
        }
    }

}
