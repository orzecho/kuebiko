package pjatk.doc2vec.fetchers;

import java.util.stream.Collectors;

import pjatk.doc2vec.fetchers.LabelFetcher;
import pjatk.domain.data.Tag;

public class LabelFetchers {
    public static LabelFetcher allProcessedLabels() {
        return repository -> repository.findByParagraphVectorsProcessedIsTrue().stream()
                .map(Tag::getContent)
                .collect(Collectors.toList());
    }
}
