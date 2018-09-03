package pjatk.doc2vec;

import java.util.List;

import pjatk.persist.TagRepository;

@FunctionalInterface
public interface LabelFetcher {
    List<String> fetch(TagRepository tagRepository);
}