package pjatk;

import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataSource;
import pjatk.domain.data.DataTrainingModel;
import pjatk.word2vec.DataTrainingModelIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michał Dąbrowski
 */
public abstract class DataTest {
    public static final String SIMPLE_SENTENCES = "Simple sentence. Another simple sentence. One another simple sentence. ";

    public DataTrainingModelIterator createDataTrainingModelIterator() {
        return createDataTrainingModelIterator(1);
    }

    public DataTrainingModelIterator createDataTrainingModelIterator(Integer iter) {
        DataBlock dataBlock = DataBlock.builder()
                .content(SIMPLE_SENTENCES)
                .origin(DataSource.TEST)
                .build();

        List<DataBlock> dataBlockList = new ArrayList<>();
        for(int i = 0; i < iter; i++) {
            dataBlockList.add(dataBlock);
        }
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);

        return new DataTrainingModelIterator(dataTrainingModel);
    }
}
