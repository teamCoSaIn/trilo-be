package com.cosain.trilo.unit.trip.application.day.dto.factory;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.day.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.dto.factory.DayColorUpdateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidDayColorNameException;
import com.cosain.trilo.trip.domain.vo.DayColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("DayColorUpdateCommandFactory 테스트")
public class DayColorUpdateCommandFactoryTest {

    private DayColorUpdateCommandFactory dayColorUpdateCommandFactory;

    @BeforeEach
    void setUp() {
        this.dayColorUpdateCommandFactory = new DayColorUpdateCommandFactory();
    }

    @DisplayName("정상적인 색상명 -> command 생성됨")
    @ValueSource(strings = {"BLACK", "RED", "Blue", "light_GREEN"})
    @ParameterizedTest
    void successTest(String rawColorName) {
        // given : rawColorName

        // when
        DayColorUpdateCommand command = dayColorUpdateCommandFactory.createCommand(rawColorName);

        // then
        assertThat(command.getDayColor()).isNotNull();
        assertThat(command.getDayColor().name()).isEqualToIgnoringCase(rawColorName);
        assertThat(command.getDayColor()).isSameAs(DayColor.of(rawColorName));
    }


    @DisplayName("colorName null -> 검증 예외 발생")
    @Test
    void nullColorNameTest() {
        // given
        String rawColorName = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> dayColorUpdateCommandFactory.createCommand(rawColorName),
                CustomValidationException.class);

        // then
        List<CustomException> exceptions = cve.getExceptions();

        assertThat(cve).isNotNull();
        assertThat(exceptions.size()).isEqualTo(1);
        assertThat(exceptions.get(0)).isInstanceOf(InvalidDayColorNameException.class);
    }


    @ValueSource(strings = {"adfadf", "rainbow", "pink", "", "999", "GOLD"})
    @ParameterizedTest
    @DisplayName("유효하지 않은 색상 이름 -> 검증예외 발생")
    public void testInvalidDayColorName(String rawColorName) {
        // given : rawColorName

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> dayColorUpdateCommandFactory.createCommand(rawColorName),
                CustomValidationException.class);

        // then
        List<CustomException> exceptions = cve.getExceptions();

        assertThat(cve).isNotNull();
        assertThat(exceptions.size()).isEqualTo(1);
        assertThat(exceptions.get(0)).isInstanceOf(InvalidDayColorNameException.class);
    }

}
