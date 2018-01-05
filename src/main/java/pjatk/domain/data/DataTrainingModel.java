package pjatk.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DataTrainingModel {
    private List<DataBlock> dataBlockList;
}
