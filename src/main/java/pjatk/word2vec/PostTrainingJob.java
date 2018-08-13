package pjatk.word2vec;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.Tag;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.TagRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostTrainingJob {
    private static final String[] GOOD_WORDS = {"good", "happy", "success"};
    private static final String[] BAD_WORDS = {"bad", "evil", "tragic", "failure"};

    private final DataBlockRepository dataBlockRepository;
    private final TagRepository tagRepository;

    public void postTraining(Word2Vec word2Vec) {
        tagRepository.findAll().parallelStream().forEach(tag -> {
            tag.setSimilarWords(getNearestWordsDelimited(word2Vec, tag));
            tagRepository.save(tag);
            log.info("Tag " + tag.getContent() + " similar to: " + tag.getSimilarWords());
        });

        dataBlockRepository.findByWord2VecUnprocessedIsFalse().parallelStream()
                .forEach(dataBlock -> setCosineSimilarites(word2Vec, dataBlock));
    }

    private void setCosineSimilarites(Word2Vec word2Vec, DataBlock dataBlock) {
        dataBlock.setCosineSimilarityToGoodWords(Arrays.stream(dataBlock.getContent().split("\\s|\\W"))
                .map(token -> Arrays.stream(GOOD_WORDS)
                        .map(goodWord -> word2Vec.similarity(token, goodWord))
                        .collect(Collectors.averagingDouble(e -> e)))
                .collect(Collectors.averagingDouble(e -> e)));
        dataBlock.setCosineSimilarityToGoodWords(Arrays.stream(dataBlock.getContent().split("\\s|\\W"))
                .map(token -> Arrays.stream(BAD_WORDS)
                        .map(goodWord -> word2Vec.similarity(token, goodWord))
                        .collect(Collectors.averagingDouble(e -> e)))
                .collect(Collectors.averagingDouble(e -> e)));
        log.info("DataBlock(" + dataBlock.getId() + "): bad cosine similarity(" + dataBlock
                .getCosineSimilarityToBadWords() + ") good cosine similarity(" + dataBlock
                .getCosineSimilarityToGoodWords() + ")");
        dataBlockRepository.save(dataBlock);
    }

    private String getNearestWordsDelimited(Word2Vec word2Vec, Tag tag) {
        StringBuilder sb = new StringBuilder();
        word2Vec.wordsNearest(tag.getContent().toLowerCase(), 5).forEach(word -> {
            sb.append(word);
            sb.append(";");
        });
        return sb.toString();
    }


}
