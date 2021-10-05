package eu.ebrains.kg.search.controller.mergers;

import eu.ebrains.kg.search.model.target.elasticsearch.instances.Project;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProjectMerger extends Merger<Project>{

    protected void merge(Project parent, Project child){
        if(child.getDataset()!=null){
            if(parent.getDataset()==null){
                parent.setDataset(new ArrayList<>());
            }
            parent.getDataset().addAll(child.getDataset().stream().filter(d -> !parent.getDataset().contains(d)).collect(Collectors.toList()));
        }
        if(child.getPublications()!=null){
            if(parent.getPublications()==null){
                parent.setPublications(new ArrayList<>());
            }
            parent.getPublications().addAll(child.getPublications().stream().filter(d -> !parent.getPublications().contains(d)).collect(Collectors.toList()));
        }
        if (parent.getIdentifier() == null) {
            parent.setIdentifier(new ArrayList<>());
        }
        if(child.getIdentifier()!=null){
            parent.getIdentifier().addAll(child.getIdentifier().stream().filter(d -> !parent.getIdentifier().contains(d)).collect(Collectors.toList()));
        }
        if(!parent.getIdentifier().contains(child.getId())){
            parent.getIdentifier().add(child.getId());
        }

    }

}