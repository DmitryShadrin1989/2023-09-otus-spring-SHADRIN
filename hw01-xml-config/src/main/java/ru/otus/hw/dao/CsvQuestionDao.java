package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String fileName = fileNameProvider.getTestFileName();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        List<QuestionDto> questionDtoList;
        try {
            InputStreamReader inputStreamReader;
            if (inputStream != null) {
                inputStreamReader = new InputStreamReader(inputStream);
            } else {
                throw new NullPointerException();
            }
            questionDtoList = new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withSeparator('$')
                    .withSkipLines(1)
                    .withType(QuestionDto.class)
                    .build()
                    .parse();
            if (questionDtoList == null) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            throw new QuestionReadException("Error when reading questions", e);
        }
        return questionDtoList.stream().map(QuestionDto::toDomainObject).collect(Collectors.toList());
    }
}
