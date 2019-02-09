package pjatk.api;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Streams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import pjatk.domain.data.ParagraphVectorsResult;
import pjatk.domain.data.Score;
import pjatk.domain.data.Tag;
import pjatk.domain.results.AssignedTagDTO;
import pjatk.domain.results.ResultDTO;
import pjatk.domain.results.ResultsSummaryDTO;
import pjatk.persist.ParagraphVectorsResultRepository;

@RequestMapping("/results")
@RestController
@RequiredArgsConstructor
@Slf4j
@Component
public class ResultsController {
    private final ParagraphVectorsResultRepository paragraphVectorsResultRepository;

    @GetMapping("/{experimentName}")
    public ResultsSummaryDTO getResultsSummary(@PathVariable String experimentName) {
        List<ParagraphVectorsResult> results = paragraphVectorsResultRepository.findByExperimentName(experimentName);
        Long howManyTags = results.stream().map(e -> e.getDataBlock().getTags())
                .flatMap(List::stream)
                .map(Tag::getId)
                .distinct().count();
        List<ResultDTO> resultsDTO = results.stream()
                .peek(result -> Hibernate.initialize(result.getScores()))
                .filter(result -> result.getScores() != null)
                .map(result -> {
                    List<AssignedTagDTO> top5AssignedTags = getTopAssignedTags(result, 5);
                    List<AssignedTagDTO> top3AssignedTags = getTopAssignedTags(result, 3);
                    List<AssignedTagDTO> top2AssignedTags = getTopAssignedTags(result, 2);
                    List<AssignedTagDTO> top1AssignedTags = getTopAssignedTags(result, 1);
                    List<AssignedTagDTO> topFAssignedTags = getTopAssignedTags(result, result.getDataBlock().getTags().size());
                    List<AssignedTagDTO> assignedTags = result.getScores()
                    .stream()
                    .sorted(Comparator.comparingDouble(Score::getValue).reversed())
                    .map(e -> AssignedTagDTO.builder().name(e.getLabel()).score(e.getValue()).build())
                    .collect(Collectors.toList());
                    List<String> originalTags = result.getDataBlock().getTags().stream().map(Tag::getContent)
                            .collect(Collectors.toList());
                    Long howManyMatch5 = howManyMatch(top5AssignedTags, originalTags);
                    Long howManyMatch3 = howManyMatch(top3AssignedTags, originalTags);
                    Long howManyMatch2 = howManyMatch(top2AssignedTags, originalTags);
                    Long howManyMatch1 = howManyMatch(top1AssignedTags, originalTags);
                    Long howManyMatchF = howManyMatch(topFAssignedTags, originalTags);
                    return ResultDTO.builder()
                            .content(result.getDataBlock().getContent())
                            .originalTags(originalTags)
                            .assignedTags(assignedTags)
                            .howManyMatch(howManyMatch5.intValue())
                            .anyMatch(howManyMatch5 > 0)
                            .p5(getPrecision(originalTags, howManyMatch5))
                            .p3(getPrecision(originalTags, howManyMatch3))
                            .p1(getPrecision(originalTags, howManyMatch1))
                            .pf(getPrecision(originalTags, howManyMatchF))
                            .a5(getAccuracy(top5AssignedTags, originalTags, howManyMatch5))
                            .a3(getAccuracy(top3AssignedTags, originalTags, howManyMatch3))
                            .a1(getAccuracy(top1AssignedTags, originalTags, howManyMatch1))
                            .af(getAccuracy(topFAssignedTags, originalTags, howManyMatchF))
                            .r5(getRecall(howManyMatch5, top5AssignedTags))
                            .r3(getRecall(howManyMatch3, top3AssignedTags))
                            .r1(getRecall(howManyMatch1, top1AssignedTags))
                            .rf(getRecall(howManyMatchF, topFAssignedTags))
                            .oneError(howManyMatch1 == 1)
                            .twoError(howManyMatch2 > 0)
                            .build();
                }).collect(Collectors.toList());
        long resultCount = resultsDTO.stream().count();
        double p5 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getP5);
        double p3 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getP3);
        double p1 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getP1);
        double pf = avgInDataSet(resultsDTO, resultCount, ResultDTO::getPf);

        double a5 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getA5);
        double a3 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getA3);
        double a1 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getA1);
        double af = avgInDataSet(resultsDTO, resultCount, ResultDTO::getAf);

        double r5 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getR5);
        double r3 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getR3);
        double r1 = avgInDataSet(resultsDTO, resultCount, ResultDTO::getR1);
        double rf = avgInDataSet(resultsDTO, resultCount, ResultDTO::getRf);

        double oneError = avgInDataSet(resultsDTO, resultCount, dto -> dto.isOneError() ? 1.0 : 0.0);
        double twoError = avgInDataSet(resultsDTO, resultCount, dto -> dto.isTwoError() ? 1.0 : 0.0);
        val summary = ResultsSummaryDTO.builder()
                .experimentName(experimentName)
                .howManyBlocksHadAtLeastOneMatch(resultsDTO.stream().filter(ResultDTO::isAnyMatch).count())
                .howManyDataBlocks(resultCount)
                .howManyMatches((resultsDTO.stream()
                        .mapToLong(resultDTO -> Long.valueOf(resultDTO.getHowManyMatch()))).sum())
                .howManyTags(howManyTags)
                .results(resultsDTO)
                .p5(p5)
                .p3(p3)
                .p1(p1)
                .pf(pf)
                .a5(a5)
                .a3(a3)
                .a1(a1)
                .af(af)
                .r5(r5)
                .r3(r3)
                .r1(r1)
                .rf(rf)
                .oneError(oneError)
                .twoError(twoError)
                .build();

        try {
            writeSummaryTable(summary);
            writeParTable(summary);
            writeSummaryDetailsTable(summary);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        return summary;
    }

    private double avgInDataSet(List<ResultDTO> resultsDTO, double resultCount, ToDoubleFunction<ResultDTO> mapper) {
        return resultsDTO.stream().mapToDouble(mapper).sum() / resultCount;
    }

    private double getRecall(Long howManyMatchF, List<AssignedTagDTO> assignedTags) {
        return howManyMatchF.doubleValue() / (double) assignedTags.size();
    }

    private double getAccuracy(List<AssignedTagDTO> assignedTags, List<String> originalTags, Long howManyMatch5) {
        return howManyMatch5.doubleValue() / (double) union(assignedTags, originalTags).size();
    }

    private Set<String> union(List<AssignedTagDTO> assignedTags, List<String> originalTags) {
        return Streams.concat(assignedTags.stream().map(AssignedTagDTO::getName), originalTags.stream())
                .distinct()
                .collect(Collectors.toSet());
    }

    private double getPrecision(List<String> originalTags, Long howManyMatch5) {
        return howManyMatch5.doubleValue() / (double) originalTags.size();
    }

    private long howManyMatch(List<AssignedTagDTO> top5AssignedTags, List<String> originalTags) {
        return originalTags.stream().filter(originalTag -> top5AssignedTags.stream()
                .anyMatch(t -> t.getName().equals(originalTag))).count();
    }

    private List<AssignedTagDTO> getTopAssignedTags(ParagraphVectorsResult result, long n) {
        return result.getScores()
                        .stream()
                        .sorted(Comparator.comparingDouble(Score::getValue).reversed()).limit(n)
                        .map(e -> AssignedTagDTO.builder().name(e.getLabel()).score(e.getValue()).build())
                        .collect(Collectors.toList());
    }

    private void writeParTable(ResultsSummaryDTO summary) throws IOException{
        DecimalFormat df = new DecimalFormat("#.##");
        FileWriter fileWriter = new FileWriter(summary.getExperimentName() + "-par-" + LocalDateTime.now().toString());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Experiment name&P5&P3&P1&PF&A5&A3&A1&AF&R5&R3&R1&PF&OneError&TwoError\\\\%n");
        printWriter.printf("%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s\\\\%n",
                summary.getExperimentName(),
                df.format(summary.getP5()),
                df.format(summary.getP3()),
                df.format(summary.getP1()),
                df.format(summary.getPf()),
                df.format(summary.getA5()),
                df.format(summary.getA3()),
                df.format(summary.getA1()),
                df.format(summary.getAf()),
                df.format(summary.getR5()),
                df.format(summary.getR3()),
                df.format(summary.getR1()),
                df.format(summary.getRf()),
                df.format(summary.getOneError()),
                df.format(summary.getTwoError())
        );
        printWriter.close();
    }

    private void writeSummaryTable(ResultsSummaryDTO summary) throws IOException {
        FileWriter fileWriter = new FileWriter(summary.getExperimentName() + "-" + LocalDateTime.now().toString());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Experiment name&Articles in evaluation&Processed tags&No matching tags&"
                + "No articles with matching tags\\\\%n");
        printWriter.printf("%s&%s&%s&%s&%s\\\\%n",
                summary.getExperimentName(), summary.getHowManyDataBlocks().toString(),
                summary.getHowManyTags().toString(),
                summary.getHowManyMatches().toString(),
                summary.getHowManyBlocksHadAtLeastOneMatch().toString());
        printWriter.close();
    }

    private void writeSummaryDetailsTable(ResultsSummaryDTO summary) throws IOException {
        FileWriter fileWriter = new FileWriter(summary.getExperimentName() + "-details-" + LocalDateTime.now()
                .toString());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Original tags&Assigned tags&NoM&AnyM\\\\%n");
        summary.getResults().forEach(r ->
            printWriter.printf("%s&%s&%s&%s\\\\%n",
                    String.join(", ", r.getOriginalTags().stream()
                            .map(e -> e.replaceAll("#", "")).collect(Collectors.toList())),
                    String.join(", ", r.getAssignedTags().stream()
                            .limit(5)
                            .map(e -> String.format("%s(%.2f)", e.getName().replaceAll("#",""),
                                    e.getScore()))
                            .collect(Collectors.toList())),
                    r.getHowManyMatch(),
                    r.isAnyMatch()));
        printWriter.close();
    }
}
