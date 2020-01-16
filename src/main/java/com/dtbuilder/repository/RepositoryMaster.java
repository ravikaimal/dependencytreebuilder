package com.dtbuilder.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RepositoryMaster {
    private List<Repository> repositories;
    private static RepositoryMaster repositoryMaster;

    public RepositoryMaster(){
        repositories = new ArrayList<>();
        Repository repository = new Repository();
        repository.setUrl("https://repo.maven.apache.org/maven2");
        repository.setName("Central Repository");
        repository.setId("central");
        repositories.add(repository);
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void addRepository(Repository repository) {
        this.repositories.add(repository);
    }

}
