package pjatk.doc2vec;

import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.Score;

@Service
@RequiredArgsConstructor
public class LabelSeekerService {

    public List<Score> getScores(INDArray vector, List<String> labelsUsed, InMemoryLookupTable<VocabWord> lookupTable) {
        return labelsUsed.stream().map(label -> {
            INDArray vectorForLabel = getVectorOrThrow(lookupTable, label);
            double sim = Transforms.cosineSim(vector, vectorForLabel);
            return Score.builder().label(label).value(sim).build();
        }).collect(Collectors.toList());
    }

    private INDArray getVectorOrThrow(InMemoryLookupTable<VocabWord> lookupTable, String label) {
        INDArray vectorForLabel = lookupTable.vector(label);
        if (vectorForLabel == null) throw new IllegalStateException("Label '"+ label+"' has no known vector!");
        return vectorForLabel;
    }
}
