package pjatk.doc2vec;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import pjatk.domain.data.DataBlock;

@Service
@NoArgsConstructor
public class MeansBuilderService {

    public INDArray documentAsVector(DataBlock dataBlock, InMemoryLookupTable<VocabWord> lookupTable,
            TokenizerFactory tokenizerFactory) {
        List<String> documentAsTokens = tokenizerFactory.create(dataBlock.getContent()).getTokens();
        AtomicInteger cnt = new AtomicInteger(0);
        for (String word: documentAsTokens) {
            if (lookupTable.getVocabCache().containsWord(word)) cnt.incrementAndGet();
        }
        INDArray allWords = Nd4j.create(cnt.get(), lookupTable.layerSize());

        cnt.set(0);
        for (String word: documentAsTokens) {
            if (lookupTable.getVocabCache().containsWord(word))
                allWords.putRow(cnt.getAndIncrement(), lookupTable.vector(word));
        }

        INDArray mean = allWords.mean(0);

        return mean;
    }
}