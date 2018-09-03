package pjatk.doc2vec;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.springframework.stereotype.Service;

import pjatk.domain.modelconfig.ParagraphVectorsConfiguration;

/**
 * @author Michał Dąbrowski
 */
@Service
public class ParagraphVectorsService {
    public ParagraphVectors train(ParagraphVectorsConfiguration paragraphVectorsConfiguration) {
        ParagraphVectors paragraphVectors = buildParagraphVectorsModel(paragraphVectorsConfiguration);
        paragraphVectors.fit();
        return paragraphVectors;
    }

    private ParagraphVectors buildParagraphVectorsModel(ParagraphVectorsConfiguration paragraphVectorsConfiguration) {
        return new ParagraphVectors.Builder()
                .minWordFrequency(paragraphVectorsConfiguration.getMinWordFrequency())
                .iterations(paragraphVectorsConfiguration.getIterations())
                .epochs(paragraphVectorsConfiguration.getEpochs())
                .layerSize(paragraphVectorsConfiguration.getLayerSize())
                .seed(paragraphVectorsConfiguration.getSeed())
                .windowSize(paragraphVectorsConfiguration.getWindowSize())
                .iterate(paragraphVectorsConfiguration.getLabelAwareSentenceIterator())
                .tokenizerFactory(paragraphVectorsConfiguration.getTokenizerFactory())
                .stopWords(paragraphVectorsConfiguration.getStopWords())
                .labelsSource(paragraphVectorsConfiguration.getLabelsSource())
                .build();
    }
}
